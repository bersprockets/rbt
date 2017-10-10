package javaplay.redblacktree;

import java.util.Collections;

import javaplay.redblacktree.Node.Color;
import javaplay.redblacktree.Node.Dir;
import javaplay.symboltable.SymbolTable;

public class RedBlackTree implements SymbolTable {
	private static class FindResult {
		public Node target;
		public Node parent;
		
		public FindResult(Node target, Node parent) {
			super();
			this.target = target;
			this.parent = parent;
		}
	}
	
	private static class WalkStats {
		public int maxDepth;
		public int nodeCount;
	}
	
	private Node nil = new Node(null, null, null, null, null);
	{
		nil.setColor(Node.Color.BLACK);
		nil.setLeft(nil);
		nil.setRight(nil);
	}
	
	private Node root = nil;
	
	private static class BadTreeException extends Exception {
		public BadTreeException(String message) {
			super(message);
		}
	}
	
	private FindResult find(String key, Node node) {
		Node parent = nil;
		while (node != nil && !node.getKey().equals(key)) {
			parent = node;
			if (key.compareTo(node.getKey()) < 0) {
				node = node.getLeft();
			} else {
				node = node.getRight();
			}
		}
		return new FindResult(node, parent);
	}

	@Override
	public String get(String key) {
		FindResult result = find(key, root);
		if (result.target == nil) {
			return null;
		}
		return result.target.getValue();
	}
	
