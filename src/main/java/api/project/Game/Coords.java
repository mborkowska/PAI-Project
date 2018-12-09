package api.project.Game;

public class Coords {
	private int x;
	private int y;

	private int rows = 20;
	private int cols = 20;

	public int getX() {
		return x;
	}

	public int getY() {
		return y;

	}

	public void setX(int x) {
		if (x >= 0 && x <= rows - 1)
			this.x = x;
	}

	public void setY(int y) {
		if (y >= 0 && y <= cols - 1)
			this.y = y;
	}

	public void changeX(int a) {
		int newX = this.x + a;
		if (newX >= 0 && newX <= rows - 1)
			this.x = newX;
	}

	public void changeY(int a) {
		int newY = this.y + a;
		if (newY >= 0 && newY <= cols - 1)
			this.y = newY;
	}

	@Override
	public boolean equals(Object obj) {
		Coords c = (Coords) obj;
		return (c.x == this.x && c.y == this.y);
	}
}
