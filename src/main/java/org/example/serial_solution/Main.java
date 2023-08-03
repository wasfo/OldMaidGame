package org.example.serial_solution;


import org.example.serial_solution.CardGame;

public class Main {
    public static void main(String[] args) {
        CardGame cardGame = new CardGame(10);

        cardGame.distributeCardsForPlayers();

        cardGame.playGame();

        cardGame.displayWinners();
        System.out.println("________________________________________");


    }
}