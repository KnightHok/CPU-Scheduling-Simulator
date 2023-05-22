package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private Parent root;
    private Scene scene;
    private Stage stage;

    // init javaFX sources
    @FXML
    private Label instructionLabel;
    @FXML
    private ChoiceBox<String> methodChoiceBox;
    @FXML
    private Spinner<Integer> numOfProcessesSpinner;




    private String method;
    private String schedulingType;
    private int numOfProcesses;

    String[] methods = {"FCFS", "Shortest Job First (Pre-emptive)", "Priority", "DVFS (Energy Aware)"};

    /*
    startSimulator(ActionEvent event) - starts the simulator and changes the scene
     */
    public void startSimulator(ActionEvent event) throws IOException {
        // get choices
        System.out.println(numOfProcesses);

        // create instance of controller
        SimulatorController newSim = new SimulatorController(methodChoiceBox.getValue(), numOfProcesses);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("SimulatorScene.fxml"));

        loader.setController(newSim);

        root = loader.load();

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        methodChoiceBox.getItems().setAll(methods);

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,3);
        valueFactory.setValue(3);
        numOfProcesses = valueFactory.getValue();
        numOfProcessesSpinner.setValueFactory(valueFactory);

        numOfProcessesSpinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer integer, Integer t1) {
                numOfProcesses = numOfProcessesSpinner.getValue();

            }
        });
    }
}
