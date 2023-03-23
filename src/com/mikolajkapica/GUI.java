package com.mikolajkapica;

import com.mikolajkapica.process.Process;
import com.mikolajkapica.process.ProcessesStorage;
import com.mikolajkapica.schedulers.FCFS;
import com.mikolajkapica.schedulers.RR;
import com.mikolajkapica.schedulers.SJF;
import com.mikolajkapica.time.TimeSimulator;
import processing.core.PApplet;
import processing.event.MouseEvent;

import java.util.Comparator;
import java.util.List;

public class GUI extends PApplet {
    final int WINDOW_WIDTH = 960;
    // Constants
    int WINDOW_HEIGHT = 640;
    int Y_AXIS = 1;
    int X_AXIS = 2;
    int b1, b2;
    int[] c1, c2;
    int topBarHeight = 50;
    float rectHeight = 100; // height of each rectangle
    float scrollbarY = topBarHeight + 20; // y position of scrollbar
    float scrollbarWidth = 20; // width of scrollbar
    float scrollbarX = WINDOW_WIDTH - scrollbarWidth; // x position of scrollbar
    float scrollbarHeight = WINDOW_HEIGHT - topBarHeight - 20; // height of scrollbar
    float scrollbarMin = 0; // minimum value of scrollbar
    float scrollbarMax; // maximum value of scrollbar
    float scrollbarValue = 0; // current value of scrollbar
    float padding = 5;
    int maximumAmountOfGeneratedProcessesAtOnce = 5;
    int numberOfProcessesToExectue = 1000;
    int moreProcessesPerIncrement = 100;
    int quantum = 5;

    CentralProcessingUnit cpu;
    boolean pressedFCFS = true;
    boolean pressedSJF = false;
    boolean pressedRR = false;
    boolean pressedStart = false;
    boolean pressedHideTerminated = false;
    boolean pressedDelete = false;
    boolean pressedStatistics = false;
    boolean pressedFastForward = false;
    int hidingInfoTextSize;
    boolean hidePressed = false;

    @Override
    public void settings() {
        size(960, 640);
        pixelDensity(displayDensity());

    }

    @Override
    public void setup() {
        surface.setTitle("Activity Monitor");
        background(0);
        frameRate(60);
        // Define colors
        b1 = 255;
        b2 = 0;
        c1 = new int[]{64, 65, 69};
        c2 = new int[]{53, 53, 56};
        textFont(createFont("Inter", 12, true));
    }

    @Override
    public void draw() {
        background(28, 29, 35);
        if (pressedFastForward && pressedStart) {
            if (cpu == null & (pressedFCFS || pressedSJF || pressedRR)) {
                pickCPU();
            }
            while (!ProcessesStorage.isTerminatedOrStarved()) {
                cpu.run();
            }
            pressedFastForward = false;
            pressedStart = false;
        }
        if (pressedStart) {
            cpu.run();
        }
        if (pressedStatistics) {
            showStats();
        } else {
            if (pressedHideTerminated) {
                drawRunningTasks();
            } else {
                drawAllTasks();
            }
            infoBar();
            scrollBar();
            showTime();
        }
        topBar();
        mouseHovered();
    }

    private void showTime() {
        fill(42, 45, 48);
        stroke(85, 87, 89);
        rect(10, WINDOW_HEIGHT-70, 160, 60, 20);
        fill(206, 208, 214);
        noStroke();
        textFont(createFont("Inter-Bold", 16, true));
        text("Time:", 20, WINDOW_HEIGHT-43);
        text(TimeSimulator.getCurrentTime(), 20, WINDOW_HEIGHT-23);
        textFont(createFont("Inter", 12, true));
    }

