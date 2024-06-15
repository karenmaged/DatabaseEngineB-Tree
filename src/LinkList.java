import java.io.Serializable;

class Link implements Serializable{
	public Object data;
	public Link next; 
	private static final long serialVersionUID = 7117699347461924370L;
//public static void farah() {
//		
//	}
	public Link(Object o) {
		this.data = o;
		this.next = null;
	}

	public String toString() {
		return data.toString();
	}
}

class LinkList implements Serializable{
	private static final long serialVersionUID = 6772928313463292683L;
	private Link head;
	//
public static void farah() {
		
	}
	public LinkList() {
		head = null;
	}

	public void insertFirst(Object o) {
		Link newLink = new Link(o);
		newLink.next = head;
		head = newLink;
	}

	public Object removeFirst() {
		Object res = head.data;
		head = head.next;
		return res;
	}
	
	public void search(Reference reference){
		
		Link current = this.head; // Assuming head is the reference to the first node
		Link prev = null;

		while (current != null) {
		    Reference ref = (Reference) current.data;
		    if (ref.page== reference.page && ref.record == reference.record) {
		        // If it matches, remove the current node
		        if (prev != null) {
		            // If the previous node is not null, update its next reference
		            prev.next = current.next;
		        } else {
		            // If the previous node is null, update the head reference
		            this.head = current.next;
		        }
		        // Return true indicating the reference was found and removed
		    }
		    
		    // Update prev reference to current node
		    prev = current;
		    // Move to the next node
		    current = current.next;
		}

		// If the loop completes without finding the reference, return false

		
		
		
		
	}

	public Object getFirst() {
		return head.data;
	}

	public void insertLast(Object o) {
		Link newLink = new Link(o);
		if (head == null) {
			head = newLink;
			return;
		}
		Link current = head;
		while (current.next != null)
			current = current.next;
		current.next = newLink;
	}

	public Object removeLast() {
		if (head.next == null) {
			Object res = head.data;
			head = null;
			return res;
		}
		Link current = head;
		while (current.next.next != null)
			current = current.next;
		Object res = current.next.data;
		current.next = null;
		return res;
	}

	public Object getLast() {
		Link current = head;
		while (current.next != null)
			current = current.next;
		return current.data;
	}

	public boolean isEmpty() {
		return head == null;
	}

