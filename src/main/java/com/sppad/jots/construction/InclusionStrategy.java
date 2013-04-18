package com.sppad.jots.construction;

import java.lang.reflect.Field;

public interface InclusionStrategy
{
  boolean include(Field field);
}
