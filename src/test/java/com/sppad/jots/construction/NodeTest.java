package com.sppad.jots.construction;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.sppad.jots.construction.nodes.Node;

public class NodeTest
{
	@Test
	public void testIsCollection_Collection()
	{
		assertThat(Node.isTable(Collection.class), is(true));
	}

	@Test
	public void testIsCollection_List()
	{
		assertThat(Node.isTable(List.class), is(true));
	}

	@Test
	public void testIsCollection_Map()
	{
		assertThat(Node.isTable(Map.class), is(true));
	}

	@Test
	public void testIsCollection_Set()
	{
		assertThat(Node.isTable(Set.class), is(true));
	}
}
