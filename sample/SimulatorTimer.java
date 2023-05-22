package sample;

import java.util.Timer;
import java.util.TimerTask;

public class SimulatorTimer {
    // keeps track of time in seconds
    private int seconds;

    Timer timer;
    TimerTask task = new TimerTask() {
        public void run() {
            seconds++;
        }
    };


    public SimulatorTimer() {
        seconds = 0;
        timer = new Timer(true);

        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    /*
    stopSimulationTimer() - stops the simulation timer
     */
    public void stopSimulationTimer(){
        task.cancel();
        timer.cancel();
    }

    /*
    pauseSimulationTimer() - pauses the simulation timer
     */
    public void pauseSimulationTimer() {
        task.cancel();
        timer.purge();
    }

    /*
    resumeSimulationTimer() - resumes the simulation timer
     */
    public void resumeSimulationTimer() {
        task = new TimerTask() {
            public void run() {
                seconds++;
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    /*
    getSimulationTime() - gets the current simulation time in seconds
     */
    public synchronized int getSimulationTime() {
        return seconds;
    }
}
