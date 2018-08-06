package life.qbic.portal.presenter.tabs;


import life.qbic.portal.presenter.MainPresenter;
import life.qbic.portal.view.TabView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    abstract public void extractData();

    abstract public void addChartSettings();

    abstract public void addChartData();

    abstract public void addChart(TabView tabView, String title);

    //TODO 4: Extend this class in order to create a new presenter. As examples you can look at the SuperKingdomCountPresenter or GenusSpeciesCountPresenter
}
