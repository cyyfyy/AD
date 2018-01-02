package com.wp.game.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.wp.game.Optimism;
import com.wp.game.actors.ConnectionActor;
import com.wp.game.models.MainModel;
import com.wp.game.stages.MainStage;

/**
 * Created by cyyfyy on 10/24/17.
 */

public class MainScreen implements Screen{
    private Optimism parent;
    private Stage stage;
    private Skin skin;
    private boolean waiting = false;

    public MainScreen(Optimism optimism){
        parent = optimism;

        parent.warehouse.queueAddSkin();
        parent.warehouse.manager.finishLoading();
        skin = parent.warehouse.manager.get("skins/flatUI.json");

        stage = new MainStage(new ScreenViewport(),parent);
        parent.warehouse.queueAddSkin();
        parent.warehouse.manager.finishLoading();

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.addActor(new ConnectionActor(parent.netClient, skin));
        table.setFillParent(true);
        stage.addActor(table);

    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();

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
