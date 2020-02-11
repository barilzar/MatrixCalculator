package matrix;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A class suited for representing sparse matrixes.
 * <p>A sparse matrix is a matrix where most of the values are zero. Therefore
 * this class only keeps track of the non-zero values of the matrix and defaults
 * the rest to zero. This way space can be saved, as only a fraction of the values
 * in the matrix are tracked.
 */
public class SparseMatrix implements Serializable{
    
    
    private AATree<Pair, Double> values;
    
    private int rows;
    private int columns;
    
    /**
     * Creates a new matrix with zero size (i.e. no rows or columns).
     */
    public SparseMatrix(){
        this(0, 0);
    }
    
    /**
     * Creates a new matrix with the desired size. All values are by default zero.
     * @param rows Amount of rows in the new matrix.
     * @param columns Amount of columns in the new Matrix.
     */
    public SparseMatrix(int rows, int columns){
        checkIndexLow(rows);
        checkIndexLow(columns);
        
        values = new AATree();
        
        this.rows = rows;
        this.columns = columns;
    }
    
    /**
     * Creates a new matrix from the two dimensional array provided.
     * <p><b>NOTE: </b>If any of the nested arrays are null a NullPointerException will occur.
     * @param val Two dimensional array with the values to create this matrix by.
     */
    public SparseMatrix(double[][] val){
        this(val[0].length, val.length);
        
        for(int i = 0; i < val.length; i++)
            for(int j = 0; j < val[i].length; j++)
                setValueUnsafe(val[i][j], j, i);
    }
    
    /**
     * Creates a new matrix which is a copy of the original parameter matrix.
     * @param org Sparse matrix to be copied.
     */
    public SparseMatrix(SparseMatrix org){
        this(org.rows, org.columns);
        
        Objects.requireNonNull(org);
        
        //Loop through all the original values.
        org.values.forEach(e -> values.add(
                                new Pair(e.getKey().x, e.getKey().y),//Generate a new pair from the original.
                                e.getValue()));//Copy the value from the original.
    }
    
    /**
     * Writes all of the values in parameter array to the specified row of this matrix.
     * <p><b>NOTE: </b>Existing values in the row will be overwritten.
     * @param row Values to write.
     * @param index The index of the row where to write the values.
     * @throws IllegalArgumentException If there are not enough values in the 
     * array or if the index is out of bounds.
     */
    public void writeRow(double[] row, int index){
        Objects.requireNonNull(row);
        checkRow(index);
        
        if(row.length != columns)
            throw new IllegalArgumentException(
                    "Provided row does not have enough values. Got " + row.length + " needed " + columns);
        
        for(int i = 0; i < row.length; i++)
            setValueUnsafe(row[i], index, i);
    }
    
    /**
     * Writes all of the values in the parameter array to the specified column of this matrix.
     * <p><b>NOTE: </b>Existing values in the column will be overwritten.
     * @param column Values to write.
     * @param index The index of the column where the values will be written.
     * @throws IllegalArgumentException If the parameter array does not contain 
     * enough values, or if the index is less tan zero.
     */
    public void writeColumn(double[] column, int index){
        Objects.requireNonNull(column);
        checkColumn(index);
        
        if(column.length != rows)
            throw new IllegalArgumentException(
                    "Provided column does not have enough values. Got " + column.length + " needed " + rows);
        
        for(int i = 0; i < column.length; i++)
            setValueUnsafe(column[i], i, index);
    }
    
    /**
     * Writes all of the values in specified row to the array provided.
     * <p><b>NOTE: </b>if the provided array is not large enough 
     * (i.e.<code> space.length != getColumns()</code>) only as many values as can be fitted 
     * will be saved. That is to say, an IndexOutOfBounds error will not occur.
     * @param index Index of the row.
     * @param space Array where to save the values.
     * @throws IllegalArgumentException If the index is outside of the matrix bounds.
     */
    public void getRow(int index, double[] space){
        checkRow(index);
        for(int i = 0; i < space.length; i++)
            space[i] = getValueUnsafe(index, i);
    }
    
