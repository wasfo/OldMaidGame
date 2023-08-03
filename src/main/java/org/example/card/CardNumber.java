package org.example.card;

public enum CardNumber {
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    JACK(11),
    QUEEN(12),
    KING(13),
    ACE(14),
    JOKER(15);

    private final int index;

    private CardNumber(int num) {
        this.index = num;
    }

    public int getIndex() {
        return index;
    }

}
