package api.project.Game;

import api.project.ServerClient.Packet;
import api.project.ServerClient.Packet.Type;
import api.project.ServerClient.ServerConnection;

public class Player extends Character {
	public ServerConnection connection;
	public Weapon weapon;
	public int life;
	
	public Player(ServerConnection sc, int ammo, int life, int diamondLife) {
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
		life--;
	}
	public boolean isAlive() {
		return life > 0;
	}
	
	public void revive() {
		life = 20;
	}
}
