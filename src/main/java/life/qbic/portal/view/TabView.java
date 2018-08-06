package life.qbic.portal.view;


import com.vaadin.server.FontAwesome;
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

    private final Button returnButton = new Button(FontAwesome.ANGLE_DOUBLE_LEFT);
    private final AView mainView;
    private final AModel mainModel;
    private final Label title;
    private final Label subtitle;

    private final Panel graphPanel = new Panel();

    public TabView(AView view, AModel model){
        this.mainView = view;
        this.mainModel = model;

        this.title = new Label("", ContentMode.HTML);
        title.setStyleName(ValoTheme.LABEL_NO_MARGIN);
        title.setStyleName(ValoTheme.LABEL_BOLD);
        title.addStyleName(ValoTheme.LABEL_COLORED);
        title.addStyleName(ValoTheme.LABEL_HUGE);
        title.addStyleName(ValoTheme.LABEL_H1);
        title.setWidth(null);

        this.subtitle = new Label("", ContentMode.HTML);
        subtitle.setStyleName(ValoTheme.LABEL_NO_MARGIN);
        subtitle.setStyleName(ValoTheme.LABEL_BOLD);
        subtitle.addStyleName(ValoTheme.LABEL_COLORED);
        subtitle.addStyleName(ValoTheme.LABEL_LARGE);
        subtitle.addStyleName(ValoTheme.LABEL_H1);
        subtitle.setWidth(null);

        setMargin(false);
        setSpacing(false);

        addComponent(title);
        addComponent(subtitle);
        setComponentAlignment(title, Alignment.MIDDLE_CENTER);
        setComponentAlignment(subtitle, Alignment.MIDDLE_CENTER);

        returnButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        returnButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);

        graphPanel.setWidth(100, Unit.PERCENTAGE);
        //this.mainView.getComponent().setHeight(String.valueOf(this.mainView.getComponent().getHeight() + 50.0));

    }

    public Button getReturnButton() {
        return returnButton;
    }

    public void addMainComponent(){

        removeComponents();

        if(mainView instanceof AChartView) {
            ((AChartView) mainView).draw((AChartModel) mainModel);

        }
        graphPanel.setContent(mainView.getComponent());

        setTitle(mainModel.getTitle());
        setSubtitle(mainModel.getSubTitle());

        addComponents(this.title, this.subtitle, graphPanel);

        setComponentAlignment(this.title, Alignment.MIDDLE_CENTER);
        setComponentAlignment(this.subtitle, Alignment.MIDDLE_CENTER);

        logger.info("Main component was added.");

    }

    private void removeComponents() {
        removeAllComponents();
        setTitle("");
        setSubtitle("");
        logger.info("All components were removed from tab.");
    }

    public void addSubComponent(AModel model, AView view){

        removeComponents();

        if(view instanceof AChartView) {
            ((AChartView)view).draw((AChartModel) model);
            //FIXME: Something is off with vaadin charts size (multiple open issues on GitHub.
            //FIXME: Using this work-around for now. The labels are not accounted for in the height measure,
            //FIXmE: so adding a padding manually.
            view.getComponent().setHeight(Double.toString(view.getComponent().getHeight() + 50.0f));
        }
        graphPanel.setContent(view.getComponent());

        setTitle(model.getTitle());
        setSubtitle(model.getSubTitle());

        addComponents(returnButton,this.title, this.subtitle, graphPanel);

        setComponentAlignment(this.title, Alignment.TOP_CENTER);
        setComponentAlignment(this.subtitle, Alignment.TOP_CENTER);
        setComponentAlignment(returnButton, Alignment.TOP_LEFT);

        logger.info("Sub-component and return button was added to tab.");

    }

    private void setTitle(String title) {
        this.title.setValue(title);
    }

    private void setSubtitle(String subtitle){
        this.subtitle.setValue(subtitle);
    }
}
