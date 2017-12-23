package com.wp.game.commonClasses;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import java.lang.*;
import java.util.ArrayList;


/**
 * Created by cyyfyy on 12/22/2017.
 * This class is a convenient place to keep things common to both the client and server.
 */
public class Network {
    static public final int port = 54555;

    // This registers objects that are going to be sent over the network.
    static public void register (EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(Login.class);
        kryo.register(RegistrationRequired.class);
        kryo.register(Register.class);
        kryo.register(AddCharacter.class);
        kryo.register(UpdateCharacter.class);
        kryo.register(RemoveCharacter.class);
        kryo.register(Character.class);
        kryo.register(MoveCharacter.class);
        kryo.register(CardChoice.class);
        kryo.register(State.class);
    }

    static public class Login {
        public String name;
    }

    static public class RegistrationRequired {
    }

    static public class Register {
        public String name;
        public String otherStuff;
    }

    static public class UpdateCharacter {
        public int id, x, y;
    }

    static public class AddCharacter {
        public Character character;
    }

    static public class RemoveCharacter {
        public int id;
    }

    static public class MoveCharacter {
        public int x, y;
    }

    static public class CardChoice {
        public int discard;
        public int ban;
        public int pick;
    }

    static public class State {
        public ArrayList<Pair> cities;
        public ArrayList<Pair> cards;
        public ArrayList<Pair> otherStuff;
    }

    public class Pair {
        public int first;
        public int second;
    }
}
