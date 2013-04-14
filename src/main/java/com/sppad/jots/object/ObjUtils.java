package com.sppad.jots.object;

public class ObjUtils
{
  /**
   * Gets the object hashcode information.
   * 
   * @param object
   *          The object to get the information for.
   * @return A String containing the class name and the object hashcode.
   */
  public static String getRefInfo(final Object object)
  {
    return object.getClass() + "@" +
        Integer.toHexString(System.identityHashCode(object));
  }

}