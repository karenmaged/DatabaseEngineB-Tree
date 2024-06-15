import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * A B+ tree
 * Since the structures and behaviors between internal node and external node are different, 
 * so there are two different classes for each kind of node.
 * @param <TKey> the data type of the key
 * @param <TValue> the data type of the value
 */
public class BTree<TKey extends Comparable<TKey>, TValue> implements Serializable{
	private BTreeNode<TKey> root;
	private static final long serialVersionUID = -2013725378627670402L;
	
	public BTree() {
		this.root = new BTreeLeafNode<TKey, TValue>();
	}
	
	public static void farah() {
		
	}

	/**
	 * Insert a new key and its associated value into the B+ tree.
	 */
//	public void insert(TKey key, TValue value) {
//		BTreeLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);
//		leaf.insertKey(key, value);
//		
//		if (leaf.isOverflow()) {
//			BTreeNode<TKey> n = leaf.dealOverflow();
//			if (n != null)
//				this.root = n; 
//		}
//	}
	
	public void decrementIfValueGreaterThanX( int x) {
		BTreeNode<TKey> node= root;
		if (node == null) {
	        return;
	    }

	    if (node instanceof BTreeInnerNode) {
	        BTreeInnerNode<TKey> innerNode = (BTreeInnerNode<TKey>) node;
	        for (int i = 0; i <= innerNode.getKeyCount(); i++) {
	            decrementIfValueGreaterThanX( x);
	        }
	    }

	    if (node instanceof BTreeLeafNode) {
	        BTreeLeafNode<TKey, TValue> leafNode = (BTreeLeafNode<TKey, TValue>) node;
	        for (int i = 0; i < leafNode.getKeyCount(); i++) {
	            TKey key = leafNode.getKey(i);
	            TValue value = leafNode.getValue(i);
	            LinkList references= (LinkList) value;
	            references.decrementIfGreaterThan(x);
	        }
	    }
	}
	
	

	    // Other methods and properties of the BTree class...

	    /**
	     * Insert a new key and its associated value into the B+ tree if the key-value pair does not already exist.
	     */
	    public void insert(TKey key, TValue value) {
	        BTreeLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);
	       // leaf.insertKey(key, value);
	        // Search for the key in the leaf node
	        int index = leaf.search(key);
	        if (index != -1) {
	            // Key already exists, check if the value already exists for that key
	            LinkList existingValues = (LinkList) leaf.getValue(index);
	            LinkList ll = (LinkList) value;
	            
	            Reference ref= (Reference)ll.getFirst();
	            
	           

	            // Check if the value already exists in the list of values for the key
	            if (LinkList.contains(existingValues, ref)) {
	                // Value already exists for the key, do nothing
	                return;
	            }

	            // Value does not exist, insert it into the list of values for the key
	            existingValues.insertLast(ref);
	        } else {
	            // Key does not exist, proceed with insertion
	            leaf.insertKey(key, value);
	        }

	        if (leaf.isOverflow()) {
	            BTreeNode<TKey> n = leaf.dealOverflow();
	            if (n != null)
	                this.root = n;
	        }
	    }

	    // Other methods of the BTree class...
	

	
	
	public void serializeBtree(String filename){
		 try {
			 FileOutputStream temp=	 new FileOutputStream(filename);
			 ObjectOutputStream out = new ObjectOutputStream(temp);
		     out.writeObject(this);
		     temp.close();
		     out.close();
		}
		catch (IOException e) {
          e.printStackTrace();
      }
	}
	public static BTree deserialiseBTree( String btreename){ // check it 
		BTree tmp = null;
		try {
	         FileInputStream fileIn = new FileInputStream(btreename);
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	          tmp  = (BTree) in.readObject();
	         in.close();
	         fileIn.close();
	         return tmp;
	      } catch (IOException i) {
	         i.printStackTrace();
	         return tmp;
	      } catch (ClassNotFoundException c) {
	         System.out.println("this class is not found");
	         c.printStackTrace();
	         return tmp;
	      }
	}
	
	
	/**
	 * Search a key value on the tree and return its associated value.
	 */
	public TValue search(TKey key) {
		BTreeLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);
		
		int index = leaf.search(key);
		return (index == -1) ? (TValue) new LinkList() : leaf.getValue(index);
	}
	
	/**
	 * Delete a key and its associated value from the tree.
	 */
