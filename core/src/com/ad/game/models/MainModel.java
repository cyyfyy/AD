package com.ad.game.models;

import com.ad.game.controller.KeyboardController;
import com.ad.game.loader.AssetWarehouse;
import com.ad.game.sprites.PlayerSquare;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by cfenderson on 10/24/17.
 */

public class MainModel {
    private AssetWarehouse warehouse;
    private Sound ping;
    private Sound boing;

    private final float UPDATE_TIME = 1/60f;
    private float timer;
    private Socket socket;
    private String playerId;
    private PlayerSquare player;
    private Color playerColor;
    private Texture playerBanner;
    private Texture check;
    private Texture nocheck;
    private HashMap<String, PlayerSquare> connectedPlayers;
    private ArrayList<Color> colors;
    private ArrayList<Vector2> positions;

    public static final int BOING_SOUND = 0;
    public static final int PING_SOUND = 1;

    public MainModel(AssetWarehouse argWarehouse){
        this.warehouse = argWarehouse;

        warehouse.queueAddSounds();
        warehouse.queueAddImages();
        warehouse.manager.finishLoading();

        playerBanner = warehouse.manager.get("blotch.png");
        check = warehouse.manager.get("check.png");
        nocheck = warehouse.manager.get("nocheck.png");
        ping = warehouse.manager.get("sounds/ping.wav");
        boing = warehouse.manager.get("sounds/boing.wav");
        connectedPlayers = new HashMap<String, PlayerSquare>();
        colors = new ArrayList<Color>();
        colors.add(Color.RED);
        colors.add(Color.ORANGE);
        colors.add(Color.YELLOW);
        colors.add(Color.GREEN);
        colors.add(Color.BLUE);
        positions = new ArrayList<Vector2>();
        positions.add(new Vector2(350, 50));
        positions.add(new Vector2(200, 200));
        positions.add(new Vector2(250, 350));
        positions.add(new Vector2(400, 350));
        positions.add(new Vector2(500, 200));
    }

    public void connect(Color chosenColor){
        playerColor = chosenColor;
        colors.remove(chosenColor);
        connectSocket();
        configSocketEvents();
        playSound(0);
    }

    public void draw(Batch batch){
        batch.begin();
        if(player != null){ //draw a player if they have connected
            batch.setColor(playerColor);
            batch.draw(player.getTexture(),player.getX(),player.getY());
        }
        for(HashMap.Entry<String, PlayerSquare> entry : connectedPlayers.entrySet()){
            batch.setColor(entry.getValue().getPlayerColor());
            batch.draw(entry.getValue().getTexture(),entry.getValue().getX(),entry.getValue().getY());
        }
        batch.end();
    }

    /**
     * Game logic here such as processing keyboard actions
     */
    public void logicStep(float dt){
        handleInput(dt);
        updateServer(dt);
    }

    private void handleInput(float dt){

    }

    public void updateServer(float dt){
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

    public void connectSocket(){
        try {
            socket = IO.socket("http://localhost:8080");
            socket.connect();
        } catch (Exception e){
            System.out.println("Error in connectSocket: " + e);
        }
    }

    public void configSocketEvents(){
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {

            }
        }).on("socketID", new Emitter.Listener(){
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    playerId = data.getString("id");
                    Gdx.app.log("SocketIO", "Connected");
                    player = new PlayerSquare(playerBanner, playerColor);
                    Vector2 pos = positions.remove(0);
                    player.setPosition(pos.x, pos.y);
                    Gdx.app.log("SocketIO", "My ID: " + playerId);
                } catch (JSONException e){
                    Gdx.app.log("SocketIO", "Error getting id: " + e);
                }
            }
        }).on("reject", new Emitter.Listener(){
            @Override
            public void call(Object... args) {
                Gdx.app.log("SocketIO", "Too many players!");
                Gdx.app.exit();
            }
        }).on("newPlayer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String newPlayerId = data.getString("id");
                    Gdx.app.log("SocketIO", "New player connected: " + newPlayerId);
                    PlayerSquare otherPlayer = new PlayerSquare(playerBanner, colors.remove(0)); //color is now taken
                    Vector2 pos = positions.remove(0);
                    otherPlayer.setPosition(pos.x, pos.y);
                    connectedPlayers.put(newPlayerId, otherPlayer); //color is now taken
                } catch (JSONException e){
                    Gdx.app.log("SocketIO", "Error getting new player id: " + e);
                }
            }
        }).on("playerDisconnected", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String id = data.getString("id");
                    colors.add(connectedPlayers.get(id).getPlayerColor()); //add the color back to the available list
                    positions.add(new Vector2(connectedPlayers.get(id).getX(), connectedPlayers.get(id).getY()));
                    connectedPlayers.remove(id); //remove the player that disconnected from the game state

                } catch (JSONException e){
                    Gdx.app.log("SocketIO", "Error getting player id: " + e);
                }
            }
        }).on("getPlayers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONArray objects = (JSONArray) args[0];
                try {
                    for(int i = 0; i < objects.length(); i++){
                        PlayerSquare otherPlayer = new PlayerSquare(playerBanner, colors.remove(0)); //color is now taken
                        Vector2 pos = positions.remove(0);
                        otherPlayer.setPosition(pos.x, pos.y);
                        connectedPlayers.put(objects.getJSONObject(i).getString("id"), otherPlayer);
                    }
                } catch (JSONException e){
                    Gdx.app.log("SocketIO", "Error getting player list: " + e);
                }
            }
        });
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
}