	private void fixRedBlackOnInsert(Node newNode) {
		Node node = newNode.getParent();

		// loop while there is a red violation
		while (node.getColor().equals(Color.RED)) {
			Node.Dir dir = Dir.RIGHT;
			if (node == node.getParent().getLeft()) {
				dir = Dir.LEFT;
			}
			Node sibling = node.getParent().getChild(dir.opp());
			if (sibling.getColor().equals(Color.RED)) {
				// case #1: node is red and node's sibling is red
				node.setColor(Color.BLACK);
				sibling.setColor(Color.BLACK);
				node.getParent().setColor(Color.RED);
				// pretend the newly red node is the new node, move up the tree, and check again
				newNode = node.getParent(); 
				node = node.getParent().getParent();
			} else if (newNode == node.getChild(dir)) {
				// case #2. New node has same left/right relationship with node as node has
				// with its own parent
				node.getParent().setColor(Color.RED);
				node.setColor(Color.BLACK);
				rotate(node.getParent(), dir.opp());
				break; // all good, we're done
			} else {
				// case # 3. New node has a different left/right relationship with node than node's
				// relationship with its own parent
				node.setColor(Color.RED);
				newNode.setColor(Color.BLACK);
				rotate(node, dir);
				node = newNode.getParent();
				node.setColor(Color.RED);
				rotate(node, dir.opp());
				break; // all good, we're done
			}
		}
		
		root.setColor(Color.BLACK);
	}

	
	private void fixRedBlackOnDelete(Node subtreeRoot, Node.Dir dir) {
		// case 1: The replacement node is red. Just change it to black restore black height
		// and get out.
		Node replacementNode = subtreeRoot.getChild(dir);
		
		if (replacementNode.getColor() == Color.RED) {
			replacementNode.setColor(Color.BLACK);
			return;
		}
		
		// OK, not so easy. Our replacement node is black, which means we can't
		// fix the black height problem simply by re-coloring a red replacement node
		while (subtreeRoot != nil) {
			// sibling var points to the sibling of the replacement node (that is, sibling var points to
			// the sibling of the now removed node).
			Node sibling = subtreeRoot.getChild(dir.opp());
			
			if (sibling.getColor() == Color.RED) {
				// case 2: node has a red sibling. Convert to a black sibling case.
				// this case takes advantage of the fact that the sibling's parent (the subtree
				// root) and the sibling's children must be black (because you can't have two adjacent
				// red nodes).
				// This pushes the replacement node (and our subtree root) down one level				
				rotate(subtreeRoot, dir);
				
				subtreeRoot.setColor(Color.RED);
				subtreeRoot.getParent().setColor(Color.BLACK);

				// try again with a black sibling case.
				continue;
			} else {
				if (sibling.getLeft().getColor() == Color.BLACK &&
						sibling.getRight().getColor() == Color.BLACK) {
					// case 3: black sibling case and sibling's children are also black.
					// we can simply change the sibling from black to red to restore
					// black height balance (but not necessarily have the correct black height)
					// on this subtree.
					sibling.setColor(Color.RED);
					
					if (subtreeRoot.getColor() == Color.RED) {
						// case 3a: The subtree's root is red. We can restore this subtree's
						// black height by changing the subtree's root to black
						subtreeRoot.setColor(Color.BLACK);
						// we're done fixing the tree
						break;
					}
					// case 3b: the subtree root is black, which means this subtree's black height
					// is not in line with its sibling's black height. Therefore. we must go up one level
					// and continue correcting.
					dir = Node.Dir.LEFT;
					if (subtreeRoot == subtreeRoot.getParent().getRight()) {
						dir = Node.Dir.RIGHT;
					}
					subtreeRoot = subtreeRoot.getParent();
					continue;
				} else {
					if (sibling.getChild(dir.opp()).getColor() == Color.RED) {
						// case 4: black sibling with red outer child
						// pull up the outer child on one side, push down the replacement node
						// on the other side
						rotate(subtreeRoot, dir);
						
						// new subtree root is old subtree root's new parent
						Node oldSubtreeRoot = subtreeRoot;
						subtreeRoot = oldSubtreeRoot.getParent();
						
						// this is not the replacement node's sibling, but the sibling of the
						// node that now sits where the replacement node was before the rotation
						sibling = subtreeRoot.getChild(dir.opp());
						
						// the subtree's new root should have the same color as the
						// subtree's old root
						subtreeRoot.setColor(oldSubtreeRoot.getColor());
						
						// the siblings should be black
						sibling.setColor(Color.BLACK);
						oldSubtreeRoot.setColor(Color.BLACK);
						
						// the tree is now fixed
						break;
					} else {
						// case 5: black sibling with black outer child and red inner child.
						// basically, we just want to get an extra node on the
						// dir-side of the subtree so that we have something to color black
						// to restore the black height. So, in a sense, we're stealing a red node
						// from the dir.opp()-side so we can get this extra node on the dir-side.
						// first, move the inner child into the spot that holds the sibling.
						// this pushes down the sibling to the opposite side from the inner child's
						// current location
						rotate(sibling, dir.opp());
						
						// now pull up the former inner child into the root of the subtree
						// and push the current root into the location that currently holds the
						// replacement node
						rotate(subtreeRoot, dir);
						
						Node oldSubtreeRoot = subtreeRoot;
						subtreeRoot = oldSubtreeRoot.getParent();
						subtreeRoot.setColor(oldSubtreeRoot.getColor());
						oldSubtreeRoot.setColor(Color.BLACK);
						
						// tree is fixed
						break;
					}
				}
			}
		}
		
		// there seems to be some concern that the root will go red. Ensure that doesn't happen
		// we can do this whether root is nil or not, since the nil sentinel is already black.
		this.root.setColor(Color.BLACK);
	}

	@Override
	public SymbolTable put(String key, String value) {
		FindResult result = find(key, root);
		if (result.target != nil) {
			result.target.setValue(value);
			return this;
		}
		Node newNode = new Node(result.parent, nil, nil, key, value);
		if (result.parent == nil) {
			root = newNode;
		} else {
			if (key.compareTo(result.parent.getKey()) < 0) {
				result.parent.setLeft(newNode);
			} else {
				result.parent.setRight(newNode);
			}
		}
		fixRedBlackOnInsert(newNode);
		return this;
	}
	
	private Node minimum(Node node) {
		Node targetNode = node;
		while (targetNode.getLeft() != nil) {
			targetNode = targetNode.getLeft();
		}
		return targetNode;
	}
	
	private Node maximum(Node node) {
		Node targetNode = node;
		while (targetNode.getRight() != nil) {
			targetNode = targetNode.getRight();
		}
		return targetNode;
	}
	
