package com.mikolajkapica.schedulers;


import com.mikolajkapica.process.Process;
import com.mikolajkapica.process.ProcessesStorage;
import com.mikolajkapica.process.Status;

public class FCFS implements SchedulingAlgorithm {
    private Process currentProcess;
    @Override
    public void run() {
        if (currentProcess == null || currentProcess.getStatus() == Status.TERMINATED) {
            currentProcess = ProcessesStorage.getNewProcesses().get(0);
        }
        currentProcess.run();
    }
}
