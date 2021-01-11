package system;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class KeyInput extends KeyAdapter {
	private Handler handler = Game.handler;
	public static HashMap<Integer, Boolean[]> keysDown = new HashMap<>();

	private int hold_timer = 0;
	private final int hold_threshold = 15;

	private int move_speed_timer = 0;
	private final int move_speed = 2;

	public KeyInput() {
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
				if(hold_timer >= hold_threshold) {
					if(move_speed_timer >= move_speed) {
						doKeyFunction(key);
						move_speed_timer = 0;
					}
					move_speed_timer++;
				}
				hold_timer++;
			}
		}
	}

	public void keyPressed(KeyEvent e) {
		if(keysDown.containsKey(e.getKeyCode())) {
			Boolean[] old = keysDown.get(e.getKeyCode());
			old[0] = true;
			keysDown.put(e.getKeyCode(), old);
		}
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
		switch(keyCode) {
			case KeyEvent.VK_RIGHT:
				handler.moveTetromino(1, 0);
				break;
			case KeyEvent.VK_LEFT:
				handler.moveTetromino(-1, 0);
				break;
			case KeyEvent.VK_DOWN:
				handler.moveTetromino(0, 1);
				break;
			case KeyEvent.VK_UP:
				handler.rotateTetromino(true);
				break;
			case KeyEvent.VK_Z:
				handler.rotateTetromino(false);
				break;
			case KeyEvent.VK_SPACE:
				handler.moveTetrominoToBottom();
				break;
			case KeyEvent.VK_C:
				handler.holdTetromino();
				break;
			case KeyEvent.VK_R:
				handler.restart();
				break;
		}
	}
}
