package org.example;

import org.example.card.Card;
import org.example.card.CardUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.example.GamePlay.queue;
import static org.example.card.CardUtils.sortCards;

public class GamePlay {

    static final HashMap<String, List<Card>> playersHands = new HashMap<>();
    static final LinkedList<String> queue = new LinkedList<>();
    private final int numberOfPlayers;

    public GamePlay(int numberOfPlayers) {
        if (numberOfPlayers < 2)
            throw new IllegalArgumentException("number of players must be above 2");

        this.numberOfPlayers = numberOfPlayers;
    }

    private List<Thread> setupGame() {

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numberOfPlayers; i++) {
            Thread thread = new Thread(new Player(this), "Player" + i);
            threads.add(thread);
            queue.add(thread.getName());
            playersHands.put(thread.getName(), new ArrayList<>());
        }

        distributeCardsForPlayers();

        return threads;
    }

    public void start() {
        List<Thread> threads = setupGame();
        for (Thread thread : threads) {
            thread.start();
        }

    }

    public Card getCardFromNextPlayer() throws InterruptedException {
        String nextPlayerName = queue.get(1);
        return drawRandomCard(playersHands.get(nextPlayerName));
    }

    public Card drawRandomCard(List<Card> cards) {
        int rand = (int) (Math.random() * cards.size());
        return cards.remove(rand);
    }

    public void removePairs(List<Card> playerHand) {
        for (int i = 0; i < playerHand.size(); i++) {
            Card currentCard = playerHand.get(i);
            for (int j = i + 1; j < playerHand.size(); j++) {
                Card nextCard = playerHand.get(j);
                if (cardsMatch(currentCard, nextCard)) {
                    playerHand.remove(currentCard);
                    playerHand.remove(nextCard);
                }
            }
        }
    }

    public boolean cardsMatch(Card c1, Card c2) {
        return c1.getCardColor() == c2.getCardColor() && c1.getCardNumber() == c2.getCardNumber();
    }

    public void distributeCardsForPlayers() {
        List<Card> cards = CardUtils.getPackOfCards();
        CardUtils.shuffleCards(cards);


        for (Card card : cards) {
            String playerName = queue.removeFirst();
            playersHands.get(playerName).add(card);
            queue.addLast(playerName);
        }

        // each player will sort his cards;
        for (int i = 0; i < numberOfPlayers; i++) {
            String playerName = queue.removeFirst();
            sortCards(playersHands.get(playerName));
            queue.addLast(playerName);
        }

    }


}

class Player implements Runnable {
    private final GamePlay gamePlay;

    Player(GamePlay gamePlay) {
        this.gamePlay = gamePlay;
    }

    @Override
    public void run() {

        while (gameIsNotFinished()) {

            synchronized (gamePlay) {
                try {
                    String playerName = Thread.currentThread().getName();
                    while (playerName != queue.peek()) {
                        gamePlay.wait();
                    }

                    gamePlay.notifyAll();
                    System.out.println("____________________________________________________");


                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            //notify all threads that the game is finished;

        }
        System.out.println(queue);
        //gamePlay.notifyAll();

    }


    public boolean gameIsNotFinished() {
        return queue.size() > 1;
    }

}