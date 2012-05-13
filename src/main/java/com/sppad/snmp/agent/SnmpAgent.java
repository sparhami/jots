package com.sppad.snmp.agent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.*;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.PrivAES128;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.mp.StatusInformation;

import com.sppad.snmp.constructor.SnmpTree;
import com.sppad.snmp.exceptions.SnmpBadValueException;
import com.sppad.snmp.exceptions.SnmpException;
import com.sppad.snmp.exceptions.SnmpNoMoreEntriesException;
import com.sppad.snmp.exceptions.SnmpNotWritableException;
import com.sppad.snmp.exceptions.SnmpOidNotFoundException;
import com.sppad.snmp.exceptions.SnmpPastEndOfTreeException;
import com.sppad.snmp.exceptions.SnmpPduLengthException;
import com.sppad.snmp.exceptions.SnmpWrongTypeException;

public class SnmpAgent implements CommandResponder
{
    public enum Protocol
    {
        tcp, udp;
    }

    private static final Logger logger = LoggerFactory
            .getLogger(SnmpAgent.class);

    static int version = SnmpConstants.version3;

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

    public SnmpAgent(SnmpTree tree, InetSocketAddress address, Protocol proto)
            throws IOException
    {
        this.tree = tree;

        if (proto == Protocol.udp)
            this.transport = new DefaultUdpTransportMapping(new UdpAddress(
                    address.getAddress(), address.getPort()));
        else
            this.transport = new DefaultTcpTransportMapping(new TcpAddress(
                    address.getAddress(), address.getPort()));

        snmp = new Snmp(transport);

        if (version == SnmpConstants.version3)
        {
            // byte[] localEngineID = MPv3.createLocalEngineID();
            byte[] localEngineID = "foobar".getBytes();
            USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(
                    localEngineID), 0);
            SecurityModels.getInstance().addSecurityModel(usm);
            snmp.setLocalEngine(localEngineID, 0, 0);

            OctetString secName = new OctetString("foo");
            OID authProto = AuthSHA.ID;
            OctetString authPass = new OctetString("foo12345");
            OID privProto = PrivAES128.ID;
            OctetString privPass = new OctetString("foo12345");

            UsmUser user = new UsmUser(secName, authProto, authPass, privProto,
                    privPass);

            OctetString engineId = new OctetString(localEngineID);
            usm.addUser(user.getSecurityName(), engineId, user);
            // Add the configured user to the USM
        }

        // USM usm = snmp.getUSM();

        snmp.addCommandResponder(this);
        snmp.listen();