/*	public void delete(TKey key) {
		BTreeLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);
		
		if (leaf.delete(key) && leaf.isUnderflow()) {
			BTreeNode<TKey> n = leaf.dealUnderflow();
			if (n != null)
				this.root = n; 
		}
	}*/
	
	public void delete(TKey key, Reference reference) {
	    BTreeLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);
	    
	    if (leaf.delete(key, reference) && leaf.isUnderflow()) {
	        BTreeNode<TKey> n = leaf.dealUnderflow();
	        if (n != null)
	            this.root = n; 
	    }
	}

	
	
	
	
	
	/**
	 * Search the leaf node which should contain the specified key
	 */
	@SuppressWarnings("unchecked")
	private BTreeLeafNode<TKey, TValue> findLeafNodeShouldContainKey(TKey key) {
		BTreeNode<TKey> node = this.root;
		while (node.getNodeType() == TreeNodeType.InnerNode) {
			node = ((BTreeInnerNode<TKey>)node).getChild( node.search(key) );
		}
		
		return (BTreeLeafNode<TKey, TValue>)node;
	}
	
	public void displayTree() {
	    displayTree(root, 0);
	}

	private void displayTree(BTreeNode<TKey> node, int level) {
	    if (node == null) {
	        return;
	    }

	    if (node.getNodeType() == TreeNodeType.InnerNode) {
	        BTreeInnerNode<TKey> innerNode = (BTreeInnerNode<TKey>) node;
	        for (int i = 0; i < node.getKeyCount(); i++) {
	            displayTree(innerNode.getChild(i), level + 1);
	            if (i < node.getKeyCount()) {
	                System.out.print(node.getKey(i) + " ");
	            }
	        }
	        displayTree(innerNode.getChild(node.getKeyCount()), level + 1);
	    } else if (node.getNodeType() == TreeNodeType.LeafNode) {
	        BTreeLeafNode<TKey, TValue> leafNode = (BTreeLeafNode<TKey, TValue>) node;
	        for (int i = 0; i < node.getKeyCount(); i++) {
	            System.out.print("(" + node.getKey(i) + " -> " + leafNode.getValue(i).toString() + ") ");
	        }
	    }
	}
	
	public LinkList traverseAndCheckKeyEqualsToOne(Object keyy, String operator) {
		BTreeNode<TKey> node = root;
	    if (node == null) {
	        return null;
	    }
	    LinkList result = new LinkList();

	    // Recursively traverse child nodes if it's an inner node
	    if (node instanceof BTreeInnerNode) {
	        BTreeInnerNode<TKey> innerNode = (BTreeInnerNode<TKey>) node;
	        for (int i = 0; i <= innerNode.getKeyCount(); i++) {
	            this.traverseAndCheckKeyEqualsToOne(keyy, operator);
	        }
	    }

	    // Process current node if it's a leaf node
	    if (node instanceof BTreeLeafNode) {
	        BTreeLeafNode<TKey, TValue> leafNode = (BTreeLeafNode<TKey, TValue>) node;
	        while (leafNode != null) {
	            for (int i = 0; i < leafNode.getKeyCount(); i++) {
	                TKey key = leafNode.getKey(i);
	                if (operator.equals("!=") && !(key.equals(keyy))) {
	                	System.out.println("dakhalt traverse");
	                    result = result.append((LinkList)this.search(key));
	                }
	                else if (operator.equals(">=") && (key.equals(keyy) || key.compareTo((TKey) keyy) > 0 )) {
	                	result = result.append((LinkList)this.search(key));
	                }
	                else if (operator.equals(">") && key.compareTo((TKey)keyy) > 0) {
	                	result = result.append((LinkList)this.search(key));
	                }
	                else if (operator.equals("<=") && (key.equals(keyy) || key.compareTo((TKey) keyy) < 0 )) {
	                	result = result.append((LinkList)this.search(key));	                	
	                }
	                else if (operator.equals("<") && key.compareTo((TKey)keyy) < 0) {
	                	result = result.append((LinkList)this.search(key));
	                }
	                
	                
	            }
	            leafNode = leafNode.rightNeighbor;
	        }
	        
	    }
	    result.removedupes();
	    return result;
	}
	
	public void deleteAll() {
	    if (this.root != null) {
	        deleteAll(this.root);
	        this.root = new BTreeLeafNode<TKey, TValue>(); // Reset the root node
	    }
	}

	private void deleteAll(BTreeNode<TKey> node) {
	    if (node.getNodeType() == TreeNodeType.InnerNode) {
	        BTreeInnerNode<TKey> innerNode = (BTreeInnerNode<TKey>) node;
	        for (int i = 0; i <= innerNode.getKeyCount(); i++) {
	            deleteAll(innerNode.getChild(i));
	        }
	    } else if (node.getNodeType() == TreeNodeType.LeafNode) {
	        BTreeLeafNode<TKey, TValue> leafNode = (BTreeLeafNode<TKey, TValue>) node;
	        while (leafNode != null) {
	            for (int i = 0; i < leafNode.getKeyCount(); i++) {
	                leafNode.deleteAt(i);
	            }
	            leafNode = leafNode.rightNeighbor;
	        }
	    }
	}


	
	
	
	public static void main (String args[]){
		BTree<Integer, LinkList> bt= new BTree<>();
		Reference ref = new Reference (1,2);
		LinkList ll= new LinkList();
		ll.insertLast(ref);
		bt.insert(1, ll);
		Reference ref1 = new Reference (1,3);
		LinkList ll1= new LinkList();
		ll1.insertLast(ref1);
		bt.insert(1, ll1);
		Reference ref2 = new Reference (1,4);
		LinkList ll2= new LinkList();
		ll2.insertLast(ref2);
		bt.insert(2, ll2);
		bt.displayTree();
		bt.delete(1, ref);
		bt.displayTree();
		bt.delete(2, ref2);
		bt.displayTree();



	}
}
