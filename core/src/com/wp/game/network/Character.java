package com.wp.game.network;

/**
 * Created by cyyfyy on 12/22/2017.
 */
public class Character {
    public String name;
    public String otherStuff;
    public int id;
    public int x;
    public int y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    /*
    This is the class where we store personal information that does not need to be sent to all the
    other players in the game. The player ip etc...
     */
    public class ServerCharacter extends Character{

    }
}