package com.wp.game.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.wp.game.Optimism;

/**
 * Created by cfenderson on 10/24/17.
 */

public class EndScreen implements Screen{
    private Optimism parent;
    private Stage stage;
    private Table table;

    public EndScreen(Optimism optimism){
        parent = optimism;
        stage = new Stage(new ScreenViewport());

        //get images here from assets folder
        //e.g. image = get("image.png");
    }

    @Override
    public void show() {
        table = new Table();
        //render assets here
        //e.g. table.add(img);
        //table.row();
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //load assets
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
