package com.project.digitime.ui.stats;

public class SelectedApp {
    public String selectedAppName;

    public SelectedApp(String selectedAppName) {
        this.selectedAppName = selectedAppName;
    }

    @Override
    public String toString() {
        return "SelectedApp{" +
                "selectedAppName='" + selectedAppName + '\'' +
                '}';
    }
}
