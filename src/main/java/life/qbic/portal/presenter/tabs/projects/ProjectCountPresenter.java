package life.qbic.portal.presenter.tabs.projects;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.PointClickListener;
import com.vaadin.addon.charts.model.*;
import life.qbic.portal.Styles;
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
    public void setUp() throws DataNotFoundException, NullPointerException{
        try {
            extractData();
            addChartSettings();
            addChartData();
            addChartListener();
        }catch(DataNotFoundException | NullPointerException e){
            throw e;
        }

    }

    @Override
    public void extractData() throws DataNotFoundException, NullPointerException {
        projectConfig = super.getChartConfig(ChartNames.Projects.toString());
    }

    @Override
    public void addChartSettings() {

        PlotOptionsPie plot = new PlotOptionsPie();
        plot.setDataLabels(new DataLabels(true));
        //Labels that point to a pie piece that can be clicked will be cornflower blue (Multi-omics)
        plot.getDataLabels().setFormatter("function() { " +
                "var text = this.point.name; " +
                "if (text == 'Multi-omics') " +
                "{ " +
                "       text = '<span style=\"color:CornflowerBlue;text-decoration:underline\">' + text + '</span>'; " +
                "}" +
                "return text; " +
                "}" );

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.point.name + ': <b>'+ this.y + '</b> Projects'");

        Legend legend = new Legend();
        legend.setEnabled(false);

        super.setModel(new PieChartModel(super.getView().getConfiguration(), projectConfig.getSettings().getTitle(),
                projectConfig.getSettings().getSubtitle(), projectConfig.getSettings().getTabTitle(), tooltip, legend, plot));

        logger.info("Settings were added to a chart of "+ this.getClass() +" with chart title: " + super.getView().getConfiguration().getTitle().getText());

    }

    @Override
    public void addChartData() {

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
                dataSorterList.add(new DataSorter(label,
                        (int)projectConfig.getData().get(aKeySet).get(i)));

            }
        }
        Collections.sort(dataSorterList);
        dataSorterList.forEach(d -> this.getModel().addData(new DataSeriesItem(d.getName(), d.getCount())));

        logger.info("Data was added to a chart of " + this.getClass() + " with chart title: " + projectConfig.getSettings().getTitle());


    }

    private void addChartListener() {
        ((Chart) getView().getComponent()).addPointClickListener((PointClickListener) event -> {

            logger.info("Chart of " + this.getClass() + " with chart title: " +
                    this.getView().getConfiguration().getTitle().getText() +
                    " was clicked at " + this.getModel().getDataName(event));

            if (super.getModel().getDataName(event).equals(Translator.Multi_omics.getTranslation())) {
                ATabPresenter p =
                        new MultiOmicsCountPresenter(super.getMainPresenter());
                try {
                    p.setUp();
                    p.addChart(this.getTabView(), "");
                } catch (DataNotFoundException e) {
                    logger.error("Subcharts could not be created. ", e);
                    Styles.notification("Data not found.", "Chart cannot be displayed", Styles.NotificationType.ERROR);
                }
            }
        });
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