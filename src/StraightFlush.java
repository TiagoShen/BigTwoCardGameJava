import java.util.ArrayList;

/**
 * This class is used to represent a Straight Flush
 * @author Shen Si Yuan
 */
public class StraightFlush extends Hand {
	private CardGamePlayer player;
	/**
	 * Number of cards in this hand, cannot be changed
	 */
	public final int cardnum=5;
	/**
	 * Rank of this hand
	 */
	public int handrank=7;
	/**
	 * Constructor of StraightFlush
	 * @param player player who played this flush
	 * @param cards the cards in the flush
	 */
	public StraightFlush(CardGamePlayer player, CardList cards){
		super(player,cards);
	}
	
	/**
	 * A method to determine whether this hand is an valid StraightFlush
	 * @return true if it is valid
	 */
	@Override
	public boolean isValid() {
		if(hand.size()==5){
			hand.sort(null);
			int i=1;
			int r=hand.get(0).rank;
			while(i<5){
				if(hand.get(i).rank!=(r+i)%13 && hand.get(i).rank!=2) return false;
				i++;
			}
			int s=hand.get(0).suit;
			i=1;
			while(i<5){
				if(hand.get(i).suit!=s) return false;
				i++;
			}
			return true;
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
		return "StraightFlush";
	}
	@Override
	public int getHandRank(){
		return handrank;
	}
}
