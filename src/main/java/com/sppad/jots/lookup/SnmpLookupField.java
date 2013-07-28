package com.sppad.jots.lookup;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

import com.google.common.base.Function;
import com.sppad.jots.annotations.SnmpNotSettable;
import com.sppad.jots.annotations.SnmpSettable;
import com.sppad.jots.exceptions.SnmpBadValueException;
import com.sppad.jots.exceptions.SnmpException;
import com.sppad.jots.exceptions.SnmpInternalException;
import com.sppad.jots.util.Fields;

public class SnmpLookupField extends LookupEntry
{
	public static SnmpLookupField create(final OID oid, final Field field,
			final Object object)
	{
		final Function<String, ? extends Object> valueConverter = ValueParsers
				.get(field.getType());

		if (valueConverter == null)
		{
			throw new IllegalArgumentException(
					"Cannot create SnmpLookupField for: " + field.getType());
		}

		return new SnmpLookupField(oid, field, object, valueConverter);
	}

	/** The object that corresponds to this OID instance */
	final Object enclosingObject;

	/** The field object for this OID */
	final Field field;

	/** The method that should be used when performing a set */
	final Method setter;

	/**
	 * Used for converting a String value into an Object for setting on the
	 * field
	 */
	final Function<String, ? extends Object> valueParser;

	/**
	 * Constructs an snmpLookupField object.
	 * 
	 * @param oid
	 * @param field
	 * @param object
	 */
	private SnmpLookupField(final OID oid, final Field field,
			final Object enclosingObject,
			final Function<String, ? extends Object> valueConverter)
	{
		super(oid);

		this.field = field;
		this.enclosingObject = enclosingObject;
		this.setter = Fields.getSetterForField(field);
		;
		this.valueParser = valueConverter;
	}

	/**
	 * @param annotationClass
	 *            An Annotation class
	 * @return The Annotation for the specified type if it exists, null
	 *         otherwise
	 */
	public <T extends Annotation> T getAnnotation(final Class<T> annotationClass)
	{
		return field.getAnnotation(annotationClass);
	}

	/**
	 * @return The object that contains the field
	 */
	public Object getEnclosingObject()
	{
		return enclosingObject;
	}

	/**
	 * @return The name of the field
	 */
	public String getFieldName()
	{
		return field.getName();
	}

	/**
	 * @return The type of this field.
	 */
	public Type getType()
	{
		return field.getType();
	}

	/**
	 * Performs a get, implementation specific to the type of a field.
	 * 
	 * @return An object representing the value of this field when the method is
	 *         called.
	 * @throws SnmpException
	 */
	public Object getValue() throws SnmpInternalException
	{
		try
		{
			return field.get(enclosingObject);
		}
		catch (final IllegalAccessException e)
		{
			throw new SnmpInternalException(e);
		}
	}

	/**
	 * @return Whether or not this field is considered to be writable
	 */
	public boolean isWritable()
	{
		final boolean ss = getAnnotation(SnmpSettable.class) != null;
		final boolean sns = getAnnotation(SnmpNotSettable.class) != null;

		return ss || (setter != null && !sns);
	}

	/**
	 * Sets the value of the field by using a setter if available, or directly
	 * otherwise. This does not check if the field is considered 'settable'.
	 * {@link #isWritable()} can be used to check if the field is considered
	 * 'settable'.
	 * 
	 * @param data
	 *            The data to set.
	 * @throws SnmpException
	 */
	public void set(String data) throws SnmpInternalException
	{
		try
		{
			setParsedValue(valueParser.apply(data));
		}
		catch (IllegalArgumentException e)
		{
			throw new SnmpBadValueException(oid, data);
		}
		catch (SecurityException | IllegalAccessException
				| InvocationTargetException e)
		{
			throw new SnmpInternalException(e.getCause().getMessage());
		}
	}

	/**
	 * Creates a VariableBInding with the OID for this field and the current
	 * value of the field.
	 */
	public VariableBinding toVarBind()
	{
		final Variable variable;
		final Object object = getValue();

		if (object instanceof Integer)
			variable = new Integer32((Integer) object);
		else if (object instanceof Enum)
			variable = new OctetString(((Enum<?>) object).name());
		else
			variable = new OctetString(object == null ? "" : object.toString());

		return new VariableBinding(oid, variable);
	}

	private void setParsedValue(final Object value)
			throws IllegalAccessException, InvocationTargetException
	{
		if (setter != null)
			setter.invoke(enclosingObject, value);
		else
			field.set(enclosingObject, value);
	}
}
