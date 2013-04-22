package com.sppad.jots.lookup;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sppad.jots.exceptions.SnmpBadValueException;

class ValueConverters
{
  @SuppressWarnings("rawtypes")
  private static final LoadingCache<Class<? extends Enum>, Function<String, Object>> cacheEnumConverters = CacheBuilder
      .newBuilder().weakValues()
      .build(new CacheLoader<Class<? extends Enum>, Function<String, Object>>()
      {
        @Override
        public Function<String, Object> load(final Class<? extends Enum> key)
            throws Exception
        {
          return new Function<String, Object>()
          {
            @SuppressWarnings("unchecked")
            @Override
            public Object apply(final String input)
            {
              try
              {
                return Enum.valueOf(key, input);
              }
              catch (IllegalArgumentException e)
              {
                throw new SnmpBadValueException(String.format(
                    "Value %s is not valid for this field", input));
              }
            }
          };
        }
      });

  static final Function<String, Object> CONVERT_TO_BOOLEAN = new Function<String, Object>()
  {
    @Override
    public Object apply(final String input)
    {
      if ("true".equalsIgnoreCase(input))
        return Boolean.TRUE;
      else if ("false".equalsIgnoreCase(input))
        return Boolean.FALSE;
      else
        throw new SnmpBadValueException(input);
    }
  };

  static final Function<String, Object> CONVERT_TO_DOUBLE = new Function<String, Object>()
  {
    @Override
    public Object apply(final String input)
    {
      try
      {
        return Double.parseDouble(input);
      }
      catch (NumberFormatException e)
      {
        throw new SnmpBadValueException(input);
      }
    }
  };

  static final Function<String, Object> CONVERT_TO_FLOAT = new Function<String, Object>()
  {
    @Override
    public Object apply(final String input)
    {
      try
      {
        return Float.parseFloat(input);
      }
      catch (NumberFormatException e)
      {
        throw new SnmpBadValueException(input);
      }
    }
  };

  static final Function<String, Object> CONVERT_TO_INT = new Function<String, Object>()
  {
    @Override
    public Object apply(final String input)
    {
      try
      {
        return Integer.parseInt(input);
      }
      catch (NumberFormatException e)
      {
        throw new SnmpBadValueException(input);
      }
    }
  };

  static final Function<String, Object> CONVERT_TO_LONG = new Function<String, Object>()
  {
    @Override
    public Object apply(final String input)
    {
      try
      {
        return Long.parseLong(input);
      }
      catch (NumberFormatException e)
      {
        throw new SnmpBadValueException(input);
      }
    }
  };

  static final Function<String, Object> CONVERT_TO_STRING = new Function<String, Object>()
  {
    @Override
    public Object apply(final String input)
    {
      return input;
    }
  };

  @SuppressWarnings("rawtypes")
  static Function<String, Object> enumConverter(
      final Class<? extends Enum> enumClass)
  {
    return cacheEnumConverters.getUnchecked(enumClass);
  }
}