    /**
     * Returns a new array with all of the values on the specified row.
     * @param index Index of the row.
     * @return All of the values in the specified row.
     * @throws IllegalArgumentException If the index is outside of the matrix bounds.
     */
    public double[] getRow(int index){
        double[] d = new double[columns];
        getRow(index, d);
        return d;
    }
    
    /**
     * Writes all of the values in the specified column to the array provided.
     * <p><b>NOTE: </b>if the provided array is not large enough 
     * (i.e.<code> space.length != getRows()</code>) only as many values as can be fitted 
     * will be saved. That is to say, an IndexOutOfBounds error will not occur.
     * @param index The index of the column. 
     * @param space Array where to save the values.
     * @throws IllegalArgumentException If the index is outside of the matrix bounds.
     */
    public void getColumn(int index, double[] space){
        checkColumn(index);
        for(int i = 0; i < space.length; i++)
            space[i] = getValueUnsafe(i, index);
    }
    
    /**
     * Returns a new array with all of the values of the specified column.
     * @param index Index of the column.
     * @return An array containing all of the values in the row.
     * @throws IllegalArgumentException If the index is outside of the matrix bounds.
     */
    public double[] getColumn(int index){
        double[] d = new double[rows];
        getColumn(index, d);
        return d;
    }
    
    /**
     * Finds the value in the specified location.
     * @param row Row index of the value.
     * @param column Column index of the value.
     * @return The value in the specified spot.
     */
    public double getValue(int row, int column){
        checkRow(row);
        checkColumn(column);
        
        return getValueUnsafe(row, column);
    }
    
    /**
     * Returns the amount of rows in this matrix.
     * @return Amount of rows in this matrix.
     */
    public int getRows(){
        return rows;
    }
    
    /**
     * Returns the amount of columns in this matrix.
     * @return Amount of columns in this matrix.
     */
    public int getColumns(){
        return columns;
    }
    
    /**
     * Sets the size of this matrix to the specified dimensions.
     * <p>Increasing the size will not change existing values and all new 
     * values added will be zero by default
     * <p>Decreasing the size will remove values outside of the new dimensions.
     * @param rows New amount of rows for this matrix.
     * @param columns New Amount of columns for this matrix.
     * @throws IllegalArgumentException If the new dimensions are less than zero.
     */
    public void setSize(int rows, int columns){
        checkIndexLow(rows);
        checkIndexLow(columns);
        
        //New dimensions will decrease the matrix -> remove values
        if(rows <= this.rows || columns <= this.columns){
            
            //Use a cache to avoid any weird concurrent modification nonsense.
            ArrayList<Pair> cache = new ArrayList(values.size());
            
            //Find all of the values outside the new dimensions
            values.forEach(e -> {
                if(e.getKey().x < rows || e.getKey().y < columns)
                    cache.add(e.getKey());});
            
            //Remove all values found
            cache.forEach(p -> values.remove(p));
        }
        
        this.rows = rows;
        this.columns = columns;
    }
    
    /**
     * Checks that the provided index is higher than zero, and throws an error if not.
     * @param index Index to check.
     * @throws IllegalArgumentException If the provided index is less than zero.
     */
    private static void checkIndexLow(int index){
        if(index < 0)
            throw new IllegalArgumentException("Index must be higher than zero.");
    }
    
    /**
     * Checks that the provided index is less than the limit, and throws an error if not.
     * @param index Index to check.
     * @param limit Limit for the index.
     * @throws IllegalArgumentException If the provided index is less than the limit.
     */
    private static void checkIndexHigh(int index, int limit){
        if(index >= limit)
            throw new IllegalArgumentException("Index was too high. Got: " + index + " max: " + limit);
    }
    
    /**
     * Checks that the index is a valid row of this matrix and throws an error if not.
     * @param index The index to check.
     * @throws IllegalArgumentException If the index is not a valid row.
     */
    private void checkRow(int index){
        checkIndexLow(index);
        checkIndexHigh(index, rows);
    }
    
    /**
     * Checks that the index is a valid column of this matrix, ad throws an error if not.
     * @param index The index to check.
     * @throws IllegalArgumentException If the index is not a valid column.
     */
    private void checkColumn(int index){
        checkIndexLow(index);
        checkIndexHigh(index, columns);
    }
    
