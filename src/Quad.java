import java.util.ArrayList;

/**
 * This class is used to represent a Quadruplet
 * @author Shen Si Yuan
 */
public class Quad extends Hand {
	/**
	 * Number of cards in this hand, cannot be changed
	 */
	public final int cardnum=5;
	private CardGamePlayer player;
	/**
	 * Rank of this hand
	 */
	public int handrank=6;
	/**
	 * Constructor of Hand
	 * @param player player who played this flush
	 * @param cards the cards in the flush
	 */
	public Quad(CardGamePlayer player, CardList cards){
		super(player,cards);
	}
	
	/**
	 * A method to determine whether this hand is an valid Quadruplet 
	 * @return true if it is valid
	 */
	@Override
	public boolean isValid() {
		if(hand.size()==5){
			hand.sort(null);
			int r=hand.get(0).rank;
			int i=1;
			while(hand.get(i).rank==r){
				i++;
			}
			if(i==4) return true;
			r=hand.get(4).rank;
			i=1;
			while(hand.get(i).rank==r && i!=4){
				i++;
			}
			if(i==4) return true;
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
		return "Quad";
	}
	@Override
	public int getHandRank(){
		return handrank;
	}
}
