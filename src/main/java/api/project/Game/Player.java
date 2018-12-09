package api.project.Game;

import api.project.ServerClient.ServerConnection;

public class Player extends Character {
	public ServerConnection connection;
	public Weapon weapon;
	public int life = 20;
	
	public Player(ServerConnection sc, int ammo, int life) {
		connection = sc;
		weapon = new Weapon(ammo);
		this.life = life; 
	}
	
	public boolean shoot() {
		return weapon.shoot();
	}
	
	public void getDamage() {
		life--;
	}
	public boolean isAlive() {
		return life > 0;
	}
	
	public void revive() {
		life = 20;
	}
}
