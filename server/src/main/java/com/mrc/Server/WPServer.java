package com.mrc.Server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

import com.wp.game.network.Network;
import com.wp.game.network.Network.*;
import com.wp.game.network.Character;

/**
 * Created by cyyfyy on 12/22/2017.
 */
public class WPServer {
    Server server;
    HashSet<Character> loggedIn = new HashSet();
    World world;
    private final int WORLDSIZE = 33;//world size must be 2^n +1 e.g. 17, 33, 65 etc.
    BufferedWriter errorOut;

    public WPServer () throws IOException {
        server = new Server() {
            protected Connection newConnection () {
                // By providing our own connection implementation, we can store per
                // connection state without a connection ID to state look up.
                return new CharacterConnection();
            }
        };

        // For consistency, the classes to be sent over the network are
        // registered by the same method for both the client and server.
        Network.register(server);

        server.addListener(new Listener() {
            public void received (Connection c, Object object) {
                // We know all connections for this server are actually CharacterConnections.
                CharacterConnection connection = (CharacterConnection)c;
                Character character = connection.character;

                if (object instanceof Login) {
                    // Ignore if already logged in.
                    if (character != null) return;

                    // Reject if the name is invalid.
                    String name = ((Login)object).name;
                    if (!isValid(name)) {
                        sendError(c , Network.LOGIN_ERROR ,  "Invalid Name" );
                        return;
                    }

                    // Reject if already logged in.
                    for (Character other : loggedIn) {
                        if (other.name.equals(name)) {
                            sendError(c , Network.LOGIN_ERROR ,  "Already Logged in" );
                            c.close();
                            return;
                        }
                    }

                    //try and find a saved character
                    character = loadCharacter(name);

                    // Ask for registration if we could not load the character
                    if (character == null) {
                        c.sendTCP(new RegistrationRequired());
                        return;
                    }

                    //found saved character, log them in
                    loggedIn(connection, character);
                    return;
                }

                if (object instanceof Register) {
                    // Ignore if already logged in.
                    if (character != null) return;

                    Register register = (Register)object;

                    // Reject if the login is invalid.
                    if (!isValid(register.name)) {
                        sendError(c , Network.LOGIN_ERROR ,  "Invalid Name" );
                        c.close();
                        return;
                    }
                    if (!isValid(register.otherStuff)) {
                        sendError(c , Network.LOGIN_ERROR ,  "Invalid Other Stuff" );
                        c.close();
                        return;
                    }

                    // Reject if character already exists.
                    if (loadCharacter(register.name) != null) {
                        sendError(c , Network.LOGIN_ERROR ,  "Duplicate Name" );
                        c.close();
                        return;
                    }

                    character = new Character();
                    character.name = register.name;
                    character.otherStuff = register.otherStuff;
                    character.x = 0;
                    character.y = 0;
                    if (!saveCharacter(character)) {
                        sendError(c , Network.LOGIN_ERROR ,  "Error Saving Character" );
                        c.close();
                        return;
                    }

                    //new character created, log them in
                    loggedIn(connection, character);
                    return;
                }

                //TODO: not currently using MoveCharacter
                if (object instanceof MoveCharacter) {
                    // Ignore if not logged in.
                    if (character == null) return;

                    MoveCharacter msg = (MoveCharacter)object;

                    // Ignore if invalid move.
                    if (Math.abs(msg.x) != 1 && Math.abs(msg.y) != 1) return;

                    character.x += msg.x;
                    character.y += msg.y;
                    if (!saveCharacter(character)) {
                        connection.close();
                        return;
                    }

                    UpdateCharacter update = new UpdateCharacter();
                    update.id = character.id;
                    update.x = character.x;
                    update.y = character.y;
                    server.sendToAllTCP(update);
                    return;
                }
            }

            //Name must not be empty
            private boolean isValid (String value) {
                if (value == null) return false;
                value = value.trim();
                if (value.length() == 0) return false;
                return true;
            }

            //let everyone know that someone disconnected
            public void disconnected (Connection c) {
                CharacterConnection connection = (CharacterConnection)c;
                if (connection.character != null) {
                    loggedIn.remove(connection.character);

                    RemoveCharacter removeCharacter = new RemoveCharacter();
                    removeCharacter.id = connection.character.id;
                    server.sendToAllTCP(removeCharacter);
                }
            }
        });
        server.bind(Network.port);
        server.start();
    }
    void sendError(Connection c, int code, String message){
        // we are sending an error to the client. Log the message we are
        // going to send and then send it.
        logError(code, message);

        GameServerError errorCode = new GameServerError();
        errorCode.errorCode = code;
        errorCode.message = message;
        c.sendTCP(errorCode);
        while(!c.isIdle()){
            System.out.println("Wait for non idle");
            try {
                this.wait(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
    void logError(int code, String message){
        int tryAgain = 0;
        File file = new File("logs", "error.log");
        while(tryAgain < 2) {
            try {
                StringBuilder errorBuilder = new StringBuilder();
                errorBuilder.append(code);
                errorBuilder.append(": ");
                errorOut = new BufferedWriter(new FileWriter(file,true));

                SimpleDateFormat sdf = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss.SSS] ");
                Date now = new Date();
                errorBuilder.append(sdf.format(now));
                errorBuilder.append("Message: ");
                errorBuilder.append(message);
                errorBuilder.append('\n');

                errorOut.write(errorBuilder.toString());
                errorOut.flush();
                break;
            } catch (IOException e) {
                    file.getParentFile().mkdirs();
                    tryAgain += 1;
            }
        }

    }

    //someone logged in -- add them to active player list
    void loggedIn (CharacterConnection c, Character character) {
        Login loginResponse = new Login();
        loginResponse.name = character.name;
        c.sendTCP(loginResponse);

        c.character = character;

        // Send existing characters to new logged in connection.
        for (Character other : loggedIn) {
            AddCharacter addCharacter = new AddCharacter();
            addCharacter.character = other;
            c.sendTCP(addCharacter);
        }

        loggedIn.add(character);

        // Send logged in character to all other connections.
        AddCharacter addCharacter = new AddCharacter();
        addCharacter.character = character;
        server.sendToAllTCP(addCharacter);
    }

    //Serialize character to file in assets folder for later lookup
    boolean saveCharacter (Character character) {
        File file = new File("characters", character.name.toLowerCase());
        file.getParentFile().mkdirs();

        //assign next id to this character
        if (character.id == 0) {
            String[] children = file.getParentFile().list();
            if (children == null) return false;
            character.id = children.length + 1;
        }

        //write character to file
        DataOutputStream output = null;
        try {
            output = new DataOutputStream(new FileOutputStream(file));
            output.writeInt(character.id);
            output.writeUTF(character.otherStuff);
            output.writeInt(character.x);
            output.writeInt(character.y);
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            try {
                output.close();
            } catch (IOException ignored) {
            }
        }
    }

    //get character from saved file if any such file exists
    Character loadCharacter (String name) {
        File file = new File("characters", name.toLowerCase());
        if (!file.exists()) return null; //could not find a saved character
        DataInputStream input = null;
        try {
            input = new DataInputStream(new FileInputStream(file));
            Character character = new Character();
            character.id = input.readInt();
            character.name = name;
            character.otherStuff = input.readUTF();
            character.x = input.readInt();
            character.y = input.readInt();
            input.close();
            return character;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } finally {
            try {
                if (input != null) input.close();
            } catch (IOException ignored) {
            }
        }
    }

    // This holds per connection state.
    static class CharacterConnection extends Connection {
        public Character character;
    }

    public static void main (String[] args) throws IOException {
        new WPServer();
    }
}
