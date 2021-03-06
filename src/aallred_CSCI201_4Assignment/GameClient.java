package aallred_CSCI201_4Assignment;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class GameClient extends Thread{
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	public GameClient(String hostname, int port) {
		try {
			System.out.print("Trying to connect to server ..." );
			Socket s = new Socket(hostname, port);
			System.out.println("Connected!");
			ois = new ObjectInputStream(s.getInputStream());
			oos = new ObjectOutputStream(s.getOutputStream());
			this.start();
			
		} catch (IOException ioe) {
			System.out.println("Unable to connect to server " + hostname + " on port " + port);
		}
	}
	public void multiPlayer(UserAction ua, Game game) {
		System.out.println();
		System.out.println("Determining secret word...");
		if(ua.getUsername().equals(game.getCurrPlay().getUsername())) {
			ua.setAction("wordM");
			try {
				oos.reset();
				ua.setGame(game);
				oos.writeObject(ua);
				oos.flush();
			}catch(IOException e) {
				//System.out.println(e.getMessage());
			}
		}
		try {
			game = (Game)ois.readObject();
		}catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		boolean play = true;
		while(game.getLives() > 0 && game.getWin() && game.active() > 0) {
			System.out.println();
			System.out.println("Secret word " + game.getCodeWord());
			System.out.println();
			System.out.println("You have " + game.getLives() + " incorrect guesses remaining.");
			if(ua.getUsername().equals(game.getCurrPlay().getUsername())) {
				boolean valid = false;
				int userInput = 0;
				Scanner scan = new Scanner(System.in);
				while(!valid) {
					System.out.println("\t1) Guess a letter");
					System.out.println("\t2) Guess a word");
					System.out.print("What would you like to do?");
					String input = scan.nextLine();
					try {
						userInput = Integer.parseInt(input);
						if(userInput == 1 || userInput == 2) {
							valid = true;
						}
						else {
							System.out.println();
							System.out.println("Invalid selection");
						}
					}
					catch(NumberFormatException e) {
						System.out.println();
						System.out.println("Invalid selection");
					}
				}
				if(userInput == 1) {
					boolean letterB = false;
					String letter = "";
					while(!letterB) {
						System.out.println();
						System.out.print("Letter to guess -");
						letter = scan.nextLine();
						if(letter.length() > 1) {
							System.out.println();
							System.out.println("Please only enter a letter");
						}else {
							letterB = true;
						}
					}
					ua.setLetter(letter);
					ua.setAction("gLM");
					boolean gotP = true;
					while(gotP) {
						if(game.getCurr() + 1 == game.numPlayers()) {
							game.setCurr(0);
							if(game.getCurrPlay().getAlive()) {
								gotP = false;
							}
						}else {
							game.setCurr(game.getCurr() + 1);
							if(game.getCurrPlay().getAlive()) {
								gotP = false;
							}
						}
					}
					ua.setGame(game);
					try {
						oos.reset();
						oos.writeObject(ua);
						oos.flush();
					} catch (IOException e) {
						System.out.println(e.getMessage());
					}try {
						ua = (UserAction)ois.readObject();
					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						//e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						//e1.printStackTrace();
					}
					if(ua.getAction().equals("gC")) {
						System.out.println();
						System.out.println("The letter '" + letter + "' is in the secret word");
						
					}else {
						System.out.println();
						System.out.println("The letter/character '" + letter + "' is not in the secret word.");
						
					}
				}else {
					System.out.println();
					System.out.print("What is the secret word?");
					String sword = scan.nextLine();
					ua.setLetter(sword);
					ua.setAction("gWordM");
					ua.setAlive(false);
					game.updateArray(ua);
					boolean gotP = true;
					int aliveC = 0;
					while(gotP && aliveC < game.numPlayers()) {
						aliveC++;
						if(game.getCurr() + 1 == game.numPlayers()) {
							game.setCurr(0);
							if(game.getCurrPlay().getAlive()) {
								gotP = false;
							}
						}else {
							game.setCurr(game.getCurr() + 1);
							if(game.getCurrPlay().getAlive()) {
								gotP = false;
							}
						}
					}
					ua.setGame(game);
					try {
						oos.reset();
						oos.writeObject(ua);
						oos.flush();
					} catch (IOException e) {
						System.out.println(e.getMessage());
					}try {
						ua = (UserAction)ois.readObject();
					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						//e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						//e1.printStackTrace();
					}
					if(ua.getAction().equals("gC")) {
						System.out.println();
						System.out.println("That is correct! You win!");
						int wins = ua.getWin() + 1;
						ua.setWin(wins);
						ua.setAction("upW");
						ua.getGame().setWin(false);
					}else {
						System.out.println();
						System.out.println("That is not the secret word! You lose and are no longer in the game!");
						System.out.println();
						System.out.println("The secret word was: " + game.getWord());
						int lost = ua.getLose() + 1;
						ua.setLose(lost);
						ua.setAction("upL");
					}
					try {
						oos.writeObject(ua);
						oos.flush();
					} catch (IOException e) {
						System.out.println(e.getMessage());
					}
					ua.setAlive(false);
					//if the user guesses the word and it is false does it reduce the game life total
				}
				game = ua.getGame();
			}else {
				UserAction otherUser = null;
				System.out.println();
				System.out.println("Waiting for " + game.getCurrPlay().getUsername() + " to do something...");
					try {
						otherUser = (UserAction)ois.readObject();
					}catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
				game = otherUser.getGame();
				System.out.println(game.getAction());
				ua.setGame(game);
			}
		}
		if(ua.getAlive()) {
			int lost = ua.getLose() + 1;
			ua.setLose(lost);
			ua.setAction("upL");
			try {
				oos.reset();
				oos.writeObject(ua);
				oos.flush();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
		System.out.println();
			System.out.println(ua.getUsername() + "'s Record");
			System.out.println("----------------------");
			System.out.println("Wins " + ua.getWin());
			System.out.println("Losses " + ua.getLose());
			for(int i = 0; i < game.numPlayers(); i++) {
				System.out.println();
				UserAction temp = game.getUsers().get(i);
				if(!temp.getUsername().equals(ua.getUsername())) {
					System.out.println(temp.getUsername() + "'s Record");
					System.out.println("----------------------");
					if(temp.getUsername().equals(game.getWinner())) {
						int wins = temp.getWin() + 1;
						System.out.println("Wins " + wins);
						System.out.println("Losses " + temp.getLose());
					}else {
						int lose = temp.getLose() + 1;
						System.out.println("Wins " + temp.getWin());
						System.out.println("Losses " + lose);
					}
				}
			}
			System.out.println();
			System.out.println("Thank you for playing Hangman!");
			ua.setAction("remG");
			try {
				oos.reset();
				oos.writeObject(ua);
				oos.flush();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
	}
	//this is single player
	public void playGame(UserAction ua, Game game) {
		System.out.println();
		System.out.println("Determining secret word...");
		ua.setAction("word");
		UserAction player = new UserAction("word", ua.getUsername(), ua.getPassword());
		player.setGame(game);
		player.setGameName(game.getGName());
		player.setLose(ua.getLose());
		player.setWin(ua.getWin());
		try {
			oos.writeObject(player);
			oos.flush();
		} catch (IOException e) {
			//System.out.println(e.getMessage());
		}try {
			player = (UserAction)ois.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		boolean play = true;
		while(player.getLives() > 0 && play) {
			System.out.println();
			System.out.println("Secret word " + player.getCode());
			System.out.println();
			System.out.println("You have " + player.getLives() + " incorrect guesses remaining.");
			boolean valid = false;
			int userInput = 0;
			Scanner scan = new Scanner(System.in);
			while(!valid) {
				System.out.println("\t1) Guess a letter");
				System.out.println("\t2) Guess a word");
				System.out.print("What would you like to do?");
				String input = scan.nextLine();
				try {
					userInput = Integer.parseInt(input);
					if(userInput == 1 || userInput == 2) {
						valid = true;
					}
					else {
						System.out.println();
						System.out.println("Invalid selection");
					}
				}
				catch(NumberFormatException e) {
					System.out.println();
					System.out.println("Invalid selection");
				}
			}
			if(userInput == 1) {
				boolean letterB = false;
				String letter = "";
				while(!letterB) {
					System.out.println();
					System.out.print("Letter to guess -");
					letter = scan.nextLine();
					if(letter.length() > 1) {
						System.out.println();
						System.out.println("Please only enter a letter/single character");
					}else {
						letterB = true;
					}
				}
				player.setLetter(letter);
				player.setAction("gL");
				try {
					UserAction pass = new UserAction("gL", player.getUsername(), player.getPassword());
					pass.setGameName(player.getGameName());
					pass.setLetter(letter);
					pass.setGame(player.getGame());
					pass.setWord(player.getWord());
					pass.setUserWord(player.getCode());
					pass.setWin(player.getWin());
					pass.setLose(player.getLose());
					pass.setLives(player.getLives());
					oos.writeObject(pass);
					oos.flush();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}try {
					player = (UserAction)ois.readObject();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					//e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					//e1.printStackTrace();
				}
				if(player.getAction().equals("gC")) {
					System.out.println();
					System.out.println("The letter '" + letter + "' is in the secret word");
					
				}else {
					System.out.println();
					System.out.println("The letter/character '" + letter + "' is not in the secret word.");
					if(player.getLives() == 0) {
						System.out.println();
						System.out.println("You did not guess the secret words within 7 lives!");
						System.out.println();
						System.out.println("The secret word was: " + player.getWord());
						int lost = player.getLose() + 1;
						player.setLose(lost);
						player.setAction("upL");
						try {
							oos.writeObject(player);
							oos.flush();
						} catch (IOException e) {
							System.out.println(e.getMessage());
						}
					}
				}
			}if(userInput == 2) {
				System.out.println();
				System.out.print("What is the secret word?");
				String sword = scan.nextLine();
				player.setLetter(sword);
				player.setAction("gWord");
				try {
					UserAction pass = new UserAction("gWord", player.getUsername(), player.getPassword());
					pass.setGameName(player.getGameName());
					pass.setLetter(sword);
					pass.setGame(player.getGame());
					pass.setWord(player.getWord());
					pass.setUserWord(player.getCode());
					pass.setWin(player.getWin());
					pass.setLose(player.getLose());
					pass.setLives(player.getLives());
					oos.writeObject(pass);
					oos.flush();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}try {
					player = (UserAction)ois.readObject();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					//e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					//e1.printStackTrace();
				}
				if(player.getAction().equals("gC")) {
					System.out.println();
					System.out.println("That is correct! You win!");
					int wins = player.getWin() + 1;
					player.setWin(wins);
					player.setAction("upW");
					
				}else {
					System.out.println();
					System.out.println("That is not the secret word!");
					System.out.println();
					System.out.println("The secret word was: " + player.getWord());
					int lost = player.getLose() + 1;
					player.setLose(lost);
					player.setAction("upL");
				}
				try {
					oos.writeObject(player);
					oos.flush();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
				play = false;
			}
		}
		System.out.println();
		System.out.println(player.getUsername() + "'s Record");
		System.out.println("----------------------");
		System.out.println("Wins " + player.getWin());
		System.out.println("Losses " + player.getLose());
		System.out.println();
		System.out.println("Thank you for playing Hangman!");
		player.setAction("remG");
		try {
			oos.reset();
			oos.writeObject(player);
			oos.flush();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	public void run() {
		boolean logged = false;
		UserAction ua = null;
		while(!logged) {
			System.out.println();
			System.out.print("Username:");
			Scanner scan2 = new Scanner(System.in);
			String uname = scan2.nextLine();
			System.out.print("Password:");
			String password = scan2.nextLine();
			ua = new UserAction("data", uname, password);
			try {
				oos.writeObject(ua);
				oos.flush();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			try {
				ua = (UserAction)ois.readObject();
				if(ua.getAction().equals("UR")) {
					logged = true;
				}else if(ua.getAction().equals("UNR")) {
					System.out.println();
					System.out.println("No account exists with those credentials.");
					System.out.println("Would you like to create an account with the given credintials?(yes/no)");
					String newA = scan2.nextLine();
					if(newA.toLowerCase().equals("yes")) {
						ua.setAction("ca");
						try {
							oos.reset();
							oos.writeObject(ua);
							oos.flush();
							
						
							break;
						} catch (IOException e) {
							System.out.println(e.getMessage());
						}
					}else if(newA.toLowerCase().equals("no")){
						System.out.println();
						System.out.println("Enter different username and password.");
					}
					else{
						boolean inpt = false;
						while(!inpt) {
							System.out.println();
							System.out.println("Incorrect selection");
							System.out.println("Would you like to create an account with the given credintials?(yes/no)");
							newA = scan2.nextLine();
							if(newA.toLowerCase().equals("yes")) {
								ua.setAction("ca");
								try {
									oos.writeObject(ua);
									oos.flush();
									logged = true;
									break;
								} catch (IOException e) {
									System.out.println(e.getMessage());
								}
								inpt = true;
							}
							else if(newA.toLowerCase().equals("no")){
								inpt = true;
								System.out.println();
								System.out.println("Enter different username and password.");
							}
						}
					}
				}else {
					System.out.println();
					System.out.println("Username is already in use. Please enter different credentials for username and/or password.");
				}
			} catch (ClassNotFoundException e) {
				//System.out.println(e.getMessage());
			} catch (IOException e) {
				//System.out.println(e.getMessage());
			}
		}
		System.out.println();
		System.out.println("Great! You are now logged in as " + ua.getUsername());
		System.out.println();
		System.out.println(ua.getUsername() + "'s Record");
		System.out.println("----------------------");
		System.out.println("Wins " + ua.getWin());
		System.out.println("Losses " + ua.getLose());
		ua.setAction("newG");
		boolean notval = true;
		Scanner scan = new Scanner(System.in);
		int userInput = 0;
		while(notval) {
			System.out.println();
			System.out.println("1) Start a Game");
			System.out.println("2) Join a Game");
			System.out.print("Would you like to start a game or join a game?");
			
			try {
				userInput = Integer.parseInt(scan.nextLine());
				if(userInput == 1 || userInput == 2) {
					notval = false;
				}
				else {
					System.out.println();
					System.out.println("Invalid selection");
				}
			}
			catch(NumberFormatException e) {
				System.out.println();
				System.out.println("Invalid selection");
			}
		}
		Game nG = null;
		if(userInput == 1) {
			boolean valG = false;
			String gameName = "";
			while(!valG) {
				System.out.println();
				System.out.print("What is the name of the game?");
				gameName = scan.nextLine(); //for multiple player need to store this 
				//maybe have a game object
				gameName = gameName.toLowerCase();
				ua.setAction("newG");
				ua.setGameName(gameName);
				UserAction newua = new UserAction("newG", ua.getUsername(), ua.getPassword());
				newua.setWin(ua.getWin());
				newua.setLose(ua.getLose());
				newua.setGameName(gameName);
				try {
					oos.writeObject(newua);
					oos.flush();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
				try {
					ua = (UserAction)ois.readObject();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}if(ua.getAction().equals("numPlay")) {
					int numPlay = 0;
					System.out.println();
					System.out.print("How many users will be playing (1-4)?");
					boolean validP = false;
					while(!validP) {
						try {
							numPlay = Integer.parseInt(scan.nextLine());
							if(numPlay >= 1 || numPlay <= 4) {
								validP = true;
							}
							else {
								System.out.println();
								System.out.println("A game can only have between 1-4 players.");
								System.out.print("How many users will be playing (1-4)?");
							}
						}
						catch(NumberFormatException e) {
							System.out.println();
							System.out.println("Invalid selection");
							System.out.print("How many users will be playing (1-4)?");
						}
					}
					nG = new Game(gameName, numPlay, ua);
					ua.setGame(nG);
					ua.setGameName(gameName);
					ua.setAction("addGame");
					try {
						oos.writeObject(ua);
						oos.flush();
					} catch (IOException e) {
						System.out.println(e.getMessage());
					}
					valG = true;
				}else {
					System.out.println();
					System.out.println(gameName + " already exist.");
				}
			}
		}
		else {
			boolean validG = false;
			while(!validG) {
				System.out.println();
				System.out.print("What is the name of the game?");
				String game = scan.nextLine();
				game = game.toLowerCase();
				UserAction newua = new UserAction("currG", ua.getUsername(), ua.getPassword());
				newua.setWin(ua.getWin());
				newua.setLose(ua.getLose());
				newua.setGameName(game);
				try {
					oos.reset();
					oos.writeObject(newua);
					oos.flush();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
				try {
					ua = (UserAction)ois.readObject();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				if(ua.getAction().equals("fullG")) {
					System.out.println();
					System.out.println("The game " + game + " does not have space for another user to join.");
				}
				else if(ua.getGame() == null) {
					System.out.println();
					System.out.println("Game " + game + " does not exist. Please enter a game name that exist.");
				}else {
					validG = true;
					nG = ua.getGame();
					ArrayList<UserAction> us = nG.getUsers();
					for(int i = 0; i < us.size(); i++) {
						System.out.println();
						UserAction temp = us.get(i);
						if(!temp.getUsername().equals(ua.getUsername())) {
							System.out.println("User " + temp.getUsername() + " is in the game");
							System.out.println(temp.getUsername() + "'s Record");
							System.out.println("----------------------");
							System.out.println("Wins " + temp.getWin());
							System.out.println("Losses " + temp.getLose());
						}
					}
				}
			}
		}
		if(nG.getCap() == 1) {
			System.out.println();
			System.out.println("All users have joined.");
			playGame(ua, nG);
		}else if(nG.getCap() == nG.numPlayers()){
			System.out.println();
			System.out.println("All users have joined.");
			multiPlayer(ua, nG);
		}
		else {
			int wait = nG.getCap() - nG.numPlayers();
			if(wait == 1) {
				System.out.println();
				System.out.println("Waiting for " + wait + " other user to join...");
			}else {
				System.out.println();
				System.out.println("Waiting for " + wait + " other users to join...");
			}while(nG.getCap() != nG.numPlayers()) {
				nG = null;
				try {
					Game temp = (Game)ois.readObject();
					nG = temp;
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}System.out.println();
				UserAction temp2 = nG.getUsers().get(nG.numPlayers()-1);
				System.out.println("User " + temp2.getUsername() + " is in the game");
				System.out.println(temp2.getUsername() + "'s Record");
				System.out.println("----------------------");
				System.out.println("Wins " + temp2.getWin());
				System.out.println("Losses " + temp2.getLose());
				wait = nG.getCap() - nG.numPlayers();
				if(wait == 1) {
					System.out.println();
					System.out.println("Waiting for " + wait + " other user to join...");
				}else if(wait > 1){
					System.out.println();
					System.out.println("Waiting for " + wait + "other users to join...");
				}else {
					System.out.println();
					System.out.println("All users have joined");
					multiPlayer(ua, nG);
				}
			}
		}
	}
	public static void main(String [] args) {
		System.out.print("What is the name of the config file? ");
		Scanner scan = new Scanner(System.in);
		String inputFilename = scan.nextLine();
		System.out.println("Reading config file...");
		Config cg = new Config(inputFilename);
		while(cg.file() == false) {
			System.out.println("Configuration file " + inputFilename + " could not be found.");
			System.out.print("What is the name of the config file? ");
			inputFilename = scan.nextLine();
			cg = new Config(inputFilename);
		}
		if(!cg.valid()) {
			System.out.println("Missing parameter(s):");
			if(cg.getDBC() == null) {
				System.out.println("- Database Connection String");
			}if(cg.getDBP() == null) {
				System.out.println("- Database Password");
			}if(cg.getDBU() == null) {
				System.out.println("- Database Username");
			}if(cg.getHostName() == null) {
				System.out.println("- Server Hostname");
			}if(cg.getPort() == -1) {
				System.out.println("- Server Port");
			}if(cg.getWordFile() == null) {
				System.out.println("- Secret Word File");
			}
			return;
		}
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(cg.getWordFile());
			br = new BufferedReader(fr);
			String tword = "";
			tword = br.readLine();
		}catch(FileNotFoundException ioe) {
			System.out.println("Seceret word file " +  cg.getWordFile() + " does not exist.");
			return;
		}catch(IOException ioe) {
			System.out.println("Problem reading the word file");
			return;
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
		System.out.println("Server Hostname - " + cg.getHostName());
		System.out.println("Server Port - " + cg.getPort());
		System.out.println("Database Connection String - " + cg.getDBC());
		System.out.println("Database Username - " + cg.getDBU());
		System.out.println("Database Password - " + cg.getDBP());
		System.out.println("Secrect Word File - " + cg.getWordFile());
		GameClient cc = new GameClient(cg.getHostName(), cg.getPort());
		
	}
}
