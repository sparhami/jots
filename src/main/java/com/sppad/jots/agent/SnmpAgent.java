package com.sppad.jots.agent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.MessageException;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.mp.StatusInformation;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.sppad.jots.SnmpTree;
import com.sppad.jots.exceptions.SnmpBadValueException;
import com.sppad.jots.exceptions.SnmpNotWritableException;
import com.sppad.jots.exceptions.SnmpOidNotFoundException;
import com.sppad.jots.exceptions.SnmpPduLengthException;

public class SnmpAgent implements CommandResponder
{
	public enum Protocol
	{
		TCP, UDP;
	}

	private static final Logger logger = LoggerFactory
			.getLogger(SnmpAgent.class);

	/** Snmp object for dealing with SNMP requests */
	private final Snmp snmp;

	/** TransportMapping used for sending / receiving data */
	private final TransportMapping<?> transport;

	/** Used for getting / setting values */
	private SnmpTree tree;

	/**
	 * The read lock is for normal access to the tree. The write lock allows
	 * updating the tree
	 */
	private final ReentrantReadWriteLock updateLock = new ReentrantReadWriteLock();

	int version = SnmpConstants.version3;

	public SnmpAgent(final SnmpTree tree, final InetSocketAddress address,
			final Protocol proto) throws IOException
	{
		this.tree = tree;

		if (proto == Protocol.UDP)
			this.transport = new DefaultUdpTransportMapping(new UdpAddress(
					address.getAddress(), address.getPort()));
		else
			this.transport = new DefaultTcpTransportMapping(new TcpAddress(
					address.getAddress(), address.getPort()));

		snmp = new Snmp(transport);
		snmp.addCommandResponder(this);
		snmp.listen();

		logger.info("Started SNMP agent on {}", address);
	}

	public void close() throws IOException
	{
		snmp.close();
		logger.info("Stopped SNMP agent on {}", transport);
	}

	@Override
	public void processPdu(final CommandResponderEvent request)
	{
		logger.debug("Got request PDU: {}", request.getPDU());

		final long startTime = System.nanoTime();

		final PDU command = request.getPDU();

		final StatusInformation status = new StatusInformation();
		final CustomPDU response = new CustomPDU(
				request.getMaxSizeResponsePDU());

		response.setType(PDU.RESPONSE);
		response.setRequestID(command.getRequestID());

		try
		{
			updateLock.readLock().lock();

			switch (command.getType())
			{
			case PDU.GET:
				processGet(request, response);
				break;
			case PDU.GETNEXT:
				processGetNext(request, response);
				break;
			case PDU.GETBULK:
				processGetBulk(request, response);
				break;
			case PDU.SET:
				processSet(request, response);
				break;
			default:
				throw new RuntimeException(String.format(
						"Type not implemented {} ",
						PDU.getTypeString(command.getType())));
			}
		}
		catch (final SnmpPduLengthException e)
		{
			// nothing to do here, just return with what we have
			// System.out.println("caught exception, now to return.");
		}
		catch (final SnmpOidNotFoundException e)
		{
			response.setErrorStatus(PDU.noSuchName);
			response.setErrorIndex(response.getRequestIndex());
		}
		catch (final SnmpBadValueException e)
		{
			logger.info("Value {} is not valid for OID {}", e.getValue(),
					e.getOid());
			response.setErrorStatus(PDU.badValue);
			response.setErrorIndex(response.getRequestIndex());
		}
		catch (final SnmpNotWritableException e)
		{
			logger.info("Call to non writeable OID {}", e.getOid());
			response.setErrorStatus(PDU.notWritable);
			response.setErrorIndex(response.getRequestIndex());
		}
		catch (final Exception e)
		{
			response.setErrorStatus(PDU.genErr);
			response.setErrorIndex(response.getRequestIndex());
			logError(request, response, e);
		}
		finally
		{
			updateLock.readLock().unlock();
		}

		try
		{
			request.getMessageDispatcher().returnResponsePdu(
					request.getMessageProcessingModel(),
					request.getSecurityModel(), request.getSecurityName(),
					request.getSecurityLevel(), response.getPDU(),
					request.getMaxSizeResponsePDU(),
					request.getStateReference(), status);
		}
		catch (final MessageException e)
		{
			logger.error("Exception while returning response: ", e);
		}

		request.setProcessed(true);

		final long time = System.nanoTime() - startTime;
		logger.debug("Finished request, time: {}ns", time);
	}

	/**
	 * Replaces the tree used by the SnmpAgent with a new one.
	 * 
	 * @param tree
	 *            The tree use for future requests.
	 */
	public void updateTree(final SnmpTree tree)
	{
		updateLock.writeLock().lock();
		this.tree = tree;
		updateLock.writeLock().unlock();
	}

