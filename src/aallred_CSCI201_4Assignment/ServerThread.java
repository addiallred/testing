package aallred_CSCI201_4Assignment;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerThread extends Thread{
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private GameRoom gr;
	public ServerThread(Socket s, GameRoom gr) {
		try {
			this.gr = gr;
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			this.start();
		} catch (IOException ioe) {
			System.out.println("ioe in ServerThread constructor: " + ioe.getMessage());
		}
	}

	//public void sendMessage(String message) { - sending info to the game client
	
	
	public void run() {
	    
		while(true) {
			Date d = new Date();
		    SimpleDateFormat simpDate;
		    simpDate = new SimpleDateFormat("kk:mm:ss.SSS");
			
			
		try {//-add time stamp
			UserAction ua = null;
			ua = (UserAction)ois.readObject();
			if(ua.getAction().equals("data")) {
				System.out.println(simpDate.format(d) + " " + ua.getUsername() + " - trying to log in with password " + ua.getPassword() + ".");
				boolean result = gr.checkUser(ua);
				if(result) {
					ua.setAction("UR");
					System.out.println(simpDate.format(d) + " "  +ua.getUsername() + " - succeffully logged in.");
					System.out.println(simpDate.format(d) + " "  +ua.getUsername() + " - has record " + ua.getWin() + " wins and " + ua.getLose() + " losses");
				}else {
					boolean name = gr.userName(ua);
					if(name) {
						
						System.out.println(simpDate.format(d) + " " +ua.getUsername() +" - has an account but not successfully logged in.");
					}else {
						System.out.println(simpDate.format(d) + " " + ua.getUsername() + " - does not have an account so not successfully logged in");
						ua.setAction("UNR");
					}
				}
				try {
					oos.writeObject(ua);
					oos.flush();
				} catch (IOException e) {
					//System.out.println(e.getMessage());
				}
				
			}
			else if(ua.getAction().equals("ca")) {
				System.out.println(simpDate.format(d) + " " + ua.getUsername() +" - created an account with password " +ua.getPassword());
				System.out.println(simpDate.format(d) + " " + ua.getUsername() + " - has record 0 wins and 0 losses");
				gr.createAccount(ua);
			}
			else if(ua.getAction().equals("newG")) {
				System.out.println(simpDate.format(d) + " " + ua.getUsername() + " - wants to start a game called " + ua.getGameName() + ".");
				boolean exist = gr.newGame(ua);
				//this is not working figure out why
				if(exist) {
					System.out.println(simpDate.format(d) + " " + ua.getUsername() + " - " + ua.getGameName() + " already exist, so unable to start " + ua.getGameName() + ".");
					ua.setAction("notValid");
				}else {
					ua.setAction("numPlay");
				}
				try {
					oos.writeObject(ua);
					oos.flush();
				} catch (IOException e) {
					//System.out.println(e.getMessage());
				}
			}else if(ua.getAction().equals("addGame")) {
				System.out.println(simpDate.format(d) + " " + ua.getUsername() + " - successfully started game " + ua.getGameName() + ".");
				gr.createGame(ua);
			}else if(ua.getAction().equals("upW")) {
				gr.updateWins(ua);
			}else if(ua.getAction().equals("upL")) {
				gr.updateLoss(ua);
			}else if(ua.getAction().equals("word")) {
				String gW = gr.getWord();
				ua.setWord(gW);
				String code = "";
				for(int i = 0; i < gW.length(); i++) {
					code += "_ ";
				}
				ua.setUserWord(code);
				try {
					oos.writeObject(ua);
					oos.flush();
				} catch (IOException e) {
					//System.out.println(e.getMessage());
				}
		}else if(ua.getAction().equals("gL")) {
			String letter = ua.getLetter();
			System.out.println(simpDate.format(d) + " " + ua.getGameName() + " " + ua.getUsername() + " - guessed letter " + ua.getLetter() );
			if(ua.getWord().contains(letter)) {
				System.out.print(simpDate.format(d) + " " + ua.getGameName() + " " + ua.getUsername() + " - " + ua.getLetter() + " is in " + ua.getWord() + " in position(s)");
				String word = ua.getWord();
				String tcode = "";
				String codeW = ua.getCode();
				for(int i = 0; i < word.length(); i++) {
					String temp = "";
					int k = i + 1;
					temp += word.charAt(i);
					if(temp.equals(letter)) {
						System.out.print(" " + k);
						tcode += letter.toUpperCase() + " ";
					}else {
						tcode += codeW.charAt(i*2) + " ";
					}
				}
				ua.setAction("gC");
				ua.setUserWord(tcode);
				System.out.println(". Secret word now shows " + ua.getCode());
			}else {
				ua.setAction("gU");
				ua.setLives(ua.getLives()-1);
				System.out.println(simpDate.format(d) + " " +ua.getGameName() + " " + ua.getUsername() +" - " +  ua.getLetter() + " is not in " + ua.getWord() + ". " + ua.getGameName() + " now has " + ua.getLives() + " guesses remaining.");
			}
			try {
				oos.writeObject(ua);
				oos.flush();
			} catch (IOException e) {
				//System.out.println(e.getMessage());
			}
		}else if(ua.getAction().equals("gWord")) {
			System.out.println(simpDate.format(d) + " " + ua.getGameName() + " " + ua.getUsername() + " - guessed word " + ua.getLetter());
			String word = ua.getLetter();
			if(word.equals(ua.getWord())) {
				//fill in other names when working on multiplayer function
				System.out.println(simpDate.format(d) + " " + ua.getGameName() + " " + ua.getUsername() + " - " + ua.getLetter() + " is correct. " + ua.getUsername() + " wins the game. " + " <otherUsernames> have lost the game.");
				ua.setAction("gC");
			}else {
				System.out.println(simpDate.format(d) + " " + ua.getGameName() + " " + ua.getUsername() + " - " + ua.getLetter() + " is incorrect. " + ua.getUsername() + " has lost and is no longer in the game.");
				ua.setAction("gU");
			}
			try {
				oos.writeObject(ua);
				oos.flush();
			} catch (IOException e) {
				//System.out.println(e.getMessage());
			}
		}
		} catch (ClassNotFoundException e) {
			//System.out.println(e.getMessage());
			//e.printStackTrace();
		} catch (IOException e) {
			//System.out.println("error");
		}
		}
	}
}