    private void showStats() {
        background(29, 31, 35);
        fill(255);
        textFont(createFont("Inter-Black", 48, true));
        text("Statistics", 50, 140);
        fill(40, 42, 45);
        rect(30, 155, 450, 3);
        fill(220);
        textFont(createFont("Inter", 24, true));
        ProcessesStorage.stats();
        text("Max waiting time:", 50, 180+10);
        text("Avg waiting time:", 50, 220+10);
        text("Min waiting time:", 50, 260+10);
        text(ProcessesStorage.getMaxWaitingTime(), 350, 180+10);
        text(ProcessesStorage.getAvgWaitingTime(), 350, 220+10);
        text(ProcessesStorage.getMinWaitingTime(), 350, 260+10);
        fill(40, 42, 45);
        rect(30, 285, 450, 3);
        fill(220);
        text("Max suspended time:", 50, 300+20);
        text("Avg suspended time:", 50, 340+20);
        text("Min suspended time:", 50, 380+20);
        text(ProcessesStorage.getMaxSuspensionTime(), 350, 300+20);
        text(ProcessesStorage.getAvgSuspensionTime(), 350, 340+20);
        text(ProcessesStorage.getMinSuspensionTime(), 350, 380+20);
        fill(40, 42, 45);
        rect(30, 415, 450, 3);
        fill(220);
        text("Max turnaround time:", 50, 430+20);
        text("Avg turnaround time:", 50, 470+20);
        text("Min turnaround time:", 50, 510+20);
        text(ProcessesStorage.getMaxTurnaroundTime(), 350, 430+20);
        text(ProcessesStorage.getAvgTurnaroundTime(), 350, 470+20);
        text(ProcessesStorage.getMinTurnaroundTime(), 350, 510+20);
        fill(40, 42, 45);
        rect(30, 545, 450, 3);
        fill(220);
        text(new StringBuilder()
                .append("Starved Processes (>")
                .append(ProcessesStorage.getStarvationTime())
                .append("): ")
                .append(ProcessesStorage.getNumberOfStarvedProcesses())
                .toString(), 50, 560+20);
        text(new StringBuilder().append("Switches: ").append(ProcessesStorage.getProcessSwitchCounter()).toString(), 50, 600+20);
        textFont(createFont("Inter", 12, true));
    }
    private void drawAllTasks() {
        List<Process> processesToShow = ProcessesStorage.getListOfProcesses();
        processesToShow.sort(Comparator.comparingInt(Process::getId));
        if (hidePressed) {
            int processesShowing = 27;
            scrollbarMax = -1;
            for (int i = 0; i < min(processesShowing, ProcessesStorage.getSize()); i++) {
                int posY = (int) scrollbarY + i * 20;
                if (i % 2 == 0)
                    drawProcess(posY, processesToShow.get(i), new int[]{29, 31, 35});
                else
                    drawProcess(posY, processesToShow.get(i), new int[]{40, 42, 45});
            }
            if (ProcessesStorage.getSize() > processesShowing) {
                if (ProcessesStorage.isTerminatedOrStarved())
                    allProcessesEndedScreen();
                else
                    setGradientWithOpaqueTop(0, WINDOW_HEIGHT - 400, WINDOW_WIDTH, 400, new int[]{28, 29, 35}, new int[]{28, 29, 35}, Y_AXIS, 0);
                textFont(createFont("Inter", 24, true));
                textAlign(CENTER);
                fill(255);
                text("Hiding the rest of processes", WINDOW_WIDTH / 2, WINDOW_HEIGHT - 60);
                textFont(createFont("Inter", 12, true));
                textAlign(LEFT);

            }
            return;
        }
        int i = 0;
        scrollbarMax = ProcessesStorage.getSize() * 20 - WINDOW_HEIGHT + topBarHeight + 20;
        for (Process process : processesToShow) {
            int posY;
            if (scrollbarMax > 0)
                posY = (int) (-scrollbarValue + scrollbarY + i * 20);
            else
                posY = (int) scrollbarY + i * 20;
            if (i % 2 == 0)
                drawProcess(posY, process, new int[]{29, 31, 35});
            else
                drawProcess(posY, process, new int[]{40, 42, 45});
            i++;
        }
        if (ProcessesStorage.isTerminatedOrStarved())
            allProcessesEndedScreen();
    }

    private void allProcessesEndedScreen() {
        if (ProcessesStorage.isTerminatedOrStarved()) {
            setGradientWithOpaqueTop(0, WINDOW_HEIGHT - 400, WINDOW_WIDTH, 400, new int[]{28, 29, 35}, new int[]{28, 29, 35}, Y_AXIS, 0);
            textAlign(CENTER);
            fill(255);
            if (hidingInfoTextSize < 48)
                hidingInfoTextSize++;
            textFont(createFont("Inter-Bold", hidingInfoTextSize, true));
            text("All processes terminated", WINDOW_WIDTH / 2, WINDOW_HEIGHT - 100);
            textFont(createFont("Inter", 12, true));
            textAlign(LEFT);
        }
    }

