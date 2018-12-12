package api.project.ServerClient;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import api.project.Game.Direction;

public class Client implements KeyListener {
	JFrame frame;
	JTextArea textArea = new JTextArea();
	JLabel lifeLabel = new JLabel("Life:");
	JLabel ammoLabel = new JLabel("Ammo:");
	JLabel diamondLife = new JLabel("Diamond life:");
	JTextField life = new JTextField();
	JTextField ammo = new JTextField();
	JTextField diamond = new JTextField();
	JFrame gameFrame;
	JTextArea gameTextArea = new JTextArea();

	listenForInput listener;
	Socket s;
	ObjectInputStream oin;
	ObjectOutputStream oout;
	public String username;

	public static void main(String[] args) {
		new Client();
	}

	public Client() {
		String username = JOptionPane.showInputDialog("Input the username:");
		this.username = username;
		setUpWindow();
		connect();
	}

	private void setUpWindow() {
		// main window
		frame = new JFrame(username);
		frame.setSize(500, 450);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				Packet p = new Packet();
				p.type = Packet.Type.DISCONNECT;
				try {
					oout.writeObject(p);
				} catch (IOException e) {
					e.printStackTrace();
				}
				listener.terminate();
			}
		});
		Panel p = new Panel();
		frame.addKeyListener(this);
		textArea.setEditable(false);
		textArea.addKeyListener(this);

		JScrollPane areaScrollPane = new JScrollPane(textArea);
		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane.setPreferredSize(new Dimension(430, 275));
		p.add(areaScrollPane);
		frame.add(p);
		frame.setVisible(true);
		// game window
		life.setEditable(false);
		ammo.setEditable(false);
		diamond.setEditable(false);
		gameFrame = new JFrame(username + "'s game");
		gameFrame.setSize(400, 500);
		// gameFrame.addKeyListener(this);
		gameFrame.setLocationRelativeTo(null);
		/*
		 * gameFrame.addWindowListener(new WindowAdapter() {
		 * 
		 * @Override public void windowClosing(WindowEvent windowEvent) { Packet p = new
		 * Packet(); p.type = Packet.Type.EXIT; try { oout.writeObject(p); } catch
		 * (IOException e) { e.printStackTrace(); } } });
		 */
		JPanel gameP = new JPanel(new GridBagLayout());
		gameP.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		GridBagConstraints c = new GridBagConstraints();
		gameTextArea.setEditable(false);
		//gameTextArea.setMaximumSize(new Dimension(100,100));
		c.gridx = 0;
		c.gridy = 0;
		gameP.add(lifeLabel, c);
		c.gridx = 1;
		gameP.add(ammoLabel, c);
		c.gridx = 3;
		gameP.add(diamondLife, c);
		c.gridy = 1;
		c.gridx = 0;
		gameP.add(life, c);
		c.gridx = 1;
		gameP.add(ammo, c);
		c.gridx = 3;
		gameP.add(diamond, c);
		c.gridy =3;
		c.gridx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		//c.ipady = 80; // make this component tall
		c.weightx = 0.0;
		c.gridwidth = 4;

		gameP.add(gameTextArea, c);
		gameFrame.add(gameP);
	}

	private void connect() {
		try {
			s = new Socket("localhost", 3333);
			oout = new ObjectOutputStream(s.getOutputStream());
			oin = new ObjectInputStream(s.getInputStream());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Cannot connect to server");
			return;
		}
		Packet p = new Packet();
		p.type = Packet.Type.CONNECT;
		p.username = username;
		try {
			oout.writeObject(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
		listener = new listenForInput();
		listener.start();
	}

	public class listenForInput extends Thread {
		private boolean shouldRun = true;

		public void terminate() {
			shouldRun = false;
		}

		public void run() {
			while (shouldRun) {
				try {
					Packet p = (Packet) oin.readObject();
					if (p.type == Packet.Type.MESSAGE) {
						textArea.append(p.message + "\n");
					}
					if (p.type == Packet.Type.PLAY) {
						gameFrame.setVisible(true);
						gameTextArea.setText(p.message);
					}
					if (p.type == Packet.Type.BOARD_UPDATE) {
						gameTextArea.setText(p.message);
					}
					if (p.type == Packet.Type.AMMO) {
						String sAmmo = Integer.toString(p.ammo);
						ammo.setText(sAmmo);
					}
					if (p.type == Packet.Type.LIFE) {
						String sLife = Integer.toString(p.life);
						life.setText(sLife);
					}
					if (p.type == Packet.Type.DIAMOND_LIFE) {
						String sLife = Integer.toString(p.diamondLife);
						diamond.setText(sLife);
					}
					//gameTextArea.setSize(new Dimension(100,100));
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}

			}
			try {
				System.out.println("Streams closing properly");
				oin.close();
				oout.close();
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		Packet p = new Packet();
		if (e.getKeyCode() == KeyEvent.VK_ALT) {
			p.type = Packet.Type.PLAY;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			p.type = Packet.Type.MOVE;
			p.direction = Direction.DOWN;
		}
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			p.type = Packet.Type.MOVE;
			p.direction = Direction.UP;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			p.type = Packet.Type.MOVE;
			p.direction = Direction.RIGHT;
		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			p.type = Packet.Type.MOVE;
			p.direction = Direction.LEFT;
		}
		if (e.getKeyCode() == KeyEvent.VK_S) {
			p.type = Packet.Type.SHOOT;
		}
		if (e.getKeyCode() == KeyEvent.VK_R) {
			p.type = Packet.Type.RELOAD;
		}
		if (e.getKeyCode() == KeyEvent.VK_E) {
			p.type = Packet.Type.EXIT;
			gameFrame.setVisible(false);
		}
		try {
			System.out.println(e.getKeyChar());
			oout.writeObject(p);
			oout.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
//		Packet p = new Packet();
//		if (e.getKeyCode() == KeyEvent.VK_P) {
//			p.type = Packet.Type.PLAY;
//		}
//		if (e.getKeyCode() == KeyEvent.VK_S) {
//			p.type = Packet.Type.SHOOT;
//		}
//		if (e.getKeyCode() == KeyEvent.VK_R) {
//			p.type = Packet.Type.RELOAD;
//		}
//		if (e.getKeyCode() == KeyEvent.VK_E) {
//			p.type = Packet.Type.EXIT;
//		}
//		try {
//			System.out.println(e.getKeyChar());
//			oout.writeObject(p);
//			oout.flush();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
	}
}
