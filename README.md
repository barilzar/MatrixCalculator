# MatrixCalculator
Library for using sparse matrixes with self balancing trees in Java.

A sparse matrix is a matrix where most of the values are zero. Obviously in cases like this (especially if the matrix is very large), it is not space efficient to use a standard matrix implementation. Instead of saving every single zero in the matrix this class will only save numbers !=0.

This is done by using a tree. Simply only non-zero numbers are saved to the tree, with the column and row numbers as the key. When accessing the tree if the key is not found a zero is returned. All of this is encased so that none of this is observable from outside the class.

See the test classes for some examples on use.
