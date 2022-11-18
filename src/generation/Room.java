package generation;

import entities.Charger;
import entities.Enemy;
import entities.Shooter;
import network.RoomData;
import processing.core.PApplet;
import tools.Constants;
import tools.Direction;
import entities.Entity;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.random;

public class Room {
    private ArrayList<Enemy> enemies;
    protected HashMap<Direction, Door> doors = new HashMap<>();
    protected HashMap<Direction, Room> nextRooms = new HashMap<>();
    private int d_room_num;
    boolean hasSpawned;

    private float north_boundary, south_boundary, east_boundary, west_boundary;

    public Room() {
        enemies = new ArrayList<Enemy>();
        north_boundary = 15;
        south_boundary = Constants.HEIGHT - 15;
        east_boundary = 15;
        west_boundary = Constants.WIDTH - 15;
        hasSpawned = false;
    }

    public void initRoom() {
        if (hasSpawned) {
            return;
        }
        generate_one();
    }

    public void generateEnemySet(){
        double r = Math.random();
        if (r < 0.33) {
            generate_one();
        } else if (r < 0.66) {
            generate_two();
        } else {
            generate_three();
        }
    }


    public void addDoor(Direction d, Door door){
        doors.put(d, door);
    }

    public void addDoor(Direction d, boolean isLocked){
        doors.put(d, new Door(d, isLocked));
    }

    public void addRoom(Direction d, Room nextRoom){
        nextRooms.put(d, nextRoom);
    }


//    Room getRoomAt(Direction card_direction){
//        return doors.get(card_direction).getNextRoom();
//    }

    Boolean doorExistsAt(Direction card_direction){
        return doors.get(card_direction) != null;
    }

    void set_d_num(int n){
        d_room_num = n;
    }

    public void applyBoundaries(Entity entity) {
        //quick spike to see what direction velo is moving in
        if (entity.getPos().y <= north_boundary && entity.GetVel().y < 0) {
            entity.setYVel(0);
        }
        if (entity.getPos().y >= south_boundary && entity.GetVel().y > 0) {
            entity.setYVel(0);
        }
        if (entity.getPos().x <= east_boundary && entity.GetVel().x < 0) {
            entity.setXVel(0);
        }
        if (entity.getPos().x >= west_boundary && entity.GetVel().x > 0) {
            entity.setXVel(0);
        }

    }

    public void unlockDoors(){
        for (Door d : doors.values()){
            d.setLocked(false);
        }
    }

    //display and update
    public void run(PApplet pa) {
        pa.background(100);
        pa.text(d_room_num, 50, 50);
//        Iterator<Enemy> it = enemies.iterator();

//        while (it.hasNext()) {
//            Enemy e = (Enemy) it.next();
//            e.run(p, enemies, clock, pa);
//            applyBoundaries(e);
//            if (e.isDead()) {
//                it.remove();
//            }
//        }

//        if (enemies.isEmpty()){
//            unlockDoors();
//        }

//        Player's projectiles
//        for (Projectile p : p.getLauncher().getProjectiles()) {
//            for (Enemy e : enemies) {
//                if (e.collides(p)) {
//                    e.hit(p);
//                }
//            }
//        }

        for (Door d: doors.values()){
//            System.out.println(doors.size() + "  " + d.getDirection());
            d.display(pa);
        }
    }

    private void generate_one(){
        enemies.add(new Enemy());
        enemies.add(new Enemy());
        enemies.add(new Enemy());
    }
    private void generate_two(){
        enemies.add(new Enemy());
        enemies.add(new Shooter());
        enemies.add(new Shooter());
    }
    private void generate_three(){
        enemies.add(new Charger());
        enemies.add(new Shooter());
        enemies.add(new Charger());
    }

    public int getRoomNum(){
        return d_room_num;
    }

    public ArrayList<Enemy> getEnemies(){
        return enemies;
    }

    public HashMap<Direction, Door> getDoorsMap(){
        return doors;
    }

    public RoomData toStub() {
        RoomData rd = new RoomData();
        rd.setRoomNumber(d_room_num);
        for (Door d : doors.values()){

            rd.putDoor(d.getDirection(), d.toStub());
        }
        return rd;
    }

    public void setRoomNumber(int room_number) {
        d_room_num = room_number;
    }

    public Room getRoomAt(Direction card_direction) {
        return nextRooms.get(card_direction);
    }

    public boolean isHasSpawned() {
        return hasSpawned;
    }

    public void setHasSpawned(boolean hasSpawned) {
        this.hasSpawned = hasSpawned;
    }
}
