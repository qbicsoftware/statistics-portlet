package life.qbic.portal.presenter;


import life.qbic.portal.model.view.AModel;
import life.qbic.portal.portlet.StatisticsPortlet;
import life.qbic.portal.presenter.tabs.ATabPresenter;
import life.qbic.portal.presenter.tabs.organisms.SuperKingdomCountPresenter;
import life.qbic.portal.presenter.tabs.samples.SampleTypeBarPresenter;
import life.qbic.portal.presenter.tabs.workflows.WorkflowUsagePresenter;
import life.qbic.portal.presenter.utils.CustomNotification;
import life.qbic.portal.utils.PortalUtils;
import life.qbic.portal.view.TabView;
import life.qbic.portal.view.tabs.AView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import submodule.data.MainConfig;



/**
 * @author fhanssen
 * Handles input config and adds charts serially.
 * Handles button presses and listens to observabeLists in SubPresenters
 */
public class MainPresenter {

    private static final Logger logger = LogManager.getLogger(MainPresenter.class);

    private final StatisticsPortlet mainView;

    private MainConfig mainConfig;

    private ATabPresenter superKingdomCountPresenter;
    private ATabPresenter sampleTypePresenter;
    private ATabPresenter projectPresenter;
    private ATabPresenter workflowUsagePresenter;

    public MainPresenter(StatisticsPortlet mainView, String defaultInputFilename) {
        this.mainView = mainView;
        this.mainConfig = new MainConfig();

        FileLoadPresenter fileLoadPresenter = new FileLoadPresenter(this);
        fileLoadPresenter.setChartsFromConfig(defaultInputFilename);

    }

    void setMainConfig(MainConfig mainConfig) {
        this.mainConfig = mainConfig;
    }

    public StatisticsPortlet getMainView() {
        return mainView;
    }

    public void clear() {
        mainView.clearTabSheet();
    }

    boolean isAdmin() {

        try {
            for (com.liferay.portal.model.Role r : PortalUtils.getUser().getRoles()) {
                if (r.getName().equals("Administrator")) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("Could not determine whether logged in user is admin.");
            CustomNotification.error("Error", e.toString());
        }

        return false;
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    void addChildPresenter() {
        superKingdomCountPresenter = new SuperKingdomCountPresenter(this);
        sampleTypePresenter = new SampleTypeBarPresenter(this);
        //projectPresenter = new ProjectTechColumnPresenter(this);
        workflowUsagePresenter = new WorkflowUsagePresenter(this);
    }

    void addCharts() {

        clear();
        //Careful: Order matters! Determines in which order tabs are displayed.
        superKingdomCountPresenter.addChart(new TabView((AView) superKingdomCountPresenter.getView(),
                (AModel) superKingdomCountPresenter.getModel()), "Organisms");
        sampleTypePresenter.addChart(new TabView((AView) sampleTypePresenter.getView(),
                (AModel) sampleTypePresenter.getModel()), "Samples");
        //projectPresenter.addChart(new TabView((AView) projectPresenter.getView(),
        //        (AModel) projectPresenter.getModel()), "Projects");
        workflowUsagePresenter.addChart(new TabView((AView) workflowUsagePresenter.getView(),
                (AModel) workflowUsagePresenter.getModel()), "Workflow");

    }

}