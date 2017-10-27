package com.ad.game.loader;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by cfenderson on 10/24/17.
 */

public class AssetWarehouse {

    public final AssetManager manager = new AssetManager();

    // Sounds go here...
    public final String boingSound = "sounds/boing.wav";
    public final String pingSound = "sounds/ping.wav";

    // Music goes here...
    //public final String playingSong = "music/song.mp3";

    // Skin for UI goes here...
    public final String skin = "skins/flatUI.json";

    // Textures go here...
    public final String playerBanner = "blotch.png";
    public final String checked = "check.png";
    public final String unchecked = "nocheck.png";
    public final String loadingImage = "dragon.png";

    public void queueAddFonts(){

    }

    public void queueAddParticleEffects(){

    }

    public void queueAddImages(){
        manager.load(playerBanner, Texture.class);
        manager.load(checked, Texture.class);
        manager.load(unchecked, Texture.class);
    }

    // a small set of images used by the loading screen
    public void queueAddLoadingImages(){
        manager.load(loadingImage, Texture.class);
    }

    public void queueAddSkin(){
        SkinParameter params = new SkinParameter("skins/flatUI.atlas");
        manager.load(skin, Skin.class, params);

    }

    public void queueAddMusic(){
//        manager.load(playingSong, Music.class);
    }

    public void queueAddSounds(){
        manager.load(boingSound, Sound.class);
        manager.load(pingSound, Sound.class);
    }


}
