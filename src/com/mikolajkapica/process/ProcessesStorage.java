package com.mikolajkapica.process;

import java.util.ArrayList;
import java.util.List;

public class ProcessesStorage {
    private static List<Process> newProcesses = new ArrayList<>();
    private static List<Process> readyProcesses = new ArrayList<>();
    private static Process runningProcess = null;
    private static List<Process> suspendedProcesses = new ArrayList<>();
    private static List<Process> starvedProcesses = new ArrayList<>();
    private static List<Process> terminatedProcesses = new ArrayList<>();
    private static int NUMBER_OF_PROCESSES_TO_EXECUTE;
    private static int avgWaitingTime;
    private static int avgSuspensionTime;
    private static int maxWaitingTime;
    private static int maxSuspensionTime;
    private static int minWaitingTime;
    private static int minSuspensionTime;
    private static int maxTurnaroundTime;
    private static int minTurnaroundTime;
    private static int avgTurnaroundTime;
    private static int numberOfStarvedProcesses;
    private static int starvationTime;
    private static int processSwitchCounter;


    // get size of new and suspended processes
    public static int getSizeOfNewAndStarvedProcesses() {
        return newProcesses.size() + starvedProcesses.size();
    }

    public static List<Process> getNewAndStarvedProcesses() {
        List<Process> processes = new ArrayList<>();
        processes.addAll(newProcesses);
        processes.addAll(starvedProcesses);
        return processes;
    }

    public static List<Process> getStarvedProcesses() {
        return starvedProcesses;
    }

    // add to starved
    public static void addToStarved(Process process) {
        starvedProcesses.add(process);
    }
    public static void removeFromStarved(Process process) {
        starvedProcesses.remove(process);
    }
    // get size of suspended
    public static int getSizeOfSuspended() {
        return suspendedProcesses.size();
    }

    public static List<Process> getNewProcesses() {
        return newProcesses;
    }
    public static void removeFromNew(Process process) {
        newProcesses.remove(process);
    }

    public static List<Process> getReadyProcesses() {
        return readyProcesses;
    }

    public static Process getRunningProcess() {
        return runningProcess;
    }
    public static int get1ifRunningProcess() {
        if (runningProcess != null) {
            return 1;
        }
        return 0;
    }

    public static List<Process> getSuspendedProcesses() {
        return suspendedProcesses;
    }

    public static List<Process> getTerminatedProcesses() {
        return terminatedProcesses;
    }

    public static void removeFromSuspended(Process process) {
        suspendedProcesses.remove(process);
    }

    public static void removeFromRunning(Process process) {
        runningProcess = null;
    }

    public static void removeFromReady(Process process) {
        readyProcesses.remove(process);
    }

    public static void addToReady(Process process) {
        readyProcesses.add(process);
    }

    public static void addToRunning(Process process) {
        runningProcess = process;
    }

    public static void addToSuspended(Process process) {
        suspendedProcesses.add(process);
    }

    public static void addToTerminated(Process process) {
        terminatedProcesses.add(process);
    }

    public static List<Process> getListOfProcesses() {
        List<Process> returnArray = new ArrayList<>();
        returnArray.addAll(newProcesses);
        returnArray.addAll(readyProcesses);
        if (runningProcess != null)
            returnArray.add(runningProcess);
        returnArray.addAll(suspendedProcesses);
        returnArray.addAll(starvedProcesses);
        returnArray.addAll(terminatedProcesses);
        return returnArray;
    }

    public static int getMaxTurnaroundTime() {
        return maxTurnaroundTime;
    }

    public static int getMinTurnaroundTime() {
        return minTurnaroundTime;
    }

    public static int getAvgTurnaroundTime() {
        return avgTurnaroundTime;
    }

    public static List<Process> getReadyOrRunningProcesses() {
        List<Process> returnArray = new ArrayList<>();
        returnArray.addAll(newProcesses);
        returnArray.addAll(readyProcesses);
        returnArray.add(runningProcess);
        return returnArray;
    }

    public static List<Process> getReadyOrRunningOrSuspendedProcesses() {
        List<Process> returnArray = new ArrayList<>();
        returnArray.addAll(newProcesses);
        returnArray.addAll(readyProcesses);
        if (runningProcess != null)
            returnArray.add(runningProcess);
        returnArray.addAll(suspendedProcesses);
        return returnArray;
    }
    public static boolean isReadyOrRunningOrSuspendedProcessesEmpty() {
        return newProcesses.isEmpty() && readyProcesses.isEmpty() && (runningProcess == null) && (suspendedProcesses.isEmpty());
    }

