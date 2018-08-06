package life.qbic.portal.presenter.tabs.projects;

import com.vaadin.addon.charts.model.*;
import life.qbic.portal.model.view.charts.ColumnModel;
import life.qbic.portal.presenter.MainPresenter;
import life.qbic.portal.presenter.tabs.ATabPresenter;
import life.qbic.portal.presenter.utils.DataSorter;
import life.qbic.portal.presenter.utils.LabelFormatter;
import life.qbic.portal.view.TabView;
import life.qbic.portal.view.tabs.charts.ColumnView;
import submodule.data.ChartConfig;
import submodule.lexica.ChartNames;
import submodule.lexica.CommonAbbr;
import submodule.lexica.Translator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author fhanssen
 */
public class ProjectTechColumnPresenter extends ATabPresenter<ColumnModel, ColumnView> {

    private ChartConfig projectConfig;

    public ProjectTechColumnPresenter(MainPresenter mainPresenter){
        super(mainPresenter, new ColumnView());

        extractData();

        addChartSettings();
        addChartData();
    }

    @Override
    public void extractData(){
        projectConfig = super.getMainPresenter().getMainConfig().getCharts()
                .get(ChartNames.Projects_Technology.toString());
    }

    @Override
    public void addChartSettings() {
        PlotOptionsColumn plot = new PlotOptionsColumn();

        plot.setDataLabels(new DataLabels(true));

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.point.name + ': <b>'+ this.y + '</b> Projects'");

        Legend legend = new Legend();
        legend.setEnabled(false);


        super.setModel(new ColumnModel(super.getView().getConfiguration(), projectConfig.getSettings().getTitle(),
                projectConfig.getSettings().getSubtitle(),tooltip, legend, new AxisTitle(projectConfig.getSettings().getxAxisTitle()),
                new AxisTitle(projectConfig.getSettings().getyAxisTitle()), plot));

        super.getModel().setXAxisType(AxisType.CATEGORY);
        logger.info("Settings were added to a chart of "+ this.getClass() +" with chart title: " + super.getView().getConfiguration().getTitle().getText());

    }

    @Override
    public void addChartData() {

        //This is necessary to get from Object to required String arrays
        Object[] objectArray = projectConfig.getData().keySet().toArray(new Object[0]);
        //noinspection SuspiciousToArrayCall
        String[] keySet = Arrays.asList(objectArray).toArray(new String[objectArray.length]);

        //Color[] innerColors = Arrays.copyOf(Colors.getSolidColors(), projectConfig.getSettings().getXCategories().size());
        //Actually adding of data

        List<DataSorter> dataSorterList = new ArrayList<>();

        DataSeries series = new DataSeries();
        for (String aKeySet : keySet) {
            for (int i = 0; i < projectConfig.getData().get(aKeySet).size(); i++) {
                String label = (String) projectConfig.getSettings().getxCategories().get(i);

                if(CommonAbbr.getList().contains(label)){
                    label = CommonAbbr.valueOf(label).toString();
                }else if(Translator.getList().contains(label)){
                    label = Translator.valueOf(label).getTranslation();
                }else{
                    label = LabelFormatter.generateCamelCase(label);
                }
                dataSorterList.add(new DataSorter(label,
                        (int)projectConfig.getData().get(aKeySet).get(i)));

            }
        }
        Collections.sort(dataSorterList);
        dataSorterList.forEach(d -> series.add(new DataSeriesItem(d.getName(), d.getCount())));

        super.getModel().addData(series);

    }


    @Override
    public void addChart(TabView tabView, String title) {
        //Set new tab
        super.setTabView(tabView);
        //projectConfig.getSettings().getTitle()
        super.getTabView().addMainComponent();
        super.getMainPresenter().getMainView().addTabView(super.getTabView(), title);
        logger.info("Tab was added in " + this.getClass() + " for " +  super.getView().getConfiguration().getTitle().getText() );

    }
}