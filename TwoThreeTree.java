/**
 * A generic implementation of a 2-3 Tree.
 * This class allows storing and retrieving key-value pairs with an efficient search, insert, and delete functionality.
 *
 * @param <K> the type of keys, which must be comparable
 * @param <V> the type of values
 */
public class TwoThreeTree<K extends Comparable<K>, V> {
    Node<K, V> root;
    public final SentinelValueProvider<K> sentinelValueProvider;

    /**
     * Represents a node in the 2-3 Tree.
     */
    public class Node<K extends Comparable<K>, V> {


        private K key; //max key in the subtree
        private Node<K, V> left, middle, right, parent = null; // Child and parent pointers
        private boolean isLeaf;
        private V value; // Data stored only in leaf nodes
        private int size;
        private Node<K, V> prev, next;

        // Constructor for internal node
        public Node(K key) {
            this.key = key;
            this.isLeaf = false;
            this.size = 0;
            this.prev = null;
            this.next = null;
        }

        // Constructor for leaf node
        public Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.isLeaf = true;
            this.left = null;
            this.middle = null;
            this.right = null;
            this.parent = null;
            this.size = 1;
            this.prev = null;
            this.next = null;
        }

        public K getKey() {
            return key;
        }
        public V getValue() {
            return value;
        }
        public Node<K, V> getNext() { return next;}
    @Override
    public String toString() {
        if (isLeaf) {
            return "Leaf Node [Key: " + key + ", Value: " + value + "]";
        } else {
            return "Internal Node [Key: " + key + "] ";
        }
    }
    }

    /**
     * Constructs an empty 2-3 tree with sentinel nodes for minimum and maximum values.
     *
     * @param exampleKey an example key used to initialize sentinel values
     */
    public TwoThreeTree(K exampleKey) {
        sentinelValueProvider = new SentinelValueProvider<>(exampleKey);
        root = new Node<>(null);
        Node<K, V> sentinelLeft = new Node<>(sentinelValueProvider.getMinValue(), null); // Sentinel node -∞
        Node<K, V> sentinelRight = new Node<>(sentinelValueProvider.getMaxValue(), null); // Sentinel node +∞
         // Root node
        //update linked list
        sentinelLeft.next = sentinelRight;
        sentinelRight.prev = sentinelLeft;

        root.left = sentinelLeft;
        root.middle = sentinelRight;
        sentinelLeft.parent = root;
        sentinelRight.parent = root;
    }

    /**
     * Searches for a node with the given key in the 2-3 Tree.
     *
     * @param key the key to search for
     * @return the node containing the key, or null if not found
     * Complexity: O(log n)
     */
    public Node search(K key) {
        return searchInternal(root, key);
    }

    private Node searchInternal(Node<K, V> node, K key) {
        if (node.isLeaf) {
            return key.equals(node.key) ? node : null;
        }
        if (key.compareTo(node.left.key) <= 0) {
            return searchInternal(node.left, key);
        } else if (node.middle != null && key.compareTo(node.middle.key) <= 0) {
            return searchInternal(node.middle, key);
        } else {
            return searchInternal(node.right, key);
        }
    }


    /**
     * Updates the maximum key in a subtree rooted at the given node.
     *
     * @param x the node whose key and size need to be updated
     */
    private void updateKey(Node x) {
        x.key = x.left.key;
        if (x.middle != null) {
            x.key = x.middle.key;
        }
        if (x.right != null) {
            x.key = x.right.key;
        }
        //update size
        x.size = 0;
        if (x.left != null) x.size += x.left.size;
        if (x.middle != null) x.size += x.middle.size;
        if (x.right != null) x.size += x.right.size;

    }

    /**
     * Sets the children for a node.
     *
     * @param x      the node whose children are being set
     * @param left   the left child
     * @param middle the middle child
     * @param right  the right child (can be null)
     */
    private void setChildren(Node<K, V> x, Node<K, V> left, Node<K, V> middle, Node<K, V> right) {
        x.left = left;
        x.middle = middle;
        x.right = right;
        left.parent = x;
        if (middle != null) middle.parent = x;
        if (right != null) right.parent = x;
        updateKey(x);
    }


    /**
     * Inserts a new node into the tree and splits the parent if necessary.
     *
     * @param x the parent node
     * @param z the new node to insert
     * @return the resulting node from the split, if any
     */
    private Node<K, V> insertAndSplit(Node<K, V> x, Node<K, V> z) {
        Node<K, V> left = x.left;
        Node<K, V> middle = x.middle;
        Node<K, V> right = x.right;
        if (right == null) {
            if (z.key.compareTo(left.key) < 0) {
                setChildren(x, z, left, middle);
            } else if (z.key.compareTo(middle.key) < 0) {
                setChildren(x, left, z, middle);
            } else {
                setChildren(x, left, middle, z);
            }
            return null;
        } else {
            Node<K, V> y = new Node<>(null);
            if (z.key.compareTo(left.key) < 0) {
                setChildren(x, z, left, null);
                setChildren(y, middle, right, null);
            } else if (z.key.compareTo(middle.key) < 0) {
                setChildren(x, left, z, null);
                setChildren(y, middle, right, null);
            } else if (z.key.compareTo(right.key) < 0) {
                setChildren(x, left, middle, null);
                setChildren(y, z, right, null);
            } else {
                setChildren(x, left, middle, null);
                setChildren(y, right, z, null);
            }
            return y;
        }


    }


    /**
     * Inserts a new key-value pair into the tree.
     *
     * @param key   the key of the element to insert
     * @param value the value associated with the key
     */
    public void insert(K key, V value) {
        Node<K, V> z = new Node<>(key, value);
        Node<K, V> y = root;

        while (!y.isLeaf) {
            if (key.compareTo(y.left.key) < 0) {
                y = y.left;
            } else if (key.compareTo(y.middle.key) < 0) {
                y = y.middle;
            } else {
                y = y.right;
            }
        }

        // Update linked list
        if (y.prev != null) {
            y.prev.next = z;
        }
        z.prev = y.prev;
        z.next = y;
        y.prev = z;

        //continue insert
        Node<K, V> x = y.parent;
        z = insertAndSplit(x, z);
        while (x != root) {
            x = x.parent;
            if (z != null) {
                z = insertAndSplit(x, z);
            } else {
                updateKey(x);
            }
        }
        if (z != null) {
            Node<K, V> w = new Node<>(null);
            setChildren(w, x, z, null);
            root = w;
        }

    }

    /**
     *  for node with 1 child borrow/marge from brother
     *
     * @param y The node that is being borrowed or merged. This node is expected to be a child of the parent node.
     * @return The parent node (z) after the necessary adjustments. This parent could have a modified structure depending on the operation.
     */
    public Node<K,V> borrowOrMerge(Node<K,V> y) {
        Node<K,V> z = y.parent;
        Node<K,V> x;
        if (y == z.left) {
            x = z.middle;
            if (x.right != null) {
                setChildren(y, y.left, x.left, null);
                setChildren(x, x.middle, x.right, null);
            } else {
                setChildren(x, y.left, x.left, x.middle);
                setChildren(z, x, z.right, null);
            }
        } else if (y == z.middle) {
            x = z.left;
            if (x.right != null) {
                setChildren(y, x.right, y.left, null);
                setChildren(x, x.left, x.middle, null);
            } else {
                setChildren(x, x.left, x.middle, y.left);
                setChildren(z, x, z.right, null);
            }
        } else {
            x = z.middle;
            if (x.right != null) {
                setChildren(y, x.right, y.left, null);
                setChildren(x, x.left, x.middle, null);
            } else {
                setChildren(x, x.left, x.middle, y.left);
                setChildren(z, z.left, x, null);
            }
        }

        return z;
    }


    /**
     * Deletes a leaf node from the tree.
     *
     * @param z the leaf node to delete
     */
    public void delete(Node<K, V> z) {
        Node<K, V> y = z.parent;

        //update linked list
        z.prev.next = z.next;
        z.next.prev = z.prev;

        //continue delete
        if (z == y.left) {
            setChildren(y, y.middle, y.right, null);
        } else if (z == y.middle) {
            setChildren(y, y.left, y.right, null);
        } else {
            setChildren(y, y.left, y.middle, null);
        }
        while (y != null) {
            if (y.middle != null) {
                updateKey(y);
                y = y.parent;
            } else {
                if (y != root) {
                    y = borrowOrMerge(y);
                } else {
                    root = y.left;
                    if (root != null) root.parent = null;
                    y = null;
                }
            }
        }

    }

    /**
     * Calculates the rank of a node `z` in a 2-3 tree.
     * The rank is the number of nodes smaller than `z` in an in-order traversal.
     *
     * @param z The node whose rank is to be determined.
     * @return The rank of node `z`.
     */
    public int rank(Node<K, V> z) {
        int rank = 1;
        Node<K, V> y = z.parent;
        while (y != null) {
            if (z == y.middle) {
                rank += y.left.size;
            } else if (z == y.right) {
                rank += y.left.size + y.middle.size;
            }
            z = y;
            y = y.parent;
        }
        return rank;
    }


    // Method to print the tree
    public void printTree() {
        printNode(root, "" , true);
    }

    // Recursive method to print the tree
    private void printNode(Node<K, V> node, String indent, boolean isLeft) {
        if (node != null) {
            // Print the current node's key, value, and size
            System.out.println(indent + (isLeft ? "L----" : "R----") + node.key +
                    (node.isLeaf ? " : " + node.value : "") +
                    " [size=" + node.size + "]");

            // If it's an internal node, recursively print its children
            if (!node.isLeaf) {
                printNode(node.left, indent + (isLeft ? "|    " : "     "), true);
                printNode(node.middle, indent + (isLeft ? "|    " : "     "), false);
                if (node.right != null) {
                    printNode(node.right, indent + (isLeft ? "|    " : "     "), false);
                }
            }
        }
    }


}