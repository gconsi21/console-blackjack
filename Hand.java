import java.util.ArrayList;

/**
 * Represents a single blackjack hand.
 * 
 * Responsibilities:
 * - Store the cards currently in the hand
 * - Compute the blackjack total using soft-ace rules
 * - Provide helper checks for bust and blackjack
 * 
 * Note: This hand currently stores card "values" as integers (2–11).
 */

public class Hand {

    /**
     * Cards in the hand.
     * Each value is a blackjack value:
     * - 2 through 10 are face value
     * - 11 represents an Ace (initially treated as 11, may be reduced to 1 during total calculation)
     */
    private ArrayList<Integer> cards;

    /**
     * Constructs an empty hand.
     * The cards list is initialized here to ensure the Hand always starts in a valid state.
     */
    public Hand() { 
        cards = new ArrayList<>(); 
    }

    // Adds a card value to the hand. It does not compute the total.
    public void addCard(int cardValue){
        cards.add(cardValue);
    }

    /**
     * Computes the current blackjack total for this hand.
     *
     * Soft Ace logic:
     * - Aces are initially counted as 11.
     * - If the total exceeds 21, each Ace may be "converted" from 11 to 1 by subtracting 10,
     *   until the hand is no longer bust or no more soft Aces remain.
     *
     */
    public int getTotal(){
        int total = 0;
        int softAces = 0;

        // Sum card values and count how many Aces are currently treated as 11
        for (int v : cards) {
            total += v;
            if (v == 11){
                softAces++;
            }
        }

        // Convert Aces from 11 -> 1 as needed to prevent busting
        while (total > 21 && softAces > 0){
            total -= 10;
            softAces --;
        }
        return total;
    }

    // Checks whether the hand is bust
    public boolean isBust(){
        return getTotal() > 21;
    }

    // Checks for a natural blackjack. Only returns true if the first two card totals equals 21
    public boolean isBlackjack(){
        return (cards.size() == 2 && getTotal() == 21);
    }
}
