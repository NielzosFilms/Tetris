package system;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {
	private Handler handler = Game.handler;

	public KeyInput() {}

	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			handler.moveTetromino(1, 0);
		}
		if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			handler.moveTetromino(-1, 0);
		}
		if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			handler.moveTetromino(0, 1);
		}
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			handler.moveTetrominoToBottom();
		}
	}

	public void keyReleased(KeyEvent e) {

	}
}
