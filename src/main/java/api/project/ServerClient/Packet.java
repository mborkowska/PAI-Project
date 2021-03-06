package api.project.ServerClient;

import java.io.Serializable;

import api.project.Game.Direction;

public class Packet implements Serializable {
	public enum Type {
		CONNECT, 
		DISCONNECT,
		PLAY, 
		EXIT,
		MESSAGE,
		GAME_OVER, 
		MOVE,
		SHOOT,
		RELOAD,
		BOARD_UPDATE,
		LIFE,
		AMMO,
		DIAMOND_LIFE,
		DEAD
	}
	public Type type;
	public String message;
	public String username;
	public Direction direction;
	public int life;
	public int ammo;
	public int diamondLife;
}
