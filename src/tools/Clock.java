package tools;

public class Clock {
    private double time;
    private double incrememnt;

    public Clock() {
        time = 0;
        incrememnt = 0.01;
    }

    public void run() {
        time += incrememnt;
    }

    public double getTime() {
        return time;
    }
}
