package system;

import system.Game;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public enum SoundEffect {
	blip("blip.wav"),
	explosion_1("explosion_1.wav"),
	explosion_2("explosion_2.wav"),
	hard_drop("hard_drop.wav"),
	move_tetromino("move_tetromino.wav"),
	hold("hold.wav"),
	defeat("defeat.wav"),
	place("place.wav"),
	tetris("tetris.wav"),
	next_level("next_level_2.wav"),
	t_spin_b2b("t_spin_b2b.wav"),
	t_spin("t_spin_place.wav");

	private Clip clip;

	SoundEffect(String filename) {
		try {
			URL url = ClassLoader.getSystemResource("sounds/" + filename);
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			setVol(Game.VOLUME, clip);
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public void play() {
		if (clip.isRunning()) clip.stop();
		clip.setFramePosition(0);
		clip.start();
	}

	private static void setVol(double vol, Clip clip) {
		FloatControl gain = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
		float dB = (float) (Math.log(vol) / Math.log(10) * 20);
		gain.setValue(dB);
	}

	public static void init() {
		values(); // calls the constructor for all the elements
	}
}
