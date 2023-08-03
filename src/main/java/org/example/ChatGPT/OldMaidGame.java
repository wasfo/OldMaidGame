package org.example.ChatGPT;

import org.example.card.Card;
import org.example.card.CardUtils;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import static org.example.card.CardUtils.*;


class Player implements Runnable {
    private final String name;

    private static final Object lock = new Object();
    private static final Lock removePairsLock = new ReentrantLock();

    public Player(String name) {
        this.name = name;
    }


    private Card drawCardFrom(Player player) {
        int size = OldMaidGame.playersHand.get(player.getName()).size();
        int rand = (int) (Math.random() * size);
        return OldMaidGame.playersHand.get(player.getName()).remove(rand);
    }

    public String getName() {
        return name;
    }

    private void playTurn() {
        synchronized (lock) {
            while (this != getCurrentPlayer()) {
                try {
                    System.out.println(getCurrentPlayer().getName() + " is waiting");
                    if (OldMaidGame.getRemainingCards() < 2) {
                        return;
                    }

                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Player currentPlayer = getCurrentPlayer();
            Player prevPlayer = getPrevPlayer();

            if (OldMaidGame.playersHand.get(prevPlayer.getName()).size() == 0) {
                System.out.println("turn skipped");
                nextTurn();
                lock.notifyAll();
                return;
            }

            Card drawnCard = drawCardFrom(prevPlayer);
            addCardOrRemovePair(OldMaidGame.playersHand.get(currentPlayer.getName()), drawnCard);

            nextTurn();
            lock.notifyAll();


        }

    }

    private static Player getCurrentPlayer() {
        return OldMaidGame.queue.getFirst();
    }

    private static Player getPrevPlayer() {
        return OldMaidGame.queue.getLast();
    }

    private static void nextTurn() {
        Player player = OldMaidGame.queue.removeFirst();
        OldMaidGame.queue.addLast(player);
    }

    @Override
    public void run() {
        Integer i = 100;
        synchronized (removePairsLock) {

            while (this.name != getCurrentPlayer().getName()) {
                try {
                    removePairsLock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            String name = getCurrentPlayer().getName();
            removeAllPairs(OldMaidGame.playersHand.get(name));
            nextTurn();
            removePairsLock.notifyAll();
        }
        synchronized (i) {
            i = i * 2;
            try {
                Thread.sleep(i);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        //OldMaidGame.queue.forEach(System.out::println);

        while (OldMaidGame.getRemainingCards() > 2) {
            playTurn();
        }

        System.out.println(name + " has finished playing.");
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

}

public class OldMaidGame {
    private static List<Player> players;
    static HashMap<String, List<Card>> playersHand;
    private int numOfPlayers;
    static LinkedList<Player> queue;

    public static HashMap<String, List<Card>> getPlayersHand() {
        return playersHand;
    }


    public OldMaidGame(int numOfPlayers) {
        playersHand = new HashMap<>();
        this.numOfPlayers = numOfPlayers;
        queue = new LinkedList<>();
        players = new ArrayList<>();

    }

    static public int getRemainingCards() {
        int sum = 0;
        for (Player player : players) {
            sum += OldMaidGame.playersHand.get(player.getName()).size();
        }
        return sum;
    }

    private void setupGame() {
        for (int i = 0; i < numOfPlayers; i++) {
            players.add(new Player("Player" + i));
        }

        distributeCardsForPlayers();
        queue.addAll(players);
    }

    private Optional<Map.Entry<String, List<Card>>> getLostPlayer() {
        return playersHand.entrySet().stream()
                .filter(entry -> entry.getValue().size() == 1).findFirst();
    }

    public void startGame() {
        setupGame();
        // Add cards to the deck, you can modify this list according to your requirements.
        // For simplicity, let's assume the cards are represented as strings (e.g., "Ace", "King", "Queen", etc.).

        // Create threads for each player
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < numOfPlayers; i++) {
            threads.add(new Thread(players.get(i)));
        }
        // Start the threads
        for (int i = 0; i < numOfPlayers; i++) {
            threads.get(i).start();
        }

        try {
            // Wait for all players to finish playing
            for (int i = 0; i < numOfPlayers; i++) {
                threads.get(i).join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Game Over!");
    }

    public void distributeCardsForPlayers() {
        List<Card> cards = CardUtils.getPackOfCards();
        CardUtils.shuffleCards(cards);

        players.forEach(
                player ->
                        playersHand.put(player.getName(), new LinkedList<>())
        );


        int playerIndex = 0;
//        for (Card card : cards) {
//            Player player = players.get(playerIndex);
//            playersHand.get(player.getName()).add(card);
//            playerIndex = (playerIndex + 1) % numOfPlayers;
//        }
        IntStream.range(0, cards.size())
                .forEach(i -> {
                    Player player = players.get(i % numOfPlayers);
                    playersHand.get(player.getName()).add(cards.get(i));
                });


        // each player will sort his cards;
        players.forEach(
                player ->
                        sortCards(playersHand.get(player.getName()))
        );

        //add all players to queue

    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            OldMaidGame oldMaidGame = new OldMaidGame(4);
            oldMaidGame.startGame();
            System.out.println(oldMaidGame.getLostPlayer().get());
        }



    }


}