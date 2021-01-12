package system;

import objects.Wall;
import objects.tetrominos.Tetromino_I;
import objects.tetrominos.Tetromino_J;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.io.*;
import java.util.LinkedList;
import java.util.Random;

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

	public static int current_level = 1;
	public static int current_score = 0;
	public static final int MAX_LEVEL = 16;

	public static GameState gameState = GameState.start_screen;

	public static LinkedList<Integer> highscores = new LinkedList<>();

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
		//handler.setNextTetromino(160, 64);

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

		g.setColor(ColorPalette.white.color);

		if(gameState == GameState.start_screen) {
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
			g.setColor(ColorPalette.black.color);
			g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

			g.setFont(new Font("Arial", Font.BOLD, 30));
			g.setColor(ColorPalette.light_blue.color);
			g.drawString("Tetris", 128, 64);

			g.setFont(new Font("Arial", Font.BOLD, 20));
			g.drawString("Press [Space] to start...", 64, 10*TILESIZE);
			g.drawString("Highscores :", 12*TILESIZE+8, 64);
			g.drawString("Bindings :", 12*TILESIZE+8, 14*TILESIZE);

			g.setFont(new Font("Arial", Font.BOLD, 15));
			for(int i=0; i<5; i++) {
				if(i == 0) {
					g.setColor(ColorPalette.yellow.color);
				} else g.setColor(ColorPalette.white.color);
				if(i < highscores.size()) {
					g.drawString(i+1 + " : " + highscores.get(i), 12*TILESIZE+8, 96 + i*20);
				} else {
					g.drawString(i+1 + " : ---", 12*TILESIZE+8, 96 + i*20);
				}
			}

			g.setFont(new Font("Arial", Font.BOLD, 12));
			g.setColor(ColorPalette.white.color);
			g.drawString("[Left]", 12*TILESIZE+8, 15*TILESIZE);
			g.drawString("Move Tetromino", 14*TILESIZE, 15*TILESIZE);

			g.drawString("[Right]", 12*TILESIZE+8, 15*TILESIZE+16);
			g.drawString("Move Tetromino", 14*TILESIZE, 15*TILESIZE+16);

			g.drawString("[Down]", 12*TILESIZE+8, 16*TILESIZE);
			g.drawString("Soft drop", 14*TILESIZE, 16*TILESIZE);

			g.drawString("[Space]", 12*TILESIZE+8, 16*TILESIZE+16);
			g.drawString("Hard drop", 14*TILESIZE, 16*TILESIZE+16);

			g.drawString("[Up]", 12*TILESIZE+8, 17*TILESIZE);
			g.drawString("Rotate CW", 14*TILESIZE, 17*TILESIZE);

			g.drawString("[Z]", 12*TILESIZE+8, 17*TILESIZE+16);
			g.drawString("Rotate CCW", 14*TILESIZE, 17*TILESIZE+16);

			g.drawString("[C]", 12*TILESIZE+8, 18*TILESIZE);
			g.drawString("Hold Tetromino", 14*TILESIZE, 18*TILESIZE);
		} else if(gameState == GameState.game) {
			g.setColor(ColorPalette.light_blue.color);
			g.setFont(new Font("Arial", Font.BOLD, 15));
			g.drawString("Score :", 12*TILESIZE+8, 14*TILESIZE-8);
			g.drawString(String.valueOf(current_score), 14*TILESIZE+8, 14*TILESIZE-8);
			g.drawString("Level  :", 12*TILESIZE+8, 15*TILESIZE-8);
			g.drawString(String.valueOf(current_level), 14*TILESIZE+8, 15*TILESIZE-8);

			g.drawString("Next :", 12*TILESIZE+8, 2*TILESIZE-8);
			g.drawString("Holding :", 12*TILESIZE+8, 8*TILESIZE-8);
		} else if(gameState == GameState.end_screen) {
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
			g.setColor(ColorPalette.black.color);
			g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

			g.setColor(ColorPalette.light_blue.color);
			g.setFont(new Font("Arial", Font.BOLD, 30));
			g.drawString("Game Over!", 64, 64);
			g.setFont(new Font("Arial", Font.BOLD, 20));
			g.drawString("Score", 64, 128);
			g.drawString("Level", 64, 160);
			g.drawString(":", 144, 128);
			g.drawString(":", 144, 160);
			g.drawString(String.valueOf(current_score), 176, 128);
			g.drawString(String.valueOf(current_level), 176, 160);

			g.setFont(new Font("Arial", Font.BOLD, 20));
			g.drawString("Press [Space] to continue...", 64, 10*TILESIZE);
		}

		g.setFont(new Font("Arial", Font.PLAIN, 10));
		g.setColor(ColorPalette.white.color);
		g.drawString("FPS: " + current_fps, 0, 16);

		g.setFont(new Font("Arial", Font.BOLD, 10));
		g.setColor(ColorPalette.light_blue.color);
		g.drawString("Created By : NielzosFilms", 8, 22*TILESIZE-12);

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

	public static void addHighScore(int current_score) {
		if(current_score == 0) return;
		int index = 0;
		boolean place_score = false;
		for(int i=highscores.size()-1; i>=0; i--) {
			if(current_score > highscores.get(i)) {
				index = i;
				place_score = true;
			}
		}
		if(place_score) {
			highscores.add(index, current_score);
			while(highscores.size() > 5) {
				highscores.remove(5);
			}
		} else {
			highscores.add(current_score);
		}
	}

	public static void saveHighScores() {
		try {
			File file = new File("highscores.txt");
			file.createNewFile();
			FileWriter writer = new FileWriter(file, false);
			PrintWriter pwOb = new PrintWriter(writer, false);
			pwOb.flush();
			for(int score : highscores) {
				pwOb.println(score);
			}
			pwOb.close();
			writer.close();

		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	public static void loadHighScores() {
		try {
			File file = new File("highscores.txt");
			//FileWriter writer = new FileWriter(highscores);
			if(file.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(file));
				for(String line; (line = br.readLine()) != null; ) {
					highscores.add(Integer.parseInt(line));
				}
				System.out.println(highscores);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		loadHighScores();
		canvas = new Game();
	}

}
