package system;

import java.awt.*;

/**
 * @ColorPalette is Sweetie 16 palette from lospec
 * @link https://lospec.com/palette-list/sweetie-16
 */

public enum ColorPalette {
	white("#f4f4f4"),
	black("#1a1c2c"),

	light_gray("#94b0c2"),
	gray("#566c86"),
	dark_gray("#333c57"),

	dark_blue("#29366f"),
	blue("#3b5dc9"),

	dark_light_blue("#41a6f6"),
	light_blue("#73eff7"),

	dark_green("#257179"),
	green("#38b764"),
	light_green("#a7f070"),

	purple("#5d275d"),
	red("#b13e53"),

	orange("#ef7d57"),
	yellow("#ffcd75");


	public final String hex;
	public final Color color;

	private ColorPalette(String hex) {
		this.hex = hex;
		this.color = Color.decode(hex);
	}
}