	private Node successorNode(Node node) {
		if (node.getRight() != nil) {
			return minimum(node.getRight());
		}
		// find the first ancestor that has theNode somewhere in its left subtree.
		// we will return nil if no such ancestor exists
		Node parent = node.getParent();
		while (parent != nil && node == parent.getRight()) {
			node = parent;
			parent = node.getParent();
		}
		return parent;
	}
	
	private Node predecessorNode(Node node) {
		if (node.getLeft() != nil) {
			return maximum(node.getLeft());
		}
		// find the first ancestor that has theNode somewhere in its right subtree.
		// we will return nil if no such ancestor exists
		Node parent = node.getParent();
		while (parent != nil && node == parent.getLeft()) {
			node = parent;
			parent = node.getParent();
		}
		return parent;
	}
	
	private void maxDepthWalk(Node node, int currentDepth, WalkStats stats) {
		if (node == nil) {
			return;
		}
		currentDepth++;
		if (currentDepth > stats.maxDepth) {
			stats.maxDepth = currentDepth;
		}
		stats.nodeCount++;
		maxDepthWalk(node.getLeft(), currentDepth, stats);
		maxDepthWalk(node.getRight(), currentDepth, stats);
	}
	
	private void replaceNode(Node oldNode, Node newNode) {
		if (oldNode.getParent() == nil) {
			root = newNode;
			if (newNode != nil) {
				newNode.setParent(nil);
			}
			return;
		}
		
		if (oldNode.getParent().getLeft() == oldNode) {
			oldNode.getParent().setLeft(newNode);
		} else {
			oldNode.getParent().setRight(newNode);
		}
		if (newNode != nil) {
			newNode.setParent(oldNode.getParent());
		}
	}
	
	private void removeNode(Node node) {
		Node removedNode = node;
		Node.Dir dir = Node.Dir.LEFT;
		if (node.getParent().getRight() == node) {
			dir = Node.Dir.RIGHT;
		}
		
		while (node != nil) {
			if (node.getLeft() == nil && node.getRight() == nil) {
				// case 1: the target node has no children. Just delink the node from its parent
				replaceNode(node, nil);
				if (node.getParent() == nil) {
					// we've just removed the last node. Set root to nil
					root = nil;
					// stop call to fixRedBlackOnDelete
					removedNode.setColor(Color.RED);
				}
				break;
			} else if (node.getLeft() == nil && node.getRight() != nil) {
				// case 2: the target node has a right child only
				replaceNode(node, node.getRight());
				break;
			} else if (node.getLeft() != nil && node.getRight() == nil) {
				// case 3: the target node has a left child only
				replaceNode(node, node.getLeft());
				break;
			} else {
				// since the target node has two children, replace the target node's data
				// with that of its successor, and then make the successor the new target
				// node we want to remove. The successor now becomes subject to cases 1 and 2
				// above, but not case 3, as the successor cannot have a left child
				// Also, there must be a non-nil successor, or we would have hit case 1 or 3
				// above already.
				Node successor = minimum(node.getRight());
				dir = Node.Dir.LEFT;
				if (successor.getParent().getRight() == successor) {
					dir = Node.Dir.RIGHT;
				}
				node.setKey(successor.getKey());
				node.setValue(successor.getValue());
				node = successor;
				removedNode = node;
			}
		}
		
		if (removedNode.getColor() == Color.BLACK) {
			// there's only trouble if we've removed a black node
			fixRedBlackOnDelete(removedNode.getParent(), dir);
		}
	}

	@Override
	public String remove(String key) {
		FindResult result = find(key, root);
		if (result.target == nil) {
			return null;
		}
		
		String originalValue = result.target.getValue();
		removeNode(result.target);
		return originalValue;
	}

	@Override
	public String successor(String key) {
		FindResult result = find(key, root);
		if (result.target == nil) {
			return null;
		}
		Node successor = successorNode(result.target);
		if (successor == nil) {
			return null;
		}
		return successor.getKey();
	}

