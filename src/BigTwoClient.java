import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;


public class BigTwoClient implements NetworkGame, CardGame {
	JTextArea incoming;
	JTextField outgoing;
	private int round;
	private Socket sock;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private int numOfPlayers;
	private ArrayList<CardGamePlayer> playerList;
	private ArrayList<Hand> handsOnTable;
	private int playerID;
	private Deck deck;
	private String playerName;
	private String serverIP;
	private int serverPort;
	private int currentIdx;
	private BigTwoTable table;
	private boolean keepon;
	public int lastplayer=0;
	public Hand thishand=null;
	
	/**
	 * Public constructor, creat a client. This will generate a GUI interface and connect to server
	 * 
	 */
	public BigTwoClient(){
		keepon=true;
		round=0;
		playerList=new ArrayList<CardGamePlayer>();
		for(int i=0; i<4; i++) playerList.add(new CardGamePlayer());
		table=new BigTwoTable(this);
		makeConnection();
	}
	
	public static void main(String[] args) {
		BigTwoClient client = new BigTwoClient();
	}
	
	/**
	 * @author Shen Si Yuan
	 *	Handle the server broadcast, phrase the message
	 */
	public class ServerHandler implements Runnable {
		public void run() {
			CardGameMessage message;
			try {
				while ((message = (CardGameMessage) ois.readObject()) != null) {
					System.out.println("Message received from the server");
					parseMessage(message);
				} 
			} catch (Exception ex) {
					ex.printStackTrace();
				}
		}
	}

