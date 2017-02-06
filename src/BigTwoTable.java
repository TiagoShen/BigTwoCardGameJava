import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

public class BigTwoTable implements CardGameTable{
	private BigTwoClient client;
	private boolean[] selected;
	private int activePlayer;
	private int activeSelection;
	private JFrame frame=null;
	private JPanel bigTwoPanel;
	private JButton playButton;
	private JButton passButton;
	private JTextArea textArea;
	private JTextArea incoming;
	private Image[][] cardImages;
	private Image cardBackImage;
	private BufferedImage background;
	private Image dimg;
	private Image[] avatars;
	private boolean pause;
	private Hand lasthand=null;	//Store the last hand on table
	
	/**
	 * Build the game GUI for the client
	 * @param client the local player
	 */
	BigTwoTable(BigTwoClient client){
		this.client=client;
		//Update game information
		if(client.getHandsOnTable()!=null){
			lasthand=client.getHandsOnTable().isEmpty()? null : client.getHandsOnTable().get((client.getHandsOnTable().size()-1));
		}
		else lasthand=null;
		selected=new boolean[13];
		for(int i=0; i<selected.length; i++) selected[i]=false;
		
		//Load images
	       try {                
	           background = ImageIO.read(new File("src\\city.jpg"));
	        } catch (IOException ex) {
	             ex.printStackTrace();// handle exception...
	        }
		cardBackImage=new ImageIcon("src\\zootopia.jpg").getImage();
		avatars=new Image[4];
		avatars[0]=new ImageIcon("src\\Nick.png").getImage();
		avatars[1]=new ImageIcon("src\\Judy.png").getImage();
		avatars[2]=new ImageIcon("src\\Bogo.png").getImage();
		avatars[3]=new ImageIcon("src\\Lionheart.png").getImage();
		cardImages=new Image[13][4];
		for(int suit=0; suit<4; suit++){
			for(int rank=0; rank<13; rank++){
				switch(suit){
				case 0:	//diamond
					cardImages[rank][suit]=new ImageIcon("src\\"+(rank+1)+"d.gif").getImage();
					break;
				case 1: //club
					cardImages[rank][suit]=new ImageIcon("src\\"+(rank+1)+"c.gif").getImage();
					break;
				case 2: //heart
					cardImages[rank][suit]=new ImageIcon("src\\"+(rank+1)+"h.gif").getImage();
					break;
				case 3: //spade
					cardImages[rank][suit]=new ImageIcon("src\\"+(rank+1)+"s.gif").getImage();
					break;
				}
			}
		}
		this.buildGUI();
	}
	
	public void reset(){
		
		//Create a new game, update game info

		activePlayer=client.getPlayerID();
		if(client.getHandsOnTable()!=null){
			lasthand=client.getHandsOnTable().isEmpty()? null : client.getHandsOnTable().get((client.getHandsOnTable().size()-1));
		}
		else lasthand=null;
		selected=new boolean[13];
		for(int i=0; i<selected.length; i++) selected[i]=false;
		
		((BigTwoPanel) bigTwoPanel).reset();
		textArea.setText(null);
	}
	
