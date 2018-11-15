package aallred_CSCI201_4Assignment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

public class Game implements Serializable{
	public static final long serialVersionUID = 2;
	private Vector<ServerThread> serverThreads = null;
	private String name;
	private boolean full = false;
	private int capacity;
	private int lives;
	private int curr;
	private String word;
	private String codeWord;
	private ArrayList<UserAction> players; 
	public Game(String name, int capacity, UserAction ua) {
		this.name = name;
		this.capacity = capacity;
		players = new ArrayList<UserAction>();
		players.add(ua);
		if(capacity == 1) {
			this.full = true;
		}
		curr = 0;
		lives = 7;
	}public int getCap() {
		return capacity;
	}public ArrayList<UserAction> getUsers(){
		return players;
	}public String getGName() {
		return name;
	}public String getWord() {
		return word;
	}public void setWord(String word) {
		this.word = word;
	}public void setCodeWord(String word) {
		this.codeWord = word;
	}public String getCodeWord() {
		return codeWord;
	}public void addUser(UserAction ua) {
		players.add(ua);
	}public int numPlayers() {
		return players.size();
	}public void addThread(ServerThread st) {
		if(serverThreads == null) {
			serverThreads = new Vector<ServerThread>();
		}
		serverThreads.add(st);
	}public Vector<ServerThread> getThreads(){
		return serverThreads;
	}public void setFull(boolean full) {
		this.full = full;
	}public boolean getFull() {
		return this.full;
	}public void setArray(ArrayList<UserAction> ua) {
		this.players = ua;
	}public UserAction getCurrPlay() {
		return this.players.get(curr);
	}public void setCurr(int tc) {
		this.curr = tc;
	}public int getCurr() {
		return this.curr;
	}public void setLives(int lives) {
		this.lives = lives;
	}public int getLives() {
		return this.lives;
	}
}
