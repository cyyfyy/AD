package com.ad.game;

import com.ad.game.sprites.Wizard;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Optimism extends ApplicationAdapter {
	private final float UPDATE_TIME = 1/60f;
	float timer;
	SpriteBatch batch;
	private Socket socket;
	String playerId;
	Wizard player;
	Texture playerChar;
	Texture friendlyChar;
	HashMap<String, Wizard> friends;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		playerChar = new Texture("idleWiz.png");
		friendlyChar = new Texture("idleWiz2.png");
		friends = new HashMap<String, Wizard>();
		connectSocket();
		configSocketEvents();
	}

	public void handleInput(float dt){
		if (player != null){
			if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
				player.setPosition(player.getX() + (-200 * dt), player.getY());
			} else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
				player.setPosition(player.getX() + (200 * dt), player.getY());
			}
		}
	}

	public void updateServer(float dt){
		timer += dt;
		if(timer >= UPDATE_TIME && player != null && player.hasMoved()){
			JSONObject data = new JSONObject();
			try {
				data.put("x", player.getX());
				data.put("y", player.getY());
				socket.emit("playerMoved", data);
			} catch (JSONException e){
				Gdx.app.log("SOCKET.IO", "Error sending update data!");
			}
		}
	}

	@Override
	public void render () {
		handleInput(Gdx.graphics.getDeltaTime());
		updateServer(Gdx.graphics.getDeltaTime());
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		if(player != null){ //draw a player if they have connected
			player.draw(batch);
		}
		for(HashMap.Entry<String, Wizard> entry : friends.entrySet()){
			entry.getValue().draw(batch);
		}
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		playerChar.dispose();
		friendlyChar.dispose();
	}

	public void connectSocket(){
		try {
			socket = IO.socket("http://localhost:8080");
			socket.connect();
		} catch (Exception e){
			System.out.println("Error in connectSocket: " + e);
		}
	}

	public void configSocketEvents(){
		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				Gdx.app.log("SocketIO", "Connected");
				player = new Wizard(playerChar);
			}
		}).on("socketID", new Emitter.Listener(){
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					playerId = data.getString("id");
					Gdx.app.log("SocketIO", "My ID: " + playerId);
				} catch (JSONException e){
					Gdx.app.log("SocketIO", "Error getting id: " + e);
				}
			}
		}).on("newPlayer", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					String newPlayerId = data.getString("id");
					Gdx.app.log("SocketIO", "New player connected: " + newPlayerId);
					friends.put(newPlayerId, new Wizard(friendlyChar));
				} catch (JSONException e){
					Gdx.app.log("SocketIO", "Error getting new player id: " + e);
				}
			}
		}).on("playerDisconnected", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					String id = data.getString("id");
					friends.remove(id); //remove the player that disconnected from the game state
				} catch (JSONException e){
					Gdx.app.log("SocketIO", "Error getting player id: " + e);
				}
			}
		}).on("getPlayers", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONArray objects = (JSONArray) args[0];
				try {
					for(int i = 0; i < objects.length(); i++){
						Wizard coopPlayer = new Wizard(friendlyChar);
						Vector2 position = new Vector2();
						position.x = ((Double) objects.getJSONObject(i).getDouble("x")).floatValue();
						position.y = ((Double) objects.getJSONObject(i).getDouble("y")).floatValue();
						coopPlayer.setPosition(position.x, position.y);
						friends.put(objects.getJSONObject(i).getString("id"), coopPlayer);
					}
				} catch (JSONException e){
					Gdx.app.log("SocketIO", "Error getting player list: " + e);
				}
			}
		}).on("playerMoved", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try {
					String playerId = data.getString("id");
					Double x = data.getDouble("x");
					Double y = data.getDouble("y");
					if(friends.get(playerId) != null){
						friends.get(playerId).setPosition(x.floatValue(),y.floatValue());
					}
				} catch (JSONException e){
					Gdx.app.log("SocketIO", "Error getting player movement: " + e);
				}
			}
		});
	}
}
