import java.util.ArrayList;

/**
 * This class is used to represent a Flush
 * @author Shen Si Yuan
 */
public class Flush extends Hand {
	private CardGamePlayer player;
	/**
	 * Number of cards in this hand, cannot be changed
	 */
	public final int cardnum=5;
	/**
	 * Rank of this hand
	 */
	public int handrank=4;
	/**
	 * Constructor of Flush
	 * @param player player who played this flush
	 * @param cards the cards in the flush
	 */
	public Flush(CardGamePlayer player, CardList cards){
		super(player,cards);
	}
	/**
	 * A method to determine whether this hand is an valid Flush, a StraightFlush is not a valid Flush
	 * @return true if it is valid
	 */
	@Override
	public boolean isValid() {
		if(hand.size()==5){
			hand.sort(null);
			int i=1;
			int s=hand.get(0).suit;
			while(i<5){
				if(hand.get(i).suit!=s) return false;
				i++;
			}
			int r=hand.get(0).rank;
			i=1;
			while(i<5){
				if(hand.get(0).rank!=r+i) return true;
				i++;
			}
			return false;
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
		// TODO Auto-generated method stub
		return "Flush";
	}
	public int getHandRank(){
		return handrank;
	}
}
