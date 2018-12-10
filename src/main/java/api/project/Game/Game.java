package api.project.Game;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.JOptionPane;

import com.sun.swing.internal.plaf.synth.resources.synth;

import api.project.Game.Board.fieldType;
import api.project.ServerClient.Packet;
import api.project.ServerClient.Server;
import api.project.ServerClient.ServerConnection;
import api.project.ServerClient.Packet.Type;

public class Game {
	private ArrayList<ServerConnection> playersConnections = new ArrayList<>();
	private ArrayList<Player> players = new ArrayList<>();
	private ArrayList<SpawnMonster> monsters = new ArrayList<>();
	public boolean isRunning = false;
	public Board board;
	private int currentPlayer = -1;
	boolean monstersShouldExist = true;
	private int monsterAmount = 5;
	private int rows = 20;
	private int cols = 20;
	private int ammo = 20;
	private int life = 20;

	public synchronized void addPlayer(ServerConnection sc) {
		playersConnections.add(sc);
		players.add(new Player(sc, ammo, life));
		int index = players.size() - 1;
		addCharacter(players.get(index));
	}

	public synchronized void removePlayer(ServerConnection sc) {
		if (playersConnections.remove(sc)) {
			setCurrentPlayer(sc);
			board.setAt(players.get(currentPlayer).position.getX(), players.get(currentPlayer).position.getY(),
					fieldType.BLANK);
			players.remove(currentPlayer);
			if (players.size() == 0)
				endGame();
		}
	}

	public synchronized void movePlayer(ServerConnection sc, Direction dir) {
		setCurrentPlayer(sc);
		moveCharacter(players.get(currentPlayer), dir);
	}

	public synchronized boolean shoot(ServerConnection sc) {
		setCurrentPlayer(sc);
		int currentX = players.get(currentPlayer).position.getX();
		int currentY = players.get(currentPlayer).position.getY();
		boolean shot = false;
		if (players.get(currentPlayer).canShoot()) {
			// look for monsters
			for (int i = -1; i < 2; i++) {
				for (int j = -1; j < 2; j++) {
					if (i + currentX >= 0 && i + currentX <= rows - 1 && j + currentY >= 0 && j + currentY <= cols - 1
							&& (i + j) % 2 != 0) {
						if (board.getAt(currentX + i, currentY + j) == Board.fieldType.MONSTER) {
							board.setAt(currentX + i, currentY + j, Board.fieldType.BLANK);
							players.get(currentPlayer).shoot();
							for(int k = 0; k < monsterAmount; k++) {
								if(monsters.get(k).monster.position.getX() == currentX+i && monsters.get(k).monster.position.getY() == currentY+j) {
									monsters.get(k).monster.takeDamage();
									Packet p = new Packet();
									p.type = Type.BOARD_UPDATE;
									p.message = board.display();
									sc.sendPacketToClient(p);
									sc.sendPacketToOtherClients(p);
								}
							}
							System.out.println("Congrats! You shot a monster");
							shot = true;
						}
					}
				}
			}
			System.out.println();
		} else {
			System.out.println("Out of ammo. Reload to shoot.");
		}
		return shot;
	}

	public synchronized void startGame() {
		board = new Board(rows, cols);
		isRunning = true;
		for (int i = 0; i < monsterAmount; i++) {
			monsters.add(new SpawnMonster());
			monsters.get(i).start();
		}
	}

	private synchronized void endGame() {
		System.out.println("Game is ending");
		isRunning = false;
		for (int i = 0; i < monsters.size(); i++) {
			monsters.get(i).terminate();
		}
		monsters.clear();
	}

	private void setCurrentPlayer(ServerConnection sc) {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).connection == sc)
				currentPlayer = i;
		}
	}

	public class SpawnMonster extends Thread {
		private boolean shouldRun = true;
		public Monster monster = new Monster();
		public void terminate() {
			shouldRun = false;
		}

		public void run() {
			addCharacter(monster);
			Random rand = new Random();
			int sleep;
			int move;
			Packet p;
			Direction dir = null;
			while (shouldRun) {
				sleep = rand.nextInt((5000 - 4000) + 1) + 4000; // random between 1s and 2s
				try {
					Thread.sleep(sleep);
					if (!monster.alive) {
						terminate();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					terminate();
				}
				move = rand.nextInt((4 - 1) + 1) + 1;
				switch (move) {
				case 1:
					dir = Direction.DOWN;
					break;
				case 2:
					dir = Direction.UP;
					break;
				case 3:
					dir = Direction.LEFT;
					break;
				case 4:
					dir = Direction.RIGHT;
					break;
				default:
					break;
				}
				moveCharacter(monster, dir);
				p = new Packet();
				p.type = Type.BOARD_UPDATE;
				p.message = board.display();
				for (int i = 0; i < playersConnections.size(); i++) {
					players.get(i).connection.sendPacketToClient(p);
				}
			}
		}
	}

	private synchronized void addCharacter(Character character) {
		do {
			character.setRandomPosition();
		} while (board.getAt(character.position.getX(), character.position.getY()) != Board.fieldType.BLANK);
		if (character instanceof Monster) {
			board.setAt(character.position.getX(), character.position.getY(), Board.fieldType.MONSTER);
		}
		if (character instanceof Player) {
			board.setAt(character.position.getX(), character.position.getY(), Board.fieldType.PLAYER);
		}
	}

	private synchronized boolean moveCharacter(Character character, Direction dir) {
		Coords oldPosition = new Coords();
		oldPosition.setX(character.position.getX());
		oldPosition.setY(character.position.getY());

		switch (dir) {
		case DOWN:
			character.moveDown();
			break;
		case UP:
			character.moveUp();
			break;
		case LEFT:
			character.moveLeft();
			break;
		case RIGHT:
			character.moveRigh();
			break;
		default:
			break;
		}
		Coords newPosition = new Coords();
		newPosition.setX(character.position.getX());
		newPosition.setY(character.position.getY());
		if (board.getAt(newPosition.getX(), newPosition.getY()) == fieldType.BLANK) {
			if (character instanceof Player) {
				board.setAt(newPosition.getX(), newPosition.getY(), fieldType.PLAYER);
			}
			if (character instanceof Monster) {
				board.setAt(newPosition.getX(), newPosition.getY(), fieldType.MONSTER);
			}
			board.setAt(oldPosition.getX(), oldPosition.getY(), fieldType.BLANK);
			return true;
		} else {
			character.position = oldPosition;
			return false;
		}
	}
}
