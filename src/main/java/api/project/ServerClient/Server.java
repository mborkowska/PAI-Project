package api.project.ServerClient;

import java.awt.Dimension;
import java.awt.Panel;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import api.project.Game.Game;

public class Server {

	JFrame frame = new JFrame("Server");
	JTextArea textArea = new JTextArea();
	ServerSocket ss;
	ArrayList<ServerConnection> lobby = new ArrayList<ServerConnection>();
	public Game game;
	boolean shouldRun = true;
	boolean acceptsRequests = false;
	

	public static void main(String[] args) {
		new Server();
	}

	public Server() {
		setUpWindow();
		connect();
		initiateConnections();
	}

	private void setUpWindow() {
		frame.setSize(450, 375);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Panel p = new Panel();
		textArea.setEditable(false);
		JScrollPane areaScrollPane = new JScrollPane(textArea);
		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane.setPreferredSize(new Dimension(430, 275));
		p.add(areaScrollPane);
		frame.add(p);
		frame.setVisible(true);
	}

	private void connect() {
		int port = 3300;
		String filename = "serverConfig.xml";
		File file = new File(filename);
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
		        .newInstance();
		DocumentBuilder documentBuilder;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(file);
			String portString = document.getElementsByTagName("portNumber").item(0).getTextContent();
			port = Integer.parseInt(portString);
			game = new Game(document);
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void initiateConnections() {
		try {
			while (shouldRun) {
				Socket s = ss.accept();
				ServerConnection sc = new ServerConnection(s, this);
				sc.start();
				lobby.add(sc);
				Thread.sleep(5);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/*public String displayBoard() {
		return game.board.display();
	}*/
}
