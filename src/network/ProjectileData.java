package network;

import processing.core.PVector;

import java.io.Serializable;

public class ProjectileData implements Serializable {
    private float lifespan, damage, range, speed;
    private PVector location, velocity;

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getDamage() {
        return damage;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setLifespan(float lifespan) {
        this.lifespan = lifespan;
    }

    public float getLifespan() {
        return lifespan;
    }

    public void setPosition(PVector location) {
        this.location = location;
    }

    public PVector getLocation() {
        return location;
    }

    public void setVelocity(PVector velocity) {
        this.velocity = velocity;
    }

    public PVector getVelocity() {
        return velocity;
    }
}
