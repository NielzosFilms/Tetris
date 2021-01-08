package system;

import java.awt.*;

public enum ColorPalette {
	white("#f4f9e6"),

	black_dark_blue("#1b0f28"),
	blue("#4c6684"),
	light_blue("#83bfca"),

	dark_green("#233e38"),
	green("#357b45"),
	light_green("#8ab954"),
	yellow("#f2e05a"),

	dark_red("#312039"),
	red("#6e2745"),
	light_red("#c6434e"),
	pink("#e7937e"),

	brown("#544242"),
	orange("#9e523b"),
	light_orange("#e98549"),
	gray("#8d8878");


	public final String hex;
	public final Color color;

	private ColorPalette(String hex) {
		this.hex = hex;
		this.color = Color.decode(hex);
	}
}