        logger.info("Started SNMP agent on {}", address);
    }

    public void close() throws IOException
    {
        snmp.close();
        logger.info("Sopped SNMP agent on {}", transport);
    }

    public void doSnmpGet(OID oid, CustomPDU response)
    {
        response.add(tree.get(oid));
    }

    public void doSnmpGetNext(OID oid, CustomPDU response)
    {
        response.add(tree.getNext(oid));
    }

    public void doSnmpSet(VariableBinding vb, CustomPDU response)
    {
        tree.set(vb.getOid(), vb.getVariable().toString());

        response.add(tree.get(vb.getOid()));
    }

    @Override
    public void processPdu(CommandResponderEvent request)
    {
        logger.debug("Got request PDU: {}", request.getPDU());

        long startTime = System.nanoTime();

        PDU command = request.getPDU();

        StatusInformation status = new StatusInformation();
        CustomPDU response = new CustomPDU(request.getMaxSizeResponsePDU());
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
                throw new Exception(String.format("Type not implemented {} ",
                        PDU.getTypeString(command.getType())));
            }
        }
        catch (SnmpWrongTypeException e)
        {
            response.setErrorStatus(PDU.badValue);
            response.setErrorIndex(response.currentRequestPduIndex);
        }
        catch (SnmpBadValueException e)
        {
            response.setErrorStatus(PDU.badValue);
            response.setErrorIndex(response.currentRequestPduIndex);
        }
        catch (SnmpPduLengthException e)
        {
            // nothing to do here, just return with what we have
            // System.out.println("caught exception, now to return.");
        }
        catch (SnmpOidNotFoundException e)
        {
            response.setErrorStatus(PDU.noSuchName);
            response.setErrorIndex(response.currentRequestPduIndex);
        }
        catch (SnmpNotWritableException e)
        {
            response.setErrorStatus(PDU.notWritable);
            response.setErrorIndex(response.currentRequestPduIndex);
        }
        catch (SnmpPastEndOfTreeException e)
        {
            response.setErrorStatus(PDU.noSuchName);
            response.setErrorIndex(response.currentRequestPduIndex);
        }
        catch (SnmpNoMoreEntriesException e)
        {
            // nothing to do here, just return with what we have
            // System.out.println("caught exception, now to return.");
        }
        catch (IllegalArgumentException e)
        {
            response.setErrorStatus(PDU.noSuchName);
            response.setErrorIndex(response.currentRequestPduIndex);
            logError(request, response, e);
        }
        catch (SnmpException e)
        {
            response.setErrorStatus(PDU.genErr);
            response.setErrorIndex(response.currentRequestPduIndex);
            logError(request, response, e);
        }
        catch (Exception e)
        {
            response.setErrorStatus(PDU.genErr);
            response.setErrorIndex(response.currentRequestPduIndex);
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
                    request.getSecurityLevel(), response,
                    request.getMaxSizeResponsePDU(),
                    request.getStateReference(), status);
        }
        catch (MessageException e)
        {
            logger.error("Exception while returning response: ", e);
        }

        request.setProcessed(true);

        long time = System.nanoTime() - startTime;
        logger.debug("Finished request, time: {}", time);
    }

    public void updateTree(SnmpTree tree)
    {
        updateLock.writeLock().lock();

        this.tree = tree;

        updateLock.writeLock().unlock();
    }

    void doSnmpBulkGet(OID oid, int getCount, CustomPDU response)
    {
        int startIndex = tree.getNextIndex(oid);
        int lastIndex = tree.lastIndex;
        for (int indexOffset = 0; indexOffset < getCount; indexOffset++)
            if (startIndex + indexOffset > lastIndex)
                break;
            else
                response.add(tree.get(startIndex + indexOffset));
    }

    /**
     * Processes a get request by performing {@link #doSnmpGet(OID, CustomPDU)}
     * on each varBind.
     * 
     * @param request
     *            The request object.
     * @param response
     *            The response to add to.
     * @throws IllegalAccessException
     */
    void processGet(CommandResponderEvent request, CustomPDU response)
    {
        // using getBulk
        // request.getPDU().setNonRepeaters(Integer.MAX_VALUE);
        // processGetBulk(request, response);

        for (VariableBinding var : request.getPDU().getVariableBindings())
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
     * <p>
     * The size of the bulk get (number of following OIDs after the specified
     * OID to get) is maxRepititions.
     * 
     * @param request
     *            The request object.
     * @param response
     *            The response to add to.
     * @throws IllegalAccessException
     */
    void processGetBulk(CommandResponderEvent request, CustomPDU response)
    {
        // nonRepeaters - how many OIDs to do get on before starting bulkgets
        int nonRepeaters = request.getPDU().getNonRepeaters();
        // maxRepititions - how many following OIDs to get for the current OIDs
        int maxRepititions = request.getPDU().getMaxRepetitions();

        for (VariableBinding var : request.getPDU().getVariableBindings())
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
     * @throws IllegalAccessException
     */
    void processGetNext(CommandResponderEvent request, CustomPDU response)
    {
        // using getBulk
        // request.getPDU().setMaxRepetitions(1);
        // processGetBulk(request, response);

        for (VariableBinding var : request.getPDU().getVariableBindings())
        {
            doSnmpGetNext(var.getOid(), response);
            response.currentRequestPduIndex++;
        }
    }

    void processSet(CommandResponderEvent request, CustomPDU response)
    {
        for (VariableBinding var : request.getPDU().getVariableBindings())
        {
            doSnmpSet(var, response);
            response.currentRequestPduIndex++;
        }
    }

    private void logError(CommandResponderEvent request, CustomPDU response,
            Throwable t)
    {
        String commandType = PDU.getTypeString(request.getPDU().getType());
        int index = response.currentRequestPduIndex;

        logger.error("Exception while handling {} {} : {}", new Object[] {
                commandType, request.getPDU().getVariableBindings().get(index),
                t.getMessage() });
        logger.error("Stacktrace: ", t);
    }
}