	/**
	 * Performs a bulk get, getting the requested number of entries after the
	 * given OID.
	 * 
	 * @param oid
	 *            The start OID to get entries from
	 * @param getCount
	 *            How many following entries to get
	 * @param response
	 *            The response to add to
	 * @throws SnmpPastEndOfTreeException
	 * @throws SnmpOidNotFoundException
	 * @throws SnmpPduLengthException
	 */
	private void doSnmpBulkGet(final OID oid, final int getCount,
			final CustomPDU response) throws SnmpOidNotFoundException,
			SnmpPduLengthException
	{
		logger.debug("Performing bulkget for {}, count {}", oid, getCount);

		final int startIndex = tree.getNextIndex(oid);
		final int lastIndex = tree.getLastIndex();
		final int lastGetIndex = Math.min(startIndex + (getCount - 1),
				lastIndex);

		for (int i = startIndex; i <= lastGetIndex; i++)
			response.add(tree.get(i));
	}

	private void doSnmpGet(final OID oid, final CustomPDU response)
			throws SnmpOidNotFoundException, SnmpPduLengthException
	{
		logger.debug("Performing get for {}", oid);
		response.add(tree.get(oid));
	}

	private void doSnmpGetNext(final OID oid, final CustomPDU response)
			throws SnmpOidNotFoundException, SnmpPduLengthException
	{
		logger.debug("Performing getnext for {}", oid);
		response.add(tree.getNext(oid));
	}

	private void doSnmpSet(final VariableBinding vb, final CustomPDU response)
			throws SnmpNotWritableException, SnmpOidNotFoundException,
			SnmpPduLengthException
	{
		tree.set(vb.getOid(), vb.getVariable().toString());

		logger.debug("Performing set for {}", vb);
		response.add(tree.get(vb.getOid()));
	}

	private void logError(final CommandResponderEvent request,
			final CustomPDU response, final Throwable t)
	{
		final String commandType = PDU
				.getTypeString(request.getPDU().getType());
		final int index = response.getRequestIndex();
		final VariableBinding vb = request.getPDU().getVariableBindings()
				.get(index);

		logger.error("Exception while handling {} {} : {}", commandType, vb,
				t.getMessage());
		logger.error("Stacktrace: ", t);
	}

	/**
	 * Processes a get request by performing {@link #doSnmpGet(OID, CustomPDU)}
	 * on each varBind.
	 * 
	 * @param request
	 *            The request object.
	 * @param response
	 *            The response to add to.
	 * @throws SnmpOidNotFoundException
	 * @throws SnmpPduLengthException
	 */
	private void processGet(final CommandResponderEvent request,
			final CustomPDU response) throws SnmpOidNotFoundException,
			SnmpPduLengthException
	{
		for (final VariableBinding var : request.getPDU().getVariableBindings())
		{
			doSnmpGet(var.getOid(), response);
			response.incrementRequestIndex();
		}
	}

	/**
	 * Processes an SNMP GETBULK request. The request is made up of
	 * non-repeaters (simple gets) and repeaters (bulk gets). For those varBinds
	 * that are bulk gets, the {@link #doSnmpBulkGet(OID, int, CustomPDU)} is
	 * used to add varBinds to the response.
	 * 
	 * @param request
	 *            The request object.
	 * @param response
	 *            The response to add to.
	 * @throws SnmpOidNotFoundException
	 * @throws SnmpPduLengthException
	 */
	private void processGetBulk(final CommandResponderEvent request,
			final CustomPDU response) throws SnmpOidNotFoundException,
			SnmpPduLengthException
	{
		// nonRepeaters - how many OIDs to do get on before starting bulkgets
		final int nonRepeaters = request.getPDU().getNonRepeaters();
		// maxRepititions - how many following OIDs to get for the current OIDs
		final int maxRepititions = request.getPDU().getMaxRepetitions();

		for (final VariableBinding var : request.getPDU().getVariableBindings())
		{
			if (response.getRequestIndex() >= nonRepeaters)
				doSnmpBulkGet(var.getOid(), maxRepititions, response);
			else
				doSnmpGet(var.getOid(), response);

			response.incrementRequestIndex();
		}
	}

	/**
	 * Processes a getNext request by performing
	 * {@link #doSnmpGetNext(OID, CustomPDU)} on each varBind.
	 * 
	 * @param request
	 *            The request object.
	 * @param response
	 *            The response to add to.
	 * @throws SnmpOidNotFoundException
	 * @throws SnmpPduLengthException
	 */
	private void processGetNext(final CommandResponderEvent request,
			final CustomPDU response) throws SnmpOidNotFoundException,
			SnmpPduLengthException
	{
		for (final VariableBinding var : request.getPDU().getVariableBindings())
		{
			doSnmpGetNext(var.getOid(), response);
			response.incrementRequestIndex();
		}
	}

	/**
	 * Processes a set request.
	 * 
	 * @param request
	 *            The request object.
	 * @param response
	 * @throws SnmpOidNotFoundException
	 * @throws SnmpNotWritableException
	 * @throws SnmpPduLengthException
	 */
	private void processSet(final CommandResponderEvent request,
			final CustomPDU response) throws SnmpNotWritableException,
			SnmpOidNotFoundException, SnmpPduLengthException
	{
		for (final VariableBinding var : request.getPDU().getVariableBindings())
		{
			doSnmpSet(var, response);
			response.incrementRequestIndex();
		}
	}
}
