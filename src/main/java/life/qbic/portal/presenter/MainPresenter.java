package life.qbic.portal.presenter;

import life.qbic.portal.Styles;
import life.qbic.portal.exceptions.DataNotFoundException;
import life.qbic.portal.model.view.AModel;
import life.qbic.portal.portlet.StatisticsPortlet;
import life.qbic.portal.presenter.tabs.ATabPresenter;
import life.qbic.portal.presenter.tabs.organisms.SuperKingdomCountPresenter;
import life.qbic.portal.presenter.tabs.projects.ProjectCountPresenter;
import life.qbic.portal.presenter.tabs.samples.SampleTypeBarPresenter;
import life.qbic.portal.presenter.tabs.workflows.WorkflowUsagePresenter;
import life.qbic.portal.presenter.utils.CustomNotification;
import life.qbic.portal.utils.PortalUtils;
import life.qbic.portal.view.TabView;
import life.qbic.portal.view.tabs.AView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import submodule.data.MainConfig;

import java.util.ArrayList;
import java.util.List;


/**
 * @author fhanssen
 * Handles input config and adds charts serially.
 * Handles button presses and listens to observabeLists in SubPresenters
 */
public class MainPresenter {

    private static final Logger logger = LogManager.getLogger(MainPresenter.class);

    private final StatisticsPortlet mainView;

    private MainConfig mainConfig;

    private final ATabPresenter superKingdomCountPresenter;
    private final ATabPresenter sampleTypePresenter;
    private final ATabPresenter projectPresenter;
    private final ATabPresenter workflowUsagePresenter;

    private final List<ATabPresenter> tabPresenterList = new ArrayList<>();

    public MainPresenter(StatisticsPortlet mainView, String defaultInputFilename) {
        this.mainView = mainView;
        this.mainConfig = new MainConfig();

        superKingdomCountPresenter = new SuperKingdomCountPresenter(this);
        sampleTypePresenter = new SampleTypeBarPresenter(this);
        projectPresenter = new ProjectCountPresenter(this);
        workflowUsagePresenter = new WorkflowUsagePresenter(this);

        //This is necessary to allow try-loop to see wether no charts at all can be displayed and we have
        // to use the Dummy chart
        tabPresenterList.add(superKingdomCountPresenter);
        tabPresenterList.add(sampleTypePresenter);
        tabPresenterList.add(projectPresenter);
        tabPresenterList.add(workflowUsagePresenter);

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

    void addChildPresenter() throws DataNotFoundException{
        clear();
        int counter = 0;
        for(ATabPresenter presenter : tabPresenterList){
            try{
                presenter.setUp();
                presenter.addChart(new TabView((AView) presenter.getView(),
                        (AModel) presenter.getModel()), ((AModel) presenter.getModel()).getTabTitle());

            }catch(DataNotFoundException e){
                logger.error("Mainchart could not be set up.", e);
                counter++;//count up whenever exception is thrown
                //Exception was also thrown on last presenter
                if(counter > tabPresenterList.size() - 1){
                    Styles.notification("Data not found.","Chart cannot be displayed", Styles.NotificationType.ERROR);
                    throw new DataNotFoundException(e);
                }
            }
        }

    }

    void addCharts() {

        clear();
        //Careful: Order matters! Determines in which order tabs are displayed.
//
//        superKingdomCountPresenter.addChart(new TabView((AView) superKingdomCountPresenter.getView(),
//                (AModel) superKingdomCountPresenter.getModel()), "Organisms");
//        sampleTypePresenter.addChart(new TabView((AView) sampleTypePresenter.getView(),
//                (AModel) sampleTypePresenter.getModel()), "Samples");
//        //projectPresenter.addChart(new TabView((AView) projectPresenter.getView(),
//        //        (AModel) projectPresenter.getModel()), "Projects");
//        workflowUsagePresenter.addChart(new TabView((AView) workflowUsagePresenter.getView(),
//                (AModel) workflowUsagePresenter.getModel()), "Workflow");

    }

}
