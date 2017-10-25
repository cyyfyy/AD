package com.ad.game;

import com.ad.game.controller.KeyboardController;
import com.ad.game.loader.AssetWarehouse;
import com.ad.game.sprites.Wizard;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by cfenderson on 10/24/17.
 */

public class OptimismModel {
    private KeyboardController controller;
    private AssetWarehouse warehouse;
    private OrthographicCamera camera;
    private Sound ping;
    private Sound boing;

    private final float UPDATE_TIME = 1/60f;
    private float timer;
    private Socket socket;
    private String playerId;
    private Wizard player;
    private Texture playerChar;
    private Texture friendlyChar;
    private HashMap<String, Wizard> friends;

    public static final int BOING_SOUND = 0;
    public static final int PING_SOUND = 1;

    public OptimismModel(KeyboardController argController, OrthographicCamera argCamera, AssetWarehouse argWarehouse){
        this.warehouse = argWarehouse;
        camera = argCamera;
        controller = argController;

        warehouse.queueAddSounds();
        warehouse.queueAddImages();
        warehouse.manager.finishLoading();

        ping = warehouse.manager.get("sounds/ping.wav");
        boing = warehouse.manager.get("sounds/boing.wav");
        playerChar = warehouse.manager.get("idleWiz.png");
        friendlyChar = warehouse.manager.get("idleWiz2.png");
        friends = new HashMap<String, Wizard>();
        connectSocket();
        configSocketEvents();
    }

    public void draw(SpriteBatch batch){
        if(player != null){ //draw a player if they have connected
            batch.draw(player.getTexture(),player.getX(),player.getY(),2,2);
        }
        for(HashMap.Entry<String, Wizard> entry : friends.entrySet()){
            batch.draw(entry.getValue().getTexture(),entry.getValue().getX(),entry.getValue().getY(),2,2);
        }
    }

    /**
     * Game logic here such as processing keyboard actions
     */
    public void logicStep(float dt){
        handleInput(dt);
        updateServer(dt);
    }

    private void handleInput(float dt){
        if (player != null){
            if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
                player.setPosition(player.getX() + (-50 * dt), player.getY());
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
                player.setPosition(player.getX() + (50 * dt), player.getY());
            }
        }
    }

    public void updateServer(float dt){
        timer += dt;
        if(timer >= UPDATE_TIME && player != null && player.hasMoved()){
            JSONObject data = new JSONObject();
            try {
                data.put("x", player.getX());
                data.put("y", player.getY());
                socket.emit("playerMoved", data);
            } catch (JSONException e){
                Gdx.app.log("SOCKET.IO", "Error sending update data!");
            }
        }
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
                Gdx.app.log("SocketIO", "Connected");
                player = new Wizard(playerChar);
            }
        }).on("socketID", new Emitter.Listener(){
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    playerId = data.getString("id");
                    Gdx.app.log("SocketIO", "My ID: " + playerId);
                } catch (JSONException e){
                    Gdx.app.log("SocketIO", "Error getting id: " + e);
                }
            }
        }).on("newPlayer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String newPlayerId = data.getString("id");
                    Gdx.app.log("SocketIO", "New player connected: " + newPlayerId);
                    friends.put(newPlayerId, new Wizard(friendlyChar));
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
                    friends.remove(id); //remove the player that disconnected from the game state
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
                        Wizard coopPlayer = new Wizard(friendlyChar);
                        Vector2 position = new Vector2();
                        position.x = ((Double) objects.getJSONObject(i).getDouble("x")).floatValue();
                        position.y = ((Double) objects.getJSONObject(i).getDouble("y")).floatValue();
                        coopPlayer.setPosition(position.x, position.y);
                        friends.put(objects.getJSONObject(i).getString("id"), coopPlayer);
                    }
                } catch (JSONException e){
                    Gdx.app.log("SocketIO", "Error getting player list: " + e);
                }
            }
        }).on("playerMoved", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String playerId = data.getString("id");
                    Double x = data.getDouble("x");
                    Double y = data.getDouble("y");
                    if(friends.get(playerId) != null){
                        friends.get(playerId).setPosition(x.floatValue(),y.floatValue());
                    }
                } catch (JSONException e){
                    Gdx.app.log("SocketIO", "Error getting player movement: " + e);
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
