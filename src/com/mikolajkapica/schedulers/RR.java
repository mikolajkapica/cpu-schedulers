package com.mikolajkapica.schedulers;

import com.mikolajkapica.process.Process;
import com.mikolajkapica.process.ProcessesStorage;
import com.mikolajkapica.process.Status;
import com.mikolajkapica.time.TimeSimulator;

import java.util.ArrayDeque;
import java.util.Queue;

public class RR implements SchedulingAlgorithm {
    private Process currentProcess;
    private int quantumUsed;
    private int quantum;
    private Queue<Process> readyQueue;

    public RR(int quantum) {
        this.quantum = quantum;
        readyQueue = new ArrayDeque<>();
        quantumUsed = 0;
    }
    int a = 0;
    @Override
    public void run() {
        // dodaj nowe procesy na koniec kolejki
        for (int i = 0; i < ProcessesStorage.getSizeOfNewProcesses(); i++) {
            Process process = ProcessesStorage.getNewProcesses().get(0);
            readyQueue.add(process);
            ProcessesStorage.removeFromNew(process);
            ProcessesStorage.addToReady(process);
            process.setStatus(Status.READY);
        }
        // jesli brak tasku albo quantumUsed==quantum to wsadz currentTask na koniec i wez pierwszy z kolejki i ustaw quantumUsed na 0
        if (currentProcess != null && currentProcess.getStatus() == Status.TERMINATED) {
            currentProcess = null;
            quantumUsed = 0;
        }
        if (currentProcess != null && quantumUsed == quantum) {
            ProcessesStorage.removeFromRunning(currentProcess);
            ProcessesStorage.addToSuspended(currentProcess);
            currentProcess.setStatus(Status.SUSPENDED);
            currentProcess.setLastSuspendedTime(TimeSimulator.getCurrentTime());
            readyQueue.add(currentProcess);
            currentProcess = readyQueue.remove();
            quantumUsed = 0;
        }
        // remove all starved processes from readyQueue
        readyQueue.removeIf(process -> process.getStatus() == Status.STARVED);

        if (currentProcess == null && !readyQueue.isEmpty()) {
            currentProcess = readyQueue.remove();
        }

        // wykonaj task
        if (currentProcess != null) {
            currentProcess.run();
            quantumUsed++;
        }
    }
}
