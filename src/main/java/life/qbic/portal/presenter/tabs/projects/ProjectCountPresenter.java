package life.qbic.portal.presenter.tabs.projects;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.LegendItemClickEvent;
import com.vaadin.addon.charts.LegendItemClickListener;
import com.vaadin.addon.charts.PointClickListener;
import com.vaadin.addon.charts.model.*;
import com.vaadin.event.Action;
import com.vaadin.ui.Component;
import life.qbic.portal.exceptions.DataNotFoundException;
import life.qbic.portal.model.view.charts.PieChartModel;
import life.qbic.portal.presenter.MainPresenter;
import life.qbic.portal.presenter.tabs.ATabPresenter;
import life.qbic.portal.presenter.utils.DataSorter;
import life.qbic.portal.presenter.utils.LabelFormatter;
import life.qbic.portal.view.TabView;
import life.qbic.portal.view.tabs.charts.PieView;
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
public class ProjectCountPresenter extends ATabPresenter<PieChartModel, PieView> {

    private ChartConfig projectConfig;

    public ProjectCountPresenter(MainPresenter mainPresenter){
        super(mainPresenter, new PieView());
    }

    @Override
    protected void extractData() throws DataNotFoundException, NullPointerException {
        projectConfig = super.getChartConfig(ChartNames.Projects.toString());
    }

    @Override
    protected void addChartSettings() {

        PlotOptionsPie plot = new PlotOptionsPie();
        plot.setDataLabels(new DataLabels(true));
        plot.setShowInLegend(true);
        //Labels that point to a pie piece that can be clicked will be cornflower blue (Multi-omics)
        plot.getDataLabels().setFormatter("function() { " +
                "var text = this.point.name; " +
                "if (text.includes('Multi-omics')) " +
                "{ " +
                "       text = '<span style=\"color:CornflowerBlue;text-decoration:underline\">' + text + '</span>'; " +
                "}" +
                "return text; " +
                "}" );

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.point.name + ': <b>'+ this.y + '</b> Projects'");

        Legend legend = new Legend();
        legend.setLabelFormatter("function() {" +
                "var text = this.name.split('[')[0];" +
                "text = text.substring(0, text.length - 1);" +
                "return text + ': ' + this.y + ' Projects' "+
                "}");
        legend.setLayout(LayoutDirection.VERTICAL);
        legend.setVerticalAlign(VerticalAlign.BOTTOM);
        legend.setAlign(HorizontalAlign.RIGHT);

        super.setModel(new PieChartModel(super.getView().getConfiguration(), projectConfig.getSettings().getTitle(),
                projectConfig.getSettings().getSubtitle(), projectConfig.getSettings().getTabTitle(), tooltip, legend, plot));

        logger.info("Settings were added to a chart of "+ this.getClass() +" with chart title: " + super.getView().getConfiguration().getTitle().getText());

    }

    @Override
    protected void addChartData() {

        //This is necessary to get from Object to required String arrays
        Object[] objectArray = projectConfig.getData().keySet().toArray(new Object[0]);
        String[] keySet = Arrays.asList(objectArray).toArray(new String[objectArray.length]);

        List<DataSorter> dataSorterList = new ArrayList<>();

        for (String aKeySet : keySet) {
            for (int i = 0; i < projectConfig.getData().get(aKeySet).size(); i++) {
                String label = (String) projectConfig.getSettings().getxCategories().get(i);

                if(CommonAbbr.getList().contains(label)){
                    label = CommonAbbr.valueOf(label).toString();
                }else if(Translator.getList().contains(label)) {
                    label = Translator.valueOf(label).getTranslation();
                }else{
                    label = LabelFormatter.generateCamelCase(label);
                }

                label = label
                        .concat(" [")
                        .concat(String.valueOf(projectConfig.getSettings().getyCategories().get(i)))
                        .concat("%]");
                dataSorterList.add(new DataSorter(label,
                        (int)projectConfig.getData().get(aKeySet).get(i)));

            }
        }
        Collections.sort(dataSorterList);
        dataSorterList.forEach(d -> this.getModel().addData(new DataSeriesItem(d.getName(), d.getCount())));

        logger.info("Data was added to a chart of " + this.getClass() + " with chart title: " + projectConfig.getSettings().getTitle());


    }

    @Override
    public void addChart(TabView tabView, String title) {
        //Set new tab
        super.setTabView(tabView);
        super.getTabView().addMainComponent();
        super.getMainPresenter().getMainView().addTabView(super.getTabView(), title);
        logger.info("Tab was added in " + this.getClass() + " for " +  super.getView().getConfiguration().getTitle().getText() );

    }

    @Override
    protected void addChartListener() {
        ((Chart) getView().getComponent()).addPointClickListener((PointClickListener) event -> {

            logger.info("Chart of " + this.getClass() + " with chart title: " +
                    this.getView().getConfiguration().getTitle().getText() +
                    " was clicked at " + this.getModel().getDataName(event));

            if (super.getModel().getDataName(event).contains(Translator.Multi_omics.getTranslation())) {
                ATabPresenter p =
                        new MultiOmicsCountPresenter(super.getMainPresenter());
                addSubchart(p);
            }
        });

        ((Chart) getView().getComponent()).addLegendItemClickListener(legendItemClickEvent -> {
            //do nothing, overwrites normal function: hide/show clicked legend item in chart
        });
    }

}