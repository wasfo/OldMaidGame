package org.example.card;

import java.util.Objects;

public class Card {

    private CardType cardType;
    private CardNumber cardNumber;
    private CardColor cardColor;

    public CardColor getCardColor() {
        return cardColor;
    }

    public void setCardColor(CardColor cardColor) {
        this.cardColor = cardColor;
    }

    public Card(CardType cardType, CardNumber cardNumber, CardColor cardColor) {
        this.cardType = cardType;
        this.cardNumber = cardNumber;
        this.cardColor = cardColor;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public CardNumber getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(CardNumber cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public String toString() {
        return "{ " + cardType + "," + cardNumber + "," + cardColor + " }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return cardNumber == card.cardNumber && cardColor == card.cardColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardType, cardNumber, cardColor);
    }
}
