package com.wp.game.models;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.wp.game.commonClasses.Character;
import com.wp.game.commonClasses.Network;
import com.wp.game.commonClasses.Network.*;
import com.wp.game.loader.AssetWarehouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


/**
 * Created by cfenderson on 10/24/17.
 */

public class MainModel {
    private AssetWarehouse warehouse;
    private Sound ping;
    private Sound boing;

    private final float UPDATE_TIME = 1/60f;
    private Character player;
    private Texture playerBanner;
    private Texture playerBanner2;
    private Texture check;
    private Texture nocheck;
    private HashMap<Integer, Character> connectedPlayers;
    private ArrayList<Vector2> positions;

    public static final int BOING_SOUND = 0;
    public static final int PING_SOUND = 1;

    public boolean waiting = true;
    Client client;
    String name;

    public MainModel(AssetWarehouse argWarehouse){
        this.warehouse = argWarehouse;

        warehouse.queueAddSounds();
        warehouse.queueAddImages();
        warehouse.manager.finishLoading();

        playerBanner = warehouse.manager.get("idleWIZ.png");
        playerBanner2 = warehouse.manager.get("idleWIZ2.png");
        check = warehouse.manager.get("check.png");
        nocheck = warehouse.manager.get("nocheck.png");
        ping = warehouse.manager.get("sounds/ping.wav");
        boing = warehouse.manager.get("sounds/boing.wav");
        connectedPlayers = new HashMap<Integer, Character>();

        positions = new ArrayList<Vector2>();
        positions.add(new Vector2(350, 50));
        positions.add(new Vector2(200, 200));
        positions.add(new Vector2(250, 350));
        positions.add(new Vector2(400, 350));
        positions.add(new Vector2(500, 200));

        clientConfig(); //start client and add listeners
    }

    public void connect(){

        final String host = "localhost";

        try {
            client.connect(5000, host, Network.port);
            // Server communication after connection can go here, or in Listener#connected().
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        //name = getNameFromUser
        Login login = new Login();
        login.name = "connect";
        client.sendTCP(login);
        playSound(0);
    }

    public void draw(Batch batch){
        batch.begin();
        if(waiting) {
            if (player != null) { //draw a player if they have connected
                batch.draw(playerBanner, player.getX(), player.getY());
            }
            for (HashMap.Entry<Integer, Character> entry : connectedPlayers.entrySet()) {
                batch.draw(playerBanner, entry.getValue().getX(), entry.getValue().getY());
            }
        } else {
            //TODO: game code here
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

    private void clientConfig(){
        client = new Client();
        client.start();
        Network.register(client);

        // ThreadedListener runs the listener methods on a different thread.
        client.addListener(new Listener.ThreadedListener(new Listener() {
            public void connected (Connection connection) {
            }

            public void received (Connection connection, Object object) {
                if (object instanceof RegistrationRequired) {
                    Register register = new Register();
                    register.name = "connect";//UUID.randomUUID().toString();
                    register.otherStuff = "other";
                    client.sendTCP(register);
                }

                if (object instanceof AddCharacter) {
                    AddCharacter msg = (AddCharacter)object;
                    connectedPlayers.put(msg.character.id, msg.character);
                    return;
                }

                if (object instanceof UpdateCharacter) {
                    return;
                }

                if (object instanceof RemoveCharacter) {
                    RemoveCharacter msg = (RemoveCharacter)object;
                    connectedPlayers.remove(msg.id);
                    return;
                }
            }

            public void disconnected (Connection connection) {
                System.exit(0);
            }
        }));
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
