package com.wp.game.network;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.wp.game.Optimism;

import java.io.IOException;

/**
 * Created by UlmusLeo on 1/1/2018.
 */

public class GameNetClient {

    public static final int NOT_CONNECTED = 0;
    public static final int LOGGING_IN = 1;
    public static final int WAITING_TO_CONNECT = 2;
    public static final int CONNECTED_LOGGED_IN = 3;
    public static final int CONNECTION_ERROR = 4;



    final String logTag = "GameNetworkClient";
    Client client;
    Optimism game;

    volatile int connectionState = NOT_CONNECTED;
    long lastConnectionAttempt = 0;
    int connectionAttempt = 0;
    Network.GameServerError lastError;


    public GameNetClient(Optimism gameClient){
        game = gameClient;
        clientConfig();
    }
    public long timeToNextAttempt(){
        long time =  (long) Math.pow(2,connectionAttempt) - (System.currentTimeMillis() - lastConnectionAttempt);
        if (time < 0) return 0;
        return time;
    }
    public boolean attemptConnection(){
        System.out.println("Connection State: "+connectionState);

        final String host = "localhost";
        if(connectionAttempt > 0 && connectionState !=  CONNECTION_ERROR){
            if(timeToNextAttempt() > 0){
                connectionState = WAITING_TO_CONNECT;
                return false;
            }
        }

        connectionAttempt++;

        lastConnectionAttempt = System.currentTimeMillis();
        try {
            client.connect(5000, host, Network.port);
            // Server communication after connection can go here, or in Listener#connectionState().
        } catch (IOException ex) {
            Gdx.app.log(logTag,"Connection Issue");
            connectionState = WAITING_TO_CONNECT;
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
    public int getConnectionAttempt(){
        return connectionAttempt;
    }
    public Network.GameServerError getLastError(){
        return lastError;
    }
    public void reset(){
        connectionState = NOT_CONNECTED;
        connectionAttempt = 0;
        lastError = null;
        client.close();
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
                    System.out.println("connected and logged in");
                    connectionAttempt = 0;
                }
                return;
            }

            if(object instanceof Network.GameServerError){
                Network.GameServerError error = (Network.GameServerError) object;
                if(error.errorCode == Network.LOGIN_ERROR){
                    connectionState = CONNECTION_ERROR;
                    lastError = error;
                    System.out.println("Connection State: "+connectionState);
                }
                Log.error(error.errorCode + " " + error.message);
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
            if(connectionState == CONNECTED_LOGGED_IN) {
                connectionState = NOT_CONNECTED;
            }
        }
    }


}
