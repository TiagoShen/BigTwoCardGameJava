import java.util.ArrayList;

/**
 * This abstract class is used to represent a hand
 * @author Shen Si Yuan
 */
abstract public class Hand extends CardList {
	/**
	 * Store the current hand the player played
	 */
	public ArrayList<Card> hand=new ArrayList<Card>();
	private CardGamePlayer player;
	/**
	 * Number of cards in this hand, cannot be changed
	 */
	public final int cardnum;
	
	/**
	 * For simplicity purpose, assign a unchangeable rank to all kinds of hand. 
	 * Rank from 0 to 7 respectively: Single, Pair, Triple, Straight, Flush, FullHouse, Quad, StraightFlush 
	 */
	public int handrank;
	
	/**
	 * Get the rank of this type of hand
	 * @return rank of this type of hand
	 */
	abstract public int getHandRank();
	/**
	 * A constructor for building a hand with the specified player and list of cards
	 * @param player the player want to specified
	 * @param cards the hand of this player
	 */
	public Hand(CardGamePlayer player, CardList cards){
		cardnum=cards.size();
		this.player=player;
		for(int i=0;i<cards.size();i++){
			hand.add(cards.getCard(i));
		}
		hand.sort(null);
	}
	/**
	 * A method for retrieving the player of this hand
	 * @return player of this hand
	 */
	public CardGamePlayer getPlayer(){
		return player;
	}
	/**
	 * A method for retrieving the top card of this hand
	 * @return the top card of this hand
	 */
	public Card getTopCard(){
		return hand.get(hand.size()-1);
	}
	/**
	 * A method for checking if this hand beats a specified hand
	 * @return true if this hand beats a specified hand
	 */
	public boolean beats(Hand hand){
		if(this.isValid() && hand.isValid()){
			if(this.getHandRank()==hand.getHandRank()){									//Same Type of Hand
				if(this.getTopCard().compareTo(hand.getTopCard())>0) return true;	
				return false;
			}
			else if(this.getHandRank()!=hand.getHandRank() && this.getHandRank()>2 && hand.getHandRank()>2){	//5 cards but different type of hand
				return this.getHandRank()>hand.getHandRank();
			}
			return false;																//Mismatch
		}
		return false;
	}
	/**
	 * To be override by its subclass
	 * @return true if it is a valid hand
	 */
	public abstract boolean isValid();
	
	/**
	 * To be override by its subclass
	 * @return type name of the hand
	 */
	public abstract String getType();
}
