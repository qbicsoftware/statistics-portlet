package life.qbic.portal.presenter.tabs;


import com.vaadin.addon.charts.model.*;
import life.qbic.portal.model.view.charts.PieChartModel;
import life.qbic.portal.presenter.MainPresenter;
import life.qbic.portal.view.TabView;
import life.qbic.portal.view.tabs.charts.PieView;


public class DummyChartPresenter extends ATabPresenter<PieChartModel, PieView> {

    private final String mainTitle = "Dummy Chart";
    private final String subTitle = " ";
    public DummyChartPresenter(MainPresenter mainPresenter){
        super(mainPresenter, new PieView());
    }


    @Override
    public void setUp(){
        addChartSettings();
        addChartData();
    }
    @Override
    public void extractData(){

    }


    @Override
    public void addChartSettings() {
        PlotOptionsPie plot = new PlotOptionsPie();

        plot.setDataLabels(new DataLabels(true));

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("this.point.name + ': <b>'+ this.y + '</b> Samples'");

        Legend legend = new Legend();
        legend.setEnabled(false);

        this.setModel(new PieChartModel(this.getView().getConfiguration(), mainTitle, subTitle, tooltip,
                legend, plot));

        logger.info("Settings were added to a chart of "+ this.getClass() +" with chart title: " + this.getView().getConfiguration().getTitle().getText());

    }

    @Override
    public void addChartData() {

        for (int i = 0; i < 5; i++) {
            this.getModel().addData(new DataSeries(new DataSeriesItem("Genomes".concat(String.valueOf(i)), i)));
        }
        logger.info("Data was added to a chart of  " + this.getClass() + "  with chart title: " + this.getView().getConfiguration().getTitle().getText());
    }

    @Override
    public void addChart(TabView tabView, String title) {
        //Set new tab
        super.setTabView(tabView);
        super.getTabView().addMainComponent();
        super.getMainPresenter().getMainView().addTabView(super.getTabView(), title);

        logger.info("Tab was added in " + this.getClass() + " for " +  this.getView().getConfiguration().getTitle().getText() );

    }
}
