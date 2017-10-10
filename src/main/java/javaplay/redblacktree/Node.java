package javaplay.redblacktree;

public class Node {
	public static enum Color {
		RED,
		BLACK
	}
	
	public static enum Dir {
		LEFT(true),
		RIGHT(false);
		
		private boolean isLeft;
		private Dir(boolean isLeft) {
			this.isLeft = isLeft;
		}
		
		public Dir opp() {
			if (isLeft) {
				return RIGHT;
			} else {
				return LEFT;
			}
		}
	}
	
	private Node parent;
	private Node left;
	private Node right;
	private Color color;
	private String key;
	private String value;
	
	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public Node getLeft() {
		return left;
	}

	public void setLeft(Node left) {
		this.left = left;
	}

	public Node getRight() {
		return right;
	}

	public void setRight(Node right) {
		this.right = right;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public void setChild(Dir dir, Node node) {
		if (dir == Dir.LEFT) {
			this.left = node;
		} else {
			this.right = node;
		}
	}
	
	public Node getChild(Dir dir) {
		if (dir == Dir.LEFT) {
			return left;
		} else {
			return right;
		}
	}

	public Node(Node parent, Node left, Node right, String key, String data) {
		super();
		this.parent = parent;
		this.left = left;
		this.right = right;
		this.key = key;
		this.value = data;
		this.color = Color.RED;
	}
}
