package api.project.Game;

public class Monster extends Character{
	public boolean alive = true;
	
	public void takeDamage() {
		alive = false;
	}
}
