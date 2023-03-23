package com.mikolajkapica.schedulers;

import com.mikolajkapica.process.Process;
import com.mikolajkapica.process.ProcessesStorage;
import com.mikolajkapica.process.Status;
import com.mikolajkapica.time.TimeSimulator;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class SJF implements SchedulingAlgorithm {
    PriorityQueue<Process> processQueue = new PriorityQueue<>(Comparator.comparingInt(Process::getRemainingTime));
    Process lastProcess;
    // z wywlaszczeniem
    @Override
    public void run() {
        // load new processes
        for (int i = 0; i < ProcessesStorage.getSizeOfNewProcesses(); i++) {
            Process process = ProcessesStorage.getNewProcesses().get(0);
            processQueue.add(process);
            ProcessesStorage.removeFromNew(process);
            ProcessesStorage.addToReady(process);
            process.setStatus(Status.READY);
        }
        Process process = processQueue.peek();
        if (process != null) {
            process.run();
            if (process.getStatus() == Status.TERMINATED) {
                processQueue.remove();
            }
            if (lastProcess == null) {
                lastProcess = process;
            } else {
                if (lastProcess != process) {
                    ProcessesStorage.incrementProcessSwitchCounter();
                }
            }
        }

    }
}
