package entities;

import network.PlayerData;
import processing.core.PApplet;
import processing.core.PVector;

public abstract class GameObject {
    private PVector pos, centeredPos;
    private float w, h;
    private float top, bottom, left, right;

    public GameObject() {
        pos = new PVector();
        centeredPos = new PVector();
    }
    public GameObject(PVector _pos, float _w, float _h){
        pos = _pos;
        w = _w;
        h = _h;
        centeredPos = new PVector(pos.x + w/2, pos.y + h/2);
        updateHitBox();
    }

    public void updateHitBox() {
        top = pos.y;
        bottom = pos.y + h;
        left = pos.x;
        right = pos.x + w;
    }

    public boolean collides(GameObject o){
        return (this.left < o.right && this.right > o.left &&
                this.top < o.bottom && this.bottom > o.top);
    }

    public boolean isHit(PVector p) {
        if (p.x < left || p.x > right || p.y < top || p.y > bottom) {
            return false;
        }
        return true;
    }

    public void displayHitBox(PApplet pa){
        pa.fill(255,255,0, (float) 0.5);
        pa.rect(left, top, right-left, bottom-top);

    }

    public PVector getPos() {
        return pos.copy();
    }

    public PVector getCenteredPos() {
        return centeredPos.copy();
    }

    public void setCenteredPos(PVector _cp){
        centeredPos.set(_cp);
    }

    public void updateCenteredPos(){
        centeredPos.set(pos.x + w/2, pos.y + h/2);
    }

    public void setPos(PVector p){
        pos.set(p);
    }

    public void addToPos(PVector a){
        pos.add(a);
    }

    public void setPos(float x, float y){
        pos.set(x, y);
    }

    public void setPosX(float _x){
        pos.x = _x;
    }
    public void setPosY(float _y){
        pos.y = _y;
    }

    public float getWidth(){
        return w;
    }

    public float getHeight(){
        return h;
    }

    public void setWidth(float _w){
        w = _w;
    }

    public void setHeight(float _h) {
        h = _h;
    }

}


