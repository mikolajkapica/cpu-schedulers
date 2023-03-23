package com.mikolajkapica.process;


import com.mikolajkapica.time.TimeSimulator;

public class Process {
    private int id;
    private int burstTime;
    private int arrivalTime;
    private int startingTime;
    private int remainingTime;
    private int runningTime;
    private int finishingTime;
    private int suspendedTime;
    private int lastSuspendedTime;
    private Status status;
    private static Process lastProcess = null;

    public Process(int id, int burstTime, int arrivalTime) {
        this.id = id;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.remainingTime = burstTime;
        this.startingTime = -1;
        this.runningTime = 0;
        this.suspendedTime = 0;
        this.status = Status.NEW;
    }

    public int getLastSuspendedTime() {
        return lastSuspendedTime;
    }

    public void setLastSuspendedTime(int lastSuspendedTime) {
        this.lastSuspendedTime = lastSuspendedTime;
    }

    public void run() {

        if (lastProcess == null) {
            lastProcess = this;
        } else {
            if (lastProcess != this) {
                ProcessesStorage.incrementProcessSwitchCounter();
                lastProcess = this;
            }
        }

        if (status == Status.NEW) {
            status = Status.RUNNING;
            ProcessesStorage.removeFromNew(this);
            ProcessesStorage.addToRunning(this);
            startingTime = TimeSimulator.getCurrentTime();
        }
        // if is the first time that the process is running
        if (status == Status.READY) {
            status = Status.RUNNING;
            ProcessesStorage.removeFromReady(this);
            ProcessesStorage.addToRunning(this);
            startingTime = TimeSimulator.getCurrentTime();
        }

        if (status == Status.SUSPENDED) {
            status = Status.RUNNING;
            ProcessesStorage.removeFromSuspended(this);
            ProcessesStorage.addToRunning(this);
        }

        // update the remaining time
        remainingTime--;
        runningTime++;

        // if the process is finished
        if (remainingTime == 0) {
            status = Status.TERMINATED;
            ProcessesStorage.removeFromRunning(this);
            ProcessesStorage.addToTerminated(this);
            finishingTime = TimeSimulator.getCurrentTime();
        }
    }


    public int getWaitingTime() {
        if (startingTime < 0) {
            return TimeSimulator.getCurrentTime() - arrivalTime;
        }
        return startingTime - arrivalTime;
    }

    public int getTurnaroundTime() {
        if (arrivalTime > finishingTime) return 0;
        return finishingTime - arrivalTime;
    }

    public int getSuspendedTime() {
        return suspendedTime;
    }

    public void incrementSuspendedTime() {
        suspendedTime++;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getFinishingTime() {
        return finishingTime;
    }

    public int getStartingTime() {
        return startingTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getId() {
        return id;
    }

    public int getRunningTime() {
        return runningTime;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }
}
