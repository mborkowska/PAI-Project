package api.project.Game;

import java.util.Random;

public class Monster extends Character {
	public boolean alive = true;
	public int went = 0;

	public void takeDamage() {
		alive = false;
	}

	public int goTo() {
		if (went == 0) {
			if (position.getY() == 10) {
				Random rand = new Random();
				return rand.nextInt(1) + 3;
			} else if (position.getY() < 10) {
				went = 1;
				return 4;
			} else {
				went = 1;
				return 3;
			}
		} else {
			if (position.getX() == 10) {
				Random rand = new Random();
				return rand.nextInt(1) + 1;
			} else if (position.getX() < 10) {
				went = 0;
				return 1;
			} else {
				went = 0;
				return 2;
			}
		}
	}

	@Override
	public void setRandomPosition() {
		Random rand = new Random();
		int y;
		int x = rand.nextInt(20);
		if (x == 0 || x == 19) {
			y = rand.nextInt(20);
		} else {
			y = (rand.nextInt(2)) * 19;
		}
		position.setX(x);
		position.setY(y);
	}
}
