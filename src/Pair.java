import java.util.ArrayList;

/**
 * This class is used to represent a Pair
 * @author Shen Si Yuan
 */
public class Pair extends Hand {
	private CardGamePlayer player;
	/**
	 * Number of cards in this hand, cannot be changed
	 */
	public final int cardnum=2;
	
	/**
	 * Rank of this hand
	 */
	public int handrank=1;
	/**
	 * Constructor of Pair
	 * @param player player who played this flush
	 * @param cards the cards in the flush
	 */
	public Pair(CardGamePlayer player, CardList cards){
		super(player,cards);
	}

	/**
	 * A method to determine whether this hand is an valid Pair 
	 * @return true if it is valid
	 */
	@Override
	public boolean isValid() {
		if(hand.size()==2){
			return hand.get(1).rank==hand.get(0).rank;
		}
		else return false;
	}
	/** 
	 * Override the abstract class Hand
	 * @see Hand#getType()
	 * @return the type name of this hand
	 */
	@Override
	public String getType() {
		return "Pair";
	}
	@Override
	public int getHandRank(){
		return handrank;
	}

}