	/**
	 * show the winning dialog
	 */
	public void showWinningInfo(){
		Object[] options={"Play again","Quit"};
		int option=JOptionPane.showOptionDialog(null, "Player "+client.getPlayerList().get(client.getCurrentIdx()).getName()+" wins!", "Option", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if(option==JOptionPane.YES_OPTION)
			client.sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
		else System.exit(0);
	}
	/**
	 * Pause the game and disable user interactions
	 */
	public void pause(){
		this.resetPanel(true);
		pause=true;
	}
	class CardPanel extends JPanel{
		boolean show;
		int index;
		int player;
		int rank;
		int suit;
		int position;
		CardPanel(boolean show,int index,int player, int rank, int suit){
			this.position=0;
			this.show=show;
			this.player=player;
			this.index=index;
			this.rank=rank;
			this.suit=suit;
			this.setSize(73, 97);
		}
		public void paintComponent(Graphics g){
			if(this.show)
				g.drawImage(cardImages[rank][suit], 0, 0, this);
			else
				g.drawImage(cardBackImage, 0, 0, this);
		}
	}
	
	/**
	 * @ Shen Si Yuan
	 *	The main game table, it shows the cards
	 */
	class BigTwoPanel extends JPanel implements MouseListener{
		public void paintComponent(Graphics g){
			g.drawImage(dimg, 0, 0, null);
			//Draw borderline
			g.setColor(Color.BLACK);
			for(int i=0; i<5; i++){
				g.drawLine(0, (1+i)*this.getHeight()/5, this.getWidth(), (1+i)*this.getHeight()/5);
			}
			
			//Draw player name and icon
			Font font=new Font("Serif", Font.BOLD, 15);
			g.setFont(font);
			g.setColor(Color.BLACK);
			for(int i=0; i<4; i++){
				if(i==client.getCurrentIdx()) g.setColor(Color.BLUE);
				g.drawString(client.getPlayerList().get(i).getName(), 5, (1+i)*this.getHeight()/5-140);
				g.setColor(Color.BLACK);
				
				g.drawImage(avatars[i], 5, (1+i)*this.getHeight()/5-130, this);
			}

			//Draw hand on table
			if(client.getHandsOnTable()!=null){
				if(!client.getHandsOnTable().isEmpty()){
					Hand h=client.getHandsOnTable().get(client.getHandsOnTable().size()-1);
					g.drawString("Played by "+h.getPlayer().getName(), 5, this.getHeight()-140);
					for(int k=0; k<h.cardnum; k++){
						g.drawImage(cardImages[h.hand.get(k).rank][h.hand.get(k).suit], 20*k+20, this.getHeight()-120, this);
					}
				}
			}
		}
		//BigTwoPanel mouse event
		public void mouseClicked(MouseEvent e){
			if(pause) return;
			if(e.getSource() instanceof CardPanel){
				CardPanel thiscard=(CardPanel) e.getSource();
				Point p=thiscard.getLocation();
				if(thiscard.player==client.getPlayerID()){
					if(thiscard.position==0){
						p.y-=20;
						thiscard.setLocation(p);
						thiscard.position=1;
						selected[thiscard.index]=true;
					}
					else{
						p.y+=20;
						thiscard.setLocation(p);
						thiscard.position=0;
						selected[thiscard.index]=false;
					}
				}
			}	
		}
		
		
		/**
		 * This method provide function for resetting the GUI, it also handle the GUI when game ends.
		 */
		public void reset(){
			pause=false;
			bigTwoPanel.setSize(3*frame.getWidth()/4,-20+9*frame.getHeight()/10);
			this.setLayout(null);
			this.removeAll();
			this.setVisible(true);
			
			//For button "restart"
			if(!client.endOfGame()){
				for(int i=0; i<4; i++){
					if(i==activePlayer){
						CardList cardsofthisplayer=client.getPlayerList().get(i).getCardsInHand();
						for(int j=cardsofthisplayer.size()-1; j>=0; j--){
							CardPanel card=new CardPanel( true, j, client.getPlayerID(), cardsofthisplayer.getCard(j).rank, cardsofthisplayer.getCard(j).suit);
							card.addMouseListener(this);
							this.add(card);
							card.setLocation(this.getWidth()/6+20*j, -2*i+(1+i)*this.getHeight()/5-110);
							if(selected[j]){
								card.position=1;
								Point p=card.getLocation();
								p.y-=20;
								card.setLocation(p);
							}
						}
					}
					else {
						CardList cardsofthisplayer=client.getPlayerList().get(i).getCardsInHand();
						for(int j=cardsofthisplayer.size()-1; j>=0; j--){
							CardPanel card=new CardPanel( false, j, i, cardsofthisplayer.getCard(j).rank, cardsofthisplayer.getCard(j).suit);
							card.addMouseListener(this);
							this.add(card);
							card.setLocation(this.getWidth()/6+20*j, -2*i+(1+i)*this.getHeight()/5-110);
						}
					}
				}
			}
			
			//For handling the end of the game, show all player's card and informations
			else{
				println("Game ends.");
				println("Player "+client.getPlayerList().get(client.getCurrentIdx())+" wins!");
				for(int i=0; i<4; i++){
					CardList cardsofthisplayer=client.getPlayerList().get(i).getCardsInHand();
					for(int j=cardsofthisplayer.size()-1; j>=0; j--){
						CardPanel card=new CardPanel( true, j, i, cardsofthisplayer.getCard(j).rank, cardsofthisplayer.getCard(j).suit);
						this.add(card);
						card.setLocation(this.getWidth()/6+20*j, -2*i+(1+i)*this.getHeight()/5-110);
					}
				}
			}
			this.repaint();
		}
		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	/**
	 * @author Shen Si Yuan
	 * Restart the game
	 */
	class ConnectMenuItemListener implements ActionListener{
		public void actionPerformed(ActionEvent event){
		    String ip=(String) JOptionPane.showInputDialog(frame,"Please enter a server IP£º\n","Server IP",JOptionPane.PLAIN_MESSAGE,null,null,"127.0.0.1");
		    String pt=(String) JOptionPane.showInputDialog(frame,"Please enter the server port£º\n","Server Port",JOptionPane.PLAIN_MESSAGE,null,null,"2396");
		    if(client.getServerIP()!=null){
		    	if(client.getServerIP().equals(ip) && client.getServerPort()==Integer.parseInt(pt)){
		    		println("Already connected to the server.");
		    		return;
		    	}
		    }
	    	client.setServerPort(Integer.parseInt(pt));
	    	client.setServerIP(ip);
	    	client.makeConnection();
		}
	}
	
	/**
	 * @author Shen Si Yuan
	 * Exit the game, window will be closed
	 */
	class QuitMenuItemListener implements ActionListener{
		public void actionPerformed(ActionEvent event){
			System.exit(0);
		}
	}

	/**
	 * @author Shen Si Yuan
	 * Play the selected card by pressing play button
	 */
	class PlayButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent event){
			if(pause) return;
			int[] c=getSelected();
			
			//Player selected some card, if c==null i.e. player did not select any card, then do nothing
			if(c!=null){
				client.makeMove(activePlayer, c); //Handle wether it is a legal move and whether it is the end of this game
			}
		}
	}
	
