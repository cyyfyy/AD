package com.wp.game.models;

import java.util.ArrayList;

/**
 * Created by UlmusLeo on 12/25/2017.
 */

public class WorldModel {
    // Rock papaer sisors us a 2 person game
    PlayerModel[] players = new PlayerModel[2];
    int turn = 1;

    public synchronized void evaluate(){
        if(!all_submitted())
            force_choice();

        int result = evaluate_RPS(players[0].getChoice(),players[1].getChoice());
        switch (result){
            case 0: //tie
                break;
            case 1: // player 1 wins
                players[0].givePoint();
                break;
            case 2:
                players[1].givePoint();
                break;
        }

        turn += 1;
    }
    /*
    This method is entirely too clever by half
    Returns 1 if player 1 wins 2 if player 2 wins and 0 if a tie
    In this method 1 is rock 2 is paper 3 is scissors for values outside that range the methods
    returned value is undefined
     */
    private int evaluate_RPS(int player1, int player2){
        return (player1 - player2) % 3;
    }

    private boolean all_submitted(){
        for(PlayerModel player: players){
            if(player.getPlayerState() == PlayerModel.CHOOSING)
                return false;
        }
        return true;
    }

    private void force_choice(){
        for(PlayerModel player: players){
            if(player.getPlayerState() == PlayerModel.CHOOSING)
                player.updateState(((int)(Math.random()*3) + 1));
        }
    }


}
