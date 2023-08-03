package org.example.threading_solution;

import org.example.card.Card;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.example.card.CardUtils.cardsMatch;
import static org.example.card.CardUtils.removeAllPairs;

public class Player implements Runnable {
    private final String name;
    static HashMap<String, List<Card>> playersHand = new HashMap<>();
    private static final Lock lock = new ReentrantLock();
    private static final Lock removePairsLock = new ReentrantLock();

    public Player(String name) {
        this.name = name;
    }


    private Card drawCardFrom(Player player) {
        int size = Player.playersHand.get(player.getName()).size();
        int rand = (int) (Math.random() * size);
        return Player.playersHand.get(player.getName()).remove(rand);
    }

    public String getName() {
        return name;
    }

    private void playTurn() {
        synchronized (lock) {
            while (this != getCurrentPlayer()) {
                try {
//                    if (OldMaidGame.getRemainingCards() < 2) {
//                        return;
//                    }

                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Player currentPlayer = getCurrentPlayer();
            Player prevPlayer = getPreviousPlayer();

            if (Player.playersHand.get(prevPlayer.getName()).size() == 0) {
                nextTurn();
                lock.notifyAll();
                return;
            }

            Card drawnCard = drawCardFrom(prevPlayer);
            addCardOrRemovePair(Player.playersHand.get(currentPlayer.getName()), drawnCard);

            nextTurn();
            lock.notifyAll();


        }

    }

    private static Player getCurrentPlayer() {
        return OldMaidGame.queue.getFirst();
    }

    private static Player getPreviousPlayer() {
        return OldMaidGame.queue.getLast();
    }

    private static void nextTurn() {
        Player player = OldMaidGame.queue.removeFirst();
        OldMaidGame.queue.addLast(player);
    }

    @Override
    public void run() {

        synchronized (removePairsLock) {
            while (this.name != getCurrentPlayer().getName()) {
                try {
                    removePairsLock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            String name = getCurrentPlayer().getName();
            removeAllPairs(Player.playersHand.get(name));
            nextTurn();
            removePairsLock.notifyAll();
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        while (OldMaidGame.getRemainingCards() > 2) {
            playTurn();
        }


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