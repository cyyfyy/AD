package com.ad.game.loader;

import com.badlogic.gdx.assets.AssetManager;

/**
 * Created by cfenderson on 10/24/17.
 */

public class AssetWarehouse {

    public final AssetManager manager = new AssetManager();

    // Sounds go here...
    //public final String boingSound = "sounds/boing.wav";
    //public final String pingSound = "sounds/ping.wav";

    // Music goes here...
    //public final String playingSong = "music/song.mp3";

    // Skin for UI goes here...
    //public final String skin = "skin/ui.json";

    // Textures go here...
    //public final String gameImages = "images/game.atlas";
    //public final String loadingImages = "images/loading.atlas";

    public void queueAddFonts(){

    }

    public void queueAddParticleEffects(){

    }

    public void queueAddImages(){
//        manager.load(gameImages, TextureAtlas.class);
    }

    // a small set of images used by the loading screen
    public void queueAddLoadingImages(){
//        manager.load(loadingImages, TextureAtlas.class);
    }

    public void queueAddSkin(){
//        SkinParameter params = new SkinParameter("skin/glassy-ui.atlas");
//        manager.load(skin, Skin.class, params);

    }

    public void queueAddMusic(){
//        manager.load(playingSong, Music.class);
    }

    public void queueAddSounds(){
//        manager.load(boingSound, Sound.class);
//        manager.load(pingSound, Sound.class);
    }


}
