package sample;

import javafx.scene.chart.*;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.LinkedList;

public class LineChartData {
    LineChart<Number, Number> lineChart;
    Axis secXAxis;
    NumberAxis powerYAxis;
    XYChart.Series<Number, Number> series;
    LinkedList<XYChart.Data<Number, Number>> oldDataPoints;
    OperatingSystemMXBean osBean;


    LineChartData(LineChart<Number, Number> lineChart) {
        oldDataPoints = new LinkedList<>();
        // connect to line chart in JavaFX
        this.lineChart = lineChart;

        // Remove line symbol (connecting line) using CSS
        lineChart.setCreateSymbols(false);

        // x-axis
        secXAxis = this.lineChart.getXAxis();
        secXAxis.setLabel("Seconds");

        // y-axis
        powerYAxis = (NumberAxis) this.lineChart.getYAxis();
        powerYAxis.setLabel("CPU Power Usage");
        powerYAxis.setAutoRanging(false);
        powerYAxis.setUpperBound(100);
        powerYAxis.setLowerBound(0);
        powerYAxis.setTickUnit(25);

        // create XYChart data points
        series = new XYChart.Series<>();

        // add series to lineChart
        lineChart.getData().add(series);

        osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();


    }

    public XYChart.Data<Number, Number> createNewDataPoint(int second) {
        // get cpu usage
        Number cpuUsage = osBean.getSystemLoadAverage();

//        String stringValue = String.valueOf(second);

        // create new data point
        return new XYChart.Data<Number, Number>(second, cpuUsage.doubleValue());
    }

    public void addToLineChart(XYChart.Data<Number, Number> newDataPoint) {
        // if the list is not big just add it to the series
//        if ( series.getData().size() == 10 ) {
//            // else remove the first point and add the new point to the end
//            XYChart.Data<Number, Number> oldDataPoint = series.getData().remove(0);
//            oldDataPoints.add(oldDataPoint);
//        }

        // add new data point to series
        series.getData().add(newDataPoint);

        // add new series to lineChart
    }


}
