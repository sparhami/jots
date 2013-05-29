package com.sppad.jots.agent;

import org.snmp4j.PDU;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.VariableBinding;

import com.sppad.jots.exceptions.SnmpPduLengthException;

/**
 * Extends PDU class in order to keep track of the current request index being
 * processed as well as the current response length. The PDU class does not seem
 * to track the max PDU length in anyway and will allow you to happily add more
 * data, only to cause a problem when you try to send it.
 */
class CustomPDU
{
	/**
	 * Apparently, using up to the maxPduLength causes an error. TODO - Need to
	 * find the proper way to do this.
	 */
	private static final int OVERHEAD_BUFFER = 200;

	/** Keep track of the current length, quicker than recalculating it */
	private int length = 0;

	/** The max length for the response */
	private final int maxPduLength;

	private final PDU pdu = new PDU();

	/** Keep track of the current index for reporting errors */
	private int requestIndex = 0;

	/**
	 * Creates a custom PDU for making sure the length limit is not reached.
	 * 
	 * @param maxPduLength
	 *            The max length for the PDU response.
	 */
	CustomPDU(final int maxPduLength)
	{
		this.maxPduLength = maxPduLength;
	}

	/**
	 * Adds the VariableBinding object to the response after checking to make
	 * sure the max response length is not exceeded.
	 * 
	 * @param vb
	 *            The VariableBinding to add the the PDU
	 * @throws SnmpPduLengthException
	 */
	void add(final VariableBinding vb) throws SnmpPduLengthException
	{
		length += vb.getBERLength();
		if (length + OVERHEAD_BUFFER > maxPduLength)
			throw new SnmpPduLengthException();

		pdu.add(vb);
	}

	PDU getPDU()
	{
		return pdu;
	}

	int getRequestIndex()
	{
		return requestIndex;
	}

	void incrementRequestIndex()
	{
		requestIndex++;
	}

	void setErrorIndex(int errorIndex)
	{
		pdu.setErrorIndex(errorIndex);
	}

	void setErrorStatus(int errorStatus)
	{
		pdu.setErrorStatus(errorStatus);
	}

	void setRequestID(Integer32 requestID)
	{
		pdu.setRequestID(requestID);
	}

	void setType(int type)
	{
		pdu.setType(type);
	}
}
