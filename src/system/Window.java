package system;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Window extends Canvas {

	private static final long serialVersionUID = 492636734070584756L;
	
	public Window(int width, int height, String title, Game game) {
		JFrame f = new JFrame(title);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setPreferredSize(new Dimension(width, height));
		f.setMaximumSize(new Dimension(width, height));
		f.setMinimumSize(new Dimension(width, height));
		f.setLocationRelativeTo(null);
		f.add(game);
		f.pack();
		f.setVisible(true);
		game.start();
	}

}