    private void drawRunningTasks() {
        int i = 0;
        scrollbarMax = ProcessesStorage.getSize() * 20 - WINDOW_HEIGHT + topBarHeight + 20;
        List<Process> processesToShow = ProcessesStorage.getReadyOrRunningOrSuspendedProcesses();
        processesToShow.sort(Comparator.comparingInt(Process::getId));
        for (Process process : processesToShow) {
            int posY = (int) (-scrollbarValue + scrollbarY + i * 20);
            if (i % 2 == 0)
                drawProcess(posY, process, new int[]{29, 31, 35});
            else
                drawProcess(posY, process, new int[]{40, 42, 45});
            i++;
        }
        allProcessesEndedScreen();
    }

    void infoBar() {
        fill(0);
        rect(0, topBarHeight, width, 1);
        fill(54, 55, 61);
        rect(0, topBarHeight + 1, width, 20 - 2);
        fill(68, 69, 76);
        rect(0, topBarHeight + 20 - 1, width, 1);
        fill(255);
        text("Process ID", 10, 65);
        text("Burst", 100, 65);
        text("Arrival", 170, 65);
        text("Starting", 240, 65);
        text("Remaining", 320, 65);
        text("Running", 410, 65);
        text("Waiting", 490, 65);
        text("Suspended", 570, 65);
        text("Turnaround", 670, 65);
        text("Status", 825, 65);
    }

    void drawProcess(int startingY, Process process, int[] color) {
        fill(color[0], color[1], color[2]);
        rect(0, startingY, width, 20);
        fill(220, 220, 221);
        text(process.getId(), 10, startingY + 15);
        text(process.getBurstTime(), 100, startingY + 15);
        text(process.getArrivalTime(), 170, startingY + 15);

        int startingTime = process.getStartingTime();
        if (startingTime < 0) text("-", 240, startingY + 15);
        else text(startingTime, 240, startingY + 15);

        int remainingTime = process.getRemainingTime();
        if (remainingTime < 0) text("-", 320, startingY + 15);
        else text(remainingTime, 320, startingY + 15);

        int runningTime = process.getRunningTime();
        if (runningTime < 0) text("-", 410, startingY + 15);
        else text(runningTime, 410, startingY + 15);

        int waitingTime = process.getWaitingTime();
        if (waitingTime < 0) text("-", 490, startingY + 15);
        else text(waitingTime, 490, startingY + 15);


        text(process.getSuspendedTime(), 570, startingY + 15);

        int turnaroundTime = process.getTurnaroundTime();
        if (turnaroundTime < 0) text("-", 670, startingY + 15);
        else text(turnaroundTime,670, startingY + 15);

        String status = process.getStatus().toString();
        if (status.equals("TERMINATED")) fill(255, 0, 0);
        else if (status.equals("RUNNING")) fill(0, 255, 0);
        else if (status.equals("SUSPENDED")) fill(255, 0, 255);
        else fill(255, 255, 0);
        text(status, 825, startingY + 15);
        fill(255);
    }

    private void topBar() {
        setGradient(0, 0, width, topBarHeight - 1, c1, c2, Y_AXIS);
        fill(69, 71, 78);
        rect(0, 0, width, 1);
        topBarButton(50, 55, "FCFS", pressedFCFS);
        topBarButton(115, 45, "SJF", pressedSJF);
        topBarButton(170, 100, "RR (Q=" + quantum + ")", pressedRR);
        topBarButtonIncrementDecrement(248, 30, pressedRR);
        playPauseButton(10);

        topBarButton(280, 55, "Clear", pressedDelete);

        topBarButton(345, 130, "Tasks: " + numberOfProcessesToExectue, false);
        if (numberOfProcessesToExectue > 1000)
            hidePressed = true;
        topBarButtonIncrementDecrement(453, 30, false);

        topBarButton(485, 80, "/s: " + maximumAmountOfGeneratedProcessesAtOnce, false);
        topBarButtonIncrementDecrement(543, 30, false);
        topBarButton(575, 45, "hide", hidePressed);

        topBarButton(WINDOW_WIDTH - 330, 100, "Fast Forward", pressedFastForward);
        topBarButton(WINDOW_WIDTH - 130, 120, "Hide Terminated", pressedHideTerminated);

        topBarButton(WINDOW_WIDTH - 220, 80, "Statistics", pressedStatistics);

    }

