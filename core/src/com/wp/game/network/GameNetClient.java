package com.wp.game.network;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.wp.game.Optimism;

import java.io.IOException;

/**
 * Created by UlmusLeo on 1/1/2018.
 */

public class GameNetClient {

    public static final int NOT_CONNECTED = 0;
    public static final int LOGGING_IN = 1;
    public static final int CONNECTED_LOGGED_IN = 2;

    final String logTag = "GameNetworkClient";
    Client client;
    Optimism game;

    volatile int connectionState = NOT_CONNECTED;


    public GameNetClient(Optimism gameClient){
        game = gameClient;
        clientConfig();
    }

    public boolean attemptConnection(){
        final String host = "localhost";

        try {
            client.connect(5000, host, Network.port);
            // Server communication after connection can go here, or in Listener#connectionState().
        } catch (IOException ex) {
            Gdx.app.log(logTag,"Connection Issue",ex);
            return false;
        }

        //name = getNameFromUser
        Network.Login login = new Network.Login();
        login.name = game.user.getPlayerName();
        client.sendTCP(login); //send login request to server -- use name to lookup potential saved character

        return true;
    }
    public int getConnectionState(){
        return connectionState;
    }

    private void clientConfig(){
        client = new Client();
        client.start();
        Network.register(client);
        // ThreadedListener runs the listener methods on a different thread.
        client.addListener(new Listener.ThreadedListener(new NetListener()));
    }

    private class NetListener extends Listener{
        public void connected (Connection connection) {
            connectionState = LOGGING_IN;
        }

        public void received (Connection connection, Object object) { //received message from server
            if (object instanceof Network.RegistrationRequired) { //server did not find saved character
                Network.Register register = new Network.Register();
                register.name = game.user.getPlayerName();
                register.otherStuff = "other";
                client.sendTCP(register); //register this character with the server
            }

            //after a successful login a login will be echoed back from the server
            if (object instanceof Network.Login) {
                Network.Login login = (Network.Login) object;
                if(login.name.equals(game.user.getPlayerName())){
                    connectionState = CONNECTED_LOGGED_IN;
                }
                return;
            }

            if (object instanceof Network.AddCharacter) { //someone else connectionState
                Network.AddCharacter character = (Network.AddCharacter) object;
                return;
            }

            if (object instanceof Network.UpdateCharacter) { //someone's state has changed e.g. they moved etc.
                return;
            }

            if (object instanceof Network.RemoveCharacter) { //someone disconnected
                Network.RemoveCharacter character = (Network.RemoveCharacter) object;
                return;
            }

            if (object instanceof Network.GameStart) { //everyone connectionState, start the game
                Network.GameStart start = (Network.GameStart) object;

            }
        }

        public void disconnected (Connection connection) {
            connectionState = NOT_CONNECTED;
        }
    }


}
