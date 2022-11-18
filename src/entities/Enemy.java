package entities;

import network.EnemyData;
import processing.core.PApplet;
import processing.core.PVector;
import tools.Clock;
import tools.Constants;

import java.util.ArrayList;

public class Enemy extends Entity{
    public double attack_speed;
    public double attack_delay;
    public double time_of_attack;
    public double attack_backswing;
    public double damage;
    public float attack_range;
    protected boolean canAttack;


    private PApplet randomizer = new PApplet();

    public Enemy() {
        super(70, 70);
        setPos(randomizer.random(150, Constants.WIDTH - 150), randomizer.random(150, Constants.HEIGHT - 150));
        health = 255;
        r = 35;
        maxspeed = 3;
        //  attack_speed = 1.0;
        attack_delay = 0.5;
        attack_backswing = 0.25;
        time_of_attack = 0.0;
        damage = 25;
        canAttack = true;
    }

    public void run(Player player, ArrayList<Enemy> enemies, Clock clock, PApplet pa) {
        PVector hunt = target(player.getPos());
        PVector sep = separate(enemies);
        hunt.mult((float) 0.5);

        if (canAttack(clock) && this.collides(player)){
            attack(player, clock);
        }
        applyForce(hunt);
        applyForce(sep);

        if (!isAttacking(clock)) {
            update(pa);
        }
        display(pa);
    }

    public void attack(Player player, Clock c) {
        player.health -= damage;
        time_of_attack = c.getTime();
        this.vel.mult(0);
    }

    protected boolean canAttack(Clock c){
        if (time_of_attack + attack_delay + attack_backswing < c.getTime()){
            canAttack = true;
        } else {
            canAttack = false;
        }
        return canAttack;
    }


    protected boolean inRange(PApplet pa, Player p) {
        return PApplet.abs(PVector.sub(p.getPos(), getPos()).mag()) < attack_range;
    }

    protected  boolean inRange(PVector p){
        return PApplet.abs(p.mag()) < attack_range;
    }

    protected boolean isAttacking(Clock c){
        if (time_of_attack + attack_backswing > c.getTime()){
            return true;
        }
        return false;
    }

    @Override
    public void update(PApplet pa) {
        updateHitBox();
        vel.add(acc);
        vel.limit(maxspeed);
        addToPos(vel);
        acc.mult(0);
    }

    protected PVector target(PVector player_pos) {
        PVector dir = PVector.sub(player_pos, this.getPos());
        dir.normalize();
        return dir;
    }

    protected void seek(PVector target) {
        PVector dir = PVector.sub(target, this.getPos());
        dir.normalize();
        dir.mult(maxspeed);
        applyForce(dir);
    }

    public PVector separate(ArrayList<Enemy> enemies) {
        float desired_separation = 70;
        PVector sum = new PVector();
        float count = 0;
        PVector steer = new PVector();
        for (Enemy other : enemies) {
            float d = PVector.dist(getPos(), other.getPos());
            if (d > 0 && d < desired_separation) {
                PVector diff = PVector.sub(getPos(), other.getPos());
                diff.normalize();
                diff.div(d);
                sum.add(diff);
                count++;
            }
        }
        if (count > 0) {
            sum.div(count);
            sum.setMag(maxspeed);
            steer = PVector.sub(sum, vel);
            //steer.limit(maxforce);
        }

        return steer;
    }

    @Override
    public void display(PApplet pa) {
//        pa.rectMode(PApplet.CENTER);
        pa.fill(150, 0, 0);
        pa.rect(getPos().x, getPos().y, 2*r, 2*r);
        displayHealthbar(pa);
//        displayHitBox(pa);
    }
    public void setCanAttack(boolean b){
        canAttack = b;
    }

    public boolean getCanAttack(){
        return canAttack;
    }

    public EnemyData toStub(){
        EnemyData es = new EnemyData();
        es.setPosition(this.getPos());
        return es;
    }
}
