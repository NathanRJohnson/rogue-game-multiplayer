package entities;

import processing.core.PApplet;
import processing.core.PVector;
import projectiles.Projectile;

public abstract class Entity extends GameObject {
    PVector vel, acc;
    float maxspeed, r;
    float health;

    Entity(float x, float y, float _w, float _h){
        super(new PVector(x, y), _w, _h );
        vel = new PVector();
        acc = new PVector();
        maxspeed = 6;
    }

    Entity(float _w, float _h){
       super(new PVector(0,0), _w, _h);
        vel = new PVector();
        acc = new PVector();
        maxspeed = 6;
    }

    abstract void display(PApplet p);
    abstract void update(PApplet p);

    public boolean isDead(){
        if (health < 0.0){
            return true;
        }
        return false;
    }

    void applyForce(PVector force) {
        acc.add(force);
    }

    public void hit(Projectile p) {
        health -= p.getDamage();
        p.setDead();
    }

    public float getHealth(){
        return health;
    }

    public PVector GetVel() {
        return vel.copy();
    }

    public void setXVel(float _x) {
        vel.x = _x;
    }

    public void setYVel(float _y) {
        vel.y = _y;
    }

    public void displayHealthbar(PApplet pa) {
        pa.fill(255 - health, health, 0);
        pa.rect(getPos().x, getPos().y - 20, pa.map(health, 0, 255, 0, getWidth()), 5);
    }

    public PVector getVelocity() {
        return vel;
    }
}
