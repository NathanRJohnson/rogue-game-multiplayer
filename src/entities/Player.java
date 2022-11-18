package entities;

import network.PlayerData;
import processing.core.PApplet;
import processing.core.PVector;
import projectiles.Launcher;
import projectiles.Projectile;
import tools.Constants;
import tools.Direction;

import java.io.Serializable;

public class Player extends Entity {
    private boolean[] inputs;
    private Launcher launcher;
    private PVector facing;

    private PlayerData pdata;
    PVector color;

    public Player() {
        super(500,500, 40, 40);
        pdata = new PlayerData();
        facing = new PVector();
        inputs = new boolean[4];
        launcher = new Launcher(getPos(), 400, 8);
        r = 15;
        launcher.setDamage(150);
        health = 255;
        color = new PVector((float) Math.random() * 100 + 155, (float) Math.random() * 100 + 155, (float) Math.random() * 100 + 155);
    }

    public Player(float _x, float _y) {
        super( _x,_y, 40, 40);
        pdata = new PlayerData();
        facing = new PVector();
        inputs = new boolean[4];
        launcher = new Launcher(getPos(), 400, 8);
        r = 15;
        launcher.setDamage(150);
        health = 255;
        color = new PVector((float) Math.random() * 255, (float) Math.random() * 255, (float) Math.random() * 255);
    }

    private Player(PlayerData data){
        this(data.getPos().x, data.getPos().y);
        facing.set(data.getFacing());
        color.set(data.getColor());
    }

    public void run(PApplet p){
        update(p);
        display(p);
    }

    @Override
    public void update(PApplet pa) {
        launcher.update(pa);
        move();
        facing = PVector.sub(new PVector(pa.mouseX, pa.mouseY), getPos());

        //removes creep
        if (PApplet.abs(vel.mag())<= 0.1) {
            vel.mult(0);
        }

        addToPos(vel);
        launcher.setPos(getPos());
        updateHitBox();

        //drag effect
        PVector inv = vel.copy();
        inv.normalize();
        inv.mult(-1);
        inv.mult((float) 0.3);
        acc.add(inv);

        vel.add(acc);
        vel.limit(maxspeed);

        if (pa.abs(vel.mag()) < 0.5){
            vel.mult(0);
        }

        acc.mult(0);
    }

    @Override
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


        pa.fill(0);
        pa.textSize(16);
        pa.text("Health", 55, 720);
        pa.fill(0, 200, 0);
        pa.rect(55, 730, PApplet.max(health, 0), 35);
        pa.stroke(1);
        pa.noFill();
        pa.rect(55, 730, 255, 35);

//        displayHitBox(pa);
    }



    public void setPosByCompass(Direction d) {
        switch (d) {
            case NORTH:
                setPos(Constants.WIDTH/2, Constants.HEIGHT - 100);
                break;
            case SOUTH:
                setPos(Constants.WIDTH/2, 100);
                break;
            case EAST:
                setPos(100, Constants.HEIGHT/2);
                break;
            case WEST:
                setPos(Constants.WIDTH - 100, Constants.HEIGHT/2);
        }
    }

    public Projectile fire(PVector target) {
        return launcher.fire(target);
    }

    public Launcher getLauncher() {
        return launcher;
    }

    public void press(char k) {
        if (k == 'w' || k == 'W') {
            inputs[0] = true;
        } else if (k == 's' || k == 'S') {
            inputs[1] = true;
        }

        if (k == 'a' || k == 'A') {
            inputs[2] = true;
        } else if (k == 'd' || k == 'D') {
            inputs[3] = true;
        }
    }

    public void move() {
        if (inputs[0]) {
            acc.y = -1;
        }
        if (inputs[1]) {
            acc.y = 1;
        }

        if (inputs[2]) {
            acc.x = -1;
        }
        if (inputs[3]) {
            acc.x = 1;
        }
    }

    public void release(char k) {
        if (k == 'w' || k == 'W') {
            inputs[0] = false;
        } else if (k == 's' || k == 'S') {
            inputs[1] = false;
        }

        if (k == 'a' || k == 'A') {
            inputs[2] = false;
        } else if (k == 'd' || k == 'D') {
            inputs[3] = false;
        }
    }

    public PlayerData toPData(){
        pdata.load(getPos());
        pdata.setFacing(facing);
        pdata.setColor(color);
        return new PlayerData(pdata); //TODO: should be cloned at somepoint before passing back
    }

    public static Player createPlayerFromStub(PlayerData stub){
        return new Player(stub);
    }

    public PVector getFacing() {
        return facing;
    }
}
