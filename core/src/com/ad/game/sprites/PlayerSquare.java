package com.ad.game.sprites;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by cyyfyy on 10/20/2017.
 */

public class PlayerSquare extends Sprite {
    private Color playerColor;
    public PlayerSquare(Texture texture, Color argColor){
        super(texture);
        playerColor = argColor;
    }

    public Color getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(Color color) {
        this.playerColor = color;
    }


}
