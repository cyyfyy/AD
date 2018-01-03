package com.wp.game.models;

/**
 * Created by UlmusLeo on 12/25/2017.
 * The PlayerModel is a thread safe class.
 */

public class PlayerModel {
    public static final int CHOOSING = 0;
    public static final int SUBMITTING = 1;
    public static final int WAITING = 2;

    public static final int CHOICE_NONE = 0;
    public static final int CHOICE_ROCK = 1;
    public static final int CHOICE_PAPER = 2;
    public static final int CHOICE_SCISSORS = 3;

    public static final int LOCAL_USER_ID = -1;


    private int playerId;
    private int RPSPoints = 0;
    private int playerState = WAITING;
    private int playerChoiceRPS = CHOICE_NONE;
    private boolean localUser = false;
    private String playerName = "";

    public PlayerModel(int playerId){
        this.playerId = playerId;
        if(playerId == LOCAL_USER_ID)
            localUser = true;
    }

    public synchronized void updateState(int newState){
        playerState = newState;
    }
    public synchronized void updateChoice(int newChoice){
        playerChoiceRPS = newChoice;
    }
    public synchronized void clearChoice(){
        playerChoiceRPS = CHOICE_NONE;
    }
    public synchronized void givePoint(){
        RPSPoints += 1;
    }
    public synchronized void setPlayerId(int playerId){
        this.playerId = playerId;
    }
    public synchronized void setPlayerName(String name){
        playerName = name;
    }

    public synchronized boolean isLocalUser(){
        return localUser;
    }
    public synchronized int getRPSPoints(){
        return RPSPoints;
    }
    public synchronized int getId(){
        return playerId;
    }
    public synchronized int getChoice(){
        return playerChoiceRPS;
    }
    public synchronized int getPlayerState(){
        return playerState;
    }
    public synchronized String getPlayerName() {
        return playerName;
    }
}
