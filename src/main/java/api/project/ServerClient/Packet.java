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
		BOARD_UPDATE;
	}
	public Type type;
	public String message;
	public String username;
	public Direction direction;
}
