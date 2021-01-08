package com.badlogic.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {

    private final Drop game;

    // Camera
    private OrthographicCamera camera;

    // Textures
    private Texture dropImage;
    private Texture bucketImage;

    // Sounds and music
    private Sound dropSound;
    private Music rainMusic;

    // Shapes
    Rectangle bucket;

    // Raindrops
    private Array<Rectangle> raindrops;
    private long lastDropTime;
    private int dropsGathered;

    public GameScreen(Drop game) {
        this.game = game;

        // Camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // Textures
        dropImage = new Texture(Gdx.files.internal("droplet.png"));
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));

        // Shapes
        bucket = new Rectangle();
        bucket.x = (float)(800 / 2) - (float)(64 / 2);
        bucket.y = 20;
        bucket.width = 64;
        bucket.height = 64;

        // Sounds
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

        // Play music
        rainMusic.setLooping(true);
        rainMusic.play();

        // Initiate raindrops
        raindrops = new Array<Rectangle>();

        spawnRaindrop();

    }

    @Override
    public void show() {
        // start the playback of the background music
        // when the screen is shown
        rainMusic.play();
    }

    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, 800-64);
        raindrop.y = 480;
        raindrop.width = 64;
        raindrop.height = 64;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void render (float delta) {

        // Set clear color to dark blue
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        // Clear screen with clear color (above)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render stuff
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        // Draw drops collected
        game.font.draw(game.batch, "Drops collected: " + dropsGathered, 0, 480);
        // Draw bucket
        game.batch.draw(bucketImage, bucket.x, bucket.y);
        // Draw raindrops
        for(Rectangle raindrop: raindrops) {
            game.batch.draw(dropImage, raindrop.x, raindrop.y);
        }
        game.batch.end();

        // Move bucket when screen is touched or mouse button is pressed
        if(Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = (int) touchPos.x - 64 / 2;
        }

        // Controls
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
            bucket.x -= 200 * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
            bucket.x += 200 * Gdx.graphics.getDeltaTime();

        // Prevent bucket from moving outside the screen
        if(bucket.x < 0) bucket.x = 0;
        if(bucket.x > 800 - 64) bucket.x = 800 - 64;

        // Spawn raindrops
        if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();

        for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
            Rectangle raindrop = iter.next();
            raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
            if(raindrop.overlaps(bucket)) {
                dropSound.play();
                iter.remove();
                dropsGathered++;
            }
            if(raindrop.y + 64 < 0) iter.remove();
        }

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
        dropImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
    }


}
