package org.example.card;

import java.util.*;

public class CardUtils {

    static public void sortCards(List<Card> playerHand) {
        playerHand.sort(new CardUtils.CardNumberComparator());
        playerHand.sort(new CardUtils.CardColorComparator());
    }

   static public void removeAllPairs(List<Card> playerHand) {
        sortCards(playerHand);
        for (int i = 1; i < playerHand.size(); i++) {
            Card currentCard = playerHand.get(i);
            Card nextCard = playerHand.get(i - 1);
            if (cardsMatch(currentCard, nextCard)) {
                playerHand.remove(currentCard);
                playerHand.remove(nextCard);
                i = 0;
            }
        }
    }
    public static List<Card> getPackOfCards() {
        List<Card> cards = new ArrayList<>();

        cards.addAll(getSetOf(CardType.Clubs, CardColor.Black));

        cards.addAll(getSetOf(CardType.Spades, CardColor.Black));

        cards.addAll(getSetOf(CardType.Diamonds, CardColor.Red));

        cards.addAll(getSetOf(CardType.Hearts, CardColor.Red));

        cards.add(new Card(CardType.Joker, CardNumber.JOKER, CardColor.Red));

        return cards;
    }

    public static <T extends Enum<?>> T getRandomCard(Class<T> enumClass) {
        Random random = new Random();
        T[] enumValues = enumClass.getEnumConstants();
        return enumValues[random.nextInt(enumValues.length)];
    }

    static public boolean cardsMatch(Card c1, Card c2) {
        return c1.getCardColor() == c2.getCardColor() && c1.getCardNumber() == c2.getCardNumber();
    }

    private static List<Card> getSetOf(CardType type, CardColor color) {
        List<Card> cards = new ArrayList<>();
        for (CardNumber cardNumber : CardNumber.values()) {
            if (cardNumber.equals(CardNumber.JOKER))
                continue;
            cards.add(new Card(type, cardNumber, color));
        }
        return cards;
    }


    public static void shuffleCards(List<Card> cards) {
        Collections.shuffle(cards);
    }

    public static class CardNumberComparator implements Comparator<Card> {
        @Override
        public int compare(Card o1, Card o2) {
            return Integer.compare(o1.getCardNumber().ordinal(), o2.getCardNumber().ordinal());
        }
    }


    public static class CardColorComparator implements Comparator<Card> {
        @Override
        public int compare(Card o1, Card o2) {
            if (o1.getCardColor() == CardColor.Red && o2.getCardColor() == CardColor.Black)
                return -1;
            else if (o1.getCardColor() == CardColor.Black && o2.getCardColor() == CardColor.Red)
                return 1;
            return 0;
        }
    }

}

