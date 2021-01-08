package system;

import java.awt.*;
import java.awt.image.BufferStrategy;

public class Game extends Canvas implements Runnable {
	public static final int SCREEN_WIDTH = 300, SCREEN_HEIGHT = 600;
	public static final String TITLE = "Tetris | NielzosFilms";

	public static final int TILESIZE = 24;

	public static Thread thread;
	public static Canvas canvas;
	public static boolean running = true;
	public static int current_fps = 0;

	public Game() {
		new Window(SCREEN_WIDTH, SCREEN_HEIGHT, TITLE, this);
	}

	public synchronized void start() {
		thread = new Thread(this);
		thread.start();
	}

	public synchronized void stop() {
		try {
			thread.join();
			running = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		this.requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int frames = 0;
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				tick();
				delta--;
			}
			if (running)
				render();
			frames++;

			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				current_fps = frames;
				frames = 0;
			}
		}
		stop();
	}

	private void tick() {

	}

	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		Graphics2D g2d = (Graphics2D) g;

		g.setColor(ColorPalette.black_dark_blue.color);
		g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

		g.setColor(ColorPalette.gray.color);
		g.fillRect(TILESIZE, TILESIZE, TILESIZE, TILESIZE);

		g.dispose();
		g2d.dispose();
		bs.show();
	}

	public static void main(String[] args) {
		canvas = new Game();
	}

}
