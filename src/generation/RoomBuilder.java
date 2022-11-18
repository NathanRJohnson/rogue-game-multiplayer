package generation;

import entities.Player;
import processing.core.PApplet;
import tools.Clock;
import tools.Direction;

public class RoomBuilder {

    protected RoomBuilder(){}

    Room buildRoom() {
        Room r = new Room();
        r.initRoom();
        return r;
    }

    Room buildStartRoom() {
        return new Room();
    }

    void connectRooms(Room Room1, Room Room2, Direction card_direction){
        Room1.addDoor(card_direction, true);
        Room1.addRoom(card_direction, Room2);
        Room2.addDoor(card_direction.getOpposite(), true);
        Room2.addRoom(card_direction.getOpposite(), Room1);
    }
}
