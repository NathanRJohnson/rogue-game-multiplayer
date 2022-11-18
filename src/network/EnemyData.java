package network;

import entities.Enemy;
import processing.core.PVector;

import java.io.Serializable;

public class EnemyData implements Serializable {
    PVector position;

    public void setPosition(PVector position) {
        this.position = position;
    }

    public PVector getPosition() {
        return position;
    }

    public Enemy fromStub() {
        Enemy e = new Enemy();
        e.setPos(position);
        return e;
    }
}
