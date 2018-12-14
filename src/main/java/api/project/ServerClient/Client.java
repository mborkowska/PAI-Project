package api.project.ServerClient;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.awt.TextArea;
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
import api.project.ServerClient.Packet.Type;

public class Client implements KeyListener {

	JLabel lifeLabel = new JLabel("Life:");
	JLabel ammoLabel = new JLabel("Ammo:");
	JLabel diamondLife = new JLabel("Diamond life:");
	JTextField life = new JTextField();
	JTextField ammo = new JTextField();
	JTextField diamond = new JTextField();
	JFrame gameFrame;
	JTextArea gameTextArea = new JTextArea();
	chatWindow chat;
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
		chat = new chatWindow();
		chat.setup();
		life.setEditable(false);
		ammo.setEditable(false);
		diamond.setEditable(false);
		gameFrame = new JFrame(username + "'s game");
		gameFrame.setSize(400, 500);
		gameFrame.setLocationRelativeTo(null);

		gameFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				Packet p = new Packet();
				p.type = Packet.Type.EXIT;
				try {
					oout.writeObject(p);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		JPanel gamePanel = new JPanel(new GridBagLayout());
		gameFrame.setFocusable(true);
		gameFrame.addKeyListener(this);
		gamePanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		GridBagConstraints c = new GridBagConstraints();
		gameTextArea.setEditable(false);
		// gameTextArea.addKeyListener(this);
		c.gridx = 0;
		c.gridy = 0;
		gamePanel.add(lifeLabel, c);
		c.gridx = 1;
		gamePanel.add(ammoLabel, c);
		c.gridx = 3;
		gamePanel.add(diamondLife, c);
		c.gridy = 1;
		c.gridx = 0;
		gamePanel.add(life, c);
		c.gridx = 1;
		gamePanel.add(ammo, c);
		c.gridx = 3;
		gamePanel.add(diamond, c);
		c.gridy = 3;
		c.gridx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.gridwidth = 4;

		gamePanel.add(gameTextArea, c);
		gameFrame.add(gamePanel);
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
						chat.textArea.append(p.message + "\n");
						chat.textArea.setCaretPosition(chat.textArea.getDocument().getLength());
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
					if (p.type == Packet.Type.DEAD) {
						removeKeyListener();
						chat.textArea.append( "You are dead, You can only spactate now.\n");
						chat.textArea.setCaretPosition(chat.textArea.getDocument().getLength());
					}
					if (p.type == Packet.Type.GAME_OVER) {
						removeKeyListener();
						chat.textArea.append( "You lost.\n");
						chat.textArea.setCaretPosition(chat.textArea.getDocument().getLength());
					}
					
					// gameTextArea.setSize(new Dimension(100,100));
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
			}
			try {
				oin.close();
				oout.close();
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class chatWindow implements ActionListener {
		JFrame frame;
		JTextArea textArea = new JTextArea();
		JTextField textField = new JTextField(25);
		JButton play = new JButton("Play");
		JButton send = new JButton("Send");

		public void setup() {
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
			textArea.setEditable(false);
			JScrollPane areaScrollPane = new JScrollPane(textArea);
			areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			areaScrollPane.setPreferredSize(new Dimension(430, 275));
			play.addActionListener(this);
			send.addActionListener(this);
			p.add(areaScrollPane);
			p.add(textField);
			p.add(send);
			p.add(play);
			frame.add(p);
			frame.setVisible(true);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Packet p = new Packet();
			if (e.getSource() == play) {
				p.type = Packet.Type.PLAY;
			}
			if (e.getSource() == send) {
				p.type = Type.MESSAGE;
				p.message = username + ": " + textField.getText() + "\n";
				textField.setText(null);
			}
			try {
				oout.writeObject(p);
				oout.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
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
	private void removeKeyListener() {
		gameFrame.removeKeyListener(this);
	}
}
