import java.util.Scanner;
import java.util.Random;


/** 
 * Creating and storing the user data that is persistent across multiple hands.
 * Prevents resetting the players stats each time.
 */
class GameState {
    // Player bankroll
    double balance;

    // Basic session stats
    int wins;
    int losses;
    int handsPlayed;

    // Current deck shoe
    int[] deck;

    //Current deck position in the deck array
    int deckPosition;
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        //Random used only for shuffling
        Random random = new Random();

        // Initializes game state
        GameState state = new GameState();

        // Builds and shuffles a new deck at the program start
        state.deck = buildDeck();
        shuffleDeck(state.deck, random);
        state.deckPosition = 0;

        // Initializes starting bankroll and stats
        state.balance = 1000;
        state.wins = 0;
        state.losses = 0;
        state.handsPlayed = 0;

        // Main menu loop
        boolean play = true;

        System.out.println("Welcome to Blackjack!");
        System.out.printf("You start with $%.2f%n", state.balance);

        // Main menu loop: play, view stats, or quit
        while (play){

            // Reshuffle Logic: Reshuffles when there are 15 cards or fewer
            if (state.deckPosition >= state.deck.length - 15){
                System.out.println("Card count is too low, Shuffling.");
                state.deck = buildDeck();
                shuffleDeck(state.deck, random);
                state.deckPosition = 0;
            } 

            // Menu prompt
            System.out.println("1) Play a hand \n2) View Stats \n3) Quit");

            // Input validation
            while (!scanner.hasNextInt()){
                System.out.println("Please enter 1, 2, or 3.");
                scanner.nextLine();
            }

            // Reads user menu selection
            int gameChoice = scanner.nextInt();
            scanner.nextLine();

            // Controls menu selection
            if (gameChoice == 1) {
                playGame(scanner, state, random);
            } else if (gameChoice == 2) {
                viewStats(state);
            } else if (gameChoice == 3) {
                play = false; 
            } else {
                // Handles inputs outside desired range
                System.out.println("Please enter 1, 2, or 3.");
            }
        }
    }

    /**
     * Plays one full hand of blackjack (bet -> deal -> player actions -> dealer actions -> payout).
     * This method is intentionally "all-in-one" right now for readability during early stages,
     * but could later be refactored into smaller helper methods.
     */
    public static void playGame(Scanner scanner, GameState state, Random random){

        // Player decision and outcome state flags
        boolean playerStanding = false; // player ended decisions voluntarily
        boolean playerBusted = false;   // player exceeded 21
        boolean dealerBusted = false;   // dealer exceeded 21
        boolean playerLoses = false;    // final outcome control flag
        boolean push = false;           // tie hand

        // Get bet amount from player
        System.out.println("How much do you want to bet?");

        // Input validation for bet
        while (!scanner.hasNextDouble()){
                System.out.println("Please enter a valid bet.");
                scanner.nextLine();
            }

        // Read bet and consumes newline
        double bet = scanner.nextDouble();
        scanner.nextLine();

        // Validates bet range. Must be > 0, Must not exceed current balance. If invalid returns to menu
        if (bet <= 0 ||  bet > state.balance) {
            System.out.println("Error, bet not valid");
            return;
        } else {

            /**
             * Initial deal in correct casino order:
             * Player -> Dealer -> Player -> Dealer
             */
            int playerCard1 = drawCard(state);
            int dealerCard1 = drawCard(state);
            int playerCard2 = drawCard(state);          
            int dealerCard2 = drawCard(state);

            // Compute initial totals using numeric values
            int playerTotal = playerCard1 + playerCard2;
            int dealerTotal = dealerCard1 + dealerCard2; 

            // Tracks whether the player has a natural blackjack
            boolean playerBlackjack = false;

            // Handles double down rule of only being allowed on the player's first decision
            boolean isFirstDecision = true;

            // Handles soft aces, giving an ace the value of 11 or 1
            int playerSoftAces = 0;
            int dealerSoftAces = 0;

            // Count players initial aces
            if (playerCard1 == 11){
                playerSoftAces ++;
            }
            if (playerCard2 == 11){
                playerSoftAces ++;
            }

            // Adjust player total if soft aces are used
            while (playerTotal > 21 && playerSoftAces > 0){
                playerTotal -= 10;
                playerSoftAces --;
            }

            // Count dealers initial aces
            if (dealerCard1 == 11){
                dealerSoftAces ++;
            }
            if (dealerCard2 == 11){
                dealerSoftAces ++;
            }

            // Adjust dealer total if soft aces are used
            while (dealerTotal > 21 && dealerSoftAces > 0){
                dealerTotal -= 10;
                dealerSoftAces --;
            }

            // Shows player's starting hand
            System.out.println("Your cards: " + playerCard1 +", " +  playerCard2 + " (Total: " + playerTotal + ")");

            /**
             * Natural blackjack handling:
             * - If player and dealer both have blackjack: push
             * - If only player has blackjack: set playerBlackjack true (payout later)
             */
            if (playerTotal == 21 && dealerTotal == 21){
                System.out.println("You might have a BlackJack! Lets check the dealers cards.");
                System.out.println("Dealer shows: " + dealerCard1);
                System.out.println("Dealer flips their second card and shows: " + dealerCard2);
                System.out.println("Dealer's total: " + dealerTotal);
                push = true;
                System.out.println("Unlucky. The dealer also has a BlackJack. This hand is a push!");
            } else if (playerTotal == 21){
                System.out.println("You might have a BlackJack! Lets check the dealers cards.");
                System.out.println("Dealer shows: " + dealerCard1);
                System.out.println("Dealer flips their second card and shows: " + dealerCard2);
                System.out.println("Dealer's total: " + dealerTotal);
                playerBlackjack = true;
            } else {

                // Normal case: player does not have a blackjack
                System.out.println("Dealer shows: " + dealerCard1);

                /**
                 * Dealer peek logic:
                 * If dealer's upcard is 10 or Ace, dealer checks for blackjack immediately.
                 * If dealer has blackjack, the hand ends right away (no player decisions).
                 */
                if (dealerCard1 == 10 || dealerCard1 == 11){
                    System.out.println("Dealer checks second card for potential BlackJack.");
                    if (dealerTotal == 21) {
                        System.out.println("The Dealer flips the second card and shows: " + dealerCard2);
                        System.out.println("Dealer has a BlackJack!");
                        playerLoses = true;
                    }
                }
            }

             /**
             * Player decision loop:
             * Continues until player stands, busts, has blackjack, pushes, or loses immediately to dealer blackjack.
             */
            while (!playerStanding && !playerBusted && !playerBlackjack && !push && !playerLoses){

                // Show correct decision prompt based on whether double down is still allowed
                if (isFirstDecision){
                    System.out.println("Hit, Stand, or Double Down? (H/S/D)");
                } else {
                    System.out.println("Hit or Stand? (H/S)");
                }

                // Read player action as a full line
                String hitStand = scanner.nextLine();

                // Hit action. Take one card and update totals
                if (hitStand.equalsIgnoreCase("h")){
                    isFirstDecision = false;    // once you hit, double down is no longer allowed
                    int playerHit = drawCard(state);
                    playerTotal = playerTotal + playerHit;

                    // Track soft ace if the new card is an ace
                    if (playerHit == 11){
                        playerSoftAces ++;
                    }

                    // Apply soft ace adjustment to prevent incorrect busts
                    while (playerTotal > 21 && playerSoftAces > 0){
                        playerTotal -= 10;
                        playerSoftAces --;
                    }
                    System.out.println("Your card: " + playerHit + " Your new total: " + playerTotal);

                    // Checks for player bust
                    if (playerTotal > 21){
                        playerBusted = true;
                    }

                // Stand action. Ends players decision
                } else if (hitStand.equalsIgnoreCase("s")){
                    playerStanding = true;

                // Double Down action. Only allowed on the first decision, doubles bet and forces one hit then stand
                } else if (hitStand.equalsIgnoreCase("d")){
                    if (isFirstDecision) {

                        // Ensure player has enough balance to double down
                        if(bet * 2 > state.balance){
                            System.out.println("Error, you do not have the funds to Double Down.");
                        } else {

                            // Double down forces player to stand after taking one hit
                            playerStanding = true;

                            // Increase bet immediately
                            bet = bet * 2;
                            System.out.println("You choose to Double Down. Your bet is now: " + bet);

                            // Deal exactly one card to player
                            int playerHit = drawCard(state);
                            playerTotal = playerTotal + playerHit;

                            // Soft ace tracking for double down card
                            if (playerHit == 11){
                                playerSoftAces ++;
                            }

                            // Adjust for soft ace if needed
                            while (playerTotal > 21 && playerSoftAces > 0){
                                playerTotal -= 10;
                                playerSoftAces --;
                            }
                            System.out.println("Your card: " + playerHit + " Your new total: " + playerTotal);

                            // Bust checker
                            if (playerTotal > 21){
                                playerBusted = true;
                            }
                        }
                    } else {
                        // Prevent double down after a hit
                        System.out.println("Error, you can only Double Down on your first decision. Please select Hit or Stand (H/S)");
                    }
                    
                } else {
                    // Invalid input handling. Shows correct options based on allowed actions
                    if (isFirstDecision){
                        System.out.println("Error. Please press H, S or D");
                    } else {
                        System.out.println("Error. Please press H or S"); 
                    }
                }
            }

            // If player busts, end hand immediately and player loses
            if (playerBusted == true){
                System.out.println("You have Busted!");
                playerLoses = true;

            // If player has a blackjack and dealer does not, blackjack payout applies
            } else if (playerBlackjack == true && dealerTotal != 21) {
                System.out.println("You have a BlackJack! This pays 3 to 2");

            // Dealer play phase. Dealer reveals card until reaching 17 or higher. Dealer stands on all 17s
            } else if (!playerLoses){
                System.out.println("Dealer flips their second card and shows: " + dealerCard2);
                System.out.println("Dealer's total: " + dealerTotal);

                // Dealer hits while under 17
                while (dealerTotal < 17) {
                    int dealerHit = drawCard(state);
                    dealerTotal = dealerTotal + dealerHit;

                    // Soft aces handling for dealer
                    if (dealerHit == 11){
                        dealerSoftAces ++;
                    }
                    while (dealerTotal > 21 && dealerSoftAces > 0){
                        dealerTotal -= 10;
                        dealerSoftAces --;
                    }
                    System.out.println("Dealer drew a " + dealerHit);
                    System.out.println("Dealer's total: " + dealerTotal);

                    // Dealer bust check
                    if (dealerTotal > 21){
                        System.out.println("Dealer has Busted!");
                        playerLoses = false;
                        dealerBusted = true;
                    }
                }

                /**
                 * Compare hands (only meaningful if dealer did not bust).
                 * - If dealer busted, player wins (handled by dealerBusted flag).
                 * - Otherwise compare totals.
                 */
                if (playerTotal > dealerTotal || dealerBusted){
                    playerLoses = false;
                } else if (playerTotal < dealerTotal) {
                    playerLoses = true;
                } else {
                    push = true;
                }
            }

            /**
             * Final resolution & bankroll update:
             * - Push returns bet (no balance change)
             * - Blackjack pays 3:2 (adds 1.5x bet to balance)
             * - Normal win adds bet
             * - Loss subtracts bet
             *
             * Note: bet may already have been doubled if player chose Double Down.
             */
            if (push){
                System.out.println("This game was a push. Your bet will be returned.");
                state.handsPlayed ++;
            } else if (playerBlackjack) {
                state.balance = state.balance + ((bet * 3) / 2);
                state.wins ++;
                state.handsPlayed ++;
            } else if (!playerLoses){
                System.out.println("Player Wins!");
                state.balance = state.balance + bet;
                state.wins ++;
                state.handsPlayed ++;
            } else if (playerLoses){
                System.out.println("Dealer Wins!");
                state.balance = state.balance - bet;
                state.losses ++;
                state.handsPlayed ++;
            }

            // End of hand status display
            System.out.printf("Your new balance: $%.2f%n" , state.balance);
            System.out.println("Wins: " + state.wins + " Losses: " + state.losses);
        }
    }

    // Displays session stats. uses double division to avoid integer truncation when computing win rate.
    public static void viewStats(GameState state){
        System.out.printf("Your balance: $%.2f%n" , state.balance);
        System.out.println("Your wins: " + state.wins);
        System.out.println("Your losses: " + state.losses);
        System.out.println("Your total hands played: " + state.handsPlayed);

        // Only computes win rate if at least one hand has been played
        if (state.handsPlayed > 0){
            double rate = (double) state.wins / state.handsPlayed;
            System.out.printf("Total win rate: %.2f%%%n" , (rate * 100));
        }
    }

    /**
     * Draws the next card from the deck using deckPosition, then advances deckPosition.
     * This simulates dealing from the top of a shuffled deck.
     */
    public static int drawCard(GameState state){
        int card = state.deck[state.deckPosition];
        state.deckPosition++;
        return card;
    }

    /**
     * Builds a standard single 52-card blackjack deck using numeric values:
     * - 2 through 9 appear 4 times each
     * - 10-value cards (10, J, Q, K) appear 16 times total
     * - Aces appear 4 times as value 11 (soft-ace logic adjusts to 1 when needed)
     * Can increase total decks by changing C values
     */
    public static int[] buildDeck() {
        int[] deck = new int[52];
        int index = 0;

        // Add ranks(card value) 2-9, four(suits) of each. 
        for (int v = 2; v <= 9; v++) {
            for (int c = 0; c < 4; c++) {
                deck[index] = v;
                index++;
            }
        }

        // Adds 16 ten-value cards (10/J/Q/K)
        for (int c =0; c < 16; c++) {
            deck[index] = 10;
            index ++;
        }

        // Adds 4 aces as value 11
        for (int c =0; c < 4; c++) {
            deck[index] = 11;
            index ++;
        }
        return deck;
    }

    /**
     * Fisher–Yates shuffle:
     * Produces an unbiased shuffle of the deck array in O(n) time.
     */
    public static void shuffleDeck(int[] deck, Random random) {
        for (int i = deck.length -1; i > 0; i--){
            int j = random.nextInt(i + 1);

            int temp = deck[i];
            deck[i] = deck[j];
            deck[j] = temp;
        }
    }
}
