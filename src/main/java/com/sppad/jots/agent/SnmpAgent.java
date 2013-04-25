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
import com.sppad.jots.exceptions.SnmpNoMoreEntriesException;
import com.sppad.jots.exceptions.SnmpNotWritableException;
import com.sppad.jots.exceptions.SnmpOidNotFoundException;
import com.sppad.jots.exceptions.SnmpPastEndOfTreeException;
import com.sppad.jots.exceptions.SnmpPduLengthException;

public class SnmpAgent implements CommandResponder
{
	public enum Protocol
	{
		tcp, udp;
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

		if (proto == Protocol.udp)
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
	 * @throws SnmpNoMoreEntriesException
	 * @throws SnmpPduLengthException
	 */
	void doSnmpBulkGet(final OID oid, final int getCount,
						final CustomPDU response)
			throws SnmpPastEndOfTreeException, SnmpNoMoreEntriesException,
			SnmpOidNotFoundException, SnmpPduLengthException
	{
		final int startIndex = tree.getNextIndex(oid);
		final int lastIndex = tree.getLastIndex();
		for (int indexOffset = 0; indexOffset < getCount; indexOffset++)
			if (startIndex + indexOffset > lastIndex)
				break;
			else
				response.add(tree.get(startIndex + indexOffset));
	}

	public void doSnmpGet(final OID oid, final CustomPDU response)
			throws SnmpNoMoreEntriesException, SnmpOidNotFoundException,
			SnmpPduLengthException
	{
		response.add(tree.get(oid));
	}

	public void doSnmpGetNext(final OID oid, final CustomPDU response)
			throws SnmpNoMoreEntriesException, SnmpOidNotFoundException,
			SnmpPastEndOfTreeException, SnmpPduLengthException
	{
		response.add(tree.getNext(oid));
	}

	public void doSnmpSet(final VariableBinding vb, final CustomPDU response)
			throws SnmpNotWritableException, SnmpNoMoreEntriesException,
			SnmpOidNotFoundException, SnmpPduLengthException
	{
		tree.set(vb.getOid(), vb.getVariable().toString());

		response.add(tree.get(vb.getOid()));
	}

	private void logError(final CommandResponderEvent request,
							final CustomPDU response, final Throwable t)
	{
		final String commandType = PDU
				.getTypeString(request.getPDU().getType());
		final int index = response.currentRequestPduIndex;
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
	 * @throws SnmpNoMoreEntriesException
	 * @throws SnmpPduLengthException
	 */
	void processGet(final CommandResponderEvent request,
					final CustomPDU response)
			throws SnmpNoMoreEntriesException, SnmpOidNotFoundException,
			SnmpPduLengthException
	{
		for (final VariableBinding var : request.getPDU().getVariableBindings())
		{
			doSnmpGet(var.getOid(), response);
			response.currentRequestPduIndex++;
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
	 * @throws SnmpNoMoreEntriesException
	 * @throws SnmpPastEndOfTreeException
	 * @throws SnmpPduLengthException
	 */
	void processGetBulk(final CommandResponderEvent request,
						final CustomPDU response)
			throws SnmpPastEndOfTreeException, SnmpNoMoreEntriesException,
			SnmpOidNotFoundException, SnmpPduLengthException
	{
		// nonRepeaters - how many OIDs to do get on before starting bulkgets
		final int nonRepeaters = request.getPDU().getNonRepeaters();
		// maxRepititions - how many following OIDs to get for the current OIDs
		final int maxRepititions = request.getPDU().getMaxRepetitions();

		for (final VariableBinding var : request.getPDU().getVariableBindings())
		{
			if (response.currentRequestPduIndex >= nonRepeaters)
				doSnmpBulkGet(var.getOid(), maxRepititions, response);
			else
				doSnmpGet(var.getOid(), response);

			response.currentRequestPduIndex++;
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
	 * @throws SnmpPastEndOfTreeException
	 * @throws SnmpOidNotFoundException
	 * @throws SnmpNoMoreEntriesException
	 * @throws SnmpPduLengthException
	 */
	void processGetNext(final CommandResponderEvent request,
						final CustomPDU response)
			throws SnmpNoMoreEntriesException, SnmpOidNotFoundException,
			SnmpPastEndOfTreeException, SnmpPduLengthException
	{
		for (final VariableBinding var : request.getPDU().getVariableBindings())
		{
			doSnmpGetNext(var.getOid(), response);
			response.currentRequestPduIndex++;
		}
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
		} catch (final SnmpBadValueException e)
		{
			response.setErrorStatus(PDU.badValue);
			response.setErrorIndex(response.currentRequestPduIndex);
		} catch (final SnmpPduLengthException | SnmpNoMoreEntriesException e)
		{
			// nothing to do here, just return with what we have
			// System.out.println("caught exception, now to return.");
		} catch (final SnmpOidNotFoundException
				| SnmpPastEndOfTreeException
				| IllegalArgumentException e)
		{
			response.setErrorStatus(PDU.noSuchName);
			response.setErrorIndex(response.currentRequestPduIndex);
		} catch (final SnmpNotWritableException e)
		{
			response.setErrorStatus(PDU.notWritable);
			response.setErrorIndex(response.currentRequestPduIndex);
		} catch (final Exception e)
		{
			response.setErrorStatus(PDU.genErr);
			response.setErrorIndex(response.currentRequestPduIndex);
			logError(request, response, e);
		} finally
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
		} catch (final MessageException e)
		{
			logger.error("Exception while returning response: ", e);
		}

		request.setProcessed(true);

		final long time = System.nanoTime() - startTime;
		logger.debug("Finished request, time: {}", time);
	}

	/**
	 * Processes a set request.
	 * 
	 * @param request
	 *            The request object.
	 * @param response
	 * @throws SnmpOidNotFoundException
	 * @throws SnmpNoMoreEntriesException
	 * @throws SnmpNotWritableException
	 * @throws SnmpPduLengthException
	 */
	void processSet(final CommandResponderEvent request,
					final CustomPDU response) throws SnmpNotWritableException,
			SnmpNoMoreEntriesException, SnmpOidNotFoundException,
			SnmpPduLengthException
	{
		for (final VariableBinding var : request.getPDU().getVariableBindings())
		{
			doSnmpSet(var, response);
			response.currentRequestPduIndex++;
		}
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
}
