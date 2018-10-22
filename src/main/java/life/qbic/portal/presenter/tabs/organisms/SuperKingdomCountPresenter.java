package life.qbic.portal.presenter.tabs.organisms;


import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.PointSelectListener;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.PointClickListener;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
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
import submodule.lexica.Kingdoms;

import java.util.*;

/**
 * @author fhanssen
 */
public class SuperKingdomCountPresenter extends ATabPresenter<PieChartModel, PieView> {

    private ChartConfig kingdomConfig;

    public SuperKingdomCountPresenter(MainPresenter mainPresenter){
        super(mainPresenter, new PieView());
    }

    @Override
    protected void extractData() throws DataNotFoundException, NullPointerException{
        kingdomConfig = super.getChartConfig(ChartNames.SuperKingdom.toString());
    }

    @Override
    protected void addChartSettings() {

        PlotOptionsPie plot = new PlotOptionsPie();

        plot.setDataLabels(new DataLabels(true));
        plot.setShowInLegend(true);
        plot.setCursor(Cursor.POINTER);
        plot.setEnableMouseTracking(true);

        Hover hover = new Hover();
        hover.setEnabled(true);
        plot.getStates().setHover(hover);

        //Labels that point to a pie piece that can be clicked will be cornflower blue (Other Eukaryota/Bacteria/...)
        plot.getDataLabels().setFormatter("function() { " +
                "var text = this.point.name; " +
                "var category = text.split(' ')[0]; " +
                "var kingdoms = ['Eukaryota', 'Bacteria', 'Viruses', 'Archae', 'Viroids'];" +
                "var isKingdom = false;"+
                "for(var i =0;i < kingdoms.length; i++){ "+
                //Either 'Kingdoms' or 'Other Kingdom' is clickable and thus blue underlined
                "   if(category.indexOf(kingdoms[i]) != -1 || text.split(' ')[1].indexOf(kingdoms[i]) != -1)"+
                "       isKingdom = true;"+
                "}"+
                "if (isKingdom) " +
                "{ " +
                "       text = '<span style=\"color:CornflowerBlue;text-decoration:underline\">' + text + '</span>'; " +
                "}" +
                "return text; " +
                "}");

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter(  "function() { " +
                "var text = this.point.name  + ': <b>'+ this.y + '</b> Samples';" +
                        "var category = text.split(' ')[0]; " +
                        "var kingdoms = ['Eukaryota', 'Bacteria', 'Viruses', 'Archae', 'Viroids'];" +
                        "var isKingdom = false;"+
                        "for(var i =0;i < kingdoms.length; i++){ "+
                       "   if(category.indexOf(kingdoms[i]) != -1 || text.split(' ')[1].indexOf(kingdoms[i]) != -1)"+
                        "       isKingdom = true;"+
                        "}"+
                        "if (isKingdom) " +
                        "{ " +
                        "       text = text + '<br> Click to show species';" +
                        "}" +
                        "return text; " +
                "}"
        );

        Legend legend = new Legend();
        legend.setLabelFormatter("function() {" +
                "var text = this.name.split('[')[0];" +
                "text = text.substring(0, text.length - 1);" +
                "return text + ': ' + this.y + ' Samples' "+
                "}");

        legend.setLayout(LayoutDirection.VERTICAL);
        legend.setVerticalAlign(VerticalAlign.BOTTOM);
        legend.setAlign(HorizontalAlign.RIGHT);

        this.setModel(new PieChartModel(this.getView().getConfiguration(), this.kingdomConfig.getSettings().getTitle(),
                this.kingdomConfig.getSettings().getSubtitle(), this.kingdomConfig.getSettings().getTabTitle(), tooltip, legend, plot));

        Credits credits = new Credits("If a species's ratio exceeds 50% in its respective domain, it is displayed and visualized on domain level.");
        credits.setPosition(new Position());
        credits.getPosition().setHorizontalAlign(HorizontalAlign.LEFT);
        credits.getPosition().setX(10);
        super.getModel().getConfiguration().setCredits(credits);

        logger.info("Settings were added to a chart of " + this.getClass() + " with chart title: " + this.getView().getConfiguration().getTitle().getText());

    }

    @Override
    protected void addChartData() {

        //This is necessary to get from Object to required String arrays
        Object[] objectArray = kingdomConfig.getData().keySet().toArray(new Object[0]);
        @SuppressWarnings("SuspiciousToArrayCall") String[] keySet = Arrays.asList(objectArray).toArray(new String[objectArray.length]);

        List<DataSorter> dataSorters = new ArrayList<>();

        //Retrieve and Sort data
        for (String aKeySet : keySet) {
            for (int i = 0; i < kingdomConfig.getData().get(aKeySet).size(); i++) {
                String label = LabelFormatter.generateCamelCase((String) kingdomConfig.getSettings().getxCategories().get(i))
                        .concat(" [")
                        .concat(String.valueOf(kingdomConfig.getSettings().getyCategories().get(i)))
                        .concat("%]");
                dataSorters.add(new DataSorter(label,
                        (int) kingdomConfig.getData().get(aKeySet).get(i)));

            }
        }

        Collections.sort(dataSorters);

        //Add data


        dataSorters.forEach(d -> {
            DataSeriesItem dataSeriesItem = new DataSeriesItem(d.getName(), d.getCount());
            //Either 'Kingdoms' or 'Other Kingdom' is clickable and thus is selected on hover
            if(!isKingdom(d.getName())) {
                dataSeriesItem.setSelected(true);
            }
            this.getModel().addData(dataSeriesItem);
        });

        logger.info("Data was added to a chart of  " + this.getClass() + "  with chart title: " + this.getView().getConfiguration().getTitle().getText());

    }

    private boolean isKingdom(String name){
        //returns true if name is of format <kingdom> [x%] or Other <kingdom> [x%]
        //else returns false
        return Kingdoms.getList().contains(name.split(" ")[0]) || Kingdoms.getList().contains(name.split(" ")[1]);
    }

    @Override
    public void addChart(TabView tabView, String title) {
        //Set new tab
        super.setTabView(tabView);
        super.getTabView().addMainComponent();
        super.getMainPresenter().getMainView().addTabView(super.getTabView(), title);

        logger.info("Tab was added in " + this.getClass() + " for " + this.getView().getConfiguration().getTitle().getText());

    }

    @Override
    protected void addChartListener() {
        ((Chart) getView().getComponent()).addPointClickListener((PointClickListener) event -> {

            logger.info("Chart of " + this.getClass() + " with chart title: " +
                    this.getView().getConfiguration().getTitle().getText() +
                    " was clicked at " + this.getModel().getDataName(event));

            //In case it is Other Bacteria etc.
            if (Kingdoms.getList().contains(this.getModel().getDataName(event).split(" ")[1])) {
                ATabPresenter p =
                        new GenusSpeciesCountPresenter(super.getMainPresenter(), this.getModel().getDataName(event).split(" ")[1]);
                addSubchart(p);
            }

            //In case it is not Other, but plain 'Viruses' etc.
            if (Kingdoms.getList().contains(this.getModel().getDataName(event).split(" ")[0])) {
                ATabPresenter p =
                        new GenusSpeciesCountPresenter(super.getMainPresenter(), this.getModel().getDataName(event).split(" ")[0]);

                addSubchart(p);
            }

        });

        ((Chart) getView().getComponent()).addLegendItemClickListener(legendItemClickEvent -> {
            //do nothing, overwrites normal function: hide/show clicked legend item in chart
        });



    }

}