package tools;

import java.io.Serializable;

public enum Direction implements Serializable {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    public int getNumericalValue() {
        switch (this) {
            case NORTH:
                return 0;
            case SOUTH:
                return 2;
            case EAST:
                return 3;
            case WEST:
                return 1;
        }
        return -1;
    }

    public Direction getOpposite() {
        switch (this) {
            case NORTH:
                return SOUTH;
            case SOUTH:
                return NORTH;
            case EAST:
                return WEST;
            case WEST:
                return EAST;
        }
        return NORTH;
    }

    public static Direction getRandomDirection() {
        int r = (int) (Math.random() * values().length);
        return values()[r];
    }
}
