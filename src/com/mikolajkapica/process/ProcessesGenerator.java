package com.mikolajkapica.process;


import com.mikolajkapica.time.TimeSimulator;

import java.util.ArrayList;
import java.util.Random;

public class ProcessesGenerator {
    private final int MAXIMUM_AMOUNT_OF_GENERATED_PROCESSES_AT_ONCE;
    private final Random RANDOM;
    private int newID;

    public ProcessesGenerator(int MAXIMUM_AMOUNT_OF_GENERATED_PROCESSES) {
        this.MAXIMUM_AMOUNT_OF_GENERATED_PROCESSES_AT_ONCE = MAXIMUM_AMOUNT_OF_GENERATED_PROCESSES;
        this.newID = 0;
        this.RANDOM = new Random();
        RANDOM.setSeed(420);
    }

    public ArrayList<Process> generateProcesses() {
        int amountOfProcesses;
        amountOfProcesses = RANDOM.nextInt(MAXIMUM_AMOUNT_OF_GENERATED_PROCESSES_AT_ONCE + 1);
        ArrayList<Process> processes = new ArrayList<>();
        for (int i = 0; i < amountOfProcesses; i++) {
            processes.add(new Process(++newID, newBurstTime(), TimeSimulator.getCurrentTime()));
        }
        return processes;
    }

    private int newBurstTime() {
        double probability = RANDOM.nextFloat(1);
        if (probability < 0.1) {
            return RANDOM.nextInt(15, 30);
        }
        if (probability >= 0.1 && probability <= 0.8) {
            return RANDOM.nextInt(5, 15);
        }
        return RANDOM.nextInt(1, 5);
    }
}
