package system;

import java.util.LinkedList;

public interface Tetromino {
	LinkedList<GameObject> getCubes();
	void setCubes(LinkedList<GameObject> cubes);

	LinkedList<GameObject> getRotatedInstance(boolean cw);

	int getRotation();
	void setRotation(int rotation);
}
