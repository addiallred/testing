package aallred_CSCI201_4Assignment;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
	public void playGame(UserAction ua, Game game) {
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
			System.out.println(e.getMessage());
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
			System.out.println("Secret word " + player.getCode());
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
						System.out.println("Invalid selection");
					}
				}
				catch(NumberFormatException e) {
					System.out.println("Invalid selection");
				}
			}
			if(userInput == 1) {
				boolean letterB = false;
				String letter = "";
				while(!letterB) {
					System.out.print("Letter to guess -");
					letter = scan.nextLine();
					if(letter.length() > 1) {
						System.out.println("Please only enter a letter");
						System.out.print("Letter to guess -");
						letter = scan.nextLine();
					}else {
						letterB = true;
					}
				}
				System.out.println(player.getWord());
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
					System.out.println("The letter '" + letter + "' is in the secret word");
					
				}else {
					System.out.println("The letter '" + letter + "' is not in the secret word.");
					if(player.getLives() == 0) {
						System.out.println("You did not guess the secret words within 7 lives!");
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
					System.out.println("That is correct! You win!");
					int wins = player.getWin() + 1;
					player.setWin(wins);
					player.setAction("upW");
					
				}else {
					System.out.println("That is not the secret word!");
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
		System.out.println(player.getUsername() + "'s Record");
		System.out.println("----------------------");
		System.out.println("Wins " + player.getWin());
		System.out.println("Losses " + player.getLose());
		System.out.println("Thank you for playing Hangman!");
	}
	public void run() {
		boolean logged = false;
		UserAction ua = null;
		while(!logged) {
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
					System.out.println("No account exists with those credentials.");
					System.out.println("Would you like to create an account with the given credintials?(yes/no)");
					String newA = scan2.nextLine();
					if(newA.toLowerCase().equals("yes")) {
						ua.setAction("ca");
						try {
							oos.writeObject(ua);
							oos.flush();
							
							System.out.println("in here");
							break;
						} catch (IOException e) {
							System.out.println(e.getMessage());
						}
					}
					else if(newA.toLowerCase().equals("no")){
						System.out.println("Enter different username and password.");
					}else {
						System.out.println("Incorrect selection");
					}
				}
			} catch (ClassNotFoundException e) {
				System.out.println(e.getMessage());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
		System.out.println("Great! You are now logged in as " + ua.getUsername());
		System.out.println(ua.getUsername() + "'s Record");
		System.out.println("----------------------");
		System.out.println("Wins " + ua.getWin());
		System.out.println("Losses " + ua.getLose());
		ua.setAction("newG");
		boolean notval = true;
		Scanner scan = new Scanner(System.in);
		int userInput = 0;
		while(notval) {
			System.out.println("1) Start a Game");
			System.out.println("2) Join a Game");
			System.out.println("Would you like to start a game or join a game?");
			
			try {
				userInput = Integer.parseInt(scan.nextLine());
				if(userInput == 1 || userInput == 2) {
					notval = false;
				}
				else {
					System.out.println("Invalid selection");
				}
			}
			catch(NumberFormatException e) {
				System.out.println("Invalid selection");
			}
		}
		if(userInput == 1) {
			Game nG = null;
			boolean valG = false;
			String gameName = "";
			while(!valG) {
				System.out.println("What is the name of the game?");
				gameName = scan.nextLine(); //for multiple player need to store this 
				//maybe have a game object
				ua.setAction("newG");
				ua.setGameName(gameName);
				UserAction newua = new UserAction("newG", ua.getUsername(), ua.getPassword());
				newua.setWin(ua.getWin());
				newua.setLose(ua.getLose());
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
					System.out.println("How many users will be playing (1-4)?");
					boolean validP = false;
					while(!validP) {
						try {
							numPlay = Integer.parseInt(scan.nextLine());
							if(numPlay >= 1 || numPlay <= 4) {
								validP = true;
							}
							else {
								System.out.println("Invalid selection");
							}
						}
						catch(NumberFormatException e) {
							System.out.println("Invalid selection");
						}
					}
					nG = new Game(gameName, numPlay, ua);
					ua.setGame(nG);
					ua.setGameName(gameName);
					try {
						oos.writeObject(ua);
						oos.flush();
					} catch (IOException e) {
						System.out.println(e.getMessage());
					}
					valG = true;
				}else {
					System.out.println(gameName + " already exist.");
				}
			}
			if(nG.getCap() == 1) {
				System.out.println("All users have joined.");
				playGame(ua, nG);
			}
		}
		else {
			//this is for multi player handle later,wants to join a game
		}
	}
	public static void main(String [] args) {
		System.out.print("What is the name of the config file? ");
		Scanner scan = new Scanner(System.in);
		String inputFilename = scan.nextLine();
		System.out.println("Reading config file...");
		Config cg = new Config(inputFilename);
		if(!cg.valid()) {
			System.out.println("Missing parameters");
			return;
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
