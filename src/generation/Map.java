package generation;

import entities.Enemy;
import entities.Player;
import processing.core.PApplet;
import processing.core.PVector;
import tools.Clock;
import tools.Constants;
import tools.Direction;

import java.util.ArrayList;
import java.util.HashMap;

public class Map {
    int max_rooms, current_rooms;
    int grid_width;

    private Room startRoom;
    private RoomBuilder rb;
    private Player player;
    private Clock clock;
    private PApplet randomizer;
    private HashMap<Integer, ArrayList<Enemy>> enemiesByRoom;
    private Room[] rooms;

    public Map() {
        rb = new RoomBuilder();
        max_rooms = 10;
        rooms = new Room[max_rooms];
        startRoom = rb.buildStartRoom();
        rooms[0] = startRoom;
        current_rooms = 1;
        grid_width = 5;
        randomizer = new PApplet();
    }

    public void buildMapGraph() {
        Room prevRoom = startRoom, newRoom;
        PVector walker = new PVector((int) randomizer.random(0, grid_width), (int) randomizer.random(0, grid_width));
        Direction card_direction;
        int debug = 0;
        while (current_rooms <= max_rooms && debug < 4) {
            do {
                card_direction = Direction.getRandomDirection();
            } while (step(walker, card_direction) == Constants.FAIL);
            if (!prevRoom.doorExistsAt(card_direction)) {
                current_rooms++;
                newRoom = rb.buildRoom();
                newRoom.set_d_num(current_rooms);
                rooms[current_rooms] = newRoom;
                rb.connectRooms(prevRoom, newRoom, card_direction);
                prevRoom = newRoom;
            } else {
                prevRoom = prevRoom.getRoomAt(card_direction);
            }
        }
    }

    public Room getStartRoom(){
        return startRoom;
    }

    public Room getRoomById(int i) {
        if ( i < 0 || i > max_rooms - 1 ) {
            return null;
        } else {
            return rooms[i];
        }
    }

    //0 on success, -1 on fail
    public int step(PVector walker, Direction card_direction) {
        switch(card_direction) {
            case NORTH:
                if (walker.x == 0) return Constants.FAIL;
                walker.x--;
                return Constants.VALID;
            case SOUTH:
                if (walker.x == grid_width) return Constants.FAIL;
                walker.x++;
                return Constants.VALID;
            case EAST:
                if (walker.y == grid_width) return Constants.FAIL;
                walker.y++;
                return Constants.VALID;
            case WEST:
                if (walker.y == 0) return Constants.FAIL;
                walker.y--;
                return Constants.VALID;
        }
        return Constants.VALID;
    }
}
