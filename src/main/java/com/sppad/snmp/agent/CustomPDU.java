package com.sppad.snmp.agent;

import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;

import com.sppad.snmp.exceptions.SnmpPduLengthException;

/**
 * Extends PDU class in order to keep track of the current request index being
 * processed as well as the current response length. The PDU class does not seem
 * to track the max PDU length in anyway and will allow you to happily add more
 * data, only to cause a problem when you try to send it.
 * 
 * @author sepand
 */
public class CustomPDU extends PDU
{
    /**
     * Apparently, using up to the maxPduLength causes an error. TODO - Need to
     * find the proper way to do this.
     */
    private static final int OVERHEAD_BUFFER = 200;

    /** Default serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** Keep track of the current index for reporting errors */
    public int currentRequestPduIndex = 0;

    /** Keep track of the current length, quicker than recalculating it */
    private int length = 0;

    /** The max length for the response */
    private final int maxPduLength;

    /**
     * Creates a custom PDU for making sure the length limit is not reached.
     * 
     * @param maxPduLength
     *            The max length for the PDU response.
     */
    public CustomPDU(int maxPduLength)
    {
	this.maxPduLength = maxPduLength;
    }

    /**
     * Adds the VariableBinding object to the response after checking to make
     * sure the max response length is not exceeded.
     * 
     * @param vb
     *            The VariableBinding to add the the PDU
     */
    @Override
    public void add(VariableBinding vb)
    {
	length += vb.getBERLength();
	if (length + OVERHEAD_BUFFER > maxPduLength)
	    throw new SnmpPduLengthException("Max payload exceeded");

	super.add(vb);
    }
}
