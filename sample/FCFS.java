package sample;

public class FCFS implements Method {
    Process[] processes;

    public FCFS(Process[] processes) {
        this.processes = processes;
    }
//    public void shutDownProcesses() {
//        for ( int i=0; i<processes.length; i++ ) {
//            processes[i].
//        }
//    }

    public synchronized Process chooseNextProcess() {
        Process nextProcess = null;
        int lowestArrivalTime = Integer.MAX_VALUE;

        for ( int i=0; i<processes.length; i++ ) {
            int currentArrivalTime = processes[i].getArrivalTime();
//            System.out.println(currentArrivalTime);
            if ( processes[i].getBurstTime() != 0 && currentArrivalTime < lowestArrivalTime ) {
                lowestArrivalTime = currentArrivalTime;
                nextProcess = processes[i];
            }
        }
        return nextProcess;
    }
}
