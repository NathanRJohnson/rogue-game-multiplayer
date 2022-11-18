package network;

import generation.Door;
import generation.Room;
import tools.Direction;

import java.io.Serializable;
import java.util.HashMap;

public class RoomData implements Serializable {
    private int room_number;
    protected HashMap<Direction, DoorData> doors = new HashMap<>();
    protected HashMap<Direction, RoomData> nextRooms = new HashMap<>();

    public void setRoomNumber(int n) {
        room_number = n;
    }

    public void putDoor(Direction direction, DoorData d) {
        doors.put(direction, d);
    }

    public Room fromStub(){
        Room room = new Room();
        room.setRoomNumber(room_number);
            for (Direction dir : Direction.values()){
                if (doors.containsKey(dir)){
                    room.addDoor(dir, doors.get(dir).fromStub());
                    if (nextRooms.get(dir) != null) {
                        room.addRoom(dir, nextRooms.get(dir).fromStub());
                    }
                }
            }
        return room;
    }

    public int getRoomNumber() {
        return room_number;
    }
}

//    Room room = new Room();
//        room.setRoomNumber(room_number);
//                for (DoorData dd : doors.values()) {
//                System.out.println("YEAH");
//                room.addDoor(dd.getDirection(), dd.fromStub());
//                room.addRoom(dd.getDirection(), nextRooms.get(dd.getDirection()).fromStub());
//                }
//                return room;
