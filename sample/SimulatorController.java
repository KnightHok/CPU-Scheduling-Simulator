package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SimulatorController implements Initializable {
    private Parent root;
    private Scene scene;
    private Stage stage;

    // determines if simulation is paused
    public boolean isPaused;

    // method and scheduling type
    String method;
    int numOfProcesses;

    Method currentMethod;
    DVFS energyAwareMethod;

    // holds thread
    Process[] processes = new Process[3];
    Thread[] threads = new Thread[3];

    Label[] arrivalTimeLabelArr;
    Label[] burstTimeLabelArr;
    Label[] runningTimeLabelArr;
    Label[] priorityLabelArr;

    SimulatorTimer simulationRunningTime;

    @FXML
    private Button pauseButton, stopButton;
    @FXML
    private Label mainLabel;
    @FXML
    private Label simulationTimerLabel;
    @FXML
    private GridPane myGridPane;
    @FXML
    private ProgressBar progressBar1, progressBar2, progressBar3;

    @FXML
    private Label methodLabel;

    @FXML
    Label voltageLabel;
    @FXML
    Label frequencyLabel;

    @FXML
    private LineChart<Number, Number> myLineChart;

    LineChartData lineChart;

    private ProgressBar[] progressBars;

    private ProgressBar[] progressBars2;


    public SimulatorController(String method, int numOfProcesses) {
        this.method = method;
        this.numOfProcesses = numOfProcesses;
    }

    // Timeline
    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
        simulationTimerLabel.setText("Time: " + simulationRunningTime.getSimulationTime());

        updateProgressBars();
        updateDataColumn();
        updateLineChart();
        if ( energyAwareMethod != null ) {
            updateVoltageAndFrequency();
        }
    }));

    /*
    methodIsPriority() - checks to see if the method used is the Priority Method
     */
    public boolean methodIsPriority() {
        if ( method.equals("Priority") ) {
            return true;
        }
        return false;
    }

    public RowConstraints makeNewRowConstraints(GridPane grid) {
        RowConstraints newRow = new RowConstraints();
        RowConstraints row = grid.getRowConstraints().get(0);

        // get all constriants of already used row
        boolean isFillHeight = row.isFillHeight();
        double maxHeight = row.getMaxHeight();
        double minHeight = row.getMinHeight();
        double precentHeight = row.getPrefHeight();
        double prefHeight = row.getPrefHeight();
        VPos vAlightment = row.getValignment();
        Priority vGrow = row.getVgrow();

        newRow.setFillHeight(isFillHeight);
        newRow.setMaxHeight(maxHeight);
        newRow.setMinHeight(minHeight);
        newRow.setPercentHeight(precentHeight);
        newRow.setPrefHeight(prefHeight);
        newRow.setValignment(vAlightment);
        newRow.setVgrow(vGrow);

        return newRow;
    }

    /*
    makeNewColumnConstrains(GridPane grid) - takes a grid pane as a parameter and constructs
    a column from the already made columns
     */
    public ColumnConstraints makeNewColumnConstraints(GridPane grid) {
        ColumnConstraints newColumn = new ColumnConstraints();
        ColumnConstraints col = grid.getColumnConstraints().get(1);

        // get all constraints of already used column
        boolean fillWidth = col.isFillWidth();
        HPos hAlignment = col.getHalignment();
        Priority hGrow = col.getHgrow();
        double minWidth = col.getMinWidth();
        double maxWidth = col.getMaxWidth();
        double percentWidth= col.getPercentWidth();
        double prefWidth = col.getPrefWidth();

        newColumn.setFillWidth(fillWidth);
        newColumn.setHalignment(hAlignment);
        newColumn.setHgrow(hGrow);
        newColumn.setMinWidth(minWidth);
        newColumn.setMaxWidth(maxWidth);
        newColumn.setPercentWidth(percentWidth);
        newColumn.setPrefWidth(prefWidth);

        return newColumn;
    }

    public void addProgressRow() {
        myGridPane.getRowConstraints().add(makeNewRowConstraints(myGridPane));

        for ( int i=0; i<processes.length; i++ ) {
            progressBars2[i] = new ProgressBar();
            progressBars2[i].setPrefWidth(280);
//            newProgressBar.set
            Label newLabel = new Label("Process " + i);
            myGridPane.setConstraints(progressBars2[i], 0, i);
            myGridPane.setConstraints(newLabel, 1, i);
            myGridPane.getChildren().addAll(progressBars2[i], newLabel);
        }
    }

    /*
    addDataColumn() - adds a column full of data to the grid pane
     */
    public void addDataColumn() {
        // init the arrivalTime, burstTime, and runningTime
        arrivalTimeLabelArr = new Label[processes.length];
        burstTimeLabelArr = new Label[processes.length];
        runningTimeLabelArr = new Label[processes.length];

        if ( methodIsPriority() ) {
            priorityLabelArr = new Label[processes.length];
        }

        // add a new column for data for each process
        myGridPane.getColumnConstraints().addAll(makeNewColumnConstraints(myGridPane));

        for ( int i=0; i<processes.length; i++ ) {
            // create the FlowPane and set its orientation
            FlowPane newFlowPane = new FlowPane();
            newFlowPane.setOrientation(Orientation.VERTICAL);

            // create the Labels to be added to the new FlowPane
            String arrivalTimeData = "Arrival Time: ";
            String burstTimeData = "Burst Time Left: ";
            String runningTimeData = "Running Time: ";
            arrivalTimeLabelArr[i] = new Label(arrivalTimeData);
            burstTimeLabelArr[i] = new Label(burstTimeData);
            runningTimeLabelArr[i] = new Label(runningTimeData);
            if ( methodIsPriority() ) {
                priorityLabelArr[i] = new Label("Priority: ");
                newFlowPane.getChildren().addAll(priorityLabelArr[i]);
            }

            newFlowPane.getChildren().addAll(arrivalTimeLabelArr[i], burstTimeLabelArr[i], runningTimeLabelArr[i]);

            myGridPane.setConstraints(newFlowPane, 2, i);
            myGridPane.getChildren().addAll(newFlowPane);
        }
    }

    public void createRandomProcesses() {
        for ( int i=0; i<processes.length; i++ ) {
            int randomArrivalTime = (int) (Math.random() * 30);
            int randomBurstTime = (int) (Math.random() * 50);
            int randomPriority = (int) (Math.random() * 10);
            processes[i] = new Process(i, randomArrivalTime, randomBurstTime, simulationRunningTime, currentMethod, randomPriority);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        progressBars2 = new ProgressBar[numOfProcesses];
        voltageLabel.setVisible(false);
        frequencyLabel.setVisible(false);
        methodLabel.setText("method: " + method);

        lineChart = new LineChartData(myLineChart);
// not ready yet
//        addProgressRow();
        addDataColumn();

        timeline.setCycleCount(Timeline.INDEFINITE);
        isPaused = false;

        // start SimulationTimer
        simulationRunningTime = new SimulatorTimer();
        switch(method) {
            case "FCFS":
                currentMethod = new FCFS(processes);
                System.out.println("hitting fcfs");
                break;
            case "Shortest Job First (Pre-emptive)":
                currentMethod = new ShortestJobFirst(processes, simulationRunningTime);
                System.out.println("hit shortest Job");
                break;
            case "Priority":
                currentMethod = new sample.Priority(processes, simulationRunningTime);
                break;
            case "DVFS (Energy Aware)":
                energyAwareMethod = new DVFS(processes, simulationRunningTime);
                currentMethod = energyAwareMethod;

                voltageLabel.setVisible(true);
                frequencyLabel.setVisible(true);
                break;
            default:
//                currentMethod = new FCFS(processes);
                System.out.println("broke on purpose");
                System.out.println(this.methodLabel.getText());
                break;
        }

        //  init processes
        // not ready yet
//        createRandomProcesses();
        processes[0] = new Process(0, 0, 50, simulationRunningTime, currentMethod, 2);
        processes[1]  = new Process(1, 20, 12, simulationRunningTime, currentMethod, 0);
        processes[2] = new Process(2, 30, 10, simulationRunningTime, currentMethod, 1);

        progressBars = new ProgressBar[]{progressBar1, progressBar2, progressBar3};

        initDataColumn();

        // start all processes
        startAllThreadProcesses();

        // start Timeline
        timeline.play();

    }

    /*
    pauseAllThreadProcesses() - pauses all processes
     */
    public void pauseAllThreadProcesses() {
        for( int i=0; i<processes.length; i++ ) {
            processes[i].pauseThread();

        }
    }

    /*
    startAllThreadProcesses() - starts all thread processes
     */
    public void startAllThreadProcesses() {
        if ( threads[0] == null ) {
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(processes[i]);
                threads[i].setDaemon(true);
                threads[i].start();
            }
        } else {
            for ( int i=0; i<threads.length; i++ ) {
                processes[i].resumeThread();
            }
        }
    }

    /*
    stopAllThreadProcesses() - pauses all thread processes and joins them to the main thread
     */
    public void stopAllThreadProcesses() {
        pauseAllThreadProcesses();
        for ( int i=0; i<threads.length; i++ ) {
            threads[i].interrupt();
        }
        for ( int i=0; i<threads.length; i++ ) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    updateVoltage() - updates the voltage and frequency labels
     */
    public void updateVoltageAndFrequency() {
        voltageLabel.setText("Voltage: " + energyAwareMethod.getVoltage());
        frequencyLabel.setText("Frequency: " + energyAwareMethod.getFrequency());
    }

    /*
    updateLineChart() - updates the linechart with new data
     */
    public void updateLineChart() {
        XYChart.Data<Number, Number> data = lineChart.createNewDataPoint(simulationRunningTime.getSimulationTime());
        lineChart.addToLineChart(data);
    }

    /*
    updateProgressBars() - update progress with progression
     */
    public void updateProgressBars() {
        for ( int i=0; i<processes.length; i++ ) {
            double progress = processes[i].getProgress();
//            System.out.println(progress);
            progressBars[i].setProgress(progress);
        }
    }

    /*
    updateDataColumn() - update data column with new data from processes
     */
    public void updateDataColumn() {
        for ( int i=0; i<processes.length; i++ ) {
            arrivalTimeLabelArr[i].setText("Arrival Time: " + processes[i].getArrivalTime());
            burstTimeLabelArr[i].setText("Burst Time Left: " + processes[i].getBurstTime());
        }
    }

    /*
    initDataColumn() - initialize data column
     */
    public void initDataColumn() {
        for ( int i=0; i<processes.length; i++ ) {
            arrivalTimeLabelArr[i].setText("Arrival Time: " + processes[i].getArrivalTime());
            burstTimeLabelArr[i].setText("Burst Time: " + processes[i].getBurstTime());
            if ( methodIsPriority() ) {
                priorityLabelArr[i].setText("Priority: " + processes[i].getPriority());
            }

        }
    }

    /*
    pauseSimulation() - pause the entire simulation
     */
    public void pauseSimulation(ActionEvent event) throws IOException {
        if ( !isPaused ) {
            simulationRunningTime.pauseSimulationTimer();
            pauseAllThreadProcesses();
            timeline.pause();
            isPaused = true;
            mainLabel.setText("PAUSED");
            pauseButton.setText("START");
        } else {
            simulationRunningTime.resumeSimulationTimer();
            startAllThreadProcesses();
            timeline.play();
            isPaused = false;
            mainLabel.setText("CPU Simulation");
            pauseButton.setText("PAUSE");
        }
    }

    /*
    stopSimulation() - stop the entire simulation
     */
    public void stopSimulation(ActionEvent event) throws IOException{
        // stop all processes
        stopAllThreadProcesses();

        // stop SimulationTimer
        simulationRunningTime.stopSimulationTimer();

        // stop Timeline
        timeline.stop();


        // change scene to first StartingScene
        root = FXMLLoader.load(getClass().getResource("StartingScene.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


}
