package entities;

import processing.core.PApplet;
import processing.core.PVector;
import tools.Clock;

import java.util.ArrayList;

public class Charger extends Enemy{

    private double charge_speed, walk_speed;
    private double time_of_charge, charge_time;
    private double windup_time, time_of_windup, time_till_charge;

    private double walk_time, time_of_walk;

    private double time_of_cooldown, cooldown_time;
    private boolean isCharging, isWinding, isCooldown, isWalking;

    public Charger(){
        super();
        charge_speed = 11;
        walk_speed = 0.8;
        isCharging = false;
        isWinding = false;
        isCooldown = false;
        isWalking = true;

        time_of_windup = 0;
        windup_time = 1;

        time_of_charge = 0;
        charge_time = 0.8;

        time_of_cooldown = 0;
        cooldown_time = 1;

        time_of_walk = 0;
        walk_time = 2;

        damage = 100;

    }

    public void run(Player player, ArrayList<Enemy> enemies, Clock clock, PApplet pa){
        if (canAttack(clock) && this.collides(player)){
            attack(player, clock);
        }

        if (isWalking) {
            maxspeed = 2;
            seek(player.getPos());
        }

        //start windup
        if (isWalking && time_of_walk + walk_time < clock.getTime()) {
            windup(clock);
            isWalking = false;
            System.out.println("winding up!");
            setCanAttack(false);
            maxspeed = 0;
        }

        if (isWinding) {
            time_till_charge = windup_time + time_of_windup - clock.getTime();
        }


        //check if done winding up
        if (isWinding && time_of_windup + windup_time < clock.getTime()){
            isWinding = false;
            maxspeed = (float) charge_speed;
            charge(target(player.getPos()));
            time_of_charge = clock.getTime();
            setCanAttack(true);
            System.out.println("charging!");
        }

        //player gets hit
        if (isCharging && collides(player) && canAttack(clock)){
            isCharging = false;
            attack(player, clock);
            isCooldown = true;
            time_of_cooldown = clock.getTime();
        }

        //end of charge, start cool down
        if (isCharging && time_of_charge + charge_time < clock.getTime()){
            isCharging = false;
            isCooldown = true;
            maxspeed = 0;
            time_of_cooldown = clock.getTime();
            System.out.println("cool down time!");
        }

        //end of cool down, start walking
        if (isCooldown && time_of_cooldown + cooldown_time < clock.getTime()){
            isCooldown = false;
            isWalking = true;
            time_of_walk = clock.getTime();
            seek(player.getPos());
            System.out.println("walk time");
            maxspeed = (float) walk_speed;
        }


        display(pa);
        update(pa);
    }

    private void windup(Clock clock){
        time_of_windup = clock.getTime();
        maxspeed = 0;
        isWinding = true;
    }

    private void bounceBack(){
        PVector dir = PVector.mult(vel, (float) -5);
        applyForce(dir);
    }

    private void charge(PVector target){
        PVector charge = PVector.mult(target, (float) charge_speed);
        applyForce(charge);
        isCharging = true;
    }

    public void display(PApplet pa){
        if (isWinding) {
            float c = 155 + PApplet.map((float) time_till_charge, 0, 1, 0, 100);
            pa.fill(255,c,c);
        } else if (isCooldown){
            pa.fill(0, 0, 200);
        } else {
            pa.fill(150, 0, 0);
        }
        pa.rect(getPos().x, getPos().y, 2*r, 2*r);
        displayHealthbar(pa);
    }



//    private boolean canCharge(Clock clock){
//        if (isWinding || isCooldown) return false;
//        if (time_of_charge + charge_time > clock.getTime()){
//            return false;
//        }
//        return true;
//    }


}
