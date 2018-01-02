package com.wp.game.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import com.wp.game.network.GameNetClient;

/**
 * Created by UlmusLeo on 1/2/2018.
 */

public class ConnectionActor extends Table{
    GameNetClient netClient;
    Label currentState;
    Skin skin;
    float elapsedTime = 0f;
    public ConnectionActor(GameNetClient netClient, Skin skin){
        this.netClient = netClient;
        currentState = new Label("Initializing...",skin);
        this.add(currentState);
        this.setFillParent(true);
    }

    public void act(float dt){
        String ellipsis = "";
        elapsedTime += dt;
        if( elapsedTime > 1)
            ellipsis = ".";
        if( elapsedTime > 2)
            ellipsis = "..";
        if( elapsedTime > 3)
            ellipsis = "...";
        if( elapsedTime > 4)
            elapsedTime = 0f;

        switch (netClient.getConnectionState()){
            case GameNetClient.NOT_CONNECTED:
                currentState.setText("Connecting" + ellipsis);
                break;
            case GameNetClient.LOGGING_IN:
                currentState.setText("Logging in" + ellipsis);
                break;
            case GameNetClient.CONNECTED_LOGGED_IN:
                currentState.setText("Logged in");
                break;
            default:
                currentState.setText("Unknown Connection state: " + netClient.getConnectionState());
        }

    }

}
