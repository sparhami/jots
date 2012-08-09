package com.sppad.snmp.constructor.mib;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import com.sppad.snmp.annotations.BooleanInterfaceComment;

public class SnmpDescription
{
    public static String getDescription(Field field)
    {
	Type type = field.getType();
	if(type.equals(Boolean.class))
	    return getBooleanDescription(field);
	else if(type.equals(Boolean.TYPE))
	    return getBooleanDescription(field);
	
	return null;
    }
    
    private static String getBooleanDescription(Field field)
    {
	String descriptionString;
	
	BooleanInterfaceComment comment = field.getAnnotation(BooleanInterfaceComment.class);
	if(comment != null) {
	    StringBuilder description = new StringBuilder();
	    description.append(comment.synopsis());
	    description.append("\n\t\t 'true'  -> ");
	    description.append(comment.trueSynopsis());
	    description.append("\n\t\t 'false' -> ");
	    description.append(comment.falseSynopsis());
	    
	    descriptionString = description.toString();
	} else {
	    descriptionString = "No interface documentation";
	}

	return descriptionString;
    }
}
