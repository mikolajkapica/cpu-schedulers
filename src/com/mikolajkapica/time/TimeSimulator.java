package com.mikolajkapica.time;

public class TimeSimulator {

    private static int currentTime;

    public TimeSimulator() {
        currentTime = 0;
    }

    //    private MainWindow mainWindowContorller = new MainWindowContorller();
    public void simulateUnitOfTime() {
        currentTime++;
//        mainWindow.update();
    }

    public static int getCurrentTime() {
        return currentTime;
    }
    public static void setCurrentTime(int currentTime) {
        TimeSimulator.currentTime = currentTime;
    }
}
