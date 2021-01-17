package ai;

import system.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Random;

public class AI_Controller {
    private Game game;
    private Handler handler;
    private KeyInput keyInput;

    private boolean plant_tetromino = false;
    private boolean planted = false;
    private int wait_for_next = 0;

    public AI_Controller(Game game) {
        this.game = game;
        this.handler = game.handler;
        this.keyInput = game.keyInput;
    }

    public void tick() {
        // keyInput.doKeyFunction(KeyEvent.VK_SPACE);
        if(!plant_tetromino) {
            if(handler.getCurrent_tetromino() != null) {
                LinkedList<Position> positions = new LinkedList<>();
                positions.addAll(getPossiblePositions(0));
                positions.addAll(getPossiblePositions(1));
                positions.addAll(getPossiblePositions(1));
                positions.addAll(getPossiblePositions(1));
                keyInput.doKeyFunction(KeyEvent.VK_UP);
                if(positions.size() > 0) {
                    placeAtPosition(getBestPosition(positions));
                    plant_tetromino = true;
                }
            }
        } else {
            if(!planted) {
                keyInput.doKeyFunction(KeyEvent.VK_SPACE);
                planted = true;
            }
            wait_for_next++;
            if(wait_for_next >= 10) {
                wait_for_next = 0;
                plant_tetromino = false;
                planted = false;
            }
        }
    }

    private LinkedList<Position> getPossiblePositions(int cw_rotation) {
        for(int r=0; r<cw_rotation; r++) {
            keyInput.doKeyFunction(KeyEvent.VK_UP);
        }
        LinkedList<Integer> all_x_offsets = new LinkedList<>();
        GameObject current = handler.getCurrent_tetromino();
        LinkedList<GameObject> objects = handler.getObjectsByID(new ID[]{ID.tetromino_cube, ID.wall});

        for(int x= -(Game.PLAYSPACE_WIDTH+2); x<Game.PLAYSPACE_WIDTH-2; x++) {
            if(handler.canMove(x, 0)) {
                all_x_offsets.add(x);
            } else {
                //break;
            }
        }

        LinkedList<Position> positions = new LinkedList<>();
        for(int x_pos : all_x_offsets) {
            for(int y=0; y<Game.PLAYSPACE_HEIGHT-2; y++) {
                if(!handler.canMove(x_pos, y)) {
                    Position position = new Position(x_pos, y-1, cw_rotation);
                    setPositionScore(position);
                    positions.add(position);
                    break;
                }
            }
        }
        return positions;
    }

    private void setPositionScore(Position position) {
        placeAtPosition(position);
        int score = 0;
        int touching = 0;
        //LinkedList<GameObject> objects = handler.getPlantedTetrominoField();
        for(GameObject cube : ((Tetromino)handler.getCurrent_tetromino()).getCubes()) {
            cube.tick();
            if(cube.getY() < (Game.PLAYSPACE_HEIGHT-1) * Game.TILESIZE) {
                for(GameObject object_2 : handler.getObjectsByID(new ID[]{ID.wall, ID.tetromino_cube})) {
                    if(object_2.getY() == cube.getY() + Game.TILESIZE) {
                        touching++;
                        score += 1;
                    }
                    if(object_2.getY() <= cube.getY()) {
                        score += 1;
                    }
                }
            }
        }
        if(touching == ((Tetromino)handler.getCurrent_tetromino()).getCubes().size()) score += 5;
        position.setScore(score);
    }

    private Position getBestPosition(LinkedList<Position> positions) {
        int highest_score = -1;
        Position ret = positions.get(0);
        for(Position position : positions) {
            if(position.getScore() > highest_score) {
                ret = position;
                highest_score = position.getScore();
            }
        }
        return ret;
    }

    /**
     * @param offset_x -1 > 1
     */
    private int getHighestPossibleOffsetX(int offset_x) {
        for(int x=1; x<Game.PLAYSPACE_WIDTH; x++) {
            if(!handler.canMove(offset_x*x, 0)) {
                return offset_x*(x-1);
            }
        }
        return 0;
    }

    /**
     * @param offset_y -1 > 1
     */
    private int getHighestPossibleOffsetY(int offset_y) {
        for(int y=1; y<Game.PLAYSPACE_HEIGHT-2; y++) {
            if(!handler.canMove(0, offset_y*y)) {
                return offset_y*(y-1);
            }
        }
        return 0;
    }

    private void placeAtPosition(Position position) {
        if(position != null) {
            for(int r=0; r<position.getRotation(); r++) {
                keyInput.doKeyFunction(KeyEvent.VK_UP);
            }
            handler.moveTetromino(position.getOffset_x(), position.getOffset_y());
            for(int r=0; r<4 - position.getRotation(); r++) {
                keyInput.doKeyFunction(KeyEvent.VK_UP);
            }
        }
    }
}
