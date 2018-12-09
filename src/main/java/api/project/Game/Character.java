package api.project.Game;

import java.util.Random;

public class Character {
	public Coords position = new Coords();

	public void setRandomPosition() {
		Random rand = new Random();
		position.setX(rand.nextInt(((19 - 0) + 1) + 0));
		position.setY(rand.nextInt(((19 - 0) + 1) + 0));
	}

	public void moveUp() {
		position.changeX(-1);
	}

	public void moveDown() {
		position.changeX(1);
	}

	public void moveRigh() {
		position.changeY(1);
	}

	public void moveLeft() {
		position.changeY(-1);
	}
}