	/**
	 * Reset and repaint panel, used to update the game
	 * @param mod value TRUE will reset the cards selected to their original position
	 */
	public void resetPanel(boolean mod){
		if(mod){
			for(int i=0; i<selected.length; i++) selected[i]=false;
		}
		activePlayer=client.getPlayerID();
		((BigTwoPanel) bigTwoPanel).reset();
	}
	
	/**
	 * @author Shen Si Yuan
	 * Player can pass by pressing pass button
	 */
	class PassButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent event){
			if(pause) return;
				client.makeMove(activePlayer, new int[0]);  //An array of length 0 indicate that this is a pass
		}
	}
	
	/**
	 * build GUI
	 */
	public void buildGUI(){
		if(frame!=null) frame.removeAll();
		else frame=new JFrame();
		
		//Set frame configurations
		frame.setSize(1440, 900);
		frame.setDefaultCloseOperation((JFrame.EXIT_ON_CLOSE));
		String n=(String) JOptionPane.showInputDialog(frame,"Please enter your name£º\n","Name",JOptionPane.PLAIN_MESSAGE,null,null,"Nicholas P. Wilde");
		client.setPlayerName(n);
	    String ip=(String) JOptionPane.showInputDialog(frame,"Please enter a server IP£º\n","Server IP",JOptionPane.PLAIN_MESSAGE,null,null,"127.0.0.1");
	    client.setServerIP(ip);
	    String pt=(String) JOptionPane.showInputDialog(frame,"Please enter the server port£º\n","Server Port",JOptionPane.PLAIN_MESSAGE,null,null,"2396");
	    client.setServerPort(Integer.parseInt(pt));
	    
		//Create menu
		JMenuBar menubar=new JMenuBar();
		JMenu Game=new JMenu("Game");
		menubar.add(Game);
		JMenuItem Quit=new JMenuItem("Quit");
		Quit.addActionListener(new QuitMenuItemListener());
		JMenuItem Connect=new JMenuItem("Connect");
		Connect.addActionListener(new ConnectMenuItemListener());
		
		//Change background picture by clicking this menu item
        JMenuItem changebackground = new JMenuItem("Background");
        changebackground.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                JFileChooser fc = new JFileChooser();
                int result = fc.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
                    	    "Image files", ImageIO.getReaderFileSuffixes());
                    fc.addChoosableFileFilter(imageFilter);
                    fc.setAcceptAllFileFilterUsed(false);
                    try {               
            	           background = ImageIO.read(file);
            	           dimg = background.getScaledInstance(bigTwoPanel.getWidth(), bigTwoPanel.getHeight(),Image.SCALE_SMOOTH);
            	           bigTwoPanel.repaint();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Game.add(changebackground);
		Game.add(Connect);
		Game.add(Quit);
		
		//Create buttons
		playButton=new JButton("Play");
		playButton.addActionListener(new PlayButtonListener());
		
		passButton=new JButton("Pass");
		passButton.addActionListener(new PassButtonListener());
		
		//Create panel for these two buttons
		JPanel board=new JPanel();
		board.add(playButton);
		board.add(passButton);
		
		//Create text areas
		JPanel board2=new JPanel();
		board.setSize(350, 900);
		board2.setLayout(new BoxLayout(board2, BoxLayout.Y_AXIS));
		JTextField outgoing;
		incoming = new JTextArea(25, 29);
		incoming.setLineWrap(true);
		incoming.setWrapStyleWord(true);
		incoming.setEditable(false);
		JScrollPane qScroller = new JScrollPane(incoming);
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		outgoing = new JTextField(15);
		outgoing.addKeyListener(new KeyListener() {
		        public void keyPressed(KeyEvent keyEvent) {
		        	if(keyEvent.getKeyCode()==10){
		        		client.sendMessage(new CardGameMessage(CardGameMessage.MSG,-1, outgoing.getText()));
						outgoing.setText("");
						outgoing.requestFocus();
		        	}
		        }

		        public void keyReleased(KeyEvent keyEvent) {
		        }

		        public void keyTyped(KeyEvent keyEvent) {
		         }
		        
		      });
		textArea=new JTextArea(25, 29);
		textArea.setText("");
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		JScrollPane scroll =new JScrollPane(textArea);
		scroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
		scroll.validate();
		board2.add(scroll);
		board2.add(qScroller);
		board2.add(outgoing);
		//Create game table
		bigTwoPanel=new BigTwoPanel();
		
		//Add components to the frame
		frame.add(BorderLayout.CENTER, bigTwoPanel);
		this.resetPanel(true);
		dimg = background.getScaledInstance(bigTwoPanel.getWidth(), bigTwoPanel.getHeight(),Image.SCALE_SMOOTH);
		frame.add(BorderLayout.EAST, board2);
		frame.add(BorderLayout.SOUTH, board);
		frame.add(BorderLayout.NORTH, menubar);
		frame.setVisible(true);
		
	}
	
	public void setActivePlayer(int activePlayer){
		this.activePlayer=activePlayer;
	}

	public void setActiveSelection(int activeSelection){
		this.activeSelection=activeSelection;
	}

	public int[] getSelected(){
		int count=0;
		for(int i=0;i<selected.length;i++){
			if(selected[i]) count++;
		}
		if(count==0) return null;
		int[] s=new int[count];
		for(int i=0; i<selected.length; i++){
			if(selected[i]){
				s[s.length-count]=i;
				count--;
			}
		}
		return s;
		
	}

	public void resetSelected(){
		selected=null;
	}

	public void repaint(){
		frame.repaint();
	}

	public void print(String msg){
		textArea.append(msg);
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
	
	/**
	 * Print message on the chat board
	 * @param msg message to be printed in the chat board
	 */
	public void msgprint(String msg){
		incoming.append(msg);
		incoming.setCaretPosition(incoming.getDocument().getLength());
	}
	
	public void println(String msg){
		textArea.append(msg+"\n");
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
	
	/**
	 * Print a line of message on the chat board
	 * @param msg message to be printed in the chat board
	 */
	public void msgprintln(String msg){
		incoming.append(msg+"\n");
		incoming.setCaretPosition(incoming.getDocument().getLength());
	}
	

	/**
	 * Clear chat board
	 */
	public void clearIncoming(){
		incoming.setText(null);
	}
	
	public void clearTextArea(){
		textArea.setText(null);
	}

	public void enable(){
		frame.setEnabled(false);
	}

	public void disable(){
		frame.setEnabled(true);
	}

}
