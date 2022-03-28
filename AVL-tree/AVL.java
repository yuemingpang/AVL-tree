import java.util.NoSuchElementException;

public class AVL<T extends Comparable<? super T>> {

  private AVLNode<T> root;
  private int size;

   /**
     * Adds the element to the tree.
     *
     * Start by adding it as a leaf like in a regular BST and then rotate the
     * tree as necessary.
     *
     * If the data is already in the tree, then nothing should be done (the
     * duplicate shouldn't get added, and size should not be incremented).
     *
     * @param data The data to add.
     * @throws java.lang.IllegalArgumentException If data is null.
     */
    public void add(T data) {
        if (data == null) {
            throw new IllegalArgumentException();
        }
        root = rAdd(root, data);
    }
    
    private AVLNode<T> rAdd(AVLNode<T> curr, T data) {
        if (curr == null) {
            size++;
            return new AVLNode<T>(data);
        } else if (data.compareTo(curr.getData()) < 0) {
            curr.setLeft(rAdd(curr.getLeft(), data));
        } else if (data.compareTo(curr.getData()) > 0) {
            curr.setRight(rAdd(curr.getRight(), data));
        }
        curr = balance(curr);
        updateHeightAndBF(curr);
        return curr;
    }

    /**
     * Removes and returns the element from the tree matching the given
     * parameter.
     *
     * 3 cases to consider:
     * 1: The node containing the data is a leaf (no children). In this case,
     *    simply remove it.
     * 2: The node containing the data has one child. In this case, simply
     *    replace it with its child.
     * 3: The node containing the data has 2 children. Use the successor to
     *    replace the data, NOT predecessor. 
     *
     * @param data The data to remove.
     * @return The data that was removed.
     * @throws java.lang.IllegalArgumentException If the data is null.
     * @throws java.util.NoSuchElementException   If the data is not found.
     */
    public T remove(T data) {
        if (data == null) {
            throw new IllegalArgumentException();
        }
        AVLNode<T> dummy = new AVLNode<>(null);
        root = rRemove(root, data, dummy);
        return dummy.getData();
    }
    
    private AVLNode<T> rRemove(AVLNode<T> curr, T data, AVLNode<T> dummy) {
        if (curr == null) {
            throw new NoSuchElementException();
        } else if (data.compareTo(curr.getData()) < 0) {
            curr.setLeft(rRemove(curr.getLeft(), data, dummy));
        } else if (data.compareTo(curr.getData()) > 0) {
            curr.setRight(rRemove(curr.getRight(), data, dummy));
        } else {
            dummy.setData(curr.getData());
            size--;
            if (curr.getLeft() == null && curr.getRight() == null) {
                return null;
            } else if (curr.getLeft() != null && curr.getRight() == null) {
                return curr.getLeft();
            } else if (curr.getRight() != null && curr.getLeft() == null) {
                return curr.getRight();
            } else {
                AVLNode<T> dummy2 = new AVLNode<>(null);
                curr.setRight(removeSuccessor(curr.getRight(), dummy2));
                curr.setData(dummy2.getData());
            }
        }
        //balancing the remove data finding path:
        curr = balance(curr);
        updateHeightAndBF(curr);
        return curr;
    }
    
    private AVLNode<T> removeSuccessor(AVLNode<T> curr, AVLNode<T> dummy) {
        if (curr.getLeft() == null) {
            dummy.setData(curr.getData());
            return curr.getRight();
        } else {
            curr.setLeft(removeSuccessor(curr.getLeft(), dummy));
        }
        //balancing the successor finding path:
        curr = balance(curr);
        updateHeightAndBF(curr);
        return curr;
    }

    /**
     * Updates the height and balance factor of a node using its
     * setter methods.
     *
     * The balance factor of a node is the height of its left child
     * minus the height of its right child.
     *
     * This method runs in O(1).
     * Assuming that the passed in node is not null.
     *
     * @param currentNode The node to update the height and balance factor of.
     */
    public void updateHeightAndBF(AVLNode<T> currentNode) {
        int rightH;
        int leftH;
        
        if (currentNode.getLeft() == null) {
            leftH = -1;
        } else {
            leftH = currentNode.getLeft().getHeight();
        }
        if (currentNode.getRight() == null) {
            rightH = -1;
        } else {
            rightH = currentNode.getRight().getHeight();
        }
        if (rightH>leftH) {
            currentNode.setHeight(rightH+1);
        } else {
            currentNode.setHeight(leftH+1);
        }
        
        currentNode.setBalanceFactor(leftH-rightH);
    }

    /**
     * Method that rotates a current node to the left. After saving the
     * current's right node to a variable, the right node's left subtree will
     * become the current node's right subtree. The current node will become
     * the right node's left subtree.
     *
     * This method runs in O(1).
     * Assuming that the passed in node is not null and that the subtree
     * starting at that node is right heavy.
     *
     * @param currentNode The current node under inspection that will rotate.
     * @return The parent of the node passed in (after the rotation).
     */
    public AVLNode<T> rotateLeft(AVLNode<T> currentNode) {
        AVLNode<T> B = currentNode.getRight();
        currentNode.setRight(B.getLeft());
        B.setLeft(currentNode);
        updateHeightAndBF(currentNode);
        updateHeightAndBF(B);
        return B;
    }

    /**
     * Method that rotates a current node to the right. After saving the
     * current's left node to a variable, the left node's right subtree will
     * become the current node's left subtree. The current node will become
     * the left node's right subtree.
     *
     * This method runs in O(1).
     *
     * Assuming that the passed in node is not null and that the subtree
     * starting at that node is left heavy.
     *
     * @param currentNode The current node under inspection that will rotate.
     * @return The parent of the node passed in (after the rotation).
     */
    public AVLNode<T> rotateRight(AVLNode<T> currentNode) {
        AVLNode<T> B = currentNode.getLeft();
        currentNode.setLeft(B.getRight());
        B.setRight(currentNode);
        updateHeightAndBF(currentNode);
        updateHeightAndBF(B);
        return B;
    }

    /**
     * This is the overarching method that is used to balance a subtree
     * starting at the passed in node. This method will utilize
     * updateHeightAndBF(), rotateLeft(), and rotateRight() to balance
     * the subtree.
     *
     * Assuming that the passed in node is not null. 
     *
     * This method runs in O(1).
     *
     * @param cur The current node under inspection.
     * @return The AVLNode that the caller should return.
     */
    public AVLNode<T> balance(AVLNode<T> currentNode) {

        updateHeightAndBF(currentNode);

        if (currentNode.getBalanceFactor()<-1) {
            if (currentNode.getRight().getBalanceFactor()>0) {
                currentNode.setRight(rotateRight(currentNode.getRight()));
            }
            currentNode = rotateLeft(currentNode);
        } else if (currentNode.getBalanceFactor()>1) {
            if (currentNode.getLeft().getBalanceFactor()<0) {
                currentNode.setLeft(rotateLeft(currentNode.getLeft()));
            }
            currentNode = rotateRight(currentNode);
        }

        return currentNode;
    }

  /**
     * Returns the root of the tree.
     *
     * @return The root of the tree.
     */
    public AVLNode<T> getRoot() {
        return root;
    }

    /**
     * Returns the size of the tree.
     *
     * @return The size of the tree.
     */
    public int size() {
        return size;
    }
}