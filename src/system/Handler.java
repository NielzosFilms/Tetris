package system;

import audioEngine.AudioFiles;
import audioEngine.AudioPlayer;
import objects.Effect_Clear_Cube;
import objects.Tetromino_Cube;
import objects.tetrominos.*;

import java.awt.*;
import java.util.LinkedList;
import java.util.Random;

public class Handler {
	private final int MAX_TIMER = 4 * Game.MAX_LEVEL;
	LinkedList<GameObject> objects = new LinkedList<>();
	private GameObject current_tetromino;
	private GameObject next_tetromino;
	private GameObject holding_tetromino;
	private LinkedList<GameObject> tetromino_history = new LinkedList<>();
	private boolean can_hold = true;
	private int timer = 0;

	private int total_lines_cleared = 0;
	private int LINES_NEEDED_FOR_NEXT_LEVEL = 3 + Game.current_level;

	public boolean can_help_on_rotate = true;

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
			if (next_tetromino != null) next_tetromino.tick();
			if (holding_tetromino != null) holding_tetromino.tick();
			checkFilledRow();

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
			next_tetromino = null;
			holding_tetromino = null;
		}
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		for(int i=0; i<objects.size(); i++) {
			objects.get(i).render(g);
		}
		if(next_tetromino != null) {
			next_tetromino.render(g);
		}
		if(holding_tetromino != null) holding_tetromino.render(g);
		if(current_tetromino != null) {
			current_tetromino.render(g);
			for(GameObject cube : new LinkedList<>(((Tetromino)current_tetromino).getCubes())) {
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
				try {
					Tetromino_Cube cloned = (Tetromino_Cube) ((Tetromino_Cube) cube).clone();
					int y_offset = 1;
					while(canMove(0, y_offset)) {
						y_offset++;
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

	public void moveTetromino(int x_offset, int y_offset) {
		if(canMove(x_offset, y_offset)) {
			AudioPlayer.playSound(AudioFiles.move_tetromino, Game.VOLUME, false, 0);
			current_tetromino.setY(current_tetromino.getY() + y_offset*Game.TILESIZE);
			current_tetromino.setX(current_tetromino.getX() + x_offset*Game.TILESIZE);
		}
	}

	public void moveTetrominoToBottom() {
		int y_offset = 1;
		while(canMove(0, y_offset)) {
			y_offset++;
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
		rotated.addAll(current.getCubes());
		boolean canRotate = true;
		int cube_offset_x = 0;
		int cube_offset_y = 0;
		for(GameObject cube : rotated) {
			for(GameObject object : objects) {
				if(object.getId() == ID.wall || object.getId() == ID.tetromino_cube) {
					if((cube.getX() == object.getX() && cube.getY() == object.getY()) || (cube.getX() == object.getX() && cube.getY()+Game.TILESIZE == object.getY())) {
						canRotate = false;
						cube_offset_x = -((Tetromino_Cube)cube).getOffset_x() / Game.TILESIZE;
						cube_offset_y = -((Tetromino_Cube)cube).getOffset_y() / Game.TILESIZE;
					}
					if(cube.getY() >= 672 || cube.getY()+Game.TILESIZE >= 672) {
						canRotate = false;
						cube_offset_x = -1;
						cube_offset_y = -1;
					}
				}
			}
		}
		if(canRotate) {
			AudioPlayer.playSound(AudioFiles.move_tetromino, Game.VOLUME, false, 0);
			current.setCubes(current.getRotatedInstance(rotation));
			current.setRotation(rotation);
		} else {
			if(can_help_on_rotate) {
				if(canMoveCubes(cube_offset_x, cube_offset_y, rotated) ){//&& canMoveCubes(cube_offset_x, cube_offset_y+1, rotated)) {
					current.setCubes(current.getRotatedInstance(rotation));
					current.setRotation(rotation);
					moveTetromino(cube_offset_x, cube_offset_y);
				}
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
		setNextTetromino();
		AudioPlayer.playSound(AudioFiles.place, Game.VOLUME, false, 0);
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
			AudioPlayer.playSound(AudioFiles.explosion_2, Game.VOLUME, false, 0);
			AudioPlayer.playSound(AudioFiles.tetris, Game.VOLUME, false, 0);
			for(GameObject cube : cleared_cubes) {
				addObject(new Effect_Clear_Cube(cube.getX(), cube.getY(), 30f));
			}
		} else if(rows_cleared > 0) {
			AudioPlayer.playSound(AudioFiles.explosion_1, Game.VOLUME, false, 0);
			for(GameObject cube : cleared_cubes) {
				addObject(new Effect_Clear_Cube(cube.getX(), cube.getY(), 10f));
			}
		}
		total_lines_cleared += rows_cleared;
		Game.current_score += calculateScore(rows_cleared, Game.current_level);
	}

	public void setNextTetromino() {
		int x = 160;
		int y = 64;
		if(next_tetromino != null) {
			current_tetromino = next_tetromino;
			current_tetromino.setX(x);
			current_tetromino.setY(y);
		} else {
			current_tetromino = getNewTetromino(x, y);
		}
		GameObject new_tetromino = getNewTetromino(14*Game.TILESIZE, 3*Game.TILESIZE);
		while(getHistoryCount(current_tetromino) >= 3) {
			new_tetromino = getNewTetromino(14*Game.TILESIZE, 3*Game.TILESIZE);
		}
		next_tetromino = new_tetromino;

		if(tetromino_history.size() > 0) {
			tetromino_history.add(0, current_tetromino);
		} else tetromino_history.add(current_tetromino);
		while(tetromino_history.size() > 3) {
			tetromino_history.remove(2);
		}
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
		next_tetromino = null;
		holding_tetromino = null;
		setNextTetromino();

		Game.current_level = 1;
		Game.current_score = 0;
		total_lines_cleared = 0;
		can_hold = true;
	}

	private int getHistoryCount(GameObject new_tetromino) {
		int ret = 0;
		for(GameObject history : tetromino_history) {
			if(history.getClass() == new_tetromino.getClass()) {
				ret++;
			}
		}
		return ret;
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

	public int calculateScore(int lines_cleared, int level) {
		int line_score = 0;
		switch(lines_cleared) {
			case 1:
				line_score = 40;
				break;
			case 2:
				line_score = 100;
				break;
			case 3:
				line_score = 300;
				break;
			case 4:
				line_score = 1200;
				break;
		}

		return (level + 1) * line_score;
	}
}
