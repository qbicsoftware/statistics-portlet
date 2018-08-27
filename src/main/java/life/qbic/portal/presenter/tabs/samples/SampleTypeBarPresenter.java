package life.qbic.portal.presenter.tabs.samples;


import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import life.qbic.portal.exceptions.DataNotFoundException;
import life.qbic.portal.model.view.charts.BarModel;
import life.qbic.portal.presenter.MainPresenter;
import life.qbic.portal.presenter.tabs.ATabPresenter;
import life.qbic.portal.presenter.utils.LabelFormatter;
import life.qbic.portal.presenter.utils.Sorter;
import life.qbic.portal.view.TabView;
import life.qbic.portal.view.tabs.charts.BarView;
import submodule.data.ChartConfig;
import submodule.lexica.ChartNames;
import submodule.lexica.CommonAbbr;
import submodule.lexica.Translator;

import java.util.*;

/**
 * @author fhanssen
 */
public class SampleTypeBarPresenter extends ATabPresenter<BarModel, BarView> {

    private ChartConfig sampleConfig;

    public SampleTypeBarPresenter(MainPresenter mainPresenter){
        super(mainPresenter, new BarView());
    }

    @Override
    public void setUp() throws DataNotFoundException, NullPointerException{
        try {
            extractData();
            addChartSettings();
            addChartData();
        }catch(DataNotFoundException | NullPointerException e){
            throw e;
        }

    }

    @Override
    protected void extractData() throws DataNotFoundException, NullPointerException {
        sampleConfig = super.getChartConfig(ChartNames.Sample_Types.toString());
    }

    @Override
    protected void addChartSettings() {
        PlotOptionsBar plot = new PlotOptionsBar();

        plot.setStacking(Stacking.NORMAL);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.series.name + ': <b>'+ this.y + '</b> Samples'");

        Legend legend = new Legend();
        legend.setReversed(true);
        legend.setBackgroundColor(new SolidColor("#FFFFFF"));


        super.setModel(new BarModel(super.getView().getConfiguration(),sampleConfig.getSettings().getTitle(),
                sampleConfig.getSettings().getSubtitle(), sampleConfig.getSettings().getTabTitle(), tooltip, legend,
                new AxisTitle(sampleConfig.getSettings().getxAxisTitle()),
                new AxisTitle(sampleConfig.getSettings().getyAxisTitle()),
                plot));

        super.getModel().setXAxisType(AxisType.CATEGORY);
        logger.info("Settings were added to a chart of "+ this.getClass() +" with chart title: " + super.getView().getConfiguration().getTitle().getText());

    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    protected void addChartData() {
        //This is necessary to get from Object to required String arrays
        Object[] objectArray = sampleConfig.getData().keySet().toArray(new Object[0]);
        //noinspection SuspiciousToArrayCall
        @SuppressWarnings("SuspiciousToArrayCall") String[] keySet = Arrays.asList(objectArray).toArray(new String[objectArray.length]);


        //Get order of sample type counts
        Map<String, Integer> sampleTypeTotalCount = new HashMap<>();
        for (String aKeySet : keySet) {
            for (int i = 0; i < sampleConfig.getData().get(aKeySet).size(); i++) {

                Object[] omicsCounts = ((Map)sampleConfig.getData().get(aKeySet).get(i)).keySet().toArray(
                        new Object[0]);

                for(Object o : omicsCounts){
                    int counter = 0;
                    if(sampleTypeTotalCount.containsKey(o)){
                        counter = sampleTypeTotalCount.get(o);
                    }
                    counter += (Integer)((Map)sampleConfig.getData().get(aKeySet).get(i)).get(o);
                    sampleTypeTotalCount.put((String)o, counter);
                }
            }
        }

        List<String> insertOrder = Sorter.getInsertOrder(sampleTypeTotalCount);

        for (String aKeySet : keySet) {
            for (int i = 0; i < sampleConfig.getData().get(aKeySet).size(); i++) {

                String seriesName = (String)sampleConfig.getSettings().getxCategories().get(i);

                if(CommonAbbr.getList().contains(seriesName)){
                    seriesName = CommonAbbr.valueOf(seriesName).toString();
                }else if(Translator.getList().contains(seriesName)){
                    seriesName = Translator.valueOf(seriesName).getTranslation();
                }else{
                    seriesName = LabelFormatter.generateCamelCase(seriesName);
                }

                DataSeries series = new DataSeries(seriesName);
                for(String o : insertOrder){
                    String label = o;
                    if(! CommonAbbr.getList().contains(label)){
                        if(!Translator.getList().contains(label)) {
                            if (label.contains("_")) {
                                label = LabelFormatter.firstLowerCaseRestUpperCase(label.replace("_", ""));
                            } else {
                                label = LabelFormatter.generateCamelCase(label);
                            }
                        }else{
                            label = Translator.valueOf(label).getTranslation();
                        }
                    }

                    series.add(new DataSeriesItem(label, (Number)((Map)sampleConfig.getData().get(aKeySet).get(i)).get(o)));
                }

                super.getModel().getConfiguration().addSeries(series);
            }
        }

        logger.info("Data was added to a chart of " + this.getClass() + " with chart title: " + super.getView().getConfiguration().getTitle().getText());

    }

    @Override
    public void addChart(TabView tabView, String title) {
        //Set new tab
        super.setTabView(tabView);
        super.getTabView().addMainComponent();
        super.getMainPresenter().getMainView().addTabView(super.getTabView(), title);
        logger.info("Tab was added in " + this.getClass() + " for " +  super.getView().getConfiguration().getTitle().getText() );
    }
}
