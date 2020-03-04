package matrix;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class SparseMatrixTest {
    
    //Maximum allowed difference in floating point equality
    private static final double ACC = 0.000001;
    
    //Amount of zeros in matrixes (percentage wise i.e. 0.7 == 70%)
    private static final double ZPROB = 0.7;
    
    @BeforeClass
    public static void setUpClass(){
    }
    
    @AfterClass
    public static void tearDownClass(){
    }
    
    @Before
    public void setUp(){
    }
    
    @After
    public void tearDown(){
    }

    @Test
    public void testWriteAndGetRow(){
        SparseMatrix a = SparseMatrix.randomMatrix(23, 12, ZPROB);
        
        //Use both get rows -> results should be equal
        double[] r0 = a.getRow(0);
        double[] r1 = new double[a.getColumns()];
        a.getRow(0, r1);
        
        for(int i = 0; i < a.getColumns(); i++){
            assertEquals(r0[i], r1[i], 0D);
            assertEquals(a.getValue(0, i), r0[i], 0D);
        }
        
        //Write a row and get it, these should be equal
        for(int i = 0; i < r0.length; i++)
            r0[i] = Math.random();
        
        a.writeRow(r0, 0);
        a.getRow(0, r1);
        
        assertArrayEquals(r0, r1, 0D);
    }

    @Test
    public void testWriteAndGetColumn(){
        SparseMatrix a = SparseMatrix.randomMatrix(7, 42, ZPROB);
        
        //Implementation almost identical to row test, only methods change
        double[] c0 = a.getColumn(0);
        double[] c1 = new double[a.getRows()];
        a.getColumn(0, c1);
        
        for(int i = 0; i < a.getRows(); i++){
            assertEquals(c0[i], c1[i], 0D);
            assertEquals(c0[i], a.getValue(i, 0), 0D);
        }
        
        for(int i = 0; i < c0.length; i++)
            c0[i] = Math.random();
        
        a.writeColumn(c0, 0);
        a.getColumn(0, c1);
        
        assertArrayEquals(c0, c1, 0D);
    }

    @Test
    public void testGetAndSetValue() {
        SparseMatrix a = SparseMatrix.randomMatrix(19, 5, ZPROB);
        
        double d = Math.random();
        double prev;
        
        prev = a.getValue(2, 4);
        a.setValue(d, 2, 4);
        
        assertEquals(d, a.getValue(2, 4), 0D);
        assertNotEquals(prev, a.getValue(2, 4), 0D);
    }

    @Test
    public void testGetRowsAndColumns() {
        int size = 20;
        SparseMatrix a = SparseMatrix.identityMatrix(size);
        
        assertEquals(a.getRows(), size);
        assertEquals(a.getColumns(), size);
    }

    @Test
    public void testSetSize() {
        SparseMatrix a = SparseMatrix.randomMatrix(25, 126, ZPROB);
        
        a.setSize(12, 56);
        assertEquals(a.getRows(), 12);
        assertEquals(a.getColumns(), 56);
        
        a.setSize(129, 58);
        assertEquals(a.getRows(), 129);
        assertEquals(a.getColumns(), 58);
    }

    @Test
    public void testEquals() {
        SparseMatrix a = SparseMatrix.randomMatrix(34, 51, ZPROB);
        SparseMatrix b = new SparseMatrix(a);
        
        //Matrix and it's copy should be equal
        assertEquals(a, b);
        
        double prev = b.getValue(0, 0);
        //Change a value -> no longer equal
        b.setValue(-1D, 0, 0);
        assertFalse(a.equals(b));
        
        //Change the value back -> equal again
        b.setValue(prev, 0, 0);
        assertEquals(a, b);
    }

    @Test
    public void testEqualsRounded() {
        System.out.println("equalsRounded");
        SparseMatrix comp = null;
        double diff = 0.0;
        SparseMatrix instance = new SparseMatrix();
        boolean expResult = false;
        boolean result = instance.equalsRounded(comp, diff);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testAddAndSubtract() {
        int x = 4;
        int y = 5;
        SparseMatrix a = SparseMatrix.randomMatrix(x, y, ZPROB);
        SparseMatrix b = SparseMatrix.randomMatrix(x, y, ZPROB);
        SparseMatrix c = SparseMatrix.randomMatrix(x, y, ZPROB);
        SparseMatrix d = SparseMatrix.randomMatrix(x, y, ZPROB);
        
        SparseMatrix t = a.add(b);
        //Make sure all values in two matrixes were added
        for(int i = 0; i < x; i++)
            for(int j = 0; j < y; j++)
                assertEquals(t.getValue(i, j), a.getValue(i, j) + b.getValue(i, j), ACC);
        
        //Make sure all values in two matrixes were subtracted
        t = a.subtract(b);
        for(int i = 0; i < x; i++)
            for(int j = 0; j < y; j++)
                assertEquals(t.getValue(i, j), a.getValue(i, j) - b.getValue(i, j), ACC);
        
        //Test A + B + C + D == C + B + A + D
        assertTrue(a.add(b).add(c).add(d).equalsRounded(c.add(b).add(a).add(d), ACC));
        
        //Test A - A == 0
        assertTrue(a.subtract(a).equalsRounded(new SparseMatrix(x, y), ACC));
        
        //Test A + B + C + D - A - B - C - D == 0
        assertTrue(a.add(b).add(c).add(d).subtract(a).subtract(b).subtract(c).subtract(d).
                   equalsRounded(new SparseMatrix(x, y), ACC));
        
        //Test A - B - C - D == A - C - D - B
        assertTrue(a.subtract(b).subtract(c).subtract(d).
                   equalsRounded(
                   a.subtract(c).subtract(d).subtract(b), ACC));
    }

    @Test
    public void testMultiplyScalar() {
        double multiplier = 5.3;
        SparseMatrix org = SparseMatrix.randomMatrix(100, 105, ZPROB);
        
        //Test that all non-zero values are multiplied
        SparseMatrix mul = org.multiplyScalar(multiplier);
        
        for(int i = 0; i < org.getRows(); i++)
            for(int j = 0; j < org.getColumns(); j++){
                if(org.getValue(i, j) != 0)
                    assertTrue(org.getValue(i, j) * multiplier == mul.getValue(i, j));
                else
                    assertTrue(org.getValue(i, j) == mul.getValue(i, j));
            }
        
        //Test that A + A + A + A + A == A * 5
        multiplier = 5D;
        mul = new SparseMatrix(org);
        for(int i = 1; i < (int) multiplier; i++)
            mul = mul.add(org);
        
        SparseMatrix m = org.multiplyScalar(multiplier);
        
        assertTrue(mul.equalsRounded(m, ACC));
        
        //Test that -A - A - A - A - A == A * -5
        mul = org.multiplyScalar(-1D);
        for(int i = 1; i < (int) multiplier; i++)
            mul = mul.subtract(org);
        
        m = org.multiplyScalar(-multiplier);
        
        assertTrue(mul.equalsRounded(m, ACC));
    }

    @Test
    public void testMultiply() {
        int size = 100;
        //Test that matrix multiplied with an identity matrix is equal to its self
        SparseMatrix m = SparseMatrix.randomMatrix(size, size, ZPROB);
        SparseMatrix mul = SparseMatrix.identityMatrix(size);
        
        assertEquals(m.multiply(mul), m);
        
        //Test that matrix multipled with a zero matrix is equal to a zero matrix
        mul = new SparseMatrix(size, size);
        assertEquals(m.multiply(mul), mul);
    }

    @Test
    public void testIdentityMatrix() {
        int size = 100;
        SparseMatrix m = SparseMatrix.identityMatrix(size);
        
        //Check that all of the values on diagonal are equal to one and that 
        //all other values are equal to zero
        for(int i = 0; i < size; i++)
            for(int j = 0; j < size; j++){
                if(i == j)
                    assertTrue(m.getValue(i, i) == 1D);
                else
                    assertTrue(m.getValue(i, j) == 0D);
            }  
    }
}
