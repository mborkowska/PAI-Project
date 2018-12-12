package api.project.ServerClient;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class Client implements ActionListener {
	JFrame frame;
	JTextArea textArea = new JTextArea();
	JTextField life = new JTextField();
	JTextField ammo = new JTextField();
	JLabel lifeLabel = new JLabel("Life:");
	JLabel ammoLabel = new JLabel("Ammo:");
	JButton PlayButton = new JButton("Play");
	JButton MoveUp = new JButton("Move Up");
	JButton MoveDown = new JButton("Move Down");
	JButton MoveRight = new JButton("Move Right");
	JButton MoveLeft = new JButton("Move Left");
	JButton Shoot = new JButton("Shoot");
	JButton Reload = new JButton("Reload");
	JButton ExitGame = new JButton("Exit Game");
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
		PlayButton.addActionListener(this);
		textArea.setEditable(false);
		life.setEditable(false);
		ammo.setEditable(false);
		JScrollPane areaScrollPane = new JScrollPane(textArea);
		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane.setPreferredSize(new Dimension(430, 275));
		p.add(areaScrollPane);
		p.add(PlayButton);
		frame.add(p);
		frame.setVisible(true);
		// game window
		gameFrame = new JFrame(username + "'s game");
		gameFrame.setSize(700, 700);
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
		MoveDown.addActionListener(this);
		MoveUp.addActionListener(this);
		MoveLeft.addActionListener(this);
		MoveRight.addActionListener(this);
		ExitGame.addActionListener(this);
		Shoot.addActionListener(this);
		Reload.addActionListener(this);
		gameTextArea.setEditable(false);
		gameTextArea.setSize(200, 200);
		c.gridx = 0;
		c.gridy = 0;
		gameP.add(lifeLabel, c);
		c.gridx = 1;
		gameP.add(ammoLabel, c);
		c.gridy = 1;
		c.gridx = 0;
		gameP.add(life, c);
		c.gridx = 1;
		gameP.add(ammo, c);
		c.gridx = 1;
		c.gridy = 2;
		gameP.add(MoveUp, c);
		c.gridx = 3;
		gameP.add(Shoot, c);
		c.gridy = 3;
		c.gridx = 0;
		gameP.add(MoveLeft, c);
		c.gridx = 2;
		gameP.add(MoveRight, c);
		c.gridx = 3;
		gameP.add(Reload, c);
		c.gridy = 4;
		c.gridx = 1;
		gameP.add(MoveDown, c);
		c.gridx = 3;
		gameP.add(ExitGame, c);

		c.gridy = 5;
		c.gridx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 200;      //make this component tall
		c.weightx = 0.0;
		c.gridwidth = 4;
		//gameP.add(new JButton("New"), c);
		gameP.add(gameTextArea);
		
		
		//c.weighty = 10;
		
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

	public void actionPerformed(ActionEvent e) {
		Packet p = new Packet();
		if (e.getSource() == PlayButton) {
			p.type = Packet.Type.PLAY;
		}
		if (e.getSource() == MoveDown) {
			p.type = Packet.Type.MOVE;
			p.direction = Direction.DOWN;
		}
		if (e.getSource() == MoveUp) {
			p.type = Packet.Type.MOVE;
			p.direction = Direction.UP;
		}
		if (e.getSource() == MoveLeft) {
			p.type = Packet.Type.MOVE;
			p.direction = Direction.LEFT;
		}
		if (e.getSource() == MoveRight) {
			p.type = Packet.Type.MOVE;
			p.direction = Direction.RIGHT;
		}
		if (e.getSource() == Shoot) {
			p.type = Packet.Type.SHOOT;
		}
		if (e.getSource() == Reload) {
			p.type = Packet.Type.RELOAD;
		}
		if (e.getSource() == ExitGame) {
			p.type = Packet.Type.EXIT;
			gameFrame.setVisible(false);
		}

		try {
			oout.writeObject(p);
			oout.flush();
			System.out.println();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
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
						gameTextArea.setText(null);
						gameTextArea.append(p.message + "\n");
					}
					if (p.type == Packet.Type.BOARD_UPDATE) {
						gameTextArea.setText(null);
						gameTextArea.append(p.message + "\n");
					}
					if (p.type == Packet.Type.AMMO) {
						String sAmmo = Integer.toString(p.ammo);
						ammo.setText(sAmmo);
					}
					if (p.type == Packet.Type.LIFE) {
						String sLife = Integer.toString(p.life);
						life.setText(sLife);
					}

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
}