    private void topBarButtonIncrementDecrement(int xStart, int width, boolean pressed) {
        xStart -= 7;
        fill(68, 69, 76);
        rect(xStart, 25, width, 1);
        rect(xStart, 10, 1, 30);
        if (pressed)
            fill(58, 59, 62);
        else
            fill(246, 247, 251);
        textAlign(CENTER);
        text("+", xStart + 15, 21);
        text("-", xStart + 15, 37);
        textAlign(LEFT);
    }

    private void playPauseButton(int startX) {

        if (ProcessesStorage.isTerminatedOrStarved() && pressedStart) {
            drawPlay(startX);
            pressedStart = false;
            return;
        }
        if (pressedStart) {
            fill(246, 247, 251);
            circle(startX + 15, 10 + 15, 30);
            fill(101, 102, 106);
            drawPause(startX);
        } else {
            fill(101, 102, 106);
            circle(startX + 15, 10 + 15, 30);
            fill(246, 247, 251);
            drawPlay(startX);
        }
    }

    private void drawPause(int startX) {
        noStroke();
        float x = startX + 15;
        float y = 25;
        float size = 15;
        float barWidth = (float) (size * 0.3);
        float barHeight = (float) (size * 0.8);

        // Draw left bar
        rect(x - size / 4 - barWidth / 2, y - barHeight / 2, barWidth, barHeight);

        // Draw right bar
        rect(x + size / 4 - barWidth / 2, y - barHeight / 2, barWidth, barHeight);
    }

    private void drawPlay(int startX) {
        beginShape();
        int x = startX + 17;
        int y = 25;
        int arrowSize = 15;
        beginShape();
        vertex(x - arrowSize / 2, y - arrowSize / 2);
        vertex(x - arrowSize / 2, y + arrowSize / 2);
        vertex(x + arrowSize / 2 - 2, y);
        endShape(CLOSE);
    }

    private void topBarButton(int xStart, int width, String s, boolean pressed) {
        if (pressed) {
            fill(197, 198, 201);
        } else {
            fill(101, 102, 106);
        }
        rect(xStart, 10, width, 30, 7);
        if (pressed)
            fill(58, 59, 62);
        else
            fill(246, 247, 251);
        text(s, xStart + 10, 30);
    }

    public void mouseHovered() {
        if (mouseX >= 10 && mouseX < 40 && mouseY >= 10 && mouseY < 10 + 30) {
            cursor(HAND);
        } else if (mouseX >= 50 && mouseX < 50 + 55 && mouseY >= 10 && mouseY < 10 + 30) {
            cursor(HAND);
        } else if (mouseX >= 115 && mouseX < 115 + 45 && mouseY >= 10 && mouseY < 10 + 30) {
            cursor(HAND);
        } else if (mouseX >= 170 && mouseX < 170 + 100 && mouseY >= 10 && mouseY < 10 + 30) {
            cursor(HAND);
        } else if (mouseX >= WINDOW_WIDTH - 130 && mouseX < WINDOW_WIDTH - 10 && mouseY >= 10 && mouseY < 10 + 30) {
            cursor(HAND);
        } else if (mouseX >= WINDOW_WIDTH - 220 && mouseX < WINDOW_WIDTH - 140 && mouseY >= 10 && mouseY < 10 + 30) {
            if (!pressedStart)
                cursor(HAND);
        } else if (mouseX >= 280 && mouseX < 280 + 55 && mouseY >= 10 && mouseY < 10 + 30) {
            cursor(HAND);
        } else if (mouseX >= 345 + 100 && mouseX < 345 + 130 && mouseY >= 10 && mouseY < 10 + 30) {
            cursor(HAND);
        } else if (mouseX >= 485 + 50 && mouseX < 485 + 80 && mouseY >= 10 && mouseY < 10 + 30) {
            cursor(HAND);
        } else if (mouseX >= 575 && mouseX < 575 + 45 && mouseY >= 10 && mouseY < 10 + 30) {
            cursor(HAND);
        } else if (mouseX >= 630 && mouseX < 630 + 100 && mouseY >= 10 && mouseY < 10 + 30) {
            cursor(HAND);
        } else {
            cursor(ARROW);
        }
    }

