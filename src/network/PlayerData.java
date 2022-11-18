package network;

import entities.Player;
import generation.Room;
import processing.core.PApplet;
import processing.core.PVector;

import java.io.Serializable;

public class PlayerData implements Serializable {
    PVector position = new PVector();
    PVector facing = new PVector();
    PVector color = new PVector();
    private int currentRoomNum;
    private int r;
    public PlayerData(){}

    public PlayerData(PVector _position){
        position = _position.copy();
    }

    public PlayerData(PlayerData copy){
        position = copy.position;
        facing = copy.facing;
        color = copy.color;
        currentRoomNum = 0;
        r = 15;
    }

    public void display(PApplet pa) {
        pa.fill(color.x,color.y,color.z);
        float theta = facing.heading() + PApplet.PI/2;
//        pa.fill(0,50,200);
        pa.stroke(0);
        pa.pushMatrix();
        pa.translate(getPos().x + r, getPos().y + r);
        pa.rotate(theta);
        pa.beginShape();
        pa.vertex(0, -r*2);
        pa.vertex(-r, r*2);
        pa.vertex(r, r*2);
        pa.endShape(PApplet.CLOSE);
        pa.popMatrix();


//        pa.fill(0);
//        pa.textSize(16);
//        pa.text("Health", 55, 720);
//        pa.fill(0, 200, 0);
//        pa.rect(55, 730, PApplet.max(health, 0), 35);
//        pa.stroke(1);
//        pa.noFill();
//        pa.rect(55, 730, 255, 35);

//        displayHitBox(pa);
    }

    public void load(PVector _l){
        position = _l.copy();
    }

    public PlayerData copy() {
        return new PlayerData(position);
    }

    public PVector getPos() {
        return position.copy();
    }

    public void setFacing(PVector _f){
        facing = _f.copy();
    }

    public PVector getFacing() {
        return facing;
    }

    public void setColor(PVector color) {
        this.color = color;
    }

    public PVector getColor() {
        return color;
    }

    public int getCurrentRoomNum(){
        return currentRoomNum;
    }

}


