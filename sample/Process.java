package sample;

public class Process implements Runnable {
    private final int processID;
    private final int arrivalTime;
    private final int totalBurstTime;
    private int currentBurstTime;
    private boolean isPaused;
    private volatile double progress;
    private SimulatorTimer time;
    private Method method;
    private int priority;

    public Process(int processID, int arrivalTime, int totalBurstTime, SimulatorTimer time, Method method) {
        this.processID = processID;
        progress = 0;
        this.isPaused = false;
        this.arrivalTime = arrivalTime;
        this.totalBurstTime = this.currentBurstTime = totalBurstTime;
        this.time = time;
        this.method = method;
        this.priority = 0;
    }
    public Process(int processID, int arrivalTime, int totalBurstTime, SimulatorTimer time, Method method, int priority) {
        this.processID = processID;
        progress = 0;
        this.isPaused = false;
        this.arrivalTime = arrivalTime;
        this.totalBurstTime = this.currentBurstTime = totalBurstTime;
        this.time = time;
        this.method = method;
        this.priority = priority;
    }

    /*
    getProcessID() - returns the processID
     */
    public int getProcessID() {
        return this.processID;
    }

    /*
    getBurstTime() - returns remaining burst time
     */
    public int getBurstTime() {
        return currentBurstTime;
    }

    /*
    decrementBurstTime() - decrements the burst time
     */
    public synchronized void decrementBurstTime() {
        currentBurstTime--;
    }

    /*
    updateProgress() - updates the progress of the process
     */
    public synchronized void updateProgress() {
        this.progress = 1.0 - ((double)currentBurstTime / (double)totalBurstTime);
//        System.out.println("new progress " + progress);
    }

    /*
    getProgress() - returns the progress of process
     */
    public double getProgress() {
        return progress;
    }

    /*
    getArrivalTime() - returns the arrival time of the process
     */
    public int getArrivalTime() {
        return arrivalTime;
    }

    /*
    pauseThread() - pauses the current Thread
     */
    public void pauseThread() {
        isPaused = true;
    }

    /*
    resumeThread() - resumes the current process
     */
    public synchronized void resumeThread() {
        isPaused = false;
        notify();
    }

    /*
    getPriority() - returns the priority of the current thread
     */
    public int getPriority() {
        return priority;
    }

    public void run() {
       while ( !Thread.currentThread().isInterrupted() ) {
           synchronized(this) {
           System.out.println("hit");
               System.out.println(Thread.activeCount());
               while (!isPaused && getBurstTime() != 0) {
                   // choose the next Process to run
                   Process nextProcess = method.chooseNextProcess();
                   if ( this == nextProcess ) {
                       if ( time.getSimulationTime() >= arrivalTime ) {
                           decrementBurstTime();
                           updateProgress();
                           try {
                               Thread.sleep(1000);
                           } catch (InterruptedException e) {
                               e.printStackTrace();
                           }
                       }
                   }

                   if ( nextProcess == null ) pauseThread();
               }

               // if thread is not running wait for another thread to start it
               try {
                   System.out.println("Waiting");
                   wait();
               } catch (InterruptedException e) {
//               e.printStackTrace();
                   Thread.currentThread().interrupt();
                   System.out.println("DONE");
               }
           }


       }
    }
}
