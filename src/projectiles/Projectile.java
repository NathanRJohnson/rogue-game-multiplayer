package projectiles;

import entities.GameObject;
import network.ProjectileData;
import processing.core.PApplet;
import processing.core.PVector;

public class Projectile extends GameObject {
    PVector vel;
    float size;
    float lifespan, decay_rate, damage;
    boolean isDead;

    public Projectile(PVector _pos, float _range, float _speed, float _damage){
        super(_pos.copy(),15, 15);
        vel = new PVector();
        lifespan = _range;
        decay_rate = _speed;
        damage = _damage;
        isDead = false;

    }

    public Projectile(ProjectileData pd){
        this(pd.getLocation(), pd.getLifespan(), pd.getSpeed(), pd.getDamage());
        setVel(pd.getVelocity());

    }

    public boolean isDead() {
        return isDead || lifespan < 0.0;
    }

    public void run(PApplet p) {
        update();
        display(p);
    }

    private void update() {
        addToPos(vel);
        lifespan -= decay_rate;
        updateHitBox();
    }

    private void display(PApplet pa) {
        pa.fill(255, 0, 0);
        pa.ellipse(getPos().x + 7, getPos().y + 7, 15, 15);

//        displayHitBox(pa);
    }

    public void setVel(PVector _v) {
        vel = _v;
    }

    public double getDamage(){
        return damage;
    }

    public void setDead(){
        isDead = true;
    }

    public ProjectileData toStub(){
        ProjectileData stub = new ProjectileData();
        stub.setDamage(damage);
        stub.setLifespan(lifespan);
        stub.setSpeed(decay_rate);
        stub.setPosition(getPos());
        stub.setVelocity(vel);
        return stub;
    }

    public PVector getVel() {
        return vel.copy();
    }

    public float getLifespan() {
        return lifespan;
    }

    public float getDecay_rate() {
        return decay_rate;
    }
}
