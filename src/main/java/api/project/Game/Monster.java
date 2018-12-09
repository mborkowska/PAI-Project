package api.project.Game;

public class Monster extends Character{
	public boolean alive = true;
	
	public void getDamage() {
		alive = false;
	}
}
