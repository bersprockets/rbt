package javaplay.redblacktree;

import java.util.Arrays;

import javaplay.redblacktree.RedBlackTree;
import javaplay.symboltable.SymbolTable;
import junit.framework.TestCase;

public class RedBlackTreeTest extends TestCase {
	public void testPut() {
		SymbolTable map = new RedBlackTree();
		map.put("10", "10_test");
		map.put("07", "07_test");
		map.put("17", "17_test");
		assertEquals("17_test", map.get("17"));
		assertEquals(null, ((RedBlackTree)map).check());
		//System.out.println(map.toString());
	}
	
	public void testGetNull() {
		SymbolTable map = new RedBlackTree();
		map.put("10", "10_test");
		map.put("07", "07_test");
		map.put("17", "17_test");		
		assertEquals(null, map.get("fred"));
		assertEquals(null, ((RedBlackTree)map).check());
	}
	
	public void testCase2Coloring() {
		SymbolTable map = new RedBlackTree();
		map.put("D", "D_test");
		map.put("G", "G_test");
		map.put("a", "a_test");	
		
		assertEquals(null, ((RedBlackTree)map).check());
			
		assertEquals("D_test", map.get("D"));
		assertEquals("a_test", map.get("a"));
		assertEquals("G_test", map.get("G"));
	}
	
	private String[] getBigKeys() {
		return new String[]{"10", "05", "15", "03", "07", "13",
				"17", "01", "04", "06", "08", "11", "14", "16", "18"};
	}
	
	private SymbolTable getTree(String[] keys) {
		SymbolTable map = new RedBlackTree();
		for (String key : keys) {
			map.put(key, "test_" + key);
		}
		return map;
	}
	
	public void testSuccessor() {
		SymbolTable map = getTree(getBigKeys());
		//System.out.println(map.toString());
		assertEquals("10", map.successor("08"));
		assertEquals("16", map.successor("15"));
		assertEquals(null, ((RedBlackTree)map).check());
	}
	
	public void testSuccessorNull() {
		SymbolTable map = new RedBlackTree();
		map.put("10", "test_10");
		map.put("05", "test_05");
		//System.out.println(map.toString());
		assertNull(map.successor("flippity"));
		assertNull(map.successor("10"));
		assertEquals(null, ((RedBlackTree)map).check());
	}
	
	public void testPredecessor() {
		SymbolTable map = getTree(getBigKeys());
		assertEquals("14", map.predecessor("15"));
		assertEquals("05", map.predecessor("06"));
		assertEquals(null, ((RedBlackTree)map).check());
	}
	
	public void testPredecessorNull() {
		SymbolTable map = new RedBlackTree();
		map.put("10", "test_10");
		map.put("05", "test_05");
		//System.out.println(map.toString());
		assertNull(map.successor("10"));
		assertEquals(null, ((RedBlackTree)map).check());
	}
	
	public void testRemove() {
		String [] keys = getBigKeys();
		SymbolTable map = getTree(keys);
		assertNull(((RedBlackTree)map).check());
		
		System.out.println("------------------------------------------------------------------------------------");
		System.out.println(map.toString());
		System.out.println("------------------------------------------------------------------------------------");

		System.out.println("Removing 18");
		assertEquals("test_18", map.remove("18"));
		assertNull(map.get("18"));
		System.out.println("------------------------------------------------------------------------------------");
		System.out.println(map.toString());
		System.out.println("------------------------------------------------------------------------------------");
		assertNull(((RedBlackTree)map).check());
		
		System.out.println("Removing 05");
		assertEquals("test_05", map.remove("05"));
		assertNull(map.get("05"));
		System.out.println("------------------------------------------------------------------------------------");
		System.out.println(map.toString());
		System.out.println("------------------------------------------------------------------------------------");
		assertNull(((RedBlackTree)map).check());
		
		System.out.println("Removing 17");
		assertEquals("test_17", map.remove("17"));
		assertNull(map.get("17"));
		System.out.println("------------------------------------------------------------------------------------");
		System.out.println(map.toString());
		System.out.println("------------------------------------------------------------------------------------");

		assertNull(((RedBlackTree)map).check());
		
		// remove the root node
		System.out.println("Removing 10 (root)");
		assertEquals("test_10", map.remove("10"));
		assertNull(map.get("10"));
		System.out.println("------------------------------------------------------------------------------------");
		System.out.println(map.toString());
		System.out.println("------------------------------------------------------------------------------------");

		assertNull(((RedBlackTree)map).check());
	
		// ensure rest of keys are still there
		for (String key : keys) {
			switch (key) {
			case "18":
			case "17":
			case "10":
			case "05":
				continue;
			}
			assertEquals("test_"+key, map.get(key));
		}
		assertEquals(null, ((RedBlackTree)map).check());
	}
	
	
	public void testSize() {
		SymbolTable map = new RedBlackTree();
		
		assertEquals(0, map.size());
		
		map.put("10", "test_10");
		map.put("05", "test_05");
		map.put("17", "test_17");
		
		assertEquals(3, map.size());
		
		System.out.println("------------------------------------------------------------------------------------");
		System.out.println(map.toString());
		System.out.println("------------------------------------------------------------------------------------");

		assertNull(((RedBlackTree)map).check());

		map.remove("10");
		assertNull(((RedBlackTree)map).check());
		
		System.out.println("------------------------------------------------------------------------------------");
		System.out.println(map.toString());
		System.out.println("------------------------------------------------------------------------------------");

		
		map.remove("05");
		assertNull(((RedBlackTree)map).check());
		
		System.out.println("------------------------------------------------------------------------------------");
		System.out.println(map.toString());
		System.out.println("------------------------------------------------------------------------------------");
		map.remove("17");
		assertNull(((RedBlackTree)map).check());
		
		assertEquals(0, map.size());
		assertEquals(null, ((RedBlackTree)map).check());
	}
	
	public void testHeight() {
		SymbolTable map = new RedBlackTree();
		map.put("10", "test_10");
		map.put("05", "test_05");
		
		assertEquals(2, ((RedBlackTree)map).height());
		assertEquals(null, ((RedBlackTree)map).check());
	}
}