    /**
     * Puts the provided value to the specified spot.
     * @param value Value to set.
     * @param row Index of the row for the value.
     * @param column Index of the column for the value.
     * @throws IllegalArgumentException If the specified row and column are 
     * outside of the matrix dimensions.
     */
    public void setValue(double value, int row, int column){
        checkRow(row);
        checkColumn(column);
        
        setValueUnsafe(value, row, column);
    }

    @Override
    public int hashCode() {
        //IDE generated code
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.values);
        hash = 67 * hash + this.rows;
        hash = 67 * hash + this.columns;
        return hash;
    }
    
    @Override
    public boolean equals(Object o){
        if(!(o instanceof SparseMatrix))
            return false;
        
        SparseMatrix m = (SparseMatrix) o;
        
        return values.equals(m.values) && this.rows == m.rows && this.columns == m.columns;
    }
    
    /**
     * Checks if this matrix is equal to the provided matrix within in the 
     * specified difference.
     * <p>That is to say the matrixes are "equal" if the values in the same spots 
     * differ less than the difference value.
     * <p>This method can be used to bypass the ambiguity sometimes caused by 
     * exact floating point comparison.
     * @param comp Matrix to compare.
     * @param diff Maximum allowed difference between values.
     * @return True if the matrixes are "equal" within the specifications, 
     * false otherwise.
     */
    public boolean equalsRounded(SparseMatrix comp, double diff){
        Objects.requireNonNull(comp);
        
        //Check size equality
        if(this.rows != comp.rows || this.columns != comp.columns)
            return false;
        
        boolean[] res = {true};
        
        values.forEach(e -> {
            double d = comp.getValueUnsafe(e.getKey().x, e.getKey().y);
            if(Math.abs(e.getValue() - d) > diff)
                res[0] = false;
        });
        
        return res[0];
    }
    
    /**
     * Adds the other matrix to this matrix and return the result.
     * <p>No changes will be made to this matrix.
     * @param other The matrix to add.
     * @return A new matrix which is the sum of this matrix and other matrix.
     * @throws IllegalArgumentException If this and other matrix are not of equal size.
     */
    public SparseMatrix add(SparseMatrix other){
        Objects.requireNonNull(other);
        if(this.rows != other.rows || this.columns != other.columns)
            throw new IllegalArgumentException("Summed matrixes must of equal size.");
        
        //Copy this matrix
        SparseMatrix res = new SparseMatrix(this);
        
        //Loop through all of the non-zero values of the other matrix. 
        //Zero values can be ignored since these will not affect the sum.
        other.values.forEach(e -> {
            //Find the corresponding value in this matrix (getValue() already 
            //checks if there is a value and returns 0 accordingly).
            Double val = res.getValueUnsafe(e.getKey().x, e.getKey().y) + e.getValue();
            
            //Save the summed value
            res.setValueUnsafe(val, e.getKey().x, e.getKey().y);
        });
        
        return res;
    }
    
    public SparseMatrix subtract(SparseMatrix other){
        Objects.requireNonNull(other);
        if(this.rows != other.rows || this.columns != other.columns)
            throw new IllegalArgumentException("Subtracted matrixes must of equal size.");
        
        //Implementation is identical with the add method.
        //Only the addition changes to subtraction
        SparseMatrix res = new SparseMatrix(this);
        
        other.values.forEach(e -> {
            Double val = res.getValueUnsafe(e.getKey().x, e.getKey().y) - e.getValue();
            
            res.setValueUnsafe(val, e.getKey().x, e.getKey().y);
        });
        
        return res;
    }
    
    /**
     * Multiplies all of the values in this matrix with the specified number 
     * and returns the result.
     * <p>No changes will be made to this matrix.
     * @param multiplier Number which to multiply this matrix with.
     * @return A new matrix which is the result of multiplying all the values 
     * of this matrix with the parameter value.
     */
    public SparseMatrix multiplyScalar(double multiplier){
        SparseMatrix res = new SparseMatrix(this);
        
        //Loop through all of the values in the matrix and multiply 
        //accordingly, zero values can be obviously ignored.
        res.values.forEach(e -> e.setValue(e.getValue() * multiplier));
        
        return res;
    }
    
    /**
     * Multiplies this matrix with the parameter matrix and return the resulting matrix.
     * <p>No changes are made to this matrix or the parameter matrix.
     * @param multiplier Matrix to multiply with.
     * @return The product of this matrix and the parameter matrix.
     * @throws IllegalArgumentException If the amount of columns in this matrix 
     * differs from the amount of rows in the multiplier.
     */
    public SparseMatrix multiply(SparseMatrix multiplier){
        Objects.requireNonNull(multiplier);
        if(this.columns != multiplier.rows)
            throw new IllegalArgumentException("Multiplied matrix must have as many rows as multiplier has columns.");
        
        SparseMatrix res = new SparseMatrix(this.rows, multiplier.columns);
        double[] row = new double[this.columns];
        double[] column = new double[multiplier.rows];
        double d = 0;
        
        for(int i = 0; i < res.rows; i++){
            this.getRow(i, row);
            
            for(int j = 0; j < res.columns; j++){
                multiplier.getColumn(j, column);
        
                for(int k = 0; k < this.columns; k++)
                    d += row[k] * column[k];
                
                res.setValueUnsafe(d, i, j);
                d = 0;
            }
        }
        return res;
    }
    
    /**
     * Set a value the specified location to specified value.
     * <p>No checks are done on the validity of the operation, so use sparingly.
     * @param value The new value to set.
     * @param row Row of the new value.
     * @param column Column of the new value.
     */
    private void setValueUnsafe(double value, int row, int column){
        Pair p = new Pair(row, column);
        if(value != 0)
            values.add(p, value);
        else if(values.containsKey(p))
            values.remove(p);
    }
    
    /**
     * Returns the value in the specified location.
     * <p>No checks are done on the validity of the operation, so use sparingly.
     * @param row Row of the value to get.
     * @param column Column of the value to get.
     * @return The value at the specified location.
     */
    private double getValueUnsafe(int row, int column){
        return values.getOrDefault(new Pair(row, column), 0D);
    }
    
    /**
     * Returns an identity matrix of the specified size.
     * @param size Size of the identity matrix.
     * @return An identity matrix of the specified size.
     */
    public static SparseMatrix identityMatrix(int size){
        if(size < 0)
            throw new IllegalArgumentException("Size of the identity matrix cannot be less than zero.");
        
        SparseMatrix m = new SparseMatrix(size, size);
        
        for(int i = 0; i < size; i++)
            m.setValueUnsafe(1D, i, i);
        
        return m;
    }
    
    /**
     * Creates a new matrix with random values.
     * <p>The probability of a zero value can be specified with a parameter.
     * @param rows Amount of the rows in the new matrix.
     * @param columns Amount of column in the new matrix.
     * @param zeroProbability The probability of a value being zero. Must be between 0 and 1.
     * @return A new matrix filled with random values.
     */
    public static SparseMatrix randomMatrix(int rows, int columns, double zeroProbability){
        if(zeroProbability < 0 || zeroProbability > 1)
            throw new IllegalArgumentException("Probability of zeroes must be inbetween 0 and 1.");
        
        SparseMatrix m = new SparseMatrix(rows, columns);
        
        for(int i = 0; i < rows; i++)
            for(int j = 0; j < columns; j++)
                if(Math.random() >= zeroProbability)
                    m.setValueUnsafe(Math.random(), i, j);
        
        return m;
    }
    
    /**
     * A simple class representing a tuple.
     */
    private class Pair implements Comparable<Pair>{
        
        public int x;
        public int y;

        public Pair(int x, int y){
            this.x = x;
            this.y = y;
        }
        
        @Override
        public int compareTo(Pair p) {
            //First ordering is done with x values and secondary ordering is
            //done with y values.
            if(this.x < p.x)
                return -1;
            else if(this.x > p.x)
                return 1;
            else if(this.y < p.y)
                return -1;
            else if (this.y > p.y)
                return 1;
            else
                return 0;
        }
        
        @Override
        public boolean equals(Object o){
            if(!(o instanceof Pair))
                return false;
            
            Pair p = (Pair) o;
            
            return this.x == p.x && this.y == p.y;
        }

        @Override
        public int hashCode() {
            //IDE generated code
            int hash = 3;
            hash = 41 * hash + this.x;
            hash = 41 * hash + this.y;
            return hash;
        }
    }
}
