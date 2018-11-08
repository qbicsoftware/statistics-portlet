package life.qbic.portal.model.view.charts;

import com.vaadin.addon.charts.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author fhanssen
 * Example: https://demo.vaadin.com/charts/#BasicColumn
 */
public class ColumnModel extends AChartModel<ListSeries>{

    private final List<Series> series;
    private final XAxis xAxis;
    private final YAxis yAxis;

    public ColumnModel(Configuration configuration,String title, String subtitle, String tabTitle,
                       Tooltip tooltip, Legend legend, AxisTitle xAxisTitle, AxisTitle yAxisTitle, PlotOptionsColumn options){

        super(configuration, title, subtitle, tabTitle, tooltip, legend,options);

        series = new ArrayList<>();
        super.configuration.setSeries(series);
        xAxis = super.configuration.getxAxis();
        yAxis = super.configuration.getyAxis();
        xAxis.setTitle(xAxisTitle);
        yAxis.setTitle(yAxisTitle);
        options.setAnimation(false);

    }

    public void addXCategories(String... cat){
        xAxis.setCategories(cat);
    }

    public void setYMin(int yMin){
        yAxis.setMin(yMin);
    }

    public void addData(ListSeries... listSeries){
        series.addAll(Arrays.asList(listSeries));
    }

    public void addData(DataSeries... dataSeries){
        series.addAll(Arrays.asList(dataSeries));
    }

    public void setXAxisType(AxisType type){
        xAxis.setType(type);
    }

    public void clearData(){
        series.clear();
    }

}