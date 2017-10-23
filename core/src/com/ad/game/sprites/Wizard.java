package com.ad.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by cyyfyy on 10/20/2017.
 */

public class Wizard extends Sprite {
    Vector2 previousPosition;
    public Wizard(Texture texture){
        super(texture);
        previousPosition = new Vector2(getX(), getY());
    }

    public boolean hasMoved(){
        if(previousPosition.x != getX() || previousPosition.y != getY()){
            previousPosition.x = getX();
            previousPosition.y = getY();
            return true;
        }
        return false;
    }
}
