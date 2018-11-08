package life.qbic.portal.portlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Layout;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import life.qbic.portal.presenter.MainPresenter;
import life.qbic.portal.utils.PortalUtils;
import life.qbic.portal.view.TabView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Entry point for portlet statistics-portlet. This class derives from {@link QBiCPortletUI}, which is found in the {@code portal-utils-lib} library.
 * 
 * //@see https://github.com/qbicsoftware/portal-utils-lib
 */
@Theme("mytheme")
@SuppressWarnings("serial")
@Widgetset("life.qbic.portal.portlet.AppWidgetSet")
public class StatisticsPortlet extends QBiCPortletUI {

    private static final Logger logger = LogManager.getLogger(StatisticsPortlet.class);
    private final TabSheet tabSheet = new TabSheet();
    private final VerticalLayout layout = new VerticalLayout();

    @Override
    protected Layout getPortletContent(final VaadinRequest request) {
        logger.info("Generating content for {}", StatisticsPortlet.class);
        logger.info("Generating content for portlet statistics-portlet");

        if (PortalUtils.isLiferayPortlet()) {
            logger.info("User: " + PortalUtils.getUser().getScreenName());
        } else {
            logger.info("You are currently in a local testing mode. No Liferay Portlet context found.");
        }

        layout.setMargin(false);
        setContent(layout);
        layout.addComponent(tabSheet);

        try {
            MainPresenter mainPresenter = new MainPresenter(this,  "/config.yaml" );

        }catch(Exception e){
            logger.error("Portlet failed due to: " + e.toString());
            e.printStackTrace();
        }

        return layout;
    }

    public void addTabView(TabView tabView, String title){
        this.tabSheet.addTab(tabView).setCaption(title);
        logger.info("A new tab with titel " + title +" was added.");
    }

    public VerticalLayout getLayout() {
        return layout;
    }

    public void clearTabSheet(){
        tabSheet.removeAllComponents();
    }
}