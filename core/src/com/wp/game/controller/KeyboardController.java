package com.wp.game.controller;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by cyyfyy on 10/24/17.
 */
public class KeyboardController implements InputProcessor {
    public boolean left,right,up,down;
    public boolean mouse1Down, mouse2Down, mouse3Down;
    public boolean isDragged;
    public Vector2 mouseLocation = new Vector2(0,0);

    @Override
    public boolean keyDown(int keycode) {
        boolean keyProcessed = false;
        switch (keycode) //check input
        {
            case Keys.LEFT:  	//if keycode is Keys.LEFT
                left = true;	//do this
                keyProcessed = true;	//we have reacted to a keypress
                break;
            case Keys.RIGHT:
                right = true;
                keyProcessed = true;
                break;
            case Keys.UP:
                up = true;
                keyProcessed = true;
                break;
            case Keys.DOWN:
                down = true;
                keyProcessed = true;
        }
        return keyProcessed;	//  return flag
    }
    @Override
    public boolean keyUp(int keycode) {
        boolean keyProcessed = false;
        switch (keycode)
        {
            case Keys.LEFT:
                left = false;
                keyProcessed = true;
                break;
            case Keys.RIGHT:
                right = false;
                keyProcessed = true;
                break;
            case Keys.UP:
                up = false;
                keyProcessed = true;
                break;
            case Keys.DOWN:
                down = false;
                keyProcessed = true;
        }
        return keyProcessed;	//  return flag
    }
    @Override
    public boolean keyTyped(char character) {
        System.out.println(character);
        return false;
    }
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(button == 0){
            mouse1Down = true;
        }else if(button == 1){
            mouse2Down = true;
        }else if(button == 2){
            mouse3Down = true;
        }
        mouseLocation.x = screenX;
        mouseLocation.y = screenY;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        isDragged = false;
        if(button == 0){
            mouse1Down = false;
        }else if(button == 1){
            mouse2Down = false;
        }else if(button == 2){
            mouse3Down = false;
        }
        mouseLocation.x = screenX;
        mouseLocation.y = screenY;
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        isDragged = true;
        mouseLocation.x = screenX;
        mouseLocation.y = screenY;
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mouseLocation.x = screenX;
        mouseLocation.y = screenY;
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
