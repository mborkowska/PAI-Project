package api.project.ServerClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import javax.swing.JTextArea;

import api.project.ServerClient.Packet.Type;

public class ServerConnection extends Thread {

	private final JTextArea textArea;
	Socket socket;
	Server server;
	ObjectInputStream oin;
	ObjectOutputStream oout;
	boolean shouldRun = true;
	public String username;
	int room;

	public ServerConnection(Socket socket, Server server) {
		super("ServerConnectionThred");
		this.socket = socket;
		this.server = server;
		this.textArea = server.textArea;
	}

	public void sendPacketToClient(Packet packet) {
		try {
			
			oout.writeObject(packet);
			
			oout.flush();
			oout.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendPacketToOtherClients(Packet packet) {
		for (int index = 0; index < server.lobby.size(); index++) {
			ServerConnection sc = server.lobby.get(index);
			if (sc.shouldRun == true && sc != this) {
				sc.sendPacketToClient(packet);
			}
		}
	}

	// opening ObjectInputstram, ObjectOutputStream for clients
	public void run() {
		try {
			oout = new ObjectOutputStream(socket.getOutputStream());
			oin = new ObjectInputStream(socket.getInputStream());
			while (shouldRun) {
				try {
					Packet p = (Packet) oin.readObject();
					Packet returnPacket = new Packet();
					if (p.type == Packet.Type.CONNECT) {
						returnPacket.type = Packet.Type.MESSAGE;
						this.username = p.username;
						returnPacket.message = this.username + " connected.\n";
						textArea.append(returnPacket.message);
						sendPacketToClient(returnPacket);
						sendPacketToOtherClients(returnPacket);
					}
					if (p.type == Packet.Type.DISCONNECT) {
						returnPacket.type = Packet.Type.MESSAGE;
						returnPacket.message = this.username + " disconnected.\n";
						textArea.append(returnPacket.message);
						sendPacketToClient(returnPacket);
						sendPacketToOtherClients(returnPacket);
						shouldRun = false;
					}
					if (p.type == Packet.Type.MESSAGE) {
						textArea.append(this.username + ": " + p.message);
					}
					if (p.type == Packet.Type.PLAY) {
						if (!server.game.isRunning) {
							server.game.startGame();
							textArea.append("The game has started.\n");
						}
						server.game.addPlayer(this);
						returnPacket.type = Type.PLAY;
						returnPacket.message = server.displayBoard();
						sendPacketToClient(returnPacket);
						returnPacket.type = Type.BOARD_UPDATE;
						sendPacketToOtherClients(returnPacket);
					}
					if (p.type == Packet.Type.MOVE) {
						server.game.movePlayer(this, p.direction);
						returnPacket.type = Type.BOARD_UPDATE;
						returnPacket.message = server.displayBoard();
						sendPacketToClient(returnPacket);
						sendPacketToOtherClients(returnPacket);
					}
					if (p.type == Packet.Type.SHOOT) {
						if(server.game.shoot(this)) {
							returnPacket.type = Type.MESSAGE;
							returnPacket.message = "Congratulations! You have killed a monster.";
							sendPacketToClient(returnPacket);
							textArea.append(this.username + " has killed a monster.\n");
						} 
					}
					if (p.type == Packet.Type.RELOAD) {
						server.game.reload(this);
					}
					if (p.type == Packet.Type.EXIT) {
						server.game.removePlayer(this);
						if (!server.game.isRunning) {
							textArea.append("The game has ended.\n");
						}
					}

				} catch (SocketException se) {
					shouldRun = false;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			oin.close();
			oout.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
