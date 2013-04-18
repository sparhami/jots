package com.sppad.jots.construction;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import org.snmp4j.smi.OID;

import com.sppad.jots.JotsOID;
import com.sppad.jots.constructor.SnmpTree;
import com.sppad.jots.datastructures.primative.IntStack;

public class TreeConstructor
{
  private final IntStack extensionStack = new IntStack();
  private final Map<Node, IntStack> staticOidMap;
  private final int[] prefix = new int[0];

  private TreeConstructor(Map<Node, IntStack> staticOidMap)
  {
    this.staticOidMap = staticOidMap;
  }

  public static SnmpTree create(Object obj, TreeBuilder treeBuilder)
  {
    Node node = NodeTreeConstructor.createTree(obj.getClass(),
        treeBuilder.getInclusionStrategy());
    Map<Node, IntStack> staticOidMap = OidGenerator.getStaticOidParts(node);

    create(staticOidMap, node, obj);

    return null;
  }

  public static void create(
      Map<Node, IntStack> staticOidMap,
      Node node,
      Object obj)
  {
    TreeConstructor tc = new TreeConstructor(staticOidMap);

    tc.descend(node, obj);
  }

  private void descend(Node node, Object obj)
  {
    if (node instanceof LeafNode)
    {
      String value = obj.toString();

      IntStack staticOid = staticOidMap.get(node);
      OID oid = new JotsOID(prefix, staticOid, extensionStack);

      //System.out.println(oid + " " + value);
    }
    else
    {
      for (Node child : node.nodes)
      {
        if (child instanceof TableNode)
        {
          handleCollection((TableNode) child, obj);
        }
        else
        {
          handleObject(child, obj);
        }
      }
    }
  }

  private void handleObject(Node node, Object obj)
  {
    try
    {
      Field field = node.field;
      field.setAccessible(true);

      Object next = field.get(obj);

      descend(node, next);
    }
    catch (IllegalArgumentException | IllegalAccessException e)
    {
      e.printStackTrace();
    }
  }

  private void handleCollection(TableNode node, Object obj)
  {
    try
    {
      Node child = node.snmpNodes.iterator().next();
      assert (child instanceof TableEntryNode);

      Field field = node.field;
      field.setAccessible(true);

      Collection<?> collection = (Collection<?>) field.get(obj);

      int index = 1;
      extensionStack.push(0);

      for (Object next : collection)
      {
        extensionStack.pop();
        extensionStack.push(index++);
        descend(child, next);
      }

      extensionStack.pop();

    }
    catch (IllegalArgumentException | IllegalAccessException e)
    {
      e.printStackTrace();
    }

  }
}
