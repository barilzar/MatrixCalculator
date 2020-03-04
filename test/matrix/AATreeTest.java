package matrix;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * 
 */
public class AATreeTest {
    private static final int COUNT = 10000;		//Amount of test values
    private static double[] val;				//An array for the test values
    private static AATree<Integer, Double> tree;//The test tree

    @BeforeClass
    public static void setUpClass() throws Exception {
        //Generate some test values:
        val = new double[COUNT];
        
        for(int i = 0; i < COUNT; i++)
            val[i] = Math.random();
        
        tree = new AATree();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        for(int i = 0; i < val.length; i++)
            tree.add(i, val[i]);
    }

    @After
    public void tearDown() throws Exception {
        tree.clear();
    }

    /**
     * Test of add method, of class AATree.
     */
    @Test
    public void testAdd() {
        //Add a value and try to find it:
        int k = COUNT + 1; double v = 10D;
        tree.add(k, v);
        assertTrue(tree.containsKey(k));
        assertTrue(tree.containsValue(v));
        assertTrue(tree.get(k) == v);
        
        //Add a new value to an existing key, and see that the old 
        //value is overwritten:
        double v2 = 20D;
        tree.add(k, v2);
        assertTrue(tree.containsKey(k));
        assertFalse(tree.containsValue(v));
        assertTrue(tree.containsValue(v2));
        assertTrue(tree.get(k) == v2);
    }

    /**
     * Test of get method, of class AATree.
     */
    @Test
    public void testGet() {
        //Try to find all of our test values from the tree:
        for(int i  = 0; i < val.length; i++)
            assertTrue(tree.get(i) == val[i]);
    }

    /**
     * Test of remove method, of class AATree.
     */
    @Test
    public void testRemove() {
        //Remove each key and make sure it was removed:
        for(int i = 0; i < val.length; i++){
            tree.remove(i);
            assertFalse(tree.containsKey(i));
        }
        
        //After removing all of the keys the tree should be empty:
        assertTrue(tree.isEmpty());
    }

    /**
     * Test of size method, of class AATree.
     */
    @Test
    public void testSize() {
        //Test tree and test data size should be equal:
        assertTrue(tree.size() == val.length);
        
        //Add a value and check the new size:
        tree.add(-1, 1D);
        assertTrue(tree.size() == val.length + 1);
        
        //Remove a value and check the new size:
        tree.remove(-1);
        assertTrue(tree.size() == val.length);
        
        //Empty tree has size of 0:
        tree.clear();
        assertTrue(tree.size() == 0);
    }

    /**
     * Test of isEmpty method, of class AATree.
     */
    @Test
    public void testIsEmpty() {
        AATree t = new AATree();
        
        //A new tree is always empty:
        assertTrue(t.isEmpty());
        
        //Tree with a value is not empty:
        t.add(1, 1);
        assertFalse(t.isEmpty());
        
        //Remove the value -> tree is empty again:
        t.remove(1);
        assertTrue(t.isEmpty());
        
        //Test tree should not be empty:
        assertFalse(tree.isEmpty());
    }

    /**
     * Test of containsKey method, of class AATree.
     */
    @Test
    public void testContainsKey() {
        //See that all test keys are in the tree, then remove the 
        //key and make sure it was removed:
        for(int i = 0; i < val.length; i++){
            assertTrue(tree.containsKey(i));
            tree.remove(i);
            assertFalse(tree.containsKey(i));
        }
    }

    /**
     * Test of containsValue method, of class AATree.
     */
    @Test
    public void testContainsValue() {
        //See that test values are in the tree, then remove them 
        //and make sure the value was removed:
        for(int i = 0; i < val.length; i++){
            assertTrue(tree.containsValue(val[i]));
            tree.remove(i);
            assertFalse(tree.containsValue(val[i]));
        }
    }

    /**
     * Test of put method, of class AATree.
     */
    @Test
    public void testPut() {
        int k = -1; 
        double v = 1D;
        double v2;
        
        //Add a value, replace it and make sure we got the old value back:
        tree.add(k, v);
        v2 = tree.put(k, v * 2);
        assertTrue(v == v2);
    }

    /**
     * Test of remove method, of class AATree.
     */
    @Test
    public void testRemoveObject() {
        double v;
        //Remove our test values and make sure the return 
        //value corresponds to the test value:
        for(int i = 0; i < val.length; i++){
            v = tree.remove((Object) Integer.valueOf(i));
            assertTrue(v == val[i]);
        }
    }

    /**
     * Test of putAll method, of class AATree.
     */
    @Test
    public void testPutAll() {
        //Generate another set of test values:
        double[] val2 = new double[COUNT];
        for(int i = 0; i < val2.length; i++){
            val2[i] = Math.random();
        }
        
        //Make a map of our new values:
        Map<Integer, Double> m = new HashMap();
        for(int i = 0; i < val2.length; i++)
            m.put(i + COUNT, val2[i]);
        
        //Add the new map to the test tree:
        tree.putAll(m);
        
        //Find all of the original values from the tree:
        for(int i = 0; i < val.length; i++)
            assertTrue(tree.get(i) == val[i]);
        
        //Find the added values from the tree:
        for(int i = 0; i < val2.length; i++)
            assertTrue(tree.get(i + COUNT) == val2[i]);
    }

    /**
     * Test of clear method, of class AATree.
     */
    @Test
    public void testClear() {
        //Clear the tree; it sohuld be empty:
        tree.clear();
        assertTrue(tree.isEmpty());
    }

    /**
     * Test of keySet method, of class AATree.
     */
    @Test
    public void testKeySet() {
        //See that all of the test keys can be found from the key set:
        Set<Integer> s = tree.keySet();
        for(int i = 0; i < val.length; i++)
            assertTrue(s.contains(i));
    }

    /**
     * Test of values method, of class AATree.
     */
    @Test
    public void testValues() {
        //See that all of the test values can be found from the value set:
        Collection c = tree.values();
        for(int i = 0; i < val.length; i++)
            assertTrue(c.contains(val[i]));
    }

    /**
     * Test of entrySet method, of class AATree.
     */
    @Test
    public void testEntrySet() {
        //Make sure all of the test key-value pairs are in the entry set:
        Set<Entry<Integer, Double>> s = tree.entrySet();
        
        s.forEach(e -> {
            assertTrue(tree.get(e.getKey()) == val[e.getKey()]);
            assertTrue(val[e.getKey()] == e.getValue());
        });
    }

    /**
     * Test of forEach method, of class AATree.
     */
    @Test
    public void testForEachBiConsumer() {
        tree.forEach((k, v) -> assertTrue(val[k] == v));
    }

    /**
     * Test of forEach method, of class AATree.
     */
    @Test
    public void testForEachConsumer() {
        tree.forEach(e -> {
            assertTrue(tree.get(e.getKey()) == val[e.getKey()]);
            assertTrue(val[e.getKey()] == e.getValue());
        });
    }

    /**
     * Test of replaceAll method, of class AATree.
     */
    @Test
    public void testReplaceAll() {
        tree.replaceAll((k, v) -> {
            return v * 3;
                });
        
        for(int i = 0; i < val.length; i++)
            assertTrue(tree.get(i) == val[i] * 3);
    }
}