	@Override
	public int getPlayerID() {
		return playerID;
	}
	@Override
	public void setPlayerID(int playerID) {
		this.playerID=playerID;
		
	}
	@Override
	public String getPlayerName() {
		return playerName;
	}
	@Override
	public void setPlayerName(String playerName) {
		this.playerName=playerName;
		
	}
	@Override
	public String getServerIP() {
		return serverIP;
	}
	@Override
	public void setServerIP(String serverIP) {
		this.serverIP=serverIP;
	}
	@Override
	public int getServerPort() {
		return serverPort;
	}
	@Override
	public void setServerPort(int serverPort) {
		this.serverPort=serverPort;
	}
	@Override
	public void makeConnection() {
		try {
			sock = new Socket(this.serverIP, this.serverPort);
			ois = new ObjectInputStream(sock.getInputStream());
			oos = new ObjectOutputStream(sock.getOutputStream());
			Thread handlerThread = new Thread(new ServerHandler());
			handlerThread.start();
			sendMessage(new CardGameMessage(CardGameMessage.JOIN, -1, this.getPlayerName()));
			sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	@Override
	public void parseMessage(GameMessage message) {
		// parses the message based on it type
		switch (message.getType()) {
		case CardGameMessage.FULL:
			table.msgprintln("Game is full, can not join the game.");
			break;
		case CardGameMessage.JOIN:
			// a player joins the game
			String name=(String) message.getData();
			if(name!=null) this.playerList.get(message.getPlayerID()).setName(name);
			break;
		case CardGameMessage.READY:
			// marks the specified player as ready for a new game
			name=playerList.get(message.getPlayerID()).getName();
			if(name!=null) table.msgprintln("Player "+name+" is ready.");
			table.resetPanel(true);
			break;
		case CardGameMessage.MOVE:
			// get the MOVE message from server
			checkMove(message.getPlayerID(),(int[]) message.getData());
			break;
		case CardGameMessage.PLAYER_LIST:
			this.playerID=message.getPlayerID();
			String[] names=(String[]) message.getData();
			for(int i=0; i<4; i++){
				if(names[i]!=null) this.playerList.get(i).setName(names[i]);
			}
			table.setActivePlayer(playerID);
			break;
		case CardGameMessage.MSG:
			// get messages from other players
			table.msgprintln((String) message.getData()); 
			break;
		case CardGameMessage.QUIT:
			// a player has disconnected
			playerList.get(message.getPlayerID()).setName("");
			table.msgprintln("Player "+message.getData()+" has quit.");
			if(keepon){
				table.pause();
			}
			sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
			break;
		case CardGameMessage.START:
			// game start
			this.start((BigTwoDeck) message.getData()); 
		default:
			System.out.println("Wrong message type: " + message.getType());
			// invalid message
			break;
		}
		
	}
	@Override
	public void sendMessage(GameMessage message) {
		try {
				oos.writeObject(message);
		} catch (Exception ex) {
			ex.printStackTrace();
		}	
	}
	@Override
	public int getNumOfPlayers() {
		// TODO Auto-generated method stub
		return this.numOfPlayers;
	}
	@Override
	public Deck getDeck() {
		// TODO Auto-generated method stub
		return this.deck;
	}
	@Override
	public ArrayList<CardGamePlayer> getPlayerList() {
		// TODO Auto-generated method stub
		return this.playerList;
	}
	@Override
	public ArrayList<Hand> getHandsOnTable() {
		// TODO Auto-generated method stub
		return this.handsOnTable;
	}
	@Override
	public int getCurrentIdx() {
		// TODO Auto-generated method stub
		return this.currentIdx;
	}
	@Override
	public void start(Deck deck) {
		this.deck=(BigTwoDeck) deck;
		round=0;				//Set round to 0
		keepon=true;			//Game keeps on
		handsOnTable=new ArrayList<Hand>();
		//Distribute cards to players and determine the starter
		for(int i=0; i<4; i++){
			playerList.get(i).removeAllCards();
			while(playerList.get(i).getNumOfCards()<13){
				if(deck.getCard(0).rank==2 && deck.getCard(0).suit==0){	//Three of diamond
					currentIdx=i;
				}
				playerList.get(i).addCard(deck.removeCard(0));
			}
		}
		for(int i=0; i<4; i++){
			playerList.get(i).sortCardsInHand();
		}
		table.reset();
		table.println("All players are ready. Game start!");
		table.println("Player "+playerList.get(currentIdx).getName()+"'s turn:");
		
	}
	@Override
	public void makeMove(int playerID, int[] cardIdx) {
		// TODO Auto-generated method stub
		CardGameMessage msg=new CardGameMessage(CardGameMessage.MOVE, -1, cardIdx);
		sendMessage(msg);
	}
	@Override
	public void checkMove(int playerID, int[] cardIdx) {
		if(playerID!=currentIdx) return;
		thishand=null;
		CardList c=playerList.get(playerID).getCardsInHand();
		CardList currentcards=new CardList();
		for(int i=0; i<cardIdx.length; i++){
			currentcards.addCard(c.getCard(cardIdx[i]));
		}
		
		//Did not pass
		if(!currentcards.isEmpty()){
			int size=currentcards.size();
			//Check hand type
			Hand currenthand=validType(currentcards, size, playerList.get(playerID));
			//Valid hand type
			if(currenthand!=null){
					thishand=currenthand;
					
					//TO DO Legal checking
					
					//If it is the first Round
					if(round==0){
						boolean contains=false;  //Contains three of diamond
						for(int i=0;i<currenthand.hand.size();i++){
							if(currenthand.hand.get(i).rank==2 && currenthand.hand.get(i).suit==0){
								contains=true;
								//Cannot have a winner
							}
						}
						if(contains){
							lastplayer=playerID;
							handsOnTable.add(currenthand);
							playerList.get(playerID).removeCards(currentcards);
							currentIdx=(playerID+1)%4;
							round++;
							table.print(playerList.get(playerID).getName()+": ");
							table.print("{"+thishand.getType()+"} ");
							for(int i=0; i<thishand.cardnum;i++){
								table.print("["+thishand.hand.get(i).toString()+"] ");
							}
							table.println("");
							table.println("Player "+playerList.get(currentIdx).getName()+"'s turn:");
							if(playerID==this.playerID)	table.resetPanel(true);
							else table.resetPanel(false);
							return;
						}
						else{
							if(playerID==this.playerID){
								table.print(playerList.get(playerID).getName()+": ");
								table.print("{"+thishand.getType()+"} ");
								for(int i=0; i<thishand.cardnum;i++){
									table.print("["+thishand.hand.get(i).toString()+"]");
								}
								table.println(" <==Not a legal move!");
							}
							return;
						}
					}
					
					//If it is not the first round
					
					//If all the other three player have passed, then as long as its a valid hand, its always legal
					else if(playerList.get(playerID)==handsOnTable.get(handsOnTable.size()-1).getPlayer()){
						lastplayer=playerID;
						handsOnTable.add(currenthand);
						playerList.get(playerID).removeCards(currentcards);
						round++;
						table.print(playerList.get(playerID).getName()+": ");
						table.print("{"+thishand.getType()+"} ");
						for(int i=0; i<thishand.cardnum;i++){
							table.print("["+thishand.hand.get(i).toString()+"] ");
						}
						table.println("");
						//Winner checking
						if(endOfGame()){
							table.showWinningInfo();
							keepon=false;
							table.resetPanel(true);
							return;
						}
						else{
							currentIdx=(currentIdx+1)%4;
							table.println("Player "+playerList.get(currentIdx).getName()+"'s turn:");
							if(playerID==this.playerID)	table.resetPanel(true);
							else table.resetPanel(false);
							return;
						}
					}
					
					//Otherwise
					//@param handrank mismatch or cannot beats the last hand will result in illegal move
					else if(currenthand.beats(handsOnTable.get(handsOnTable.size()-1))){
						lastplayer=currentIdx;
						handsOnTable.add(currenthand);
						playerList.get(currentIdx).removeCards(currentcards);
						round++;
						table.print(playerList.get(playerID).getName()+": ");
						table.print("{"+thishand.getType()+"} ");
						for(int i=0; i<thishand.cardnum;i++){
							table.print("["+thishand.hand.get(i).toString()+"] ");
						}
						table.println("");
						//Winner checking
						if(endOfGame()){
							table.showWinningInfo();
							keepon=false;
							table.resetPanel(true);
							return;
						}
						else{
							currentIdx=(currentIdx+1)%4;
							table.println("Player "+playerList.get(currentIdx).getName()+"'s turn:");
							if(playerID==this.playerID)	table.resetPanel(true);
							else table.resetPanel(false);
							return;
						}
					}
					
					//Valid type but did not beats the last hand
					else{
						if(playerID==this.playerID){
							table.print(playerList.get(playerID).getName()+": ");
							table.print("{"+thishand.getType()+"} ");
							for(int i=0; i<thishand.cardnum;i++){
								table.print("["+thishand.hand.get(i).toString()+"]");
							}
							table.println(" <==Not a legal move!");
						}
						return;
					}
			}
			//Invalid hand type
			else{
				if(playerID==this.playerID){
					table.println(playerList.get(playerID).getName()+": Not a legal move!");
				}
				thishand=null;
				return;
			}
		
		//Pass
		}
		else{
			//If this is not the first round, and current player is not the one who played the last hand
			if(round!=0 && playerList.get(currentIdx)!=handsOnTable.get(handsOnTable.size()-1).getPlayer()){
				currentIdx=(currentIdx+1)%4;
				round++;
				table.println(playerList.get(playerID).getName()+": {Pass}");
				table.println("Player "+playerList.get(currentIdx).getName()+"'s turn:");
				if(playerID==this.playerID)	table.resetPanel(true);
				else table.resetPanel(false);
				return;
			}
			//Otherwise its not a legal move
			else{
				if(playerID==this.playerID){
					table.println(playerList.get(playerID).getName()+": {Pass} <== Not a legal move!");
				}
				return;
			}
		}
		
	}
	
	/**
	 * Determine if it is a valid hand
	 * @param currentcards current hand
	 * @param size number of cards in this hand
	 * @param player current player
	 * @return hand played by this player, null if it is not a valid hand
	 */
	private Hand validType(CardList currentcards, int size, CardGamePlayer player){
		if(size==1){
			Hand currenthand=new Single(player,currentcards);
			if(currenthand.isValid()) return currenthand;
			return null;
		}
		else if(size==2){
			Hand currenthand=new Pair(player,currentcards);
			if(currenthand.isValid()) return currenthand;
			return null;
		}
		else if(size==3){
			Hand currenthand=new Triple(player,currentcards);
			if(currenthand.isValid()) return currenthand;
			return null;
		}
		else if(size==5){
			//If statement is from the highest hand rank to the lowest, in order to avoid problems 
			Hand currenthand=new StraightFlush(player,currentcards);
			if(currenthand.isValid()) return currenthand;
			currenthand=new Quad(player,currentcards);
			if(currenthand.isValid()) return currenthand;
			currenthand=new FullHouse(player,currentcards);
			if(currenthand.isValid()) return currenthand;
			currenthand=new Flush(player,currentcards);
			if(currenthand.isValid()) return currenthand;
			currenthand=new Straight(player,currentcards);
			if(currenthand.isValid()) return currenthand;
			return null;
		}
		return null;
	}
	
	@Override
	public boolean isPlaying() {
		// TODO Auto-generated method stub
		return keepon;
	}
	@Override
	public boolean endOfGame() {
		if(playerList.get(currentIdx).getNumOfCards()==0 && round!=0) return true;
		return false;
	}
}
