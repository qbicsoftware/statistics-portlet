package life.qbic.portal.presenter.tabs.projects;

import com.vaadin.addon.charts.model.*;
import life.qbic.portal.exceptions.DataNotFoundException;
import life.qbic.portal.model.view.charts.ColumnModel;
import life.qbic.portal.presenter.MainPresenter;
import life.qbic.portal.presenter.tabs.ATabPresenter;
import life.qbic.portal.presenter.utils.DataSorter;
import life.qbic.portal.view.TabView;
import life.qbic.portal.view.tabs.charts.ColumnView;
import submodule.data.ChartConfig;
import submodule.lexica.ChartNames;

import java.util.*;

/**
 * @author fhanssen
 */
public class MultiOmicsCountPresenter extends ATabPresenter<ColumnModel, ColumnView> {

    private ChartConfig multiOmicsConfig;

    MultiOmicsCountPresenter(MainPresenter mainPresenter) {
        super(mainPresenter, new ColumnView());
    }

    @Override
    protected void extractData() throws DataNotFoundException, NullPointerException {
        multiOmicsConfig = super.getChartConfig(ChartNames.Project_Multi_omics.toString());
    }

    @Override
    protected void addChartSettings() {
        PlotOptionsColumn plot = new PlotOptionsColumn();
        plot.setDataLabels(new DataLabels(true));

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.point.name + ': <b>'+ this.y + '</b> Projects, <br> Samples originate from multiple technologies.'");
        Legend legend = new Legend();
        legend.setEnabled(false);


        super.setModel(new ColumnModel(super.getView().getConfiguration(), multiOmicsConfig.getSettings().getTitle(),
                multiOmicsConfig.getSettings().getSubtitle(), multiOmicsConfig.getSettings().getTabTitle(),
                tooltip, legend, new AxisTitle(multiOmicsConfig.getSettings().getxAxisTitle()),
                new AxisTitle(multiOmicsConfig.getSettings().getyAxisTitle()), plot));
        super.getModel().setXAxisType(AxisType.CATEGORY);

        logger.info("Settings were added to a chart of "+ this.getClass() +" with chart title: " + super.getView().getConfiguration().getTitle().getText());

    }

    @Override
    protected void addChartData() {

        //This is necessary to get from Object to required String arrays
        Object[] objectArray = multiOmicsConfig.getData().keySet().toArray(new Object[0]);
        String[] keySet = Arrays.asList(objectArray).toArray(new String[objectArray.length]);

        List<DataSorter> dataSorterList = new ArrayList<>();

        DataSeries series = new DataSeries();
        for (String aKeySet : keySet) {
            for (int i = 0; i < multiOmicsConfig.getData().get(aKeySet).size(); i++) {
                String label = (String) multiOmicsConfig.getSettings().getxCategories().get(i);
                dataSorterList.add(new DataSorter(label,
                        (int)multiOmicsConfig.getData().get(aKeySet).get(i)));

            }
        }
        Collections.sort(dataSorterList);

        dataSorterList.forEach(d -> series.add(new DataSeriesItem(d.getName(), d.getCount())));
        super.getModel().addData(series);


        logger.info("Data was added to a chart of " + this.getClass() + " with chart title: " + multiOmicsConfig.getSettings().getTitle());


    }

    @Override
    public void addChart(TabView tabView, String title){

        //Add to existing tab
        tabView.addSubComponent(this.getModel(), this.getView());
        addReturnButtonListener(tabView);

        logger.info("View was added in " + this.getClass() + " for " +  this.getView().getConfiguration().getTitle().getText() );
    }

    @Override
    protected void addChartListener(){}
}
