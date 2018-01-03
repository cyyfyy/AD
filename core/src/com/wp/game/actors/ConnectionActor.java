package com.wp.game.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import com.badlogic.gdx.utils.Align;
import com.wp.game.network.GameNetClient;

/**
 * Created by UlmusLeo on 1/2/2018.
 */

public class ConnectionActor extends Table{
    GameNetClient netClient;
    Label currentState;
    Label otherInfo;
    Skin skin;
    float elapsedTime = 0f;
    public ConnectionActor(GameNetClient netClient, Skin skin){
        this.netClient = netClient;
        currentState = new Label("Initializing...",skin);
        otherInfo = new Label("",skin);
        this.add(currentState);
        this.row();
        this.add(otherInfo);
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
                otherInfo.setText("");
                break;
            case GameNetClient.LOGGING_IN:
                currentState.setText("Logging in" + ellipsis);
                currentState.setAlignment(Align.topLeft);
                otherInfo.setText("");
                break;
            case GameNetClient.WAITING_TO_CONNECT:
                double timeToNextConnect = netClient.timeToNextAttempt()/1000.0;
                timeToNextConnect = Math.round(timeToNextConnect);
                currentState.setText("Waiting to connect. Attempt " + netClient.getConnectionAttempt());
                otherInfo.setText(String.format("Waiting %.0f seconds", timeToNextConnect));

                break;
            case GameNetClient.CONNECTED_LOGGED_IN:
                currentState.setText("Logged in");
                break;
            case GameNetClient.CONNECTION_ERROR:
                currentState.setText("Connection Error");
                otherInfo.setText(netClient.getLastError().message);

                break;
            default:
                currentState.setText("Unknown Connection state: " + netClient.getConnectionState());
        }

    }

}