    public static void addAllProcesses(List<Process> processes) {
        newProcesses.addAll(processes);
    }

    public static boolean isTerminatedOrStarved() {
        if (getSize() == 0) return false;
        return (terminatedProcesses.size() + starvedProcesses.size()) == NUMBER_OF_PROCESSES_TO_EXECUTE;
    }

    public static boolean isSizeLessThanNumberOfProcessesToExecute() {
        return getSize() < NUMBER_OF_PROCESSES_TO_EXECUTE;
    }

    public static void clear() {
        newProcesses.clear();
        readyProcesses.clear();
        runningProcess = null;
        suspendedProcesses.clear();
        starvedProcesses.clear();
        terminatedProcesses.clear();
        processSwitchCounter = 0;
    }

    public static int getProcessSwitchCounter() {
        return processSwitchCounter;
    }

    public static void incrementProcessSwitchCounter() {
        processSwitchCounter++;
    }

    public static int getSize() {
        return newProcesses.size() + getReadyProcesses().size() + get1ifRunningProcess() + suspendedProcesses.size() + getStarvedProcesses().size() + terminatedProcesses.size();
    }

    public static int getNumberOfStarvedProcesses() {
        return numberOfStarvedProcesses;
    }

    public static int getStarvationTime() {
        return starvationTime;
    }

    public static void stats() {
        List<Process> listOfProcesses = getListOfProcesses();
        avgWaitingTime = 0;
        avgSuspensionTime = 0;
        maxWaitingTime = 0;
        maxSuspensionTime = 0;
        minWaitingTime = 0;
        minSuspensionTime = 0;
        maxTurnaroundTime = 0;
        minTurnaroundTime = 0;
        avgTurnaroundTime = 0;
        numberOfStarvedProcesses = starvedProcesses.size();
        if (listOfProcesses.size() == 0) {
            return;
        }
        // get avg max and min of all processes of waiting time and suspension time
        int processesNotTurnedaround = 0;
        for (Process process : listOfProcesses) {
            int suspendedTime = process.getSuspendedTime();
            int waitingTime = process.getWaitingTime();
            int turnaroundTime = process.getTurnaroundTime();
            if (turnaroundTime == 0)
                processesNotTurnedaround++;

            avgWaitingTime += process.getWaitingTime();
            avgSuspensionTime += suspendedTime;
            avgTurnaroundTime += turnaroundTime;
            if (waitingTime > maxWaitingTime) {
                maxWaitingTime = waitingTime;
            }
            if (suspendedTime > maxSuspensionTime) {
                maxSuspensionTime = suspendedTime;
            }
            if (waitingTime < minWaitingTime) {
                minWaitingTime = waitingTime;
            }
            if (suspendedTime < minSuspensionTime) {
                minSuspensionTime = suspendedTime;
            }
            if (turnaroundTime > maxTurnaroundTime) {
                maxTurnaroundTime = turnaroundTime;
            }
            if (turnaroundTime < minTurnaroundTime) {
                minTurnaroundTime = turnaroundTime;
            }
        }
        avgWaitingTime /= listOfProcesses.size();
        avgSuspensionTime /= listOfProcesses.size();
        avgTurnaroundTime /= listOfProcesses.size()-processesNotTurnedaround;
    }

    public static int getSizeOfNewProcesses() {
        return newProcesses.size();
    }

    public static int getAvgWaitingTime() {
        return avgWaitingTime;
    }

    public static int getAvgSuspensionTime() {
        return avgSuspensionTime;
    }

    public static int getMaxWaitingTime() {
        return maxWaitingTime;
    }

    public static int getMaxSuspensionTime() {
        return maxSuspensionTime;
    }

    public static int getMinWaitingTime() {
        return minWaitingTime;
    }

    public static int getMinSuspensionTime() {
        return minSuspensionTime;
    }

    public static int getNumberOfProcessesToExecute() {
        return NUMBER_OF_PROCESSES_TO_EXECUTE;
    }

    public static void setNumberOfProcessesToExecute(int n) {
        NUMBER_OF_PROCESSES_TO_EXECUTE = n;
        starvationTime = 2 * n;
    }
}
