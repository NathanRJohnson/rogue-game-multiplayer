package generation;

import entities.GameObject;
import entities.Player;
import network.DoorData;
import processing.core.PApplet;
import processing.core.PVector;
import tools.Constants;
import tools.Direction;

public class Door extends GameObject {
//    private Room nextRoom;
    private Direction direction;
    boolean isLocked;

    Door(Direction _direction) {
        super();
        direction = _direction;
        //north south (0, 2)
        if (direction.getNumericalValue() % 2 == 0){
            setWidth(150);
            setHeight(25);
            setPosX(Constants.WIDTH/2 - getWidth()/2);
            int m = direction.getNumericalValue()/2;
            setPosY(m * Constants.HEIGHT - m * getHeight()); // - m * h
        } else { //east west (3, 1)
            setWidth(25);
            setHeight(150);
            int m = (direction.getNumericalValue() - 1)/2;
            setPos(m * Constants.WIDTH - m * getWidth(), Constants.HEIGHT/2 - getHeight()/2);
        }
        updateHitBox();
        isLocked = false;
    }
    public Door(Direction _direction, boolean _isLocked) {
        super();
        direction = _direction;
        //north south (0, 2)
        if (direction.getNumericalValue() % 2 == 0){
            setWidth(150);
            setHeight(25);
            setPosX(Constants.WIDTH/2 - getWidth()/2);
            int m = direction.getNumericalValue()/2;
            setPosY(m * Constants.HEIGHT - getHeight()/2);
        } else { //east west (3, 1)
            setWidth(25);
            setHeight(150);
            int m = (direction.getNumericalValue() - 1)/2;
            setPos(m * Constants.WIDTH - getWidth()/2, Constants.HEIGHT/2 - getHeight()/2);
        }
        updateHitBox();
        isLocked = _isLocked;
    }

//    public Room getNextRoom(){
//        return nextRoom;
//    }

    public void setLocked(boolean b){
        isLocked = b;
    }

    public boolean isLocked(){
        return isLocked;
    }

    public Direction getDirection() {
        return direction;
    }

    void display(PApplet pa) {
        if (isLocked) {
            pa.fill(255, 106, 103);
        } else {
            pa.fill(0, 106, 0);
        }
        pa.rect(getPos().x, getPos().y, getWidth(), getHeight());
//        displayHitBox(pa);
    }

    public boolean isEntered(Player p) {
        return this.collides(p);
    }

    public DoorData toStub() {
        DoorData dd = new DoorData();
        dd.setDirection(direction);
        dd.setIsLocked(isLocked);
        return dd;
    }

}
