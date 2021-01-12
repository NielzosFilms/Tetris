package system;

import objects.Wall;
import objects.tetrominos.Tetromino_I;
import objects.tetrominos.Tetromino_J;

import java.awt.*;
import java.awt.image.BufferStrategy;

public class Game extends Canvas implements Runnable {
	public static final int TILESIZE = 32;
	public static final int PLAYSPACE_WIDTH = 12, PLAYSPACE_HEIGHT = 22;
	public static final int SCREEN_WIDTH = TILESIZE*(PLAYSPACE_WIDTH+7), SCREEN_HEIGHT = TILESIZE*PLAYSPACE_HEIGHT;
	public static final String TITLE = "Tetris | NielzosFilms";

	public static Thread thread;
	public static Canvas canvas;
	public static boolean running = true;
	public static int current_fps = 0;

	public static Handler handler = new Handler();

	public static KeyInput keyInput = new KeyInput();
	public static MouseInput mouseInput = new MouseInput();

	public static final BasicStroke stroke = new BasicStroke(4, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);

	public static int current_level = 0;
	public static int current_score = 0;
	public static final int MAX_LEVEL = 16;

	public Game() {
		for(int x=0; x<SCREEN_WIDTH; x+=TILESIZE) {
			handler.addObject(new Wall(x, 0));
			handler.addObject(new Wall(x, (PLAYSPACE_HEIGHT*TILESIZE)-TILESIZE));
		}
		for(int y=TILESIZE; y<SCREEN_HEIGHT-TILESIZE; y+=TILESIZE) {
			handler.addObject(new Wall(0, y));
			handler.addObject(new Wall((PLAYSPACE_WIDTH*TILESIZE)-TILESIZE, y));
			handler.addObject(new Wall(SCREEN_WIDTH-TILESIZE, y));
		}
		for(int x=0; x<6; x++) {
			handler.addObject(new Wall((PLAYSPACE_WIDTH * TILESIZE + (x * TILESIZE)), TILESIZE * 6));
			handler.addObject(new Wall((PLAYSPACE_WIDTH * TILESIZE + (x * TILESIZE)), TILESIZE * 12));
		}

		//handler.addObject(new Tetromino_I(64, 64));
		handler.setNextTetromino(160, 64);

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
		keyInput.tick();
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

		g2d.setStroke(stroke);
		g.setColor(ColorPalette.black.color);
		g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

		handler.render(g);

		g.setFont(new Font("Arial", Font.PLAIN, 10));
		g.setColor(ColorPalette.white.color);
		g.drawString("FPS: " + current_fps, 0, 10);

		g.setFont(new Font("Arial", Font.BOLD, 15));
		g.drawString("Score:", 0, 25);
		g.drawString(String.valueOf(current_score), 60, 25);
		g.drawString("Level:", 0, 40);
		g.drawString(String.valueOf(current_level), 60, 40);

		g.drawString("Next:", 12*Game.TILESIZE+8, 2*Game.TILESIZE);
		g.drawString("Holding:", 12*Game.TILESIZE+8, 8*Game.TILESIZE);

		g.dispose();
		g2d.dispose();
		bs.show();
	}

	public static void renderCube(Graphics g, int x, int y, Color bg, Color border) {
		int line_width = (int) stroke.getLineWidth();

		g.setColor(bg);
		g.fillRect(x+ line_width/2, y+line_width/2, TILESIZE-line_width, TILESIZE-line_width);
		g.setColor(border);
		g.drawRect(x+line_width/2, y+line_width/2, TILESIZE-line_width, TILESIZE-line_width);
	}

	public static void main(String[] args) {
		canvas = new Game();
	}

}
