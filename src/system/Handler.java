package system;

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
	private boolean can_hold = true;
	private int timer = 0;

	private int total_lines_cleared = 0;
	private final int LINES_NEEDED_FOR_NEXT_LEVEL = 24;

	public Handler() {}

	public void tick() {
		if(current_tetromino != null) {
			if (timer >= (MAX_TIMER-(Game.current_level*4))) {
				timer = 0;
				if (canMove(0,1)) {
					current_tetromino.setY(current_tetromino.getY() + Game.TILESIZE);
				} else {
					plantTetromino();
				}
			}
			timer++;
		}
		for(int i=0; i<objects.size(); i++) {
			objects.get(i).tick();
		}
		if(current_tetromino != null) current_tetromino.tick();
		if(next_tetromino != null) next_tetromino.tick();
		if(holding_tetromino != null) holding_tetromino.tick();
		checkFilledRow();

		if(total_lines_cleared / LINES_NEEDED_FOR_NEXT_LEVEL > Game.current_level) {
			Game.current_level++;
			if(Game.current_level > Game.MAX_LEVEL) Game.current_level = Game.MAX_LEVEL;
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
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
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
		for(GameObject cube : ((Tetromino)current_tetromino).getCubes()) {
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
		timer = 0;
	}

	public void rotateTetromino(boolean cw) {
		Tetromino current = (Tetromino) current_tetromino;
		int rotation = cw? current.getRotation() + 90 : current.getRotation() - 90;
		if(rotation >= 360) rotation -= 360;
		if(rotation < 0) rotation += 360;
		LinkedList<GameObject> rotated = ((Tetromino)current_tetromino).getRotatedInstance(rotation);
		boolean canRotate = true;
		for(GameObject cube : rotated) {
			for(GameObject object : objects) {
				if(object.getId() == ID.wall || object.getId() == ID.tetromino_cube) {
					if(cube.getBounds().intersects(object.getBounds())) {
						canRotate = false;
					}
				}
			}
		}
		if(canRotate) {
			current.setCubes(rotated);
			current.setRotation(rotation);
		}
	}

	private void plantTetromino() {
		can_hold = true;
		for(GameObject c : ((Tetromino)current_tetromino).getCubes()) {
			Tetromino_Cube cube = (Tetromino_Cube) c;
			cube.tick();
			cube.clearParent();
		}
		objects.addAll(((Tetromino)current_tetromino).getCubes());
		setNextTetromino(64, 64);
	}

	private void checkFilledRow() {
		int rows_cleared = 0;
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
				for(GameObject object : objects) {
					if(object.getId() == ID.tetromino_cube) {
						if(object.getY() <= y) {
							object.setY(object.getY() + Game.TILESIZE);
						}
					}
				}
			}
		}
		total_lines_cleared += rows_cleared;
		Game.current_score += calculateScore(rows_cleared, Game.current_level);
	}

	public void setNextTetromino(int x, int y) {
		if(next_tetromino != null) {
			current_tetromino = next_tetromino;
			current_tetromino.setX(x);
			current_tetromino.setY(y);
		} else {
			current_tetromino = getNewTetromino(x, y);
		}
		next_tetromino = getNewTetromino(14*Game.TILESIZE, 3*Game.TILESIZE);
	}

	public void holdTetromino() {
		if(can_hold) {
			can_hold = false;

			GameObject currentTetromino = current_tetromino;

			if(holding_tetromino == null) {
				setNextTetromino(64, 64);
			} else {
				holding_tetromino.setX(64);
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

	public void restart() {
		LinkedList<GameObject> cubes = new LinkedList<>();
		for(int i=0; i< objects.size(); i++) {
			if(objects.get(i).getId() == ID.tetromino_cube) {
				cubes.add(objects.get(i));
			}
		}
		objects.removeAll(cubes);
		next_tetromino = null;
		holding_tetromino = null;
		setNextTetromino(64, 64);

		Game.current_level = 0;
		Game.current_score = 0;
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
