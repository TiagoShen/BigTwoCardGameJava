
/**
 * This class is used to represent Big Two card
 * @author Shen Si Yuan
 *
 */
public class BigTwoCard extends Card implements Comparable<Card>{

	/**
	 * Creates and returns an instance of the BigTwoCard class.
	 * 
	 * @param suit
	 *            an int value between 0 and 3 representing the suit of a card:<p>
	 *            0 = Diamond, 1 = Club, 2 = Heart, 3 = Spade
	 * @param rank
	 *            an int value between 0 and 12 representing the rank of a card:<p>
	 *            0 = 'A', 1 = '2', 2 = '3', ..., 8 = '9', 9 = '0', 10 = 'J', 11 = 'Q', 12 = 'K'
	 */
	public BigTwoCard(int suit, int rank) {
		super(suit,rank);
	}
	/**
	 * Compares this card with the specified card for order.
	 * @param card the card to be compared
	 * @return a negative integer, zero, or a positive integer as this card is less than, equal to, or greater than the specified card
	 * parameter rank is not changed compared to super class Card, only logic of comparing rank is changed
	 */
	public int compareTo(Card card) {
		int r=(this.rank+11)%13;
		int rc=(card.rank+11)%13;
		if (r > rc) {
			return 1;
		} else if (r < rc) {
			return -1;
		} else if (this.suit > card.suit) {
			return 1;
		} else if (this.suit < card.suit) {
			return -1;
		} else {
			return 0;
		}
	}
}
