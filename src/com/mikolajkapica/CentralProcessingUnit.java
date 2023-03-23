package com.mikolajkapica;


import com.mikolajkapica.process.Process;
import com.mikolajkapica.process.ProcessesGenerator;
import com.mikolajkapica.process.ProcessesStorage;
import com.mikolajkapica.process.Status;
import com.mikolajkapica.schedulers.SchedulingAlgorithm;
import com.mikolajkapica.time.TimeSimulator;

import java.util.ArrayList;

public class CentralProcessingUnit {
    private final SchedulingAlgorithm SCHEDULING_ALGORITHM;
    private final ProcessesGenerator PROCESSES_GENERATOR;
    private final TimeSimulator timeSimulator;
    private int starvationTime;

    public CentralProcessingUnit(SchedulingAlgorithm SCHEDULING_ALGORITHM, int numberOfProcessesToExecute, int MAXIMUM_AMOUNT_OF_GENERATED_PROCESSES_AT_ONCE) {
        this.SCHEDULING_ALGORITHM = SCHEDULING_ALGORITHM;
        this.PROCESSES_GENERATOR = new ProcessesGenerator(MAXIMUM_AMOUNT_OF_GENERATED_PROCESSES_AT_ONCE);
        ProcessesStorage.setNumberOfProcessesToExecute(numberOfProcessesToExecute);
        this.timeSimulator = new TimeSimulator();
        ProcessesStorage.clear();
    }

    public void run() {
        if (ProcessesStorage.isSizeLessThanNumberOfProcessesToExecute()) {
            // get tasks from processesGenerator
            ArrayList<Process> newProcesses = PROCESSES_GENERATOR.generateProcesses();
            if (newProcesses.size() + ProcessesStorage.getSize() > ProcessesStorage.getNumberOfProcessesToExecute()) {
                newProcesses = new ArrayList<>(newProcesses.subList(0, ProcessesStorage.getNumberOfProcessesToExecute() - ProcessesStorage.getSize()));
            }
            ProcessesStorage.addAllProcesses(newProcesses);
        }

        // run 1 unit of time of scheduling algorithm
        if (!ProcessesStorage.isReadyOrRunningOrSuspendedProcessesEmpty())
            SCHEDULING_ALGORITHM.run();

        // update suspendedTime if the process is suspended, if more than starvationTime then change status to starved
        starvationTime = ProcessesStorage.getStarvationTime();
        int currentTime = TimeSimulator.getCurrentTime();
        for (int i = 0; i < ProcessesStorage.getSizeOfSuspended(); i++) {
            Process process = ProcessesStorage.getSuspendedProcesses().get(i);
            if (process.getWaitingTime() >= starvationTime || currentTime >= process.getLastSuspendedTime() + starvationTime) {
                process.setStatus(Status.STARVED);
                ProcessesStorage.addToStarved(process);
                ProcessesStorage.removeFromSuspended(process);
            }
            process.incrementSuspendedTime();
        }
        for (int i = 0; i < ProcessesStorage.getSizeOfNewProcesses(); i++) {
            Process process = ProcessesStorage.getNewProcesses().get(i);
            if (process.getWaitingTime() >= starvationTime || currentTime >= process.getLastSuspendedTime() + starvationTime) {
                process.setStatus(Status.STARVED);
                ProcessesStorage.addToStarved(process);
                ProcessesStorage.removeFromNew(process);
            }
        }

        // simulate 1 time unit of working process
        timeSimulator.simulateUnitOfTime();
    }
}
