package network;

import generation.Door;
import generation.Room;
import tools.Direction;

import java.io.Serializable;

public class DoorData implements Serializable {
    private Direction direction;
    private boolean isLocked;

    public void setDirection(Direction _direction) {
        direction = _direction;
    }

    public void setIsLocked(boolean _isLocked) {
        isLocked = _isLocked;
    }

    public Door fromStub() {
        return new Door(direction, isLocked);
    }

    public Direction getDirection() {
        return direction;
    }
}
