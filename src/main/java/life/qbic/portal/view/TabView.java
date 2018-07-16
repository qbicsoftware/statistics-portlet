package life.qbic.portal.view;



import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import life.qbic.portal.model.view.AModel;
import life.qbic.portal.model.view.charts.AChartModel;
import life.qbic.portal.view.tabs.AView;
import life.qbic.portal.view.tabs.charts.AChartView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author fhanssen
 */
public class TabView extends VerticalLayout {

    private static final Logger logger = LogManager.getLogger(TabView.class);

    private final Button returnButton = new Button("Return");
    private final AView mainView;
    private final AModel mainModel;
    private final Label title;


    public TabView(AView view, AModel model){
        this.mainView = view;
        this.mainModel = model;
        this.title = new Label("Test", ContentMode.HTML);
        title.setStyleName(ValoTheme.LABEL_BOLD);
        title.addStyleName(ValoTheme.LABEL_COLORED);
        title.addStyleName(ValoTheme.LABEL_HUGE);
        title.addStyleName(ValoTheme.LABEL_H1);
        title.setWidth(100.0f, Unit.PERCENTAGE);
        setMargin(true);

        addComponent(title);
        setComponentAlignment(title, Alignment.MIDDLE_CENTER);

    }

    public Button getReturnButton() {
        return returnButton;
    }

    public void addMainComponent(){

        removeComponents();

        if(mainView instanceof AChartView) {
            ((AChartView) mainView).draw((AChartModel) mainModel);
        }
        addComponents(title, mainView.getComponent());
        setComponentAlignment(title, Alignment.MIDDLE_CENTER);
        logger.info("Main component was added.");

    }

    private void removeComponents() {
        removeAllComponents();
        setTitle("");
        logger.info("All components were removed from tab.");
    }

    public void addSubComponent(AModel model, AView view){

        removeComponents();
        if(view instanceof AChartView) {
            ((AChartView)view).draw((AChartModel) model);
        }

        addComponents(title, view.getComponent(), returnButton);
        setComponentAlignment(title, Alignment.MIDDLE_CENTER);
        setComponentAlignment(returnButton, Alignment.TOP_RIGHT);

        logger.info("Sub-component and return button was added to tab.");

    }

    public void setTitle(String title) {
        this.title.setValue(title);
    }

}
