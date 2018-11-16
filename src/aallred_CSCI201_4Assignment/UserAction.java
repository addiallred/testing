package aallred_CSCI201_4Assignment;

import java.io.Serializable;

public class UserAction implements Serializable{
	public static final long serialVersionUID = 1;
	private String action;
	private String letter;
	private String username;
	private String password;
	private Game game = null;
	private int lives;
	private boolean alive = true;
	private String gameName;
	private int win;
	private int lose;
	private String userWord;
	public UserAction(String ac, String un, String pw) {
		action = ac;
		username = un;
		password = pw;
		lives = 7;
	}public String getLetter() {
		return letter;
	}public void setLetter(String message) {
		this.letter = message;
	}public int getLives(){
		return lives;
	}public void setLives(int lives) {
		this.lives = lives;
	}public String getAction() {
		return action;
	}public String getUsername() {
		return username;
	}public String getPassword() {
		return password;
	}public void setAction(String action) {
		this.action = action;
	}public int getWin() {
		return win;
	}public int getLose() {
		return lose;
	}public void setWin(int wins) {
		win = wins;
	}public void setLose(int loses) {
		lose = loses;
	}public void setGame(Game ga) {
		this.game = ga;
	}public void setGameName(String gN) {
		this.gameName = gN;
	}public String getGameName() {
		return this.gameName;
	}public int getNumP() {
		if(game != null) {
			return game.getCap();
		}else {
			return 0;
		}
	}public void setWord(String word) {
		game.setWord(word);
	}public String getWord() {
		return game.getWord();
	}public void setUserWord(String word) {
		game.setCodeWord(word);
	}public String getCode() {
		return game.getCodeWord();
	}public Game getGame() {
		return game;
	}public void setAlive(boolean alive){
		this.alive = alive;
	}public boolean getAlive() {
		return this.alive;
	}
}
