package api.project.Game;

public class Diamond extends Character {
	public int life;
	
	public Diamond(int life) {
		this.life = life;
	}
	public void takeDamage() {
		life--;
	}
	
	public boolean isAlive() {
		return life > 0;
	}
	
	@Override
	public void setRandomPosition(int maxX, int maxY) {
		position.setX(10);
		position.setY(10);
	}
}
