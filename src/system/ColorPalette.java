package system;

import java.awt.*;

/**
 * @ColorPalette is Sweetie 16 palette from lospec
 * @link https://lospec.com/palette-list/sweetie-16
 */

public enum ColorPalette {
	white("#f4f4f4"),
	black("#1a1c2c"),

	text_highlight("#73eff7"),
	text_effect("#3b5dc9"),
	text_highscore("#ffcd75"),

	tetromino_I("#73eff7"),
	tetromino_I_border("#41a6f6"),

	tetromino_J("#3b5dc9"),
	tetromino_J_border("#29366f"),

	tetromino_L("#ef7d57"),
	tetromino_L_border("#b13e53"),

	tetromino_O("#ffcd75"),
	tetromino_O_border("#ef7d57"),

	tetromino_S("#38b764"),
	tetromino_S_border("#257179"),

	tetromino_T("#5d275d"),
	tetromino_T_border("#333c57"),

	tetromino_Z("#b13e53"),
	tetromino_Z_border("#5d275d"),

	wall("#566c86"),
	wall_border("#94b0c2");



	public final String hex;
	public final Color color;

	private ColorPalette(String hex) {
		this.hex = hex;
		this.color = Color.decode(hex);
	}
}
