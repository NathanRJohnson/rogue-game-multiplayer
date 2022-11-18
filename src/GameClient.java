import entities.Player;
import network.Client;
import network.Message;
import network.NetworkException;
import network.PlayerData;
import processing.core.PApplet;
import processing.core.PVector;
import tools.Constants;

import java.util.ArrayList;
import java.util.HashMap;

class GameClient extends PApplet {
    String host;
    int port;
    Client client;
    int id;
    Player player;
    HashMap<Integer, PlayerData> players;

    PVector prev_facing;

    // change this to a state var

    public void settings(){
        // Server stuff -----------------------------------------------------------------------------------------------
        port = 10430;
        host = "192.168.1.173";
        id = -1;

        try {
            client = new Client(host, port);
            System.out.println("Sending JOIN message to server");
            client.send(new Message(Message.JOIN));
        } catch (NetworkException e) {
            System.out.println(e);
            return;
        }

        while (true) {
            if (client.getConnection().hasMessage()) {
                Message m = client.getConnection().getLatestMessage();
                if (m.getMessage_type() == Message.JOIN){
                    id = m.get_receiver_id();
                    break;

                }

            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // ------------------------------------------------------------------------------------------------------------
        // ------------------------------------------------------------------------------------------------------------
        size(Constants.WIDTH, Constants.HEIGHT);
        player = new Player(50, 50);
        players = new HashMap<>();
        prev_facing = player.getFacing();
        // state
    }

    public void draw() {
        this.background(50);
        textSize(48);

        if (!client.isOnline()){
            System.out.println("Exiting");
            exit();
        }
        // sync loop -----------------------------------------------------------------------------
        try {
            if (client.getConnection().hasMessage()) {
                Message m = client.getConnection().getLatestMessage();

                // get the updated display info for players
                players.clear();
                for (Integer i : m.getPlayers().keySet()) {
                    players.put(i, m.getPlayerById(i));
//                    System.out.println("Adding player " + i);
                }
                players.put(id, player.toPData());

//                System.out.println("Player " + id +"'s received position: " + player.getPos());
                player.run(this);

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Server offline");
            exit();
        }
        // end sync loop ---------------------------------------------------------------------------

        // display loop ----------------------------------------------------------------------------

        for (PlayerData pd : players.values()) {
            Player p = Player.createPlayerFromStub(pd);
            p.display(this);
        }

        // end display loop ------------------------------------------------------------------------

        player.update(this);

        if ( PVector.sub(prev_facing, player.getFacing()).mag() != 0  ||abs(player.getVelocity().mag()) != 0) {
            prev_facing = player.getFacing();
            Message response = new Message(Message.MOVE, id, player.toPData());
//            System.out.println("Player " + id +"'s sent: " + player.getPos());
            try {
                client.send(response);
            } catch (NetworkException e) {
                System.out.println(e.getMessage());
            }

        }


    }

    public void sync(){

    }

    public void send_update() {

    }

    // inputs
    public void keyPressed(){
        if (key == 'e' || key == 'E'){
            Message dc = new Message(Message.DISCONNECT, id);
            try {
                System.out.println("Sending disconnect message");
                while (client.isOnline()){
                    client.close(id);
                }
            } catch (NetworkException e) {
                throw new RuntimeException(e);
            }
            exit();
        } else {
            player.press(key);
        }

    }

    public void keyReleased(){
        player.release(key);
    }

    public static void main(String[] args) {
        String[] processingArgs = {"PaExample"};
        GameClient example = new GameClient();

        try {
            PApplet.runSketch(processingArgs, example);
        } catch (Exception e) {
            print(e + "\n");
            System.out.println("Sever closed");
            return;
        }
    }
}