package matrix;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Self balancing binary tree following the AATree model. Maps a single key 
 * to a single value.
 * <p>Null keys and values are allowed, but can cause strange functionality. 
 * For example {@link #get get} returns null when the key searched is not 
 * found or when null is associated with the key.
 * 
 * @param <A> Type of the key for this tree.
 * @param <B> Type of the value for this tree.
 */
public class AATree<A extends Comparable<? super A>, B> implements Map<A, B>{
    
    private TreeNode root;
    private int size;
    
    /**
     * Constructs a new tree with no initial values.
     */
    public AATree(){
        root = null;
        size = 0;
    }
    
    /**
     * Add the specified key-value pair to this tree. If the key has already a 
     * value associated with it, the old value is replaced with the new one.
     * @param newKey Key for the value added.
     * @param newValue The value added.
     */
    public void add(A newKey, B newValue){
        root = addRecursive(newKey, newValue, root);
        size++;
    }
    
    /**
     * Adds a new key-value pair to this tree recursively starting from the node
     * specified.
     * @param newKey Key for the value added.
     * @param newValue The value added.
     * @param t Node specifying the tree where to add the new pair.
     * @return 
     */
    private TreeNode addRecursive(A newKey, B newValue, TreeNode t){
        //If a leaf node is reached, a new key is beign added -> create a new node
        if(t == null)
            return new TreeNode(newKey, newValue);
        
        //Regular recursive tree search
        int comp = t.key.compareTo(newKey);
        
        if(comp < 0)
            t.right = addRecursive(newKey, newValue, t.right);
        else if (comp > 0)
            t.left = addRecursive(newKey, newValue, t.left);
        else
            t.value = newValue;
        
        //Re-balance the tree if necessary
        t = skew(t);
        t = split(t);
        
        return t;
    }
    
    @Override
    public B get(Object searchKey){
        return getRecursive((A) searchKey, root);
    }
    
    private B getRecursive(A searchKey, TreeNode t){
        if(t == null)
            return null;
        
        int comp = t.key.compareTo(searchKey);
        
        if(comp < 0)
            return getRecursive(searchKey, t.right);
        else if(comp > 0)
            return getRecursive(searchKey, t.left);
        else
            return t.value;
    }
    
    /**
     * Removes the key-value pair associated with the key provided.
     * @param deleteKey Key which to delete.
     */
    public void remove(A deleteKey){
        if(containsKey(deleteKey)){
            root = removeRecursive(deleteKey, root);
            size--;
        }
    }
    
    /**
     * Removes the key from the provided recursively.
     * @param deleteKey The key to remove.
     * @param t A node from under which the key is to be removed.
     * @return 
     */
    private TreeNode removeRecursive(A deleteKey, TreeNode t){
        //A leaf was reached without finding the key -> it is not in the tree
        if(t == null)
            return t;
        
        //Regular recursive tree search
        int comp = t.key.compareTo(deleteKey);
        
        if(comp < 0)
            t.right = removeRecursive(deleteKey, t.right);
        else if (comp > 0)
            t.left = removeRecursive(deleteKey, t.left);
        
        //The right node was found
        else{
            //If it's a leaf don't do anything
            if(t.left == null && t.right == null)
                return null;
            
            //Key has right children -> fetch the next key and replace the 
            //current key with that
            else if(t.left == null){
                TreeNode temp = successor(t);
                t.right = removeRecursive(temp.key, t.right);
                t.key = temp.key;
                t.value = temp.value;
            }
            //Same as above, except we fetch the previous key
            else{
                TreeNode temp = predecessor(t);
                t.left = removeRecursive(temp.key, t.left);
                t.key = temp.key;
                t.value = temp.value;
            }
        }
		
        //Tree re-balancing
        t = decreaseLevel(t);
        t = skew(t);
        t.right = skew(t.right);
        if(t.right != null)
            t.right.right = skew(t.right.right);
        
        t = split(t);
        t.right = split(t.right);
        
        return t;
    }
    
    /**
     * Checks if the height of the node provided is accurate and fixes it if not.
     * @param t A node which height to fix.
     * @return The same node with a correct height value.
     */
    private TreeNode decreaseLevel(TreeNode t){
        int shouldBe;
        
        if(t.left == null || t.right == null)
            shouldBe = 1;
        else
            shouldBe = Math.min(t.left.height, t.right.height) + 1;
        
        if(shouldBe < t.height){
            t.height = shouldBe;
            if(!(t.right == null) && shouldBe < t.right.height)
                t.right.height = shouldBe;
        }
        
        return t;
    }
    
    /**
     * Finds the node that precedes the node provided. The search only considers
     * nodes found under t, not above it. Therefore it is possible for the tree 
     * to have a value that precedes t even when this method does not find it.
     * @param t Node whose predecessor to find.
     * @return The node preceeding t.
     */
    private TreeNode predecessor(TreeNode t){
        //The previous key is the rightenmost key in the left subtree
        TreeNode cur = t.left;
        
        while(cur.right != null)
            cur = cur.right;
        
        return cur;
    }
    
    /**
     * Finds the node that succeeds t. Only values under t are considered, so it
     * is possible that no such a node is found even when it exists elsewhere 
     * in the tree.
     * @param t Node whose successor to find.
     * @return The node succeeding t.
     */
    private TreeNode successor(TreeNode t){
        //The next key is leftenmost key of the right subtree
        TreeNode cur = t.right;
        
        while(cur.left != null)
            cur = cur.left;
        
        return cur;
    }

    private TreeNode skew(TreeNode t){
        if(t != null && t.left != null && t.left.height == t.height){
            TreeNode temp = t.left;
            t.left = temp.right;
            temp.right = t;
            return temp;
        }
        else return t;
    }

    private TreeNode split(TreeNode t){
        if(t != null && t.right != null && t.right.right != null && t.height == t.right.right.height){
            TreeNode temp = t.right;
            t.right = temp.left;
            temp.left = t;
            temp.height++;
            return temp;
        }
        else return t;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.get((A) key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        //Local variables can't be written to from lambdas, but this can be 
        //circumvented by using an array of just one element. 
        boolean[] res = {false};
        
        forEach(e -> {
            if(e.getValue().equals(value))
                res[0] = true;
        });
        
        return res[0];
    }

    /**
     * Adds the specified key-value pair to this tree and return the previous 
     * value associated with the key.
     * <p>NOTE: if you do need the return value consider using {@link #add add} instead
     * @param key The new key.
     * @param value The new value.
     * @return The previous value associated with the key or null if there was none.
     */
    @Override
    public B put(A key, B value) {
        B val = get(key);
        add(key, value);
        return val;
    }

    /**
     * Removes the key and value associated with it from this tree. Also 
     * returns the value associated with the key.
     * <p>NOTE: if you do not need the return value consider using 
     * {@link #remove(java.lang.Comparable) remove} instead.
     * @param key The key to remove.
     * @return The value associated with key.
     * @throws ClassCastException if the key is of improper class.
     */
    @Override
    public B remove(Object key) {
        B val = get((A) key);
        remove((A) key);
        return val;
    }

    @Override
    public void putAll(Map<? extends A, ? extends B> m) {
        m.forEach((k, v) -> add(k, v));
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public Set<A> keySet() {
        Set<A> k = new HashSet(size);
        
        forEach(e -> k.add(e.getKey()));
        
        return k;
    }

    @Override
    public Collection<B> values() {
        Set<B> v = new HashSet(size);
        
        forEach(e -> v.add(e.getValue()));
        
        return v;
    }

    @Override
    public Set<Entry<A, B>> entrySet() {
        Set<Entry<A, B>> entries = new HashSet(size);
        
        forEach(e -> entries.add(e));
        
        return entries;
    }

    @Override
    public void forEach(BiConsumer<? super A, ? super B> action) {
        forEach(e -> action.accept(e.getKey(), e.getValue()));
    }
    
    /**
     * Performs the specified action on each of the key-value pairs in this tree.
     * Actions are performed in-order of the keys, that is from smallest to largest.
     * @param action The action to perform.
     */
    public void forEach(Consumer<? super Entry<A, B>> action){
        //in-order tree traversal
        TreeNode cur = root;
        Stack<TreeNode> stack = new Stack();
        
        while(cur != null || !stack.empty()){
            if(cur != null){
                stack.push(cur);
                cur = cur.left;
            }
            else{
                cur = stack.pop();
                action.accept(cur);
                cur = cur.right;
            }
        }
    }

    @Override
    public void replaceAll(BiFunction<? super A, ? super B, ? extends B> function) {
        TreeNode cur = root;
        Stack<TreeNode> stack = new Stack();
        B b;
        
        while(cur != null || !stack.empty()){
            if(cur != null){
                stack.push(cur);
                cur = cur.left;
            }
            else{
                cur = stack.pop();
                b = function.apply(cur.key, cur.value);
                cur.value = b;
                cur = cur.right;
            }
        }
    }
    
    @Override
    public boolean equals(Object o){
        if(!(o instanceof AATree))
                return false;
        
        AATree comp = (AATree) o;
        if(this.size != comp.size)
            return false;
        
        boolean[] res = {true};
        
        //Check that all of the entries in this tree have an equivalent entry 
        //in the other tree. Since the trees are of same size this comparison 
        //is enough.
        forEach(e -> {
                    if(!comp.get(e.getKey()).equals(e.getValue()))
                        res[0] = false;
        });
        return res[0];
    }

    @Override
    public int hashCode() {
        //IDE generated code
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.root);
        hash = 97 * hash + this.size;
        return hash;
    }
    
    /**
     * A simple container class representing the nodes of this tree.
     */
    private class TreeNode implements Entry<A, B>{
        private A key;
        private B value;

        private TreeNode left;
        private TreeNode right;

        private int height;
        
        public TreeNode(A key, B value){
            this.key = key;
            this.value = value;
            
            left = null;
            right = null;
            
            height = 1;
        }

        @Override
        public A getKey() {
            return key;
        }

        @Override
        public B getValue() {
            return value;
        }

        @Override
        public B setValue(B value) {
            B b = value;
            this.value = value;
            return b;
        }
    }
}
