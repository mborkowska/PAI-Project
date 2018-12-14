package api.project.Game;

import api.project.ServerClient.Packet;
import api.project.ServerClient.Packet.Type;
import api.project.ServerClient.ServerConnection;

public class Player extends Character {
	public ServerConnection connection;
	public Weapon weapon;
	public int life;
	public enum isAlive {
		ALIVE, DEAD, SPECTATE
	}
	public isAlive status;
	
	public Player(ServerConnection sc, int ammo, int life, int diamondLife) {
		status = isAlive.ALIVE;
		connection = sc;
		weapon = new Weapon(ammo);
		this.life = life;
		Packet p = new Packet();
		p.type = Type.AMMO;
		p.ammo = weapon.getAmmo();
		sc.sendPacketToClient(p);
		p.type = Type.LIFE;
		p.life = this.life;
		sc.sendPacketToClient(p);
		p.type = Type.DIAMOND_LIFE;
		p.diamondLife = diamondLife;
		sc.sendPacketToClient(p);
	}
	
	public boolean shoot() {
		return weapon.shoot();
	}
	public boolean canShoot() {
		return weapon.canShoot();
	}
	
	public void takeDamage() {
		if(life > 0) life--;
		if(life == 0) {
			status = isAlive.DEAD;
			life = -1;
		}
	}
	
	public void revive() {
		life = 20;
	}
}
