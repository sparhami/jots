package com.sppad.jots.agent;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.MessageException;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.sppad.jots.agent.SnmpAgent.Protocol;
import com.sppad.jots.exceptions.SnmpInformException;

public abstract class SnmpTrapReceiver implements CommandResponder
{
  static int version = SnmpConstants.version3;

  private static final Logger logger = LoggerFactory
      .getLogger(SnmpTrapReceiver.class);

  /** Snmp object for dealing with SNMP requests */
  private final Snmp snmp;

  /** TransportMapping used for sending / receiving data */
  private final TransportMapping<?> transport;

  public SnmpTrapReceiver(final InetSocketAddress address, final Protocol proto)
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

  public void close()
      throws IOException
  {
    snmp.close();
  }

  public void processInform(final CommandResponderEvent request)
  {
    final PDU response = new PDU();

    try
    {
      processTrap(request);
    }
    catch (final SnmpInformException e)
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
    catch (final MessageException e)
    {
      logger.error("Exception sending inform response: ", e);
    }
  }

  @Override
  public void processPdu(final CommandResponderEvent request)
  {
    logger.debug("Received trap: {}", request.getPDU());

    final PDU command = request.getPDU();

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
    catch (final Exception e)
    {
      logError(request, e);
    }

    request.setProcessed(true);
  }

  protected abstract void processTrap(final CommandResponderEvent request);

  private void logError(final CommandResponderEvent request, final Throwable t)
  {
    final String commandType = PDU.getTypeString(request.getPDU().getType());

    logger.error("Exception while handling {} {} : {}", new Object[] {
        commandType, request.getPDU(), t.getMessage() });
  }
}
