import java.util.ArrayList;

/**
 * This class is used to represent a Single
 * @author Shen Si Yuan
 */
public class Single extends Hand {
	/**
	 * Number of cards in this hand, cannot be changed
	 */
	public final int cardnum=1;
	public int handrank=0;
	private CardGamePlayer player;
	/**
	 * Constructor of Single
	 * @param player player who played this flush
	 * @param cards the cards in the flush
	 */
	public Single(CardGamePlayer player, CardList cards){
		super(player,cards);
	}
	
	/**
	 * A method to determine whether this hand is an valid Single 
	 * @return true if it is valid
	 */
	@Override
	public boolean isValid() {
		return true;
	}
	
	/** 
	 * Override the abstract class Hand
	 * @see Hand#getType()
	 * @return the type name of this hand
	 */
	@Override
	public String getType() {
		/** 
		 * Override the abstract class Hand
		 * @see Hand#getType()
		 * @return the type name of this hand
		 */
		return "Single";
	}
	@Override
	public int getHandRank(){
		return handrank;
	}
}
