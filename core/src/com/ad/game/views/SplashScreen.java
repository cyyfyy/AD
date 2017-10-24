package com.ad.game.views;

import com.ad.game.Optimism;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by cfenderson on 10/24/17.
 */

public class SplashScreen implements Screen{
    private Optimism parent;
    //private Animation houndAnimation;
    Image splashImage;

    private int currentLoadingStage = 0;

    public float countDown = 3f; //how long the splash screen takes
    private Stage stage;
    private Table table;

    public SplashScreen(Optimism optimism){
        parent = optimism;
        stage = new Stage(new ScreenViewport());
        System.out.println("Loading images....");
        parent.warehouse.queueAddLoadingImages();
        //houndAnimation = new Animation(0.07f, atlas.findRegions("flames/flames"), PlayMode.LOOP);

        //get images here from assets folder
        splashImage = parent.warehouse.manager.get("hound.png");
    }

    @Override
    public void show() {
        table = new Table();
        //render assets here
        table.row();
        table.add(splashImage);
        table.row();

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //load assets
        if (parent.warehouse.manager.update()) { //This returns true if loading is done
            currentLoadingStage+= 1;
            switch(currentLoadingStage){
                case 1:
                    System.out.println("Loading fonts....");
                    parent.warehouse.queueAddFonts();
                    break;
                case 2:
                    System.out.println("Loading Particle Effects....");
                    parent.warehouse.queueAddParticleEffects();
                    break;
                case 3:
                    System.out.println("Loading Sounds....");
                    parent.warehouse.queueAddSounds();
                    break;
                case 4:
                    System.out.println("Loading fonts....");
                    parent.warehouse.queueAddMusic();
                    break;
                case 5:
                    System.out.println("Finished");
                    break;
            }

            //check whether we should go to the menu yet
            if (currentLoadingStage >5){
                countDown -= delta;
                currentLoadingStage = 5;
                if(countDown < 0){
                    parent.changeScreen(Optimism.ScreenType.MENU);
                }
            }
        }
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
