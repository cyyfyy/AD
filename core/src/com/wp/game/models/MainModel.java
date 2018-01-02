package com.wp.game.models;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.wp.game.network.Character;
import com.wp.game.network.Network;
import com.wp.game.network.Network.*;
import com.wp.game.loader.AssetWarehouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


/**
 * Created by cyyfyy on 10/24/17.
 * Commented by ulmusleo 12/25/17.
 *
 * So this is not how we should be using a model this should be split into a view and a model and
 * maybe a utility The model should only be game logic, independent of the graphics and networking,
 * the view should take data from the model and display it on screen. Maybe we have an interface
 * layer between these two or parallel to the view that does all the network stuff.
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
    private ShapeRenderer shapeRenderer;

    private int[][] world;

    public static final int BOING_SOUND = 0;
    public static final int PING_SOUND = 1;

    public boolean waiting = true;
    Client client;

    public MainModel(AssetWarehouse argWarehouse){
        this.warehouse = argWarehouse;

        warehouse.queueAddSounds();
        warehouse.queueAddImages();
        warehouse.manager.finishLoading();

        shapeRenderer = new ShapeRenderer();

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
        client.sendTCP(login); //send login request to server -- use name to lookup potential saved character
        playSound(0);
    }

    public void draw(Batch batch){
        batch.begin();
        if(waiting) { //not all players have connected
            if (player != null) { //draw a player if they have connected
                batch.draw(playerBanner, player.getX(), player.getY());
            }
            for (HashMap.Entry<Integer, Character> entry : connectedPlayers.entrySet()) {
                batch.draw(playerBanner, entry.getValue().getX(), entry.getValue().getY());
            }
        } else { //main game screen
            for (int i = 0; i < world.length; i++) {
                for (int j = 0; j < world.length; j++) {
                    if(world[i][j] == 1){
                        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                        shapeRenderer.setColor(Color.BLUE);
                        shapeRenderer.rect(5+(i*14), 5+(j*14), 14, 14);
                        shapeRenderer.end();
                    } else if(world[i][j] == 2){
                        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                        shapeRenderer.setColor(Color.YELLOW);
                        shapeRenderer.rect(5+(i*14), 5+(j*14), 14, 14);
                        shapeRenderer.end();
                    } else if(world[i][j] == 3){
                        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                        shapeRenderer.setColor(Color.GREEN);
                        shapeRenderer.rect(5+(i*14), 5+(j*14), 14, 14);
                        shapeRenderer.end();
                    } else if(world[i][j] == 4){
                        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                        shapeRenderer.setColor(Color.BROWN);
                        shapeRenderer.rect(5+(i*14), 5+(j*14), 14, 14);
                        shapeRenderer.end();
                    } else if(world[i][j] == 5){
                        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                        shapeRenderer.setColor(Color.GRAY);
                        shapeRenderer.rect(5+(i*14), 5+(j*14), 14, 14);
                        shapeRenderer.end();
                    } else {

                    }
                }
            }
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
                if (object instanceof RegistrationRequired) { //server did not find saved character
                    Register register = new Register();
                    register.name = UUID.randomUUID().toString();
                    register.otherStuff = "other";
                    client.sendTCP(register); //register this character with the server
                }

                if (object instanceof AddCharacter) { //someone else connected
                    AddCharacter character = (AddCharacter)object;
                    connectedPlayers.put(character.character.id, character.character);
                    return;
                }

                if (object instanceof UpdateCharacter) { //someone's state has changed e.g. they moved etc.
                    return;
                }

                if (object instanceof RemoveCharacter) { //someone disconnected
                    RemoveCharacter character = (RemoveCharacter)object;
                    connectedPlayers.remove(character.id);
                    return;
                }

                if (object instanceof GameStart) { //everyone connected, start the game
                    GameStart start = (GameStart) object;
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
}
