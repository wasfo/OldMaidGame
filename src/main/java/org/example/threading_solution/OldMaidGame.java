package org.example.threading_solution;
import org.example.card.Card;
import org.example.card.CardUtils;
import java.util.*;
import java.util.stream.IntStream;
import static org.example.card.CardUtils.*;


public class OldMaidGame {
    private static List<Player> players;
    private final int numOfPlayers;
    static LinkedList<Player> queue;

    public static HashMap<String, List<Card>> getPlayersHand() {
        return Player.playersHand;
    }

    public OldMaidGame(int numOfPlayers) {
        this.numOfPlayers = numOfPlayers;
        queue = new LinkedList<>();
        players = new ArrayList<>();
    }

    static public int getRemainingCards() {
        int sum = 0;
        for (Player player : players) {
            sum += Player.playersHand.get(player.getName()).size();
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
        return Player.playersHand.entrySet().stream()
                .filter(entry -> entry.getValue().size() == 1).findFirst();
    }

    public void startGame() {
        setupGame();

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numOfPlayers; i++) {
            threads.add(new Thread(players.get(i)));
        }

        for (int i = 0; i < numOfPlayers; i++) {
            threads.get(i).start();
        }

        try {
            for (int i = 0; i < numOfPlayers; i++) {
                System.out.println(players.get(i).getName() + " has finished playing.");
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
                        Player.playersHand.put(player.getName(), new LinkedList<>())
        );


        IntStream.range(0, cards.size())
                .forEach(i -> {
                    Player player = players.get(i % numOfPlayers);
                    Player.playersHand.get(player.getName()).add(cards.get(i));
                });


        players.forEach(
                player -> sortCards(Player.playersHand.get(player.getName())));

    }

    public static void main(String[] args) {

        OldMaidGame oldMaidGame = new OldMaidGame(10);
        oldMaidGame.startGame();
        System.out.println("Loser player is -> " + oldMaidGame.getLostPlayer().get());

    }

}