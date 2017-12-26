package com.wp.game.stages;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.wp.game.commonClasses.Character;
import com.wp.game.commonClasses.Network;
import com.wp.game.loader.AssetWarehouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


/**
 * Created by Jake on 12/25/2017.
 */

public class MainStage extends Stage {
    private AssetWarehouse warehouse;
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
    Client client;

    public MainStage(ScreenViewport viewport){
        super(viewport);
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
        Network.Login login = new Network.Login();
        login.name = "connect";
        client.sendTCP(login); //send login request to server -- use name to lookup potential saved character
        playSound(0);
    }

    private void handleInput(float dt){
        //nah
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

            public void received (Connection connection, Object object) { //received message from server
                if (object instanceof Network.RegistrationRequired) { //server did not find saved character
                    Network.Register register = new Network.Register();
                    register.name = UUID.randomUUID().toString();
                    register.otherStuff = "other";
                    client.sendTCP(register); //register this character with the server
                }

                if (object instanceof Network.AddCharacter) { //someone else connected
                    Network.AddCharacter character = (Network.AddCharacter)object;
                    connectedPlayers.put(character.character.id, character.character);
                    return;
                }

                if (object instanceof Network.UpdateCharacter) { //someone's state has changed e.g. they moved etc.
                    return;
                }

                if (object instanceof Network.RemoveCharacter) { //someone disconnected
                    Network.RemoveCharacter character = (Network.RemoveCharacter)object;
                    connectedPlayers.remove(character.id);
                    return;
                }

                if (object instanceof Network.GameStart) { //everyone connected, start the game
                    Network.GameStart start = (Network.GameStart) object;
                    world = start.world;
                    waiting = false;
                }
            }

            public void disconnected (Connection connection) {
                System.exit(0); //GTFO
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
    public void act(){
        float dt = 1/30.0f;
        handleInput(dt);
        updateServer(dt);
        super.act();
    }

}
