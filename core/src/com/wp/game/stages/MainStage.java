package com.wp.game.stages;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.wp.game.Optimism;
import com.wp.game.network.Character;
import com.wp.game.network.GameNetClient;
import com.wp.game.network.Network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


/**
 * Created by UlmusLeo on 12/25/2017.
 */

public class MainStage extends Stage {
    private Sound ping;
    private Sound boing;

    private final float UPDATE_TIME = 1/60f;

    private HashMap<Integer, Character> connectedPlayers;
    private ArrayList<Vector2> positions;
    private ShapeRenderer shapeRenderer;

    private int[][] world;

    public static final int BOING_SOUND = 0;
    public static final int PING_SOUND = 1;

    public boolean waiting = true;
    GameNetClient netClient;

    public MainStage(ScreenViewport viewport, Optimism game){
        super(viewport);

        netClient = game.netClient;
    }


    private void handleInput(float dt){
        //nah
    }

    public void updateServer(float dt){
        if(netClient.getConnectionState() == GameNetClient.NOT_CONNECTED){
            netClient.attemptConnection();
        }
//        timer += dt;
//        if(timer >= UPDATE_TIME && player != null && player.hasMoved()){
//            JSONObject data = new JSONObject();
//            try {
//                data.put("x", player.getX());
//                data.put("y", player.getY());
//                socket.emit("playerMoved", data);
//            } catch (JSONException e){
//                Gdx.app.log("SOCKET.IO", "Error sending update data!");
//            }
//        }
    }



    public void playSound(int sound){
        switch(sound){
            case BOING_SOUND:
                boing.play();
                break;
            case PING_SOUND:
                ping.play();
                break;
        }
    }

    @Override
    public void act(float dt){
        handleInput(dt);
        updateServer(dt);
        super.act(dt);
    }

}
