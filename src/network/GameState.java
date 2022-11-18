package network;

// singleton?
public class GameState {

    private boolean new_player_flag;
    private boolean player_moved_flag;

    public GameState(){
        player_moved_flag = false;
        new_player_flag = false;
    }

    public void set_new_player_flag(boolean new_player_flag) {
        this.new_player_flag = new_player_flag;
    }

    public boolean get_new_player_flag() {
        return new_player_flag;
    }

    public void set_player_moved_flag(boolean player_moved_flag) {
        this.player_moved_flag = player_moved_flag;
    }

    public boolean get_player_moved_flag() {
        return player_moved_flag;
    }
}
