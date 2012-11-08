package com.sppad.snmp.constructor.mib;

import java.lang.reflect.Field;

import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import com.sppad.snmp.annotations.BooleanInterfaceComment;
import com.sppad.snmp.constructor.mib.SnmpDescription;

public class SnmpDescriptionTest
{
  @Test
  public void testGetBooleanDescription_class() throws SecurityException,
      NoSuchFieldException
  {
    Object obj = new Object()
    {
      @SuppressWarnings("unused")
      @BooleanInterfaceComment(
        synopsis = "This is something about the field",
        trueSynopsis = "This is a true comment",
        falseSynopsis = "This is a false comment")
      public Boolean testBoolean = true;
    };

    String expectedResult = "This is something about the field";
    expectedResult += "\n\t\t 'true'  -> This is a true comment";
    expectedResult += "\n\t\t 'false' -> This is a false comment";

    Field testField = obj.getClass().getDeclaredField("testBoolean");
    String actualResult = SnmpDescription.getDescription(testField);

    assertThat(actualResult, is(expectedResult));
  }

  @Test
  public void testGetBooleanDescription_type() throws SecurityException,
      NoSuchFieldException
  {
    Object obj = new Object()
    {
      @SuppressWarnings("unused")
      @BooleanInterfaceComment(
        synopsis = "This is something about the field",
        trueSynopsis = "This is a true comment",
        falseSynopsis = "This is a false comment")
      public boolean testBoolean = true;
    };

    String expectedResult = "This is something about the field";
    expectedResult += "\n\t\t 'true'  -> This is a true comment";
    expectedResult += "\n\t\t 'false' -> This is a false comment";

    Field testField = obj.getClass().getDeclaredField("testBoolean");
    String actualResult = SnmpDescription.getDescription(testField);

    assertThat(actualResult, is(expectedResult));
  }
}