	@Override
	public String predecessor(String key) {
		FindResult result = find(key, root);
		if (result.target == nil) {
			return null;
		}
		Node predecessor = predecessorNode(result.target);
		if (predecessor == nil) {
			return null;
		}
		return predecessor.getKey();
	}
	
	private void stringify(Node node, int currDepth, int offset, String[] lines) {
		if (node == nil) {
			return;
		}
		int linesIndex = currDepth;
		int segmentSize = (int) ((120.0/(Math.pow(2, ((double)currDepth))+1)) + 0.5);
		if (segmentSize < 4) {
			return;
		}
		int location = segmentSize*offset;
		if (lines[linesIndex].length() < location) {
			lines[linesIndex] += String.join("", Collections.nCopies(location - lines[linesIndex].length(), " "));
		}
		lines[linesIndex] += String.format("%2s%s", node.getKey(), node.getColor() == Color.RED ? "r" : "b");
		stringify(node.getLeft(), currDepth+1, (offset*2)-1, lines);
		stringify(node.getRight(), currDepth+1, offset*2, lines);
	}
	
	public String toString() {
		if (root == nil) {
			return "";
		}
		WalkStats stats = new WalkStats();
		maxDepthWalk(root, 0, stats);
		String[] lines = new String[stats.maxDepth];
		for (int i = 0; i < lines.length; i++) {
			lines[i] = "";
		}
		stringify(root, 0, 1, lines);
		return String.join("\n\n", lines);
	}
	
	public int size() {
		if (root == nil) {
			return 0;
		}
		WalkStats stats = new WalkStats();
		maxDepthWalk(root, 0, stats);	
		return stats.nodeCount;
	}
	
	public int height() {
		if (root == nil) {
			return 0;
		}
		WalkStats stats = new WalkStats();
		maxDepthWalk(root, 0, stats);	
		return stats.maxDepth;
	}
	
	private int good(Node node, Node parent) throws BadTreeException {
		if (node == nil) {
			return 1;
		}
		if (node.getParent() != parent) {
			throw new BadTreeException("Corrupted BST at node with key " + node.getKey() + "; Unexpected parent");
		}
		if (parent != nil) {
			if (node == parent.getLeft()) {
				if (node.getKey().compareTo(parent.getKey()) >= 0) {
					throw new BadTreeException("Corrupted BST at node with key " + node.getKey() + "; Unexpected key");
				} 
			} else if (node.getKey().compareTo(parent.getKey()) <= 0) {
				throw new BadTreeException("Corrupted BST at node with key " + node.getKey() + "; Unexpected key");
			}
		}
		
		if (node.getColor() == Color.RED) {
			if (node.getLeft().getColor() == Color.RED) {
				throw new BadTreeException("Corrupted RBT at node with key " +
						node.getKey() + "; Left child unexpectedly red");
			}
			if (node.getRight().getColor() == Color.RED) {
				throw new BadTreeException("Corrupted RBT at node with key " +
						node.getKey() + "; Right child unexpectedly red");
			}
		}
		int leftCount = good(node.getLeft(), node);
		int rightCount = good(node.getRight(), node);
		
		if (leftCount != rightCount) {
			throw new BadTreeException(String.format("Path at node with key %s" +
					" contains unexpected number of black nodes: %d vs. %d", node.getKey(), leftCount, rightCount));
		}
		
		// count black nodes in this subtree (including this subtree's root)
		return leftCount + (node.getColor() == Color.BLACK ? 1: 0);
	}
	
	public String check() {
		try {
			good(root, nil);
		} catch (BadTreeException bte) {
			System.err.println(bte.getMessage());
			return bte.getMessage();
		}
		return null;
	}
	
	private void rotate(Node node, Node.Dir dir) {
		// node becomes the dir child of its dir.opp() child. Also, the new parent's old
		// dir child becomes node's new dir.opp() child
		Node newParent = node.getChild(dir.opp());
		node.setChild(dir.opp(), newParent.getChild(dir));
		node.getChild(dir.opp()).setParent(node);
		newParent.setChild(dir, node);
		replaceNode(node, newParent);
		node.setParent(newParent);
			
		if (root == node) {
			root = newParent;
		}
	}
}