	public String toString() {
		if (head == null)
			return "[ ]";
		String res = "[ " + head.data;
		Link current = head.next;
		while (current != null) {
			res += ", " + (current.data).toString();
			current = current.next;
		}
		res += " ]";
		return res;
	}
/*	public static LinkList common(LinkList l1, LinkList l2) {
		LinkList res = new LinkList();
		Link curr1 = l1.head;
		Link curr2 = l2.head;
		Link curr3 = res.head;
		while (curr1 != null && curr2 != null) {
			if ((int)curr1.data > (int)curr2.data) curr2 = curr2.next;
			else if ((int)curr1.data < (int)curr2.data) curr1 = curr1.next;
			else {
				if (res.head == null) res.head = curr1;
				else curr3.next = curr1;
				curr3 = curr3.next;
				curr1 = curr1.next;
				curr2 = curr2.next;
			}
		}
		return res;
	}*/
	
	
	public static LinkList common(LinkList l1, LinkList l2) {
	    LinkList res = new LinkList();
//	    System.out.println(l1);
//	    System.out.println(l2);
	    Link curr1 = l1.head;
	    Link curr2 = l2.head;
	    Link curr3 = null; // Initialize curr3 to null
	    
	    while (curr1 != null && curr2 != null) {
	        Reference ref1 = (Reference) curr1.data; // Cast to Reference
	        Reference ref2 = (Reference) curr2.data; // Cast to Reference

	        // Compare the Reference objects
	        int comparisonResult = ref1.compareTo(ref2);
	        
	        if (comparisonResult == 0) {
	            // If the Reference objects are equal, add to the result
	            if (res.head == null) {
	                res.head = new Link(curr1.data);
	                curr3 = res.head;
	            } else {
	                curr3.next = new Link(curr1.data);
	                curr3 = curr3.next;
	            }
	            // Move both pointers
	            curr1 = curr1.next;
	            curr2 = curr2.next;
	        } else if (comparisonResult < 0) {
	            // If ref1 is less than ref2, move curr1 pointer
	            curr1 = curr1.next;
	        } else {
	            // If ref1 is greater than ref2, move curr2 pointer
	            curr2 = curr2.next;
	        }
	    }

	    return res;
	}

	
	
	
	public static LinkList notCommon(LinkList l1, LinkList l2) {
	    LinkList res = new LinkList();
	    Link curr1 = l1.head;
	    Link curr2 = l2.head;
	    Link curr3 = res.head;

	    while (curr1 != null) {
	        // Check if the current node in l1 is present in l2
	        if (!contains(l2, curr1.data)) {
	            // Add the node to the result
	            if (res.head == null) {
	                res.head = new Link(curr1.data);
	                curr3 = res.head;
	            } else {
	                curr3.next = new Link(curr1.data);
	                curr3 = curr3.next;
	            }
	        }
	        curr1 = curr1.next;
	    }
	    

	    // Check if there are any nodes in l2 that are not present in l1
	    while (curr2 != null) {
	        if (!contains(l1, curr2.data)) {
	            // Add the node to the result
	            if (res.head == null) {
	                res.head = new Link(curr2.data);
	                curr3 = res.head;
	            } else {
	                curr3.next = new Link(curr2.data);
	                curr3 = curr3.next;
	            }
	        }
	        curr2 = curr2.next;
	    }

	    return res;
	}
	
//	public static LinkList notCommon(LinkList l1, LinkList l2) {
//	    LinkList res = new LinkList();
//	    Link curr1 = l1.head;
//	    Link curr2 = l2.head;
//	    Link curr3 = res.head;
//
//	    // Helper method to check if a LinkList contains a specific Reference
//	    // Here we assume the contains method checks for equality based on the equals method of the Reference class
//	    // You may need to implement this method in the LinkList class
//	    while (curr1 != null) {
//	        // Check if the current node in l1 is present in l2
//	        if (!contains(l2, curr1.data)) {
//	            // Add the node to the result
//	            if (res.head == null) {
//	                res.head = new Link(curr1.data);
//	                curr3 = res.head;
//	            } else {
//	                curr3.next = new Link(curr1.data);
//	                curr3 = curr3.next;
//	            }
//	        }
//	        curr1 = curr1.next;
//	    }
//
//	    // Reset curr3 to the head of res, as it may have been moved during the first iteration
//	    curr3 = res.head;
//
//	    // Check if there are any nodes in l2 that are not present in l1
//	    while (curr2 != null) {
//	        if (!contains(l1, curr2.data)) {
//	            // Add the node to the result
//	            if (res.head == null) {
//	                res.head = new Link(curr2.data);
//	                curr3 = res.head;
//	            } else {
//	                curr3.next = new Link(curr2.data);
//	                curr3 = curr3.next;
//	            }
//	        }
//	        curr2 = curr2.next;
//	    }
//
//	    return res;
//	}

	
	
	
	

