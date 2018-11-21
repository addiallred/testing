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
	private boolean broad = false;
	private UserAction uaP;
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
	public void SendMessage() {
			if(broad) {
				Game temp = game();
				Game newG = new Game(temp.getGName(), temp.getCap(),getUser());
				newG.setArray(temp.getUsers());
				newG.setFull(temp.getFull());
				try {
					oos.reset();
					oos.writeObject(newG);
					oos.flush();
				}catch (IOException e) {
					System.out.println(e.getMessage());
					//System.out.println(e.getMessage());
				}
			}
	}public void SendGames(Game g) {
		try {
			oos.reset();
			oos.writeObject(g);
			oos.flush();
		}catch (IOException e) {
			System.out.println(e.getMessage());
			//System.out.println(e.getMessage());
		}
	}public void SendUser(UserAction ua) {
		try {
			oos.reset();
			oos.writeObject(ua);
			oos.flush();
		}catch (IOException e) {
			System.out.println(e.getMessage());
			//System.out.println(e.getMessage());
		}
	}
	public String name() {
		return uaP.getUsername();
	}public String gameName() {
		return uaP.getGameName();
	}public Game game() {
		return uaP.getGame();
	}public UserAction getUser() {
		return uaP;
	}
	public void run() {
	    
		while(true) {
			Date d = new Date();
		    SimpleDateFormat simpDate;
		    simpDate = new SimpleDateFormat("kk:mm:ss.SSS");
			
			
		try {//-add time stamp
			UserAction ua = null;
			ua = (UserAction)ois.readObject();
			System.out.println(ua.getAction());
			uaP = ua;
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
			}else if(ua.getAction().equals("currG")) {
				boolean exist = gr.newGame(ua);
				Game temp = null;
				if(exist) {
					temp = gr.getGame(ua);
					ua.setAction("exist");
					if(temp.getFull()) {
						ua.setAction("fullG");
					}else {
						ua.getGame().addUser(ua);
						if(temp.numPlayers() == temp.getCap()) {
							ua.getGame().setFull(true);
						}
						gr.broadcast(ua.getGame());
						broad = true;
					}
				}
				try {
					oos.writeObject(ua);
					oos.flush();
				} catch (IOException e) {
					System.out.println("error");
					//System.out.println(e.getMessage());
				}
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
				broad = true;
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
		}else if(ua.getAction().equals("wordM")) {
			String gW = gr.getWord();
			Game temp = ua.getGame();
			temp.setWord(gW);
			String code = "";
			for(int i = 0; i < gW.length(); i++) {
				code += "_ ";
			}
			temp.setCodeWord(code);
			gr.gameBroadcast(temp);
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
		}else if(ua.getAction().equals("gLM")) {
			System.out.println(ua.getGame().getWord());
			String letter = ua.getLetter();
			System.out.println(simpDate.format(d) + " " + ua.getGameName() + " " + ua.getUsername() + " - guessed letter " + ua.getLetter() );

			String action = ua.getUsername() + " has guessed the letter " + ua.getLetter() + "\n";
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
				action += "The letter '" + letter + "' is in the secret word";
				ua.getGame().setAction(action);
			}else {
				action += "The letter '" + letter + "' is not the secret word";
				ua.getGame().setAction(action);
				ua.setAction("gU");
				ua.setLives(ua.getLives()-1);
				Game game = ua.getGame();
				game.setLives(game.getLives()-1);
				System.out.println(simpDate.format(d) + " " +ua.getGameName() + " " + ua.getUsername() +" - " +  ua.getLetter() + " is not in " + ua.getWord() + ". " + ua.getGameName() + " now has " + ua.getLives() + " guesses remaining.");
			}
			gr.userBroadcast(ua);
			try {
				oos.reset();
				//oos.writeObject(ua);
				oos.flush();
			} catch (IOException e) {
				//System.out.println(e.getMessage());
			}
		}else if(ua.getAction().equals("gWordM")) {
			String word = ua.getLetter();
			String action = ua.getUsername() + " has guessed the letter " + ua.getLetter() + "\n";
			if(word.equals(ua.getWord())) {
				//fill in other names when working on multiplayer function
				System.out.println(simpDate.format(d) + " " + ua.getGameName() + " " + ua.getUsername() + " - " + ua.getLetter() + " is correct. " + ua.getUsername() + " wins the game. " + " <otherUsernames> have lost the game.");
				ua.setAction("gC");
				action += ua.getUsername() + " guessed the word correctly. You lose!";
				ua.getGame().setAction(action);
			}else {
				action += ua.getUsername() + " did not guess the word correctly." + ua.getUsername() + " lost!";
				System.out.println(simpDate.format(d) + " " + ua.getGameName() + " " + ua.getUsername() + " - " + ua.getLetter() + " is incorrect. " + ua.getUsername() + " has lost and is no longer in the game.");
				ua.setAction("gU");
				//do we reduce their life yes or no
			}try {
				oos.writeObject(ua);
				oos.flush();
			} catch (IOException e) {
				//System.out.println(e.getMessage());
			}
			gr.userBroadcast(ua);
		}
		else if(ua.getAction().equals("gWord")) {
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