    public void mousePressed() {
        if (mouseX >= 10 && mouseX < 40 && mouseY >= 10 && mouseY < 10 + 30) {
            if ((pressedFCFS || pressedSJF || pressedRR) && !pressedStatistics) {
                if (!pressedStart && numberOfProcessesToExectue == 0) {
                    return;
                }
                pressedStart = !pressedStart;
                if (ProcessesStorage.isTerminatedOrStarved() && pressedStart) {
                    pickCPU();
                }

                if (pressedStart) {
                    if (cpu == null) {
                        pickCPU();
                    }
                }
            }
            hidingInfoTextSize = 44;
        } else if (mouseX >= 50 && mouseX < 50 + 55 && mouseY >= 10 && mouseY < 10 + 30) {
            pressedFCFS = true;
            pressedSJF = false;
            pressedRR = false;
            pressedStatistics = false;
            pressedStart = false;
            cpu = new CentralProcessingUnit(new FCFS(), numberOfProcessesToExectue, maximumAmountOfGeneratedProcessesAtOnce);
        } else if (mouseX >= 115 && mouseX < 115 + 45 && mouseY >= 10 && mouseY < 10 + 30) {
            pressedFCFS = false;
            pressedSJF = true;
            pressedRR = false;
            pressedStatistics = false;
            pressedStart = false;
            cpu = new CentralProcessingUnit(new SJF(), numberOfProcessesToExectue, maximumAmountOfGeneratedProcessesAtOnce);
        } else if (mouseX >= 170 && mouseX < 170 + 80 && mouseY >= 10 && mouseY < 10 + 30) {
            pressedFCFS = false;
            pressedSJF = false;
            pressedRR = true;
            pressedStart = false;
            pressedStatistics = false;
            cpu = new CentralProcessingUnit(new RR(quantum), numberOfProcessesToExectue, maximumAmountOfGeneratedProcessesAtOnce);
        } else if (mouseX >= 240 && mouseX < 240 + 30) {
            if (mouseY >= 10 && mouseY < 10 + 15) {
                quantum++;
            } else if (mouseY > 25 && mouseY <= 25 + 15) {
                if (quantum > 1)
                    quantum--;
            }
        } else if (mouseX >= 280 && mouseX < 280 + 55 && mouseY >= 10 && mouseY < 10 + 30) {
            topBarButton(280, 60, "Clear", true);
            TimeSimulator.setCurrentTime(0);
            pressedFCFS = false;
            pressedSJF = false;
            pressedRR = false;
            pressedStart = false;
            pressedStatistics = false;
            pressedFastForward = false;
            cpu = null;
            ProcessesStorage.clear();
        } else if (mouseX >= WINDOW_WIDTH - 130 && mouseX < WINDOW_WIDTH - 10 && mouseY >= 10 && mouseY < 10 + 30) {
            pressedHideTerminated = !pressedHideTerminated;
        } else if (mouseX >= WINDOW_WIDTH - 220 && mouseX < WINDOW_WIDTH - 140 && mouseY >= 10 && mouseY < 10 + 30) {
            if (pressedStatistics) {
                pressedStatistics = false;
                return;
            }
            if (ProcessesStorage.isTerminatedOrStarved())
                pressedStatistics = !pressedStatistics;
        } else if (mouseX >= WINDOW_WIDTH - 335 && mouseX < WINDOW_WIDTH - 335 + 105 && mouseY >= 10 && mouseY < 10 + 30) {
            if (!pressedFastForward) {
                pressedFastForward = true;
                pickCPU();
            } else {
                pressedFastForward = false;
            }
        } else if (mouseX >= 345 + 100 && mouseX < 345 + 130 && mouseY >= 10 && mouseY < 10 + 15) {
            numberOfProcessesToExectue += moreProcessesPerIncrement;
            pressedStart = false;
            pickCPU();
        } else if (mouseX >= 345 + 100 && mouseX < 345 + 130 && mouseY >= 10 + 15 && mouseY < 10 + 30) {
            if (numberOfProcessesToExectue > 0)
                numberOfProcessesToExectue -= moreProcessesPerIncrement;
            pressedStart = false;
            pickCPU();
        } else if (mouseX >= 485 + 50 && mouseX < 485 + 80 && mouseY >= 10 && mouseY < 10 + 15) {
            maximumAmountOfGeneratedProcessesAtOnce++;
            pressedStart = false;
            pickCPU();
        } else if (mouseX >= 485 + 50 && mouseX < 485 + 80 && mouseY >= 10 + 15 && mouseY < 10 + 30) {
            if (maximumAmountOfGeneratedProcessesAtOnce > 1)
                maximumAmountOfGeneratedProcessesAtOnce--;
            pressedStart = false;
            pickCPU();
        } else if (mouseX >= 575 && mouseX < 575 + 45 && mouseY >= 10 && mouseY < 10 + 30) {
            hidePressed = !hidePressed;
        }
    }

