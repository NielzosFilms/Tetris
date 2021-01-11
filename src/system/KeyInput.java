package system;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {
	private Handler handler = Game.handler;

	public KeyInput() {}

	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
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

	public void keyReleased(KeyEvent e) {

	}
}
