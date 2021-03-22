package com.example.tictactoeandroid.component;

public class GotWinnerException extends Exception{
    private String winner;

    public GotWinnerException(String winner){
        this.winner = winner;
    }

    public String getWinner() {
        return winner;
    }
}
