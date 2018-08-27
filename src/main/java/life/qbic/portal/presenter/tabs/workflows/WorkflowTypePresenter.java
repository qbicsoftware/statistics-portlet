package life.qbic.portal.presenter.tabs.workflows;


import com.vaadin.ui.Accordion;
import com.vaadin.ui.VerticalLayout;
import life.qbic.portal.Styles;
import life.qbic.portal.exceptions.DataNotFoundException;
import life.qbic.portal.model.view.GridModel;
import life.qbic.portal.presenter.MainPresenter;
import life.qbic.portal.presenter.tabs.ATabPresenter;
import life.qbic.portal.view.TabView;
import life.qbic.portal.view.tabs.GridView;
import submodule.lexica.ChartNames;
import submodule.lexica.Translator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fhanssen
 */
public class WorkflowTypePresenter extends ATabPresenter<GridModel, GridView> {

    private final String workflowType;
    private final List<String> subTypes;
    private final List<AvailableWorkflowPresenter> availableWorkflowPresenters;

    private final String title;
    private final String subtitle;
    private final String tabTitle;

    private final Accordion accordion = new Accordion();

    WorkflowTypePresenter(MainPresenter mainPresenter, String workflowType, String title, String subtitle, String tabTitle) {
        super(mainPresenter, new GridView(1, 2, true, true));

        this.title = title;
        this.subtitle = subtitle;
        this.tabTitle = tabTitle;

        this.workflowType = workflowType;
        this.subTypes = new ArrayList<>();
        this.availableWorkflowPresenters = new ArrayList<>();

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
    protected void extractData() throws DataNotFoundException, NullPointerException{
        String subType = Translator.getOriginal(workflowType);
        if (subType.isEmpty()) {
            subType = workflowType;

        }

        final String s = subType;
        getMainPresenter().getMainConfig().getCharts().keySet().forEach(n -> {
            if (n.contains(ChartNames.Available_Workflows_.toString()) && (n.contains(s) || n.contains(workflowType))) {
                subTypes.add(n);
            }
        });

        if(subTypes.size() == 0){
            throw new DataNotFoundException("No data on available workflows could be retrieved: " + workflowType);
        }

    }

    @Override
    protected void addChartSettings() {
        super.setModel(new GridModel(title, subtitle, tabTitle));
        logger.info("Settings were added to " + this.getClass() + " with chart title: " + super.getModel().getTitle());

    }

    @Override
    protected void addChartData(){
        for(String s : subTypes){
            try {
                AvailableWorkflowPresenter ap = new AvailableWorkflowPresenter(getMainPresenter(), s);
                ap.setUp();
                availableWorkflowPresenters.add(ap);
            }catch(DataNotFoundException e){
                logger.error("Subchart could not be added", e);
                Styles.notification("Data not found.","Chart cannot be displayed", Styles.NotificationType.ERROR);

            }

        }
        logger.info("Data was added to a chart of " + this.getClass());

    }

    @Override
    public void addChart(TabView tabView, String title) {

        for (AvailableWorkflowPresenter a : availableWorkflowPresenters) {
            a.addChart(tabView, "");
            setGridItemLayout(a.getView(), a.getChartName());
        }

        super.getView().addGridComponents(accordion);
        tabView.addSubComponent(super.getModel(), super.getView());
        this.accordion.setSelectedTab(this.accordion.getComponentCount() - 1);

        addReturnButtonListener(tabView);
        logger.info("Tab was added in " + this.getClass() + " for " + super.getModel().getTitle());
    }

    private void setGridItemLayout(GridView view, String chartName) {
        final VerticalLayout layout = new VerticalLayout(view.getComponent());
        String title = chartName.split("_")[chartName.split("_").length - 1];

        if (!Translator.getTranslation(title).isEmpty()) {
            title = Translator.getTranslation(title);
        }

        accordion.addTab(layout, title);

    }
}
