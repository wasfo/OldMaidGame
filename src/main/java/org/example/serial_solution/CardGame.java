package org.example.serial_solution;
import org.example.card.Card;
import org.example.card.CardUtils;
import org.example.serial_solution.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.example.card.CardUtils.removeAllPairs;

public class CardGame {
    private List<Player> players;
    private final HashMap<Player, List<Card>> playersHand;
    private final LinkedList<Player> turnList;
    private final List<Player> winnersList;
    private int numOfPlayers;

    public CardGame(int numOfPlayers) {
        if (numOfPlayers < 2)
            throw new IllegalArgumentException("number of players must be above 2");

        players = new LinkedList<>();

        winnersList = new ArrayList<>();

        turnList = new LinkedList<>();

        playersHand = new HashMap<>();

        this.numOfPlayers = numOfPlayers;


        for (int i = 0; i < numOfPlayers; i++) {
            players.add(new Player(i));
        }
    }

    public void distributeCardsForPlayers() {
        List<Card> cards = CardUtils.getPackOfCards();
        CardUtils.shuffleCards(cards);

        for (Player player : players) {
            playersHand.put(player, new LinkedList<>());
        }

        int playerIndex = 0;
        for (Card card : cards) {
            Player player = players.get(playerIndex);
            playersHand.get(player).add(card);
            playerIndex = (playerIndex + 1) % numOfPlayers;
        }

        // each player will sort his cards;
        for (Player player : players) {
            sortCards(playersHand.get(player));
        }

        turnList.addAll(players);
    }

    public void sortCards(List<Card> playerHand) {
        playerHand.sort(new CardUtils.CardNumberComparator());
        playerHand.sort(new CardUtils.CardColorComparator());
    }

    public void displayWinners() {
        for (Player player : winnersList) {
            System.out.println(player.getId() + " -> is a winner");
        }
    }

    public void playGame() {
        // at the start of the game, players will throw their matching pairs
        for (Player player : players) {
            removeAllPairs(playersHand.get(player));
        }

        while (getRemainingCards() > 2) {
            Player currentPlayer = turnList.getFirst();

            Player prevPlayer = turnList.getLast();

            if (playersHand.get(prevPlayer).size() == 0) {
                nextTurn();
                continue;
            }
            Card drawnCard = drawCardFrom(prevPlayer);

            addCardOrRemovePair(playersHand.get(currentPlayer), drawnCard);

            nextTurn();
        }
        for (Player player : players) {
            System.out.println(playersHand.get(player));
        }
    }

    private void nextTurn() {
        Player removedPlayer = turnList.removeFirst();
        turnList.addLast(removedPlayer);
    }

    public int getRemainingCards() {
        int sum = 0;
        for (Player player : players) {
            sum += playersHand.get(player).size();
        }
        return sum;
    }

    public Card drawCardFrom(Player player) {
        int size = playersHand.get(player).size();
        int rand = (int) (Math.random() * size);
        return playersHand.get(player).remove(rand);
    }

    public void addPlayerToWinnerList(Player player) {
        winnersList.add(player);
        players.remove(player);
        numOfPlayers--;
    }

    public boolean cardsMatch(Card c1, Card c2) {
        return c1.getCardColor() == c2.getCardColor() && c1.getCardNumber() == c2.getCardNumber();
    }



    public void addCardOrRemovePair(List<Card> playerHand, Card card) {
        for (int i = 0; i < playerHand.size(); i++) {
            if (cardsMatch(card, playerHand.get(i))) {
                playerHand.remove(i);
                return;
            }
        }
        playerHand.add(card);
    }


    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    public void setNumOfPlayers(int numOfPlayers) {
        this.numOfPlayers = numOfPlayers;
    }
}
