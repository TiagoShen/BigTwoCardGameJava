import java.util.ArrayList;

/**
 * This class is used to represent a FullHouse
 * @author Shen Si Yuan
 */
public class FullHouse extends Hand{
	/**
	 * Number of cards in this hand, cannot be changed
	 */
	public final int cardnum=5;
	/**
	 * Rank of this hand
	 */
	public int handrank=5;
	private CardGamePlayer player;
	
	/**
	 * Constructor of FullHouse
	 * @param player player who played this flush
	 * @param cards the cards in the flush
	 */
	public FullHouse(CardGamePlayer player, CardList cards){
		super(player,cards);
	}
	/**
	 * A method to determine whether this hand is an valid Full House 
	 * @return true if it is valid
	 */
	@Override
	public boolean isValid() {
		if(hand.size()==5){
			hand.sort(null);
			int i;
			int r=hand.get(2).rank;
			if(hand.get(0).rank==r){													//AAABB
				i=0;
				while(hand.get(i).rank==r && i!=2){
				i++;
				}
				if(i==2 && hand.get(3).rank==hand.get(4).rank) return true;
				return false;
			}
			if(hand.get(4).rank==r){													//AABBB
				i=4;															
				while(hand.get(i).rank==r && i!=2){
				i--;
				}
				if(i==2 && hand.get(0).rank==hand.get(1).rank) return true;
				return false;
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
		return "FullHouse";
	}
	public int getHandRank(){
		return handrank;
	}
}
