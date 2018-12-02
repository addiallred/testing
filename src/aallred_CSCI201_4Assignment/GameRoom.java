package aallred_CSCI201_4Assignment;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

public class GameRoom {
	private ArrayList<Game> games = null;
	private Vector<ServerThread> serverThreads;
	private Connection conn = null;
	private Statement myStm = null;
	private Config cg = null;
	private ArrayList<String> words = null;
	public GameRoom(Config cg) {
		this.cg = cg;
		try {
			ServerSocket ss = new ServerSocket(cg.getPort());
			
			try {
				System.out.print("Trying to connect to the data base ...");
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection( cg.getDBC() + "?user=" + cg.getDBU() + "&password=" + cg.getDBP() + "&useSSL=false");
				myStm = conn.createStatement();
				System.out.println("Connected!");
			}catch(SQLException sqle) {
				System.out.println("Unable to connect to database " + cg.getDBC() + " with username " + cg.getDBU() + " and password " + cg.getDBP());
				return;
			}catch(ClassNotFoundException cnfe) {
				System.out.println("Unable to connect to database " + cg.getDBC() + " with username " + cg.getDBU() + " and password " + cg.getDBP());
				return;
			}
			serverThreads = new Vector<ServerThread>();
			while(true) {
				Socket s = ss.accept(); // blocking
				//System.out.println("Connection from: " + s.getInetAddress());
				ServerThread st = new ServerThread(s, this);
				serverThreads.add(st);
			}
		} catch (IOException ioe) {
			System.out.println("Port " + cg.getPort()+ " already in use");
			//System.out.println("Unable to connect to the server at " _+ );
		}
	}public Game getGame(UserAction ua) {
		if(games != null) {
			for(int i = 0; i < games.size(); i++) {
				if(games.get(i).getGName().equals(ua.getGameName())) {
					ua.setGame(games.get(i));
					return games.get(i);
				}
			}
		}
		return null;
	}
	public boolean checkUser(UserAction ua){
		ResultSet rs = null;
		boolean result = false;
		try {
			rs = myStm.executeQuery("SELECT * FROM userInfo WHERE BINARY username = '" + ua.getUsername() + "' AND BINARY userpassword = '" + ua.getPassword() + "';");
			if(rs.next()) {
				result = true;
				int wins = rs.getInt(3);
				int loses = rs.getInt(4);
				ua.setLose(loses);
				ua.setWin(wins);
			}
		} catch (SQLException e) {
			System.out.println("error" + e.getMessage());
		}finally {
			try {
				if(rs != null) {

					rs.close();
				}
			} catch (SQLException e2) {
				// TODO: handle exception
			}
		}
		return result;
	}public void readWords() {
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(cg.getWordFile());
			br = new BufferedReader(fr);
			String tword = "";
			words = new ArrayList<String>();
			while(tword != null) {
				tword = br.readLine();
				if(tword == null) {
					break;
				}
				else {
					words.add(tword);
				}
				
			}
		}catch(FileNotFoundException ioe) {
			System.out.println("Word file could not be found");
		}catch(IOException ioe) {
			
		}finally {
			if(br != null) {
				try {
					br.close();
				}catch(IOException ioe) {
					
				}
			}if(fr != null) {
				try {
				fr.close();
			}catch(IOException ioe) {
				
			}
			}
		}
	}public String getWord() {
		if(words == null) {
			this.readWords();
		}
		Random rand = new Random();
		int n = rand.nextInt(words.size()) + 0;
		if(words != null) {
			return words.get(n);
		}
		return "";
		
	}public void createAccount(UserAction ua) {
		try {
			myStm.executeUpdate("INSERT INTO userInfo (username, userpassword, win, lost) VALUES ('"+ ua.getUsername()+"', '" + ua.getPassword() +"', 0,0);");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}
	public void createGame(UserAction ua) {
		if(games == null) {
			games = new ArrayList<Game>();
		}
		games.add(ua.getGame());
	}public boolean userName(UserAction ua) {
		ResultSet rs = null;
		boolean result = false;
		try {
			rs = myStm.executeQuery("SELECT * FROM userInfo WHERE BINARY username = '" + ua.getUsername() + "';");
			
			if(rs.next()) {
				result = true;
				int wins = rs.getInt(3);
				int loses = rs.getInt(4);
				ua.setLose(loses);
				ua.setWin(wins);
			}
		} catch (SQLException e) {
			System.out.println("error" + e.getMessage());
		}finally {
			try {
				if(rs != null) {

					rs.close();
				}
			} catch (SQLException e2) {
				// TODO: handle exception
			}
		}
		return result;
	}
	public boolean newGame(UserAction ua) {
		boolean result = false;
		if(getGame(ua) == null) {
			result = false;
		}else {
			result = true;
		}
		return result;
	}public void updateWins(UserAction ua) {
		try {
			int num = ua.getWin();
			myStm.executeUpdate("UPDATE userInfo SET win = " + num +" WHERE username='" + ua.getUsername() + "';");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}public void updateLoss(UserAction ua) {
		try {
			int num = ua.getLose();
			myStm.executeUpdate("UPDATE userInfo SET lost = " + num +" WHERE username='" + ua.getUsername() + "';");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}
	public void broadcast(Game game) {
		if (game != null) {
			for(ServerThread threads : serverThreads) {
				if(threads.gameName() != null) {
					if(threads.gameName().equals(game.getGName())) {
						threads.SendMessage();
					}
				}
			}
		}
	}public void gameBroadcast(Game game) {
		if(game != null) {
			for(ServerThread threads : serverThreads) {
				if(threads.gameName().equals(game.getGName())) {
					threads.SendGames(game);
				}
			}
		}
	}public void userBroadcast(UserAction ua) {
		if(ua != null) {
			for(ServerThread threads: serverThreads) {
				if(threads.gameName().equals(ua.getGameName())) {
					threads.SendUser(ua);
				}
			}
		}
	}
	public static void main(String [] args) {
		System.out.print("What is the name of the config file? ");
		Scanner scan = new Scanner(System.in);
		String inputFilename = scan.nextLine();
		Config cg = new Config(inputFilename);
		while(cg.file() == false) {
			System.out.println("Configuration file " + inputFilename + " could not be found.");
			System.out.print("What is the name of the config file? ");
			inputFilename = scan.nextLine();
			cg = new Config(inputFilename);
		}
		if(!cg.valid()) {
			System.out.println("Missing parameters");
			return;
		}
		GameRoom gr = new GameRoom(cg);
	}
}
