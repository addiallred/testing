package aallred_CSCI201_4Assignment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

public class Game implements Serializable{
	public static final long serialVersionUID = 2;
	private Vector<ServerThread> serverThreads = null;
	private String name;
	private int capacity;
	private String word;
	private String codeWord;
	private ArrayList<UserAction> players; 
	public Game(String name, int capacity, UserAction ua) {
		this.name = name;
		this.capacity = capacity;
		players = new ArrayList<UserAction>();
		players.add(ua);
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
	}
}
