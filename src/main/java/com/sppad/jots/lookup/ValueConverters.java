package com.sppad.jots.lookup;

import java.util.Map;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.sppad.jots.exceptions.SnmpBadValueException;

class ValueConverters
{
	@SuppressWarnings("rawtypes")
	private static final LoadingCache<Class<? extends Enum>, Function<String, Enum>> cacheEnumConverters = CacheBuilder
			.newBuilder()
			.weakValues()
			.build(new CacheLoader<Class<? extends Enum>, Function<String, Enum>>()
			{
				@Override
				public Function<String, Enum> load(
						final Class<? extends Enum> key)
						throws Exception
				{
					return new Function<String, Enum>()
					{
						@SuppressWarnings("unchecked")
						@Override
						public Enum apply(final String input)
						{
							try
							{
								return Enum.valueOf(key, input);
							}
							catch (IllegalArgumentException e)
							{
								throw new SnmpBadValueException(String.format(
										"Value %s is not valid for this field",
										input));
							}
						}
					};
				}
			});

	private static final Function<String, Boolean> CONVERT_TO_BOOLEAN = new Function<String, Boolean>()
	{
		@Override
		public Boolean apply(final String input)
		{
			if ("true".equalsIgnoreCase(input))
				return Boolean.TRUE;
			else if ("false".equalsIgnoreCase(input))
				return Boolean.FALSE;
			else
				throw new SnmpBadValueException(input);
		}
	};

	private static final Function<String, Double> CONVERT_TO_DOUBLE = new Function<String, Double>()
	{
		@Override
		public Double apply(final String input)
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

	private static final Function<String, Float> CONVERT_TO_FLOAT = new Function<String, Float>()
	{
		@Override
		public Float apply(final String input)
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

	private static final Function<String, Integer> CONVERT_TO_INTEGER = new Function<String, Integer>()
	{
		@Override
		public Integer apply(final String input)
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

	private static final Function<String, Long> CONVERT_TO_LONG = new Function<String, Long>()
	{
		@Override
		public Long apply(final String input)
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

	private static final Function<String, String> CONVERT_TO_STRING = new Function<String, String>()
	{
		@Override
		public String apply(final String input)
		{
			return input;
		}
	};

	private static final Map<Class<?>, Function<String, ? extends Object>> CONVERTER_LOOKUP_MAP = ImmutableMap
			.<Class<?>, Function<String, ? extends Object>> builder()
			.put(Boolean.TYPE, CONVERT_TO_BOOLEAN)
			.put(Boolean.class, CONVERT_TO_BOOLEAN)
			.put(Integer.TYPE, CONVERT_TO_INTEGER)
			.put(Integer.class, CONVERT_TO_INTEGER)
			.put(Long.TYPE, CONVERT_TO_LONG)
			.put(Long.class, CONVERT_TO_LONG)
			.put(Float.TYPE, CONVERT_TO_FLOAT)
			.put(Float.class, CONVERT_TO_FLOAT)
			.put(Double.TYPE, CONVERT_TO_DOUBLE)
			.put(Double.class, CONVERT_TO_DOUBLE)
			.put(String.class, CONVERT_TO_STRING).build();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	static <T extends Enum> Function<String, T> enumConverter(
			final Class<? extends T> enumClass)
	{
		return (Function<String, T>) cacheEnumConverters
				.getUnchecked(enumClass);
	}

	@SuppressWarnings("unchecked")
	static Function<String, ? extends Object> get(Class<?> cls) {
		if (cls.isEnum())
			return enumConverter((Class<? extends Enum<?>>) cls);
		else
			return CONVERTER_LOOKUP_MAP.get(cls);
	}
}
