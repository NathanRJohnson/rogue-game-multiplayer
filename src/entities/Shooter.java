package entities;

import processing.core.PApplet;
import processing.core.PVector;
import projectiles.Launcher;
import projectiles.Projectile;
import tools.Clock;

import java.util.ArrayList;
import java.util.Iterator;

public class Shooter extends Enemy{
    Launcher launcher;
    public Shooter(){
        super();
        maxspeed = 0;
        attack_range = 350;
        launcher = new Launcher(getCenteredPos(), attack_range, 5);
        damage = 50;

        launcher.setDamage((float) damage);
    }

    public void run(Player player, ArrayList<Enemy> enemies, Clock clock, PApplet pa) {
        launcher.update(pa);
        PVector target = target(player.getPos());
        if (inRange(target) && canAttack(clock)){
            attack(player, clock);
        }

        for (Projectile p : launcher.getProjectiles()){
            p.run(pa);
            if (p.collides(player)){
                player.health -= damage;
                time_of_attack = clock.getTime();
                p.setDead();
            }
        }

        display(pa);
    }

    @Override
    protected PVector target(PVector player_pos) {
        return PVector.sub(player_pos, this.getPos());
    }

    @Override
    public void attack(Player player, Clock c) {
        launcher.fire(player.getPos());
        time_of_attack = c.getTime();
    }

    public void display(PApplet pa) {
//        pa.rectMode(PApplet.CENTER);
        pa.fill(150, 0, 0);
        pa.rect(getPos().x, getPos().y, 2*r, 2*r);
//        displayHitBox(pa);
        launcher.displayAttackRange(pa);
        displayHealthbar(pa);
    }

}
