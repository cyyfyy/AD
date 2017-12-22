package com.wp.game;

import com.wp.game.loader.AssetWarehouse;
import com.wp.game.views.EndScreen;
import com.wp.game.views.MainScreen;
import com.wp.game.views.MenuScreen;
import com.wp.game.views.PreferencesScreen;
import com.wp.game.views.SplashScreen;
import com.badlogic.gdx.Game;

public class Optimism extends Game {
	private SplashScreen splashScreen;
	private PreferencesScreen preferencesScreen;
	private MenuScreen menuScreen;
	private MainScreen mainScreen;
	private EndScreen endScreen;
	private AppPreferences preferences;
	public AssetWarehouse warehouse = new AssetWarehouse();
//	private Music song;

	public enum ScreenType {
		MENU, PREFERENCES, MAIN, END
	}
	
	@Override
	public void create () {
		splashScreen = new SplashScreen(this);
		preferences = new AppPreferences();
		setScreen(splashScreen);

//		warehouse.queueAddMusic(); //find any music defined in the warehouse
//		warehouse.manager.finishLoading(); //load music and wait for that to finish
//		song = warehouse.manager.get("music/song.mp3"); //<-- note the string here matches the string in the warehouse
//		song.play();

	}

	public void changeScreen(ScreenType screen){
		switch(screen){
			case MENU:
				if(menuScreen == null) menuScreen = new MenuScreen(this);
				this.setScreen(menuScreen);
				break;
			case PREFERENCES:
				if(preferencesScreen == null) preferencesScreen = new PreferencesScreen(this);
				this.setScreen(preferencesScreen);
				break;
			case MAIN:
				if(mainScreen == null) mainScreen = new MainScreen(this);
				this.setScreen(mainScreen);
				break;
			case END:
				if(endScreen == null) endScreen = new EndScreen(this);
				this.setScreen(endScreen);
				break;
		}
	}

	public AppPreferences getPreferences(){
		return this.preferences;
	}
	
	@Override
	public void dispose () {
		//song.dispose();
		warehouse.manager.dispose();
	}
}