	// Helper method to check if a linked list contains a specific data value
	public static boolean contains(LinkList list, Object data) {
	    Link curr = list.head;
	    while (curr != null) {
            Reference ref1 = (Reference)data;
            Reference ref2= (Reference)curr.data;
	    	int page = ref1.page;
	    	int page2= ref2.page;
	    	int record= ref1.record;
	    	int record2= ref2.record;

	        if (page== page2 && record == record2) {
	            return true;
	        }
	        curr = curr.next;
	    }
	    return false;
	}

	
//	public void removedupes() {
//	LinkList ll = new LinkList();
//	Link curr1 = head;
//	boolean flag = false;
//	while (curr1.next != null) {
//		if (ll.head == null) ll.head = head;
//		else {
//			curr1 = curr1.next;
//			Link curr2 = ll.head;
//			while (curr2 != null) {
//				 Reference ref1 = (Reference)curr1.data;
//		            Reference ref2= (Reference)curr2.data;
//			    	int page = ref1.page;
//			    	int page2= ref2.page;
//			    	int record= ref1.record;
//			    	int record2= ref2.record;
//
//				if (page== page2 && record == record2) flag = true;
//				curr2 = curr2.next;
//				
//			}
//			if (!flag) curr2 = curr1;
//			flag = false;
//		}
//	}
//	Link temp = ll.head;
//	Link current = head;
//	 while (temp != null) {
//		 if (head == null) head = ll.head;
//		 else {
//			current = current.next;
//			temp = temp.next;
//			current = temp;
//		 }
//	 }
//	}
	public void removedupes() {
	    // Check if the list is empty or has only one element
	    if (head == null || head.next == null) {
	        return;
	    }

	    Link curr1 = head;
	    Link prev = null;

	    // Traverse the list with curr1
	    while (curr1 != null && curr1.next != null) {
	        Link curr2 = curr1.next;
	        prev = curr1;

	        // Traverse the list with curr2 to compare with curr1
	        while (curr2 != null) {
	        	 Reference ref1 = (Reference)curr1.data;
		            Reference ref2= (Reference)curr2.data;
			    	int page = ref1.page;
			    	int page2= ref2.page;
			    	int record= ref1.record;
			    	int record2= ref2.record;	    
			    	if (page== page2 && record == record2) {
	                prev.next = curr2.next; // Remove curr2 from the list
	            } else {
	                prev = curr2; // Move prev to curr2
	            }
	            curr2 = curr2.next; // Move curr2 forward
	        }
	        curr1 = curr1.next; // Move curr1 forward
	    }
	}

	
	public void partition(int v) {
		
		Link curr = head;
		Link l = null;
		while (curr != null) {
			Link last = curr;
			Link temp = curr;
			
			if ((int)curr.data > v) {
				if (curr == head) head = curr.next;
				while (temp.next != null) temp = temp.next;
				temp.next = last;
				l = last.next;
				last.next = null;
				
			}
			if ((int)curr.data < v)
			curr = curr.next;
			else curr = l;
		}
	}
	/*
	public void compress() {
		Link curr1 = head;
		Link curr2 = curr1.next;
		while (curr1.next != null) {
			Link l = curr1;
			while (curr2 != null) {
				if (curr1.data.equals(curr2.data)) {
				Link temp = curr2.next;
				Link prev = curr2;
				curr1.next = temp;
				
			}
			}
		}
	}
*/
	
	public void delete(Object o) {
	    if (head == null) {
	        // If the list is empty, return
	        return;
	    }
	    
	    Link curr = head;
	    Link prev = null;

	    // Traverse the list to find the node containing the object
	    while (curr != null && !curr.data.equals(o)) {
	        prev = curr;
	        curr = curr.next;
	    }

	    // If the object is not found in the list, return
	    if (curr == null) {
	        return;
	    }

	    // If the object is found, update the links to remove the node
	    if (prev != null) {
	        prev.next = curr.next;
	    } else {
	        // If the node to be deleted is the head, update the head reference
	        head = curr.next;
	    }
	}
	
	public LinkList append(LinkList listToAppend) {
        LinkList newList = new LinkList();

        // Copy the elements of the current list to the new list
        Link current = this.head;
        while (current != null) {
            newList.insertLast(current.data);
            current = current.next;
        }

        // Copy the elements of the listToAppend to the new list
        current = listToAppend.head;
        while (current != null) {
            newList.insertLast(current.data);
            current = current.next;
        }

        return newList;
    }
	
		
	
	
	public void remove(int pos1, int pos2) {
		
		Link temp = null;
		for (int i = 1; i < pos1; i++) {
			head = head.next;	
		}
		Link curr = head;
		for (int j = pos1; j <= pos2; j++) {
			
			if (j == pos2) curr.next = null;
			else curr = curr.next;
		}
	}
	public static void main(String [] args) {
		LinkList l1 = new LinkList();
		LinkList l2 = new LinkList();
		l1.insertLast(3);
		l1.insertLast(9);
		l1.insertLast(12);
		l1.insertLast(15);
		l1.insertLast(21);
		l2.insertLast(2);
		l2.insertLast(3);
		l2.insertLast(6);
		l2.insertLast(12);
		l2.insertLast(19);
		l1.insertLast(3);
		
		System.out.print(l1);
		
		System.out.print(l2);
		System.out.print(l2.append(l1));

//		l3
	//	l1.removedupes();
	//	System.out.println(l1);
		
		//System.out.println(common(l1,l2));
	
	}

	public int size() {
		Link current= head;
		int i=0;
		while (current!=null){
			i++;
			current=current.next;
		}
		return i;
	}
	
	public void decrementIfGreaterThan(int x) {
	    Link current = head;
	    while (current != null) {
		       Reference ref= (Reference) current.data;

	        if (ref.page > x) {
	            ref.page = ref.page - 1;
	        }
	        current = current.next;
	    }
	}
		
	
	
	
}
