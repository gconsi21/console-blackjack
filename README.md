# Console Blackjack (Java)

## Overview

Console Blackjack is a casino-style blackjack engine built in Java and designed to run entirely in the command line. The project focuses on implementing realistic blackjack rules, proper state management, and deterministic deck behavior rather than relying on simplified random card generation.

The goal of this project was to translate real-world casino rules into structured program logic while maintaining clean control flow, input validation, and persistent session tracking.

---

## Features

- Single 52-card deck
- Fisher–Yates shuffle (unbiased shuffling algorithm)
- Cut-card reshuffle when 15 cards remain (between hands only)
- Proper casino dealing order (Player → Dealer → Player → Dealer)
- Soft Ace handling (Aces dynamically adjust from 11 to 1 when necessary)
- Dealer blackjack peek when showing 10 or Ace
- Dealer stands on all 17s (S17 rule)
- Blackjack pays 3:2
- Push handling
- Double Down (allowed on first decision only)
- Forced single card on Double Down
- Full input validation (no crashes on invalid user input)
- Session statistics tracking
- Money formatting to two decimal places
- Win rate displayed as percentage

---

## Rules Implemented

This project follows the rule set below:

- Single-deck blackjack
- Dealer stands on all 17s (including soft 17)
- Blackjack pays 3:2
- Double Down allowed only on the first decision
- Double Down forces exactly one additional card
- No mid-hand reshuffles
- Reshuffle occurs automatically when 15 cards remain
- Ten-value cards represent 10, Jack, Queen, and King
- Aces are initially valued at 11 and adjust to 1 when needed

---

## How to Run

### Compile

javac Main.java

### Run

java Main

The program runs in the terminal and requires no external libraries.

---

## Example Output

Welcome to Blackjack!
You start with $1000.00

1) Play a hand
2) View Stats
3) Quit

How much do you want to bet?
50

Your cards: 10, 6 (Total: 16)
Dealer shows: 7
Hit, Stand, or Double Down? (H/S/D)
h
Your card: 5 Your new total: 21
Dealer flips their second card and shows: 9
Dealer drew a 4
Dealer's total: 20
Player Wins!
Your new balance: $1050.00
Wins: 1 Losses: 0

---

## Technical Design Highlights

Deterministic Deck Simulation  
The program models a full 52-card deck and deals cards sequentially from a shuffled array. This prevents unrealistic behavior such as duplicate card draws or mid-hand reshuffling.

Fisher–Yates Shuffle  
The deck is shuffled using the Fisher–Yates algorithm, which provides an unbiased shuffle in linear time complexity O(n).

Soft Ace Logic  
Aces are tracked separately to allow dynamic adjustment from 11 to 1 when the hand total exceeds 21. This prevents incorrect bust scenarios and accurately models real blackjack behavior.

Dealer Peek Logic  
If the dealer's upcard is a 10-value card or Ace, the dealer checks for blackjack immediately before player decisions continue.

State Management  
A dedicated GameState class maintains persistent session data including:
- Bankroll
- Wins
- Losses
- Hands played
- Deck and deck position

Input Validation  
All numeric input is validated using scanner checks to prevent runtime crashes from invalid user input.

---

## Project Structure

Main.java

The current version is implemented in a single file for clarity during early development. The structure is intentionally straightforward to emphasize core game logic. Future versions may refactor into additional classes such as Card, Hand, or Deck for improved object-oriented structure.

---

## Future Improvements

- Track pushes separately in statistics
- Add Insurance side bet
- Implement Split functionality
- Multi-deck shoe support
- Basic strategy suggestion system
- Refactor into Card and Hand classes
- ASCII card display
- Simulation mode for automated hand runs
- Improved session summary analytics

---

## Purpose of This Project

This project was built to strengthen understanding of:

- Real-world rule translation into code
- Complex branching logic
- State tracking across program execution
- Control flow correctness
- Algorithm implementation (shuffling)
- Defensive input handling

It reflects a progression from a basic random-card implementation to a structured, rule-accurate blackjack engine.

---

## Author

Developed as a Java practice project focused on game logic, probability modeling, and structured program design.