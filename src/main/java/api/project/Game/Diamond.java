package api.project.Game;

public class Diamond extends Character {
	public int health = 50;
	
	public void takeDamage() {
		health--;
	}
	
	public boolean isAlive() {
		return health > 0;
	}
	
	@Override
	public void setRandomPosition() {
		position.setX(10);
		position.setY(10);
	}
}
