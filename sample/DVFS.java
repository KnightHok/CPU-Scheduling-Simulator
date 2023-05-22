package sample;

import javafx.scene.control.Label;

public class DVFS implements Method {
    Process[] processes;
    SimulatorTimer time;
    SimulatorController controller;
    double voltage;
    double frequency;

    public DVFS(Process[] processes, SimulatorTimer time) {
        this.processes = processes;
        this.time = time;
        this.voltage = 1.8;
        this.frequency = 1.8;
    }

    public double getVoltage() {
        return this.voltage;
    }

    public double getFrequency() {
        return this.frequency;
    }

    public Process chooseNextProcess() {
        Process nextProcess = null;
        int shortestBurstTime = Integer.MAX_VALUE;

        for ( int i=0; i<processes.length; i++ ) {
            int currentArrivalTime = processes[i].getArrivalTime();
            int currentBurstTime = processes[i].getBurstTime();
            if ( currentBurstTime != 0 && currentBurstTime < shortestBurstTime && time.getSimulationTime() >= currentArrivalTime) {
                if ( currentBurstTime <= 10 ) {
                    voltage = 0.8; // low voltage
                    frequency = 0.8; // low frequency
                } else {
                    voltage = 1.8; // high voltage
                    frequency = 1.8; // high frequency
                }
//                System.out.println("Executing process " + processes[i].getProcessID() + " at voltage: " + voltage + ", frequency: " + frequency);
                nextProcess = processes[i];
            }
        }
        return nextProcess;
    }
}
