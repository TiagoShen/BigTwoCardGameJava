import java.util.ArrayList;

/**
 * This class is used to represent a deck of Big Two cards
 * @author Shen Si Yuan
 */
public class BigTwoDeck extends Deck {
	private ArrayList<BigTwoCard> cards = new ArrayList<BigTwoCard>();
	
	/**
	 * Creates and returns an instance of the BigTwoDeck class.
	 */
	public BigTwoDeck(){
		initialize();
	}
	
	/**
	 * Initialize the deck of Big Two cards.
	 */
	public void initialize() {
		removeAllCards();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 13; j++) {
				BigTwoCard card = new BigTwoCard(i, j);
				addCard(card);
			}
		}	
	}

}
