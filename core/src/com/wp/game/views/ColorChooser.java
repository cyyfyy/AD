package com.wp.game.views;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * Created by cfenderson on 10/26/17.
 */

public class ColorChooser {
    public Color colorChoice;

    public void createColorChooser(Table table, Skin skin){
        //create buttons
        TextButton red = new TextButton("RED", skin);
        TextButton orange = new TextButton("ORANGE", skin);
        TextButton yellow = new TextButton("YELLOW", skin);
        TextButton green = new TextButton("GREEN", skin);
        TextButton blue = new TextButton("BLUE", skin);

        //add buttons to table
        table.add(red);
        table.add(orange);
        table.add(yellow);
        table.add(green);
        table.add(blue);

        // create button listeners
        red.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                colorChoice = new Color(Color.RED);
                System.out.println("RED color chosen.");
            }
        });

        orange.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                colorChoice = new Color(Color.ORANGE);
                System.out.println("ORANGE color chosen.");
            }
        });

        yellow.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                colorChoice = new Color(Color.YELLOW);
                System.out.println("YELLOW color chosen.");
            }
        });

        green.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                colorChoice = new Color(Color.GREEN);
                System.out.println("GREEN color chosen.");
            }
        });

        blue.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                colorChoice = new Color(Color.BLUE);
                System.out.println("BLUE color chosen.");
            }
        });
    }
}
