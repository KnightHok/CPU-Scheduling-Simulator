package sample;

public class ShortestJobFirst implements Method {

    Process[] processes;
    SimulatorTimer time;

    public ShortestJobFirst(Process[] processes, SimulatorTimer time) {
        this.processes = processes;
        this.time = time;
    }


    public Process chooseNextProcess() {
        Process nextProcess = null;
        int lowestArrivalTime = Integer.MAX_VALUE;
        int shortestBurstTime = Integer.MAX_VALUE;

        for ( int i=0; i<processes.length; i++ ) {
            int currentArrivalTime = processes[i].getArrivalTime();
            int currentBurstTime = processes[i].getBurstTime();
            if ( currentBurstTime != 0 && currentBurstTime < shortestBurstTime && time.getSimulationTime() >= currentArrivalTime) {
                nextProcess = processes[i];
            }
        }
        return nextProcess;
    }
}
