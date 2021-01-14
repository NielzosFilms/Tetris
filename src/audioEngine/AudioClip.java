package audioEngine;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.net.URL;

public class AudioClip {

	private File file;
	private URL url;
	private Clip clip;
	private long pauzeTime;
	private double vol;
	
	public AudioClip(String path) {
		url = ClassLoader.getSystemResource("sounds/" + path);
		/*file = new File(path);
		if(!file.exists()) {
			System.out.println("file not found");
		}*/
	}
	
	public AudioInputStream getAudioStream() {
		try {
			
			//return AudioSystem.getAudioInputStream(file);
			return AudioSystem.getAudioInputStream(url);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void setClip(Clip clip, double vol) {
		this.clip = clip;
	}
	
	public Clip getClip() {
		return clip;
	}
	
	public void setPauzeTime(long pauzeTime) {
		this.pauzeTime = pauzeTime;
		this.vol = vol;
	}
	
	public long getPauzeTime() {
		return this.pauzeTime;
	}

}
