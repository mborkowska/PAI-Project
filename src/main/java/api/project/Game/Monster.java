package api.project.Game;

import java.util.Random;

public class Monster extends Character {
	public boolean alive = true;

	public void takeDamage() {
		alive = false;
	}
	
	/*@Override
	public void setRandomPosition() {
		Random rand = new Random();
		int y;
		int x = rand.nextInt(((19 - 0) + 1) + 0);
		if(x == 0 || x == 19) {
			y = rand.nextInt(((19 - 0) + 1) + 0);
		} else {
			y = (rand.nextInt(((1 - 0) + 1) + 0))*19;
		}
	}*/
}
