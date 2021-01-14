package system;

import audioEngine.AudioFiles;
import audioEngine.AudioPlayer;
import objects.Effect_Clear_Cube;
import objects.Effect_Score_Text;
import objects.Effect_Special_Text;
import objects.Tetromino_Cube;
import objects.tetrominos.*;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class Handler {
	private final int MAX_TIMER = 4 * Game.MAX_LEVEL;
	LinkedList<GameObject> objects = new LinkedList<>();
	private GameObject current_tetromino;
	private LinkedList<GameObject> tetromino_sequence = new LinkedList<>();
	private GameObject holding_tetromino;
	private GameObject placed_tetromino;
	private LinkedList<Integer> placed_t_spin_moves = new LinkedList<>();
	private boolean can_hold = true;
	private int timer = 0;

	private int total_lines_cleared = 0;
	private int LINES_NEEDED_FOR_NEXT_LEVEL = 4 + Game.current_level;

	public boolean can_help_on_rotate = true;

	private LinkedList<Integer> t_spin_moves = new LinkedList<>();

	private LinkedList<Integer> cleared_lines_combo = new LinkedList<>();

	private boolean last_clear_tetris = false;

	public Handler() {}

	public void tick() {
		for (int i = 0; i < objects.size(); i++) {
			objects.get(i).tick();
		}
		if(Game.gameState == GameState.game) {
			if (current_tetromino != null) {
				if (timer >= (MAX_TIMER - (Game.current_level * 4))) {
					timer = 0;
					if (canMove(0, 1)) {
						current_tetromino.setY(current_tetromino.getY() + Game.TILESIZE);
					} else {
						plantTetromino();
					}
				}
				timer++;
			}
			if (current_tetromino != null) current_tetromino.tick();
			if (tetromino_sequence.size() > 1) tetromino_sequence.get(0).tick();
			if (holding_tetromino != null) holding_tetromino.tick();
			//checkFilledRow();

			if (total_lines_cleared / LINES_NEEDED_FOR_NEXT_LEVEL >= Game.current_level) {
				Game.current_level++;
				LINES_NEEDED_FOR_NEXT_LEVEL = 3 + Game.current_level;
				if (Game.current_level > Game.MAX_LEVEL) {
					Game.current_level = Game.MAX_LEVEL;
				} else {
					AudioPlayer.playSound(AudioFiles.next_level, Game.VOLUME, false, 0);
				}
			}
			for(int i=0; i<objects.size(); i++) {
				if(objects.get(i).getId() == ID.tetromino_cube) {
					if(objects.get(i).getY() < Game.TILESIZE * 2) {
						if(Game.gameState != GameState.end_screen) {
							Game.gameState = GameState.end_screen;
							Game.addHighScore(Game.current_score);
							AudioPlayer.playSound(AudioFiles.defeat, Game.VOLUME, false, 0);
						}
					}
				}
			}
		} else if(Game.gameState == GameState.end_screen) {
			current_tetromino = null;
			tetromino_sequence.clear();
			holding_tetromino = null;
		}
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		for(int i=0; i<objects.size(); i++) {
			objects.get(i).render(g);
		}
		if(tetromino_sequence.size() > 1) {
			tetromino_sequence.get(0).render(g);
		}
		if(holding_tetromino != null) holding_tetromino.render(g);
		if(current_tetromino != null) {
			current_tetromino.render(g);
			for(GameObject cube : new LinkedList<>(((Tetromino)current_tetromino).getCubes())) {
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
				try {
					Tetromino_Cube cloned = (Tetromino_Cube) ((Tetromino_Cube) cube).clone();
					int y_offset = 1;
					for(int i=0; i<Game.PLAYSPACE_HEIGHT-2; i++) {
						if(canMove(0, y_offset)) {
							y_offset++;
						} else {
							break;
						}
					}
					y_offset -=1;
					cloned.setY(cloned.getY() + (y_offset * cube.TILESIZE));
					cloned.render(g);
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
			}
		}
	}

	public void addObject(GameObject object) {
		this.objects.add(object);
	}

	public void removeObject(GameObject object) {
		this.objects.remove(object);
	}

	public void setCurrent_tetromino(GameObject tetromino) {
		this.current_tetromino = tetromino;
	}

	public GameObject getCurrent_tetromino() {
		return this.current_tetromino;
	}

	private boolean canMove(int x_offset, int y_offset) {
		if(current_tetromino == null) return false;
		for(GameObject cube : ((Tetromino)current_tetromino).getCubes()) {
			for(int i=0; i< objects.size(); i++) {
				GameObject object = objects.get(i);
				if(object.getId() == ID.wall || object.getId() == ID.tetromino_cube) {
					if(object.getY() == cube.getY() + (y_offset * Game.TILESIZE) && object.getX() == cube.getX() + (x_offset * Game.TILESIZE)) {
						return false;
					}
					Rectangle cube_bnds = cube.getBounds();
					cube_bnds.x += x_offset * Game.TILESIZE;
					cube_bnds.y += y_offset * Game.TILESIZE;
					if(object.getBounds().intersects(cube_bnds)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean canMoveCubes(int x_offset, int y_offset, LinkedList<GameObject> cubes) {
		for(GameObject cube : cubes) {
			for(int i=0; i< objects.size(); i++) {
				GameObject object = objects.get(i);
				if(object.getId() == ID.wall || object.getId() == ID.tetromino_cube) {
					if(object.getY() == cube.getY() + (y_offset * Game.TILESIZE) && object.getX() == cube.getX() + (x_offset * Game.TILESIZE)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public void moveTetromino(int x_offset, int y_offset) {
		if(canMove(x_offset, y_offset)) {
			AudioPlayer.playSound(AudioFiles.move_tetromino, Game.VOLUME, false, 0);
			current_tetromino.setY(current_tetromino.getY() + y_offset*Game.TILESIZE);
			current_tetromino.setX(current_tetromino.getX() + x_offset*Game.TILESIZE);
		}
	}

	public void moveTetrominoToBottom() {
		int y_offset = 1;
		for(int i=0; i<Game.PLAYSPACE_HEIGHT-2; i++) {
			if(canMove(0, y_offset)) {
				y_offset++;
			} else {
				break;
			}
		}
		moveTetromino(0, y_offset-1);
		plantTetromino();
		Game.current_score += y_offset*2;
		timer = 0;
	}

	public void rotateTetromino(boolean cw) {
		Tetromino current = (Tetromino) current_tetromino;
		int rotation = cw? current.getRotation() + 90 : current.getRotation() - 90;
		if(rotation >= 360) rotation -= 360;
		if(rotation < 0) rotation += 360;
		LinkedList<GameObject> rotated = current.getRotatedInstance(rotation);
		boolean canRotate = true;
		boolean potential_t_spin = false;
		int cube_offset_x = 0;
		int cube_offset_y = 0;
		for(GameObject cube : rotated) {
			cube.tick();
			if(cube.getY() >= (Game.PLAYSPACE_HEIGHT-1)*Game.TILESIZE) {
				canRotate = false;
				cube_offset_y += -1;
			}
			if(cube.getY() < Game.TILESIZE) {
				canRotate = false;
				cube_offset_y += 1;
			}
			if(cube.getX() < Game.TILESIZE) {
				canRotate = false;
				cube_offset_x += 1;
			}
			if(cube.getX() >= (Game.PLAYSPACE_WIDTH-1)*Game.TILESIZE) {
				canRotate = false;
				cube_offset_x += -1;
			}
			for(GameObject object : objects) {
				if(object.getId() == ID.tetromino_cube) {
					if(cube.getBounds().intersects(object.getBounds())) {
						if(current instanceof Tetromino_T) potential_t_spin = true;
						canRotate = false;
						if(cube.getY() > current_tetromino.getY()) cube_offset_y -= 1;
						if(cube.getY() < current_tetromino.getY()) cube_offset_y += 1;
					}
				}
			}
		}
		if(canRotate) {
			AudioPlayer.playSound(AudioFiles.move_tetromino, Game.VOLUME, false, 0);
			current.setCubes(current.getRotatedInstance(rotation));
			current.setRotation(rotation);
			if(current instanceof Tetromino_T) {
				if(isT_spin()) {
					timer = 0;
					t_spin_moves.add(rotation);
					AudioPlayer.playSound(AudioFiles.t_spin, Game.VOLUME, false, 0);
				}
			}
		} else {
			if(can_help_on_rotate) {
				Point[] offsets;
				if(potential_t_spin) {
					offsets = new Point[]{
							new Point(1, 3),
							new Point(-1, 3),
							new Point(-1, 1),
							new Point(1, 1),
							new Point(0, 1),
							new Point(1, 0),
							new Point(-1, 0),
							new Point(0, 0),
							new Point(1, -1),
							new Point(-1, -1),
							new Point(0, -1),
					};
				} else {
					offsets = new Point[]{
							new Point(0, 0),
							new Point(0, 1),
							new Point(0, -1),
							new Point(1, 0),
							new Point(-1, 0),
							new Point(-1, 1),
							new Point(1, 1),
							new Point(1, -1),
							new Point(-1, -1),
							new Point(1, 3),
					};
				}
				for(Point offset : offsets) {
					if(canMoveCubes(cube_offset_x + offset.x, cube_offset_y + offset.y, rotated) ) {
						current.setCubes(current.getRotatedInstance(rotation));
						current.setRotation(rotation);
						moveTetromino(cube_offset_x + offset.x, cube_offset_y + offset.y);
						if(potential_t_spin) {
							timer = 0;
							t_spin_moves.add(rotation);
							AudioPlayer.playSound(AudioFiles.t_spin, Game.VOLUME, false, 0);
						}
						break;
					}
				}
				can_help_on_rotate = false;
			}
		}
	}

	private void plantTetromino() {
		if(current_tetromino == null) return;
		can_hold = true;
		for(GameObject c : ((Tetromino)current_tetromino).getCubes()) {
			Tetromino_Cube cube = (Tetromino_Cube) c;
			cube.tick();
			cube.clearParent();
		}
		objects.addAll(((Tetromino)current_tetromino).getCubes());
		placed_tetromino = current_tetromino;
		placed_t_spin_moves = new LinkedList<>(t_spin_moves);
		setNextTetromino();
		AudioPlayer.playSound(AudioFiles.place, Game.VOLUME, false, 0);
		checkFilledRow();
	}

	private void checkFilledRow() {
		int rows_cleared = 0;
		LinkedList<GameObject> cleared_cubes = new LinkedList<>();
		for(int y=Game.TILESIZE; y<Game.SCREEN_HEIGHT-Game.TILESIZE; y+=Game.TILESIZE) {
			LinkedList<GameObject> cubes_on_row = new LinkedList<>();
			for(int i=0; i< objects.size(); i++) {
				GameObject object = objects.get(i);
				if(object.getId() == ID.tetromino_cube) {
					if(object.getY() == y) cubes_on_row.add(object);
				}
			}
			if(cubes_on_row.size() >= 10) {
				rows_cleared++;
				objects.removeAll(cubes_on_row);
				cleared_cubes.addAll(cubes_on_row);
				for(GameObject object : objects) {
					if(object.getId() == ID.tetromino_cube) {
						if(object.getY() <= y) {
							object.setY(object.getY() + Game.TILESIZE);
						}
					}
				}
			}
		}
		if(rows_cleared == 4) {
			for(GameObject cube : cleared_cubes) {
				addObject(new Effect_Clear_Cube(cube.getX(), cube.getY(), 30f));
			}
		} else if(rows_cleared > 0) {
			for(GameObject cube : cleared_cubes) {
				addObject(new Effect_Clear_Cube(cube.getX(), cube.getY(), 10f));
			}
		}
		total_lines_cleared += rows_cleared;

		cleared_lines_combo.add(rows_cleared);
		int score_add = 0;

		if(rows_cleared > 0 && cleared_lines_combo.size() > 1) {
			score_add = calculateComboScore(cleared_lines_combo, Game.current_level);
		} else  {
			score_add = calculateScore(rows_cleared, Game.current_level);
			if(rows_cleared == 0) {
				cleared_lines_combo.clear();
			}
		}
		if(score_add > 0) {
			Game.current_score += score_add;
			addObject(new Effect_Score_Text("+" + score_add));
		}
	}

	public void setNextTetromino() {
		int x = 160;
		int y = 64;

		if(tetromino_sequence.size() <= 7) {
			tetromino_sequence.addAll(getNewTetrominoSequence(x, y));
		}

		current_tetromino = tetromino_sequence.get(0);
		tetromino_sequence.remove(0);
		current_tetromino.setX(x);
		current_tetromino.setY(y);

		tetromino_sequence.get(0).setX(14 * Game.TILESIZE);
		tetromino_sequence.get(0).setY(3 * Game.TILESIZE);

		t_spin_moves.clear();
	}

	public void holdTetromino() {
		if(can_hold) {
			AudioPlayer.playSound(AudioFiles.hold, Game.VOLUME, false, 0);
			can_hold = false;

			GameObject currentTetromino = current_tetromino;

			if(holding_tetromino == null) {
				setNextTetromino();
			} else {
				holding_tetromino.setX(160);
				holding_tetromino.setY(64);
				current_tetromino = holding_tetromino;
			}
			holding_tetromino = currentTetromino;
			holding_tetromino.setX(14*Game.TILESIZE);
			holding_tetromino.setY(9*Game.TILESIZE);
			((Tetromino)holding_tetromino).setRotation(0);
			((Tetromino)holding_tetromino).setCubes(((Tetromino)holding_tetromino).getRotatedInstance(0));
		}
	}

	public void reset() {
		AudioPlayer.playSound(AudioFiles.blip, Game.VOLUME, false, 0);
		LinkedList<GameObject> cubes = new LinkedList<>();
		for(int i=0; i< objects.size(); i++) {
			if(objects.get(i).getId() == ID.tetromino_cube) {
				cubes.add(objects.get(i));
			}
		}
		objects.removeAll(cubes);

		/*try {
			File file = new File("./level.txt");
			if(file.exists()) {
				BufferedReader br = new BufferedReader(new FileReader(file));
				int y = 0;
				for(String line; (line = br.readLine()) != null; ) {
					for(int x=0; x<line.toCharArray().length; x++) {
						if(line.toCharArray()[x] == 'X') {
							addObject(new Tetromino_Cube(Game.TILESIZE + x * Game.TILESIZE, Game.TILESIZE+ y * Game.TILESIZE, ColorPalette.gray.color, ColorPalette.dark_gray.color, null));
						}
					}
					y++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}*/

		tetromino_sequence.clear();
		holding_tetromino = null;
		setNextTetromino();

		Game.current_level = 1;
		Game.current_score = 0;
		total_lines_cleared = 0;
		can_hold = true;
	}

	public GameObject getObjectAtCoords(int x, int y) {
		for(int i=0; i<objects.size(); i++) {
			GameObject tmp = objects.get(i);
			if(tmp.getX() == x && tmp.getY() == y) return tmp;
		}
		return null;
	}

	private boolean isT_spin() {
		Point[] offsets = new Point[]{
				new Point(Game.TILESIZE, -Game.TILESIZE),
				new Point(Game.TILESIZE, Game.TILESIZE),
				new Point(-Game.TILESIZE, -Game.TILESIZE),
				new Point(-Game.TILESIZE, Game.TILESIZE),
		};
		int x = current_tetromino.getX();
		int y = current_tetromino.getY();
		int corner_count = 0;
		for(Point offset : offsets) {
			GameObject tmp = getObjectAtCoords(x + offset.x, y + offset.y);
			if(tmp != null) {
				if(tmp.getId() == ID.tetromino_cube) {
					corner_count++;
				}
			}
		}
		return corner_count >= 3;
	}

	private GameObject getNewTetromino(int x, int y) {
		GameObject ret = new Tetromino_I(x, y);
		switch(new Random().nextInt(7)) {
			case 0:
				ret = new Tetromino_I(x, y);
				break;
			case 1:
				ret =  new Tetromino_J(x, y);
				break;
			case 2:
				ret =  new Tetromino_L(x, y);
				break;
			case 3:
				ret =  new Tetromino_O(x, y);
				break;
			case 4:
				ret =  new Tetromino_S(x, y);
				break;
			case 5:
				ret =  new Tetromino_T(x, y);
				break;
			case 6:
				ret =  new Tetromino_Z(x, y);
				break;
		}
		return ret;
	}

	private LinkedList<GameObject> getNewTetrominoSequence(int x, int y) {
		LinkedList<GameObject> ret = new LinkedList<>();
		ret.add(new Tetromino_I(x, y));
		ret.add(new Tetromino_J(x, y));
		ret.add(new Tetromino_L(x, y));
		ret.add(new Tetromino_O(x, y));
		ret.add(new Tetromino_S(x, y));
		ret.add(new Tetromino_T(x, y));
		ret.add(new Tetromino_Z(x, y));

		Collections.shuffle(ret);
		return ret;
	}

	public int calculateScore(int lines_cleared, int level) {
		int score = 0;
		if(placed_tetromino instanceof Tetromino_T && placed_t_spin_moves.size() > 0) {
			last_clear_tetris = false;
			if(placed_t_spin_moves.size() == 1) {
				switch (lines_cleared) {
					case 1:
						addObject(new Effect_Special_Text("T-Spin Single"));
						AudioPlayer.playSound(AudioFiles.explosion_1, Game.VOLUME, false, 0);
						AudioPlayer.playSound(AudioFiles.tetris, Game.VOLUME, false, 0);
						score = 800 * level;
						break;
					case 2:
						addObject(new Effect_Special_Text("T-Spin Double"));
						AudioPlayer.playSound(AudioFiles.explosion_1, Game.VOLUME, false, 0);
						AudioPlayer.playSound(AudioFiles.tetris, Game.VOLUME, false, 0);
						score = 1200 * level;
						break;
					case 3:
						addObject(new Effect_Special_Text("T-Spin Triple"));
						AudioPlayer.playSound(AudioFiles.explosion_1, Game.VOLUME, false, 0);
						AudioPlayer.playSound(AudioFiles.tetris, Game.VOLUME, false, 0);
						score = 1600 * level;
						break;
				}
			} else {
				switch (lines_cleared) {
					case 1:
						addObject(new Effect_Special_Text("B2B T-Spin Single"));
						AudioPlayer.playSound(AudioFiles.t_spin_b2b, Game.VOLUME, false, 0);
						AudioPlayer.playSound(AudioFiles.tetris, Game.VOLUME, false, 0);
						score = 1200 * level;
						break;
					case 2:
						addObject(new Effect_Special_Text("B2B T-Spin Double"));
						AudioPlayer.playSound(AudioFiles.t_spin_b2b, Game.VOLUME, false, 0);
						AudioPlayer.playSound(AudioFiles.tetris, Game.VOLUME, false, 0);
						score = 1800 * level;
						break;
					case 3:
						addObject(new Effect_Special_Text("B2B T-Spin Triple"));
						AudioPlayer.playSound(AudioFiles.t_spin_b2b, Game.VOLUME, false, 0);
						AudioPlayer.playSound(AudioFiles.tetris, Game.VOLUME, false, 0);
						score = 2400 * level;
						break;
				}
			}
		} else {
			int line_score = 0;
			switch (lines_cleared) {
				case 1:
					line_score = 100;
					AudioPlayer.playSound(AudioFiles.explosion_1, Game.VOLUME, false, 0);
					addObject(new Effect_Special_Text("Single"));
					last_clear_tetris = false;
					break;
				case 2:
					line_score = 300;
					AudioPlayer.playSound(AudioFiles.explosion_1, Game.VOLUME, false, 0);
					addObject(new Effect_Special_Text("Double"));
					last_clear_tetris = false;
					break;
				case 3:
					line_score = 500;
					AudioPlayer.playSound(AudioFiles.explosion_1, Game.VOLUME, false, 0);
					addObject(new Effect_Special_Text("Triple"));
					last_clear_tetris = false;
					break;
				case 4:
					AudioPlayer.playSound(AudioFiles.explosion_2, Game.VOLUME, false, 0);
					AudioPlayer.playSound(AudioFiles.tetris, Game.VOLUME, false, 0);
					if(last_clear_tetris) {
						addObject(new Effect_Special_Text("B2B Tetris"));
						line_score = 1200;
					} else {
						line_score = 800;
						addObject(new Effect_Special_Text("Tetris"));
						last_clear_tetris = true;
					}
					break;
			}
			score = level * line_score;
		}
		return score;
	}

	private int calculateComboScore(LinkedList<Integer> cleared_lines_combo, int current_level) {
		int index = cleared_lines_combo.size()-1;
		addObject(new Effect_Special_Text(cleared_lines_combo.size() + " x Combo"));
		return cleared_lines_combo.get(index)*current_level+50*current_level;
	}
}
