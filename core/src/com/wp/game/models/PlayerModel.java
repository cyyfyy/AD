package com.wp.game.models;

/**
 * Created by UlmusLeo on 12/25/2017.
 */

public class PlayerModel {
    public static final int CHOOSING = 0;
    public static final int SUBMITTING = 1;
    public static final int WAITING = 2;

    public static final int CHOICE_NONE = 0;
    public static final int CHOICE_ROCK = 1;
    public static final int CHOICE_PAPER = 2;
    public static final int CHOICE_SCISSORS = 3;

    private int playerId;
    private int points = 0;
    private int playerState = WAITING;
    private int playerChoice = CHOICE_NONE;

    public PlayerModel(int playerId){
        this.playerId = playerId;
    }

    public void updateState(int newState){
        playerState = newState;
    }

    public void updateChoice(int newChoice){
        playerChoice = newChoice;
    }
    public void clearChoice(){
        playerChoice = CHOICE_NONE;
    }
    public void givePoint(){
        points += 1;
    }
    public int getPoints(){
        return points;
    }

    public int getId(){
        return playerId;
    }
    public int getChoice(){
        return playerChoice;
    }
    public int getPlayerState(){
        return playerState;
    }
}
