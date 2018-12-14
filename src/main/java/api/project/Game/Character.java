package api.project.Game;

import java.util.Random;

public class Character {
	public Coords position = new Coords();

	public void setRandomPosition(int maxX, int maxY) {
		Random rand = new Random();
		position.setX(rand.nextInt(((maxX - 0) + 1) + 0));
		position.setY(rand.nextInt(((maxY - 0) + 1) + 0));
	}
	//TODO change board size!!!!!!

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
