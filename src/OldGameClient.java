import entities.Enemy;
import entities.Player;
import generation.Room;
import network.*;
import processing.core.PApplet;
import processing.core.PVector;
import projectiles.Projectile;
import tools.Constants;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class OldGameClient extends PApplet {
    String host;
    int port;
    Client client;
    int client_id, numPlayers;

    Player player;
    HashMap<Integer, PlayerData> other_players;
    ArrayList<Projectile> projectiles;
    ArrayList<Enemy> enemies;

    Room currentRoom;

    public void settings(){
        // Server stuff -----------------------------------------------------------------------------------------------
        port = 10430;
        host = "192.168.1.173";
//        host = "192.168.2.24";
        client_id = -1;
        numPlayers = 0;
        other_players = new HashMap<>();
        projectiles = new ArrayList<>();
        enemies = new ArrayList<>();

        try {
            client = new Client(host, port);
            client.send(new Message(Message.JOIN));
        } catch (NetworkException e) {
            System.out.println(e);
            return;
        }

        // ------------------------------------------------------------------------------------------------------------
        // ------------------------------------------------------------------------------------------------------------
        size(Constants.WIDTH, Constants.HEIGHT);
        player = new Player();
    }

    public void draw() {
        fill(50);
        textSize(48);

        if (!client.isOnline()){
            exit();
        }

        //Connect the client
        while (client_id < 0) {
            if (client.getConnection().hasMessage()) {
                Message m = client.getConnection().getLatestMessage();
                if (m.getMessage_type() == Message.JOIN) {
                    client_id = m.getSender_id();
                    currentRoom = m.getRoom().fromStub();
                    player.setPos(50 * client_id + 100 * (client_id - 1), 50);
                }
            }
        }

        // Display the Room
        if (currentRoom != null){ //TODO: I wanna remove this later
            currentRoom.run(this);
        }

        // Display all players
        Iterator<Projectile> it = projectiles.iterator();
        int j = 0;
        while (it.hasNext()){
            j += 1;
            Projectile projectile = it.next();
            projectile.run(this);
            if (projectile.isDead()){
                it.remove();
            }

        }

        for (Enemy e : enemies){
            e.display(this);
        }

        for (PlayerData other : other_players.values()){
            Player p = Player.createPlayerFromStub(other);
            p.display(this);
        }
        player.run(this);


        try {
            if (client.getConnection().hasMessage()) {
                Message m = client.getConnection().getLatestMessage();

                if (m.getMessage_type() == Message.MOVE) {
                    for (Integer i : m.getPlayers().keySet()) {
                        other_players.put(i, m.getPlayerById(i));
                    }
                    ArrayList<ProjectileData> other_projectiles = m.getProjectiles();
                    for (ProjectileData pd : other_projectiles){
                        Projectile p = new Projectile(pd);
                        p.getLifespan();
                        projectiles.add(p);
                    }
                }

                //you've entered a room
                else if (m.getMessage_type() == Message.ROOM_CHANGE){
                    player.setPos(m.getPlayers().get(client_id).getPos());
                    currentRoom = m.getRoom().fromStub();

                    other_players.clear();
                    for (Integer i : m.getPlayers().keySet()) {
                        if (i != client_id) other_players.put(i, m.getPlayerById(i));
//                        System.out.println("Adding player " + i + " to player " + client_id + "'s other players");
                    }
                    System.out.println("Message received");
                    enemies.clear();
                    for (EnemyData es : m.getEnemies()){
                        System.out.println("adding enemies");
                        enemies.add(es.fromStub());
                    }
                    client.send(new Message(Message.ROOM_CHANGE, client_id));

                }

                //someone entered your room
                else if (m.getMessage_type() == Message.UPDATE){
//                    System.out.println("Clearing player " + client_id + "'s viewable players");
                    other_players.clear();
                    for (Integer i : m.getPlayers().keySet()) {
//                        System.out.println("Adding player " + i + " to player " + client_id + "'s other players");
                        other_players.put(i, m.getPlayerById(i));
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Server offline");
            exit();
        }

        //only send messages when moving, otherwise buffer gets far to full
        if (abs(player.getVelocity().mag()) != 0) {
            try {
                PlayerData d = player.toPData();
                client.send(new Message(Message.MOVE, client_id, d));
            } catch (NetworkException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public Client getClient(){
        return client;
    }

    public void mousePressed(){
        Projectile p = player.fire(new PVector(mouseX, mouseY));
        ProjectileData stub = p.toStub();
        try {
            client.send(new Message(Message.FIRE, client_id, stub));
        } catch (NetworkException e) {
            throw new RuntimeException(e);
        }
    }


    public void keyPressed(){
        if (key == 'e' || key == 'E'){
            try {
                client.close(client_id);
                return;
            } catch (NetworkException e) {
                throw new RuntimeException(e);
            }
        } else {
            player.press(key);
        }

    }

    public void keyReleased(){
        player.release(key);
    }

    public static void main(String[] args) {
        String[] processingArgs = {"PaExample"};
        OldGameClient example = new OldGameClient();

        try {
            PApplet.runSketch(processingArgs, example);
        } catch (Exception e) {
            print(e + "\n");
            System.out.println("Sever closed");
            return;
        }
    }
}
