package com.sppad.jots.log;

import java.util.ResourceBundle;

public enum ErrorMessage
{
	COLLECTION_NO_ANNOTATION,
	INCLUDE_AND_IGNORE_ANNOTATIONS,
	TABLE_INDEX_NOT_INCLUDED,
	TABLE_INDEX_NOT_VALID,
	CANNOT_CREATE_SUBTREE_DUE_TO_ACCESS;

	private static final String BUNDLE_NAME = "com.sppad.jots.log.messages";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private static String getFormatFromResource(String key)
	{
		return RESOURCE_BUNDLE.getString(key);
	}

	private String fmt;

	public String getFmt()
	{
		// NOTE: name() is null within the constructor
		if (fmt != null)
			return fmt;
		else
			return fmt = getFormatFromResource(name());
	}
}
