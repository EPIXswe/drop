package com.badlogic.drop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Drop extends Game {

	public BitmapFont font;
	public SpriteBatch batch;

	@Override
	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont();

		// Set screen (is disposed when user touches screen)
		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void dispose () {
		batch.dispose();
		font.dispose();
		getScreen().dispose();
	}
}
