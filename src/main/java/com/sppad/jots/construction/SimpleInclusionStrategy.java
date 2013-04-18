package com.sppad.jots.construction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sppad.jots.log.Messages;

public class SimpleInclusionStrategy implements InclusionStrategy
{
  /**
   * Marks that a field should always be included when generating an SnmpTree.
   * This overrides all other considerations.
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface SnmpInclude
  {
  }

  /**
   * Marks that a field should always be skipped when generating an SnmpTree.
   * This overrides all other considerations except for {@link SnmpInclude}.
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface SnmpExclude
  {
  }

  private static final String INCLUDE_AND_IGNORE_ANNOTATIONS = Messages
      .getString("INCLUDE_AND_IGNORE_ANNOTATIONS");

  private static final Logger logger = LoggerFactory
      .getLogger(SimpleInclusionStrategy.class);

  @Override
  public boolean include(final Field field)
  {
    final boolean includeAnnotation = field.getAnnotation(SnmpInclude.class) != null;
    final boolean excludeAnnotation = field.getAnnotation(SnmpExclude.class) != null;
    final int mod = field.getModifiers();

    if (includeAnnotation && excludeAnnotation)
    {
      logger.warn(INCLUDE_AND_IGNORE_ANNOTATIONS, field.getDeclaringClass(),
          field.getName());
    }

    if (includeAnnotation)
      return true;
    else if (excludeAnnotation)
      return false;
    else if (Modifier.isStatic(mod) && !includeStatic())
      return false;
    else if (Modifier.isTransient(mod) && !includeTransient())
      return false;
    else if (!Modifier.isFinal(mod) && !includeNonFinal())
      return false;
    else
      return true;
  }

  protected boolean includeStatic()
  {
    return false;
  }

  protected boolean includeTransient()
  {
    return false;
  }

  protected boolean includeNonFinal()
  {
    return false;
  }

}
