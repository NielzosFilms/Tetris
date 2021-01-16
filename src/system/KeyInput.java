package system;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class KeyInput extends KeyAdapter {
	private Handler handler;
	public HashMap<Integer, Boolean[]> keysDown = new HashMap<>();

	private int hold_timer = 0;
	private final int hold_threshold = 15;

	private int move_speed_timer = 0;
	private final int move_speed = 2;

	private Game game;

	public KeyInput(Game game) {
		this.game = game;
		this.handler = game.handler;
		keysDown.put(KeyEvent.VK_RIGHT, new Boolean[]{false, false});
		keysDown.put(KeyEvent.VK_LEFT, new Boolean[]{false, false});
		keysDown.put(KeyEvent.VK_DOWN, new Boolean[]{false, false});
		keysDown.put(KeyEvent.VK_UP, new Boolean[]{false, false});
		keysDown.put(KeyEvent.VK_Z, new Boolean[]{false, false});
		keysDown.put(KeyEvent.VK_SPACE, new Boolean[]{false, false});
		keysDown.put(KeyEvent.VK_C, new Boolean[]{false, false});
		keysDown.put(KeyEvent.VK_R, new Boolean[]{false, false});
	}

	public void tick() {
		for(int key : keysDown.keySet()) {
			if(keysDown.get(key)[0]) {
				if(!keysDown.get(key)[1]) {
					doKeyFunction(key);
					Boolean[] old = keysDown.get(key);
					old[1] = true;
				}
				if(game.gameState == GameState.game) {
					if (hold_timer >= hold_threshold) {
						if (move_speed_timer >= move_speed) {
							doKeyFunction(key);
							move_speed_timer = 0;
						}
						move_speed_timer++;
					} else {
						hold_timer++;
					}
				}
			}
		}
	}

	public void keyPressed(KeyEvent e) {
		if(keysDown.containsKey(e.getKeyCode())) {
			Boolean[] old = keysDown.get(e.getKeyCode());
			old[0] = true;
			keysDown.put(e.getKeyCode(), old);
			if(!old[1]) hold_timer = 0;
		} /*else {
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				if(Game.gameState == GameState.game) {
					Game.gameState = GameState.pauzed;
				} else if(Game.gameState == GameState.pauzed){
					Game.gameState = GameState.game;
				}
			}
		}*/
	}

	public void keyReleased(KeyEvent e) {
		if(keysDown.containsKey(e.getKeyCode())) {
			Boolean[] old = keysDown.get(e.getKeyCode());
			old[0] = false;
			old[1] = false;
			keysDown.put(e.getKeyCode(), old);
			hold_timer = 0;
		}
	}

	private void doKeyFunction(int keyCode) {
		if(game.gameState == GameState.game) {
			switch(keyCode) {
				case KeyEvent.VK_RIGHT:
					if(!handler.canMove(0, 1)) handler.timer_slack = true;
					handler.moveTetromino(1, 0);
					break;
				case KeyEvent.VK_LEFT:
					if(!handler.canMove(0, 1)) handler.timer_slack = true;
					handler.moveTetromino(-1, 0);
					break;
				case KeyEvent.VK_DOWN:
					handler.moveTetromino(0, 1);
					game.current_score += 1;
					break;
				case KeyEvent.VK_UP:
					if(!handler.canMove(0, 1)) handler.timer_slack = true;
					handler.can_help_on_rotate = true;
					handler.rotateTetromino(true);
					break;
				case KeyEvent.VK_Z:
					if(!handler.canMove(0, 1)) handler.timer_slack = true;
					handler.can_help_on_rotate = true;
					handler.rotateTetromino(false);
					break;
				case KeyEvent.VK_SPACE:
					SoundEffect.hard_drop.play();
					handler.moveTetrominoToBottom();
					break;
				case KeyEvent.VK_C:
					handler.holdTetromino();
					break;
				case KeyEvent.VK_R:
					handler.reset();
					break;
			}
		} /*else if(Game.gameState == GameState.start_screen) {
			switch(keyCode) {
				case KeyEvent.VK_SPACE:
					Game.gameState = GameState.game;
					handler.reset();
					handler.setNextTetromino();
					break;
			}
		} else if(Game.gameState == GameState.end_screen) {
			switch(keyCode) {
				case KeyEvent.VK_SPACE:
					SoundEffect.blip.play();
					Game.gameState = GameState.start_screen;
					break;
			}
		}*/
	}
}
