package sample;

public class Priority implements Method {
    Process[] processes;
    SimulatorTimer time;

    public Priority(Process[] processes, SimulatorTimer time) {
        this.processes = processes;
        this.time = time;
    }


    public Process chooseNextProcess() {
        Process nextProcess = null;
        int highestPriority = Integer.MAX_VALUE; // lower numbers have higher priority
        int lowestArrivalTime = Integer.MAX_VALUE;
        int shortestBurstTime = Integer.MAX_VALUE;

        for ( int i=0; i<processes.length; i++ ) {
            int currentArrivalTime = processes[i].getArrivalTime();
            int currentPriority = processes[i].getPriority();
            int currentBurstTime = processes[i].getBurstTime();
            if ( currentBurstTime != 0 && currentPriority < highestPriority && time.getSimulationTime() >= currentArrivalTime ) {
                highestPriority = currentPriority;
                nextProcess = processes[i];
            }
        }
        return nextProcess;
    }

}
