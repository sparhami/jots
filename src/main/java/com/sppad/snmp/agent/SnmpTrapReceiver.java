package com.sppad.snmp.agent;

import java.io.IOException;
import java.net.InetSocketAddress;

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

import com.sppad.snmp.agent.SnmpAgent.Protocol;
import com.sppad.snmp.exceptions.InformException;

public abstract class SnmpTrapReceiver implements CommandResponder
{
  static int version = SnmpConstants.version3;

  private static final Logger logger = LoggerFactory
      .getLogger(SnmpTrapReceiver.class);

  /** Snmp object for dealing with SNMP requests */
  private final Snmp snmp;

  /** TransportMapping used for sending / receiving data */
  private final TransportMapping<?> transport;

  public SnmpTrapReceiver(InetSocketAddress address, Protocol proto)
      throws IOException
  {
    if (proto == Protocol.udp)
      this.transport = new DefaultUdpTransportMapping(new UdpAddress(
          address.getAddress(), address.getPort()));
    else
      this.transport = new DefaultTcpTransportMapping(new TcpAddress(
          address.getAddress(), address.getPort()));

    snmp = new Snmp(transport);

    // if (version == SnmpConstants.version3)
    // {
    // // byte[] localEngineID = MPv3.createLocalEngineID();
    // byte[] localEngineID = "foobar".getBytes();
    // USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(
    // localEngineID), 0);
    // SecurityModels.getInstance().addSecurityModel(usm);
    // snmp.setLocalEngine(localEngineID, 0, 0);
    //
    // OctetString secName = new OctetString("foo");
    // OID authProto = AuthSHA.ID;
    // OctetString authPass = new OctetString("foo12345");
    // OID privProto = PrivAES128.ID;
    // OctetString privPass = new OctetString("foo12345");
    //
    // UsmUser user = new UsmUser(secName, authProto, authPass, privProto,
    // privPass);
    //
    // OctetString engineId = new OctetString(localEngineID);
    // usm.addUser(user.getSecurityName(), engineId, user);
    // // Add the configured user to the USM
    // }

    // USM usm = snmp.getUSM();

    snmp.addCommandResponder(this);
    snmp.listen();
  }

  public void close() throws IOException
  {
    snmp.close();
  }

  public void processInform(CommandResponderEvent request)
  {
    PDU response = new PDU();

    try
    {
      processTrap(request);
    }
    catch (InformException e)
    {
      response.setErrorStatus(PDU.badValue);
      response.setErrorIndex(e.index);
      logger.debug("Inform exception: ", e);
    }

    try
    {
      response.setRequestID(request.getPDU().getRequestID());
      response.setType(PDU.RESPONSE);

      request.getMessageDispatcher().returnResponsePdu(
          request.getMessageProcessingModel(), request.getSecurityModel(),
          request.getSecurityName(), request.getSecurityLevel(), response,
          request.getMaxSizeResponsePDU(), request.getStateReference(), null);
    }
    catch (MessageException e)
    {
      logger.error("Exception sending inform response: ", e);
    }
  }

  @Override
  public void processPdu(CommandResponderEvent request)
  {
    logger.debug("Received trap: {}", request.getPDU());

    PDU command = request.getPDU();

    try
    {
      switch (command.getType())
      {
        case PDU.TRAP:
          processTrap(request);
          break;
        case PDU.INFORM:
          processInform(request);
          break;
        default:
          throw new Exception(String.format("Type not implemented {} ",
              PDU.getTypeString(command.getType())));
      }
    }
    catch (Exception e)
    {
      logError(request, e);
    }

    request.setProcessed(true);
  }

  protected abstract void processTrap(CommandResponderEvent request);

  private void logError(CommandResponderEvent request, Throwable t)
  {
    String commandType = PDU.getTypeString(request.getPDU().getType());

    logger.error("Exception while handling {} {} : {}", new Object[] {
        commandType, request.getPDU(), t.getMessage() });
  }
}
