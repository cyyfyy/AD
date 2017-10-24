package com.ad.game.views;

import com.ad.game.Optimism;
import com.ad.game.OptimismModel;
import com.ad.game.controller.KeyboardController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by cfenderson on 10/24/17.
 */

public class MainScreen implements Screen{
    private Optimism parent;
    private OptimismModel model;
    private OrthographicCamera camera;
    private KeyboardController controller;
    private SpriteBatch sb;

    public MainScreen(Optimism optimism){
        parent = optimism;
        camera = new OrthographicCamera(32, 24);
        controller = new KeyboardController();
        model = new OptimismModel(controller, camera, parent.warehouse);

        sb = new SpriteBatch();
        sb.setProjectionMatrix(camera.combined);
        //get images here from assets folder
        //e.g. image = get("image.png");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(controller);
    }

    @Override
    public void render(float delta) {
        model.logicStep(delta);
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        sb.begin();
//        sb.draw(XYZ);
        sb.end();
    }

    @Override
    public void resize(int width, int height) {

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

    }
}