    private void pickCPU() {
        if (pressedFCFS) {
            cpu = new CentralProcessingUnit(new FCFS(), numberOfProcessesToExectue, maximumAmountOfGeneratedProcessesAtOnce);
        } else if (pressedSJF) {
            cpu = new CentralProcessingUnit(new SJF(), numberOfProcessesToExectue, maximumAmountOfGeneratedProcessesAtOnce);
        } else if (pressedRR) {
            cpu = new CentralProcessingUnit(new RR(quantum), numberOfProcessesToExectue, maximumAmountOfGeneratedProcessesAtOnce);
        }
    }

    public void mouseDragged() {
        // check if mouse is within scrollbar
        int sliderPosition = (int) (scrollbarY + map(scrollbarValue, scrollbarMin, scrollbarMax, 0, scrollbarHeight - rectHeight));
        if (mouseX >= scrollbarX
                && mouseX <= scrollbarX + scrollbarWidth
                && mouseY >= sliderPosition
                && mouseY <= sliderPosition + rectHeight) {
            // update scrollbar value based on mouse position
            scrollbarValue = constrain(
                    map(
                            mouseY - scrollbarY,
                            padding,
                            WINDOW_HEIGHT - scrollbarY - padding,
                            scrollbarMin,
                            scrollbarMax),
                    scrollbarMin, scrollbarMax);
        }
    }

    public void scrollBar() {
        fill(40, 41, 45);
        rect(scrollbarX, scrollbarY, scrollbarWidth, scrollbarHeight);
        int sliderPosition = (int) (scrollbarY + map(scrollbarValue, scrollbarMin, scrollbarMax, 0 + padding, scrollbarHeight - rectHeight - padding));
        if (mouseX >= scrollbarX + padding && mouseX < scrollbarX + scrollbarWidth - padding && mouseY >= sliderPosition && mouseY < sliderPosition + rectHeight) {
            fill(148, 148, 150);
        } else {
            fill(105, 106, 108);
        }
        if (scrollbarMax > 0)
            rect(scrollbarX + padding, sliderPosition, scrollbarWidth - 2 * padding, rectHeight, 20);


    }

    public void mouseWheel(MouseEvent event) {
        scrollbarValue = constrain(scrollbarValue + 3 * event.getCount(), scrollbarMin, scrollbarMax);
    }

    void setGradient(int x, int y, float w, float h, int[] c1, int[] c2, int axis) {
        noStroke();
        if (axis == Y_AXIS) {  // Top to bottom gradient
            for (int i = y; i <= y + h; i++) {
                float inter = map(i, y, y + h, 0, 1);
                fill(lerpColor(color(c1[0], c1[1], c1[2], 255), color(c2[0], c2[1], c2[2], 255), inter));
                rect(x, i, w, 1);
            }
        } else if (axis == X_AXIS) {  // Left to right gradient
            for (int i = x; i <= x + w; i++) {
                float inter = map(i, x, x + w, 0, 1);
                fill(lerpColor(color(c1[0], c1[1], c1[2], 255), color(c2[0], c2[1], c2[2], 255), inter));
                rect(i, y, 1, h);
            }
        }

    }

    void setGradientWithOpaqueTop(int x, int y, float w, float h, int[] c1, int[] c2, int axis, int opacity) {
        noStroke();
        if (axis == Y_AXIS) {  // Top to bottom gradient
            for (int i = y; i <= y + h; i++) {
                float inter = map(i, y, y + h, 0, 1);
                fill(lerpColor(color(c1[0], c1[1], c1[2], opacity), color(c2[0], c2[1], c2[2], 255), inter));
                rect(x, i, w, 1);
            }
        }
    }
}
