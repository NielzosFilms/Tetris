package system;

import objects.Wall;
import objects.tetrominos.Tetromino_I;
import objects.tetrominos.Tetromino_J;

import java.awt.*;
import java.awt.image.BufferStrategy;

public class Game extends Canvas implements Runnable {
	public static final int TILESIZE = 32;
	public static final int SCREEN_WIDTH = TILESIZE*12, SCREEN_HEIGHT = TILESIZE*22;
	public static final String TITLE = "Tetris | NielzosFilms";

	public static Thread thread;
	public static Canvas canvas;
	public static boolean running = true;
	public static int current_fps = 0;

	public static Handler handler = new Handler();

	public static KeyInput keyInput = new KeyInput();
	public static MouseInput mouseInput = new MouseInput();

	public Game() {
		for(int x=0; x<SCREEN_WIDTH; x+=TILESIZE) {
			handler.addObject(new Wall(x, 0));
			handler.addObject(new Wall(x, SCREEN_HEIGHT-TILESIZE));
		}
		for(int y=TILESIZE; y<SCREEN_HEIGHT-TILESIZE; y+=TILESIZE) {
			handler.addObject(new Wall(0, y));
			handler.addObject(new Wall(SCREEN_WIDTH-TILESIZE, y));
		}

		//handler.addObject(new Tetromino_I(64, 64));
		handler.setCurrent_tetromino(new Tetromino_I(64, 64));

		this.addKeyListener(keyInput);
		this.addMouseListener(mouseInput);
		this.addMouseMotionListener(mouseInput);
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
		handler.tick();
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

		handler.render(g);

		g.setFont(new Font("Arial", Font.PLAIN, 10));
		g.setColor(ColorPalette.white.color);
		g.drawString(String.valueOf(current_fps), 0, 10);

		g.dispose();
		g2d.dispose();
		bs.show();
	}

	public static void main(String[] args) {
		canvas = new Game();
	}

}
