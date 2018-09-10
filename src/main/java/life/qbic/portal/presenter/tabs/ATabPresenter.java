package life.qbic.portal.presenter.tabs;


import life.qbic.portal.Styles;
import life.qbic.portal.exceptions.DataNotFoundException;
import life.qbic.portal.presenter.MainPresenter;
import life.qbic.portal.view.TabView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import submodule.data.ChartConfig;

/**
 * @author fhanssen
 * Abstract class holds fields and methods that a ChartPresenter generally needs. When extending it, just use your required
 * model and view type.
 */
public abstract class ATabPresenter<T, V> {

    protected static final Logger logger = LogManager.getLogger(ATabPresenter.class);

    private final V view;
    private final MainPresenter mainPresenter;
    private TabView tabView;
    private T model;

    public ATabPresenter(MainPresenter mainPresenter, V view){
        this.view = view;
        this.mainPresenter = mainPresenter;
    }

    public T getModel(){
        return model;
    }

    public V getView(){
        return view;
    }

    protected MainPresenter getMainPresenter() {
        return mainPresenter;
    }

    public void setModel(T model){
        this.model = model;
    }

    protected void setTabView(TabView temp){
        this.tabView = temp;
    }

    public TabView getTabView() {
        return tabView;
    }

    protected void addReturnButtonListener(TabView tabView){
        tabView.getReturnButton().addClickListener(clickEvent -> {
            logger.info("Return button was pressed");
            tabView.addMainComponent();

        });
    }

    protected void addSubchart(ATabPresenter p) {
        try {
            p.setUp();
            p.addChart(this.getTabView(), "");
        } catch (DataNotFoundException e) {
            logger.error("Subcharts could not be created. ", e);
            Styles.notification("Data not found.", "Chart cannot be displayed", Styles.NotificationType.ERROR);
        }
    }

    protected ChartConfig getChartConfig(String name) throws DataNotFoundException, NullPointerException{

        if(mainPresenter.getMainConfig() == null || mainPresenter.getMainConfig().getCharts() == null){
            throw new NullPointerException("No charts exist.");
        }

        if(!mainPresenter.getMainConfig().getCharts().containsKey(name)){
            throw  new DataNotFoundException("Data for " + name + " was not found.");
        }
        return mainPresenter.getMainConfig().getCharts().get(name);
    }

    public void setUp() throws DataNotFoundException{
        try {
            extractData();
            addChartSettings();
            addChartData();
            addChartListener();
        } catch (DataNotFoundException | NullPointerException e) {
            throw e;
        }
    }

    abstract protected void extractData()throws DataNotFoundException;

    abstract protected void addChartSettings();

    abstract protected void addChartData() throws DataNotFoundException ;

    abstract public void addChart(TabView tabView, String title);

    abstract protected void addChartListener();


        //TODO 4: Extend this class in order to create a new presenter. As examples you can look at the SuperKingdomCountPresenter or GenusSpeciesCountPresenter
}
