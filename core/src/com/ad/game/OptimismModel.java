package com.ad.game;

import com.ad.game.controller.KeyboardController;
import com.ad.game.loader.AssetWarehouse;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Created by cfenderson on 10/24/17.
 */

public class OptimismModel {
    private KeyboardController controller;
    private AssetWarehouse warehouse;
    private OrthographicCamera camera;
    private Sound ping;
    private Sound boing;

    public static final int BOING_SOUND = 0;
    public static final int PING_SOUND = 1;

    public OptimismModel(KeyboardController argController, OrthographicCamera argCamera, AssetWarehouse argWarehouse){
        this.warehouse = argWarehouse;
        camera = argCamera;
        controller = argController;

        warehouse.queueAddSounds();
        warehouse.manager.finishLoading();
        // loads the 2 sounds we use
        ping = warehouse.manager.get("sounds/ping.wav");
        boing = warehouse.manager.get("sounds/boing.wav");

        //Make wizards here

    }

    /**
     * Game logic here such as processing keyboard actions
     */
    public void logicStep(float delta){

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
