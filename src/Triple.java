import java.util.ArrayList;

/**
 * This class is used to represent a Triple
 * @author Shen Si Yuan
 */
public class Triple extends Hand {
	private CardGamePlayer player;
	/**
	 * Rank of this hand
	 */
	public int handrank=2;
	/**
	 * Number of cards in this hand, cannot be changed
	 */
	public final int cardnum=3;
	/**
	 * Constructor of Triple
	 * @param player player who played this flush
	 * @param cards the cards in the flush
	 */
	public Triple(CardGamePlayer player, CardList cards){
		super(player,cards);
	}
	/**
	 * A method to determine whether this hand is an valid Triple
	 * @return true if it is valid
	 */
	@Override
	public boolean isValid() {
		if(hand.size()==3){
			return hand.get(1).rank==hand.get(0).rank && hand.get(1).rank==hand.get(2).rank;
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
		return "Triple";
	}
	@Override
	public int getHandRank(){
		return handrank;
	}
}
