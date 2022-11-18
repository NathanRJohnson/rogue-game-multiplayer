//import entities.Player;
//import generation.Door;
//import generation.Map;
//import generation.Room;
//import processing.core.PApplet;
//import processing.core.PVector;
//import tools.Clock;
//import tools.Constants;
//import tools.Direction;
//
//public class Game extends PApplet{
//
//    Player p;
//    Room r;
//    Clock clock;
//    Map m;
//    PApplet gameWindow;
//    int gameState;
//
//    public void settings(){
//        size(Constants.WIDTH,Constants.HEIGHT);
//        p = new Player();
//        clock = new Clock();
//
//        m = new Map(p, clock);
//        m.buildMapGraph(this);
//        r = m.getStartRoom();
//        gameState = Constants.GAMESTATE_PLAY;
//    }
//
//    public void draw() {
//        gameWindow = this;
//        if (gameState == Constants.GAMESTATE_PLAY) {
//            r.run(gameWindow);
//            p.run(gameWindow);
//            r.applyBoundaries(p);
//
//            if (r.getRoomNum() == 11 && r.getEnemies().isEmpty()) gameState = Constants.GAMESTATE_WIN;
//
//            if (p.getHealth() < 0.0) gameState = Constants.GAMESTATE_LOSE;
//
//            for (Door d : r.getDoorsMap().values()) {
//                if (!d.isLocked() && d.isEntered(p)) {
//                    Direction card_dir = d.getDirection();
//                    p.setPosByCompass(card_dir);
//                    r = d.getNextRoom();
//                    r.initRoom(gameWindow);
//                }
//            }
//            clock.run();
//        } else if (gameState == Constants.GAMESTATE_LOSE) {
//            textSize(128);
//            fill(0);
//            text("You Lose!!", 345, Constants.HEIGHT/2 + 40);
//        }
//        else if (gameState == Constants.GAMESTATE_WIN) {
//            textSize(128);
//            fill(255, 255, 0);
//            text("You WIN!!", 345, Constants.HEIGHT/2 + 40);
//        }
//
//    }
//
//    public void keyPressed(){
//        p.press(key);
//    }
//
//    public void keyReleased(){
//        p.release(key);
//    }
//
//    public void mousePressed() {
//        p.fire(new PVector(mouseX, mouseY));
//    }
//
//    public static void main(String[] args) {
//        System.out.println("Hello World");
//        String[] processingArgs = {"Game"};
//        Game game = new Game();
//        PApplet.runSketch(processingArgs, game);
//    }
//}
