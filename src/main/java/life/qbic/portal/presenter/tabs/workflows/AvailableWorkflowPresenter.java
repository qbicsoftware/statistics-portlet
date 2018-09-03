package life.qbic.portal.presenter.tabs.workflows;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import life.qbic.portal.exceptions.DataNotFoundException;
import life.qbic.portal.model.components.GitHubLabels;
import life.qbic.portal.model.view.GridModel;
import life.qbic.portal.presenter.MainPresenter;
import life.qbic.portal.presenter.tabs.ATabPresenter;
import life.qbic.portal.view.TabView;
import life.qbic.portal.view.tabs.GridView;
import submodule.data.ChartConfig;

import java.util.Arrays;
/**
 * @author fhanssen
 */
public class AvailableWorkflowPresenter extends ATabPresenter<GridModel, GridView> {

    private final String chartName;
    private ChartConfig chartConfig;

    public AvailableWorkflowPresenter(MainPresenter mainPresenter,
                                      String chartName){
        super(mainPresenter, new GridView(3,0,true, true));

        this.chartName = chartName;

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
    protected void extractData() throws DataNotFoundException, NullPointerException {
        chartConfig = super.getChartConfig(chartName);
    }

    @Override
    protected void addChartSettings() {
        super.setModel(new GridModel(chartConfig.getSettings().getTitle(), chartConfig.getSettings().getSubtitle(), chartConfig.getSettings().getTabTitle()));
        super.getView().getComponent().setStyleName("workflow");

        logger.info("Settings were added to " + this.getClass() + " with chart title: " + super.getModel().getTitle());
    }

    @Override
    protected void addChartData(){

        //This is necessary to get from Object to required String arrays
        Object[] objectArray = chartConfig.getData().keySet().toArray(new Object[0]);
        //noinspection SuspiciousToArrayCall
        @SuppressWarnings("SuspiciousToArrayCall") String[] keySet = Arrays.asList(objectArray).toArray(new String[objectArray.length]);


        //Actually adding of data
        for (String aKeySet : keySet) {
            for (int i = 0; i < chartConfig.getData().get(aKeySet).size(); i++) {
                String[] title = ((String) chartConfig.getSettings().getxCategories().get(i)).split("/");
                super.getModel().addData(new GitHubLabels(title[title.length - 2].concat("/").concat(title[title.length - 1]),
                        (String) chartConfig.getSettings().getyCategories().get(i),
                        (int)(double) chartConfig.getData().get(aKeySet).get(i)));
            }
        }

        logger.info("Data was added to a chart of " + this.getClass() + " with chart title: " + chartConfig.getSettings().getTitle());


    }

    @Override
    public void addChart(TabView tabView, String title) {

        super.getView().setRows(super.getModel().getData().size()/2 + 1);
        for (Object labels : super.getModel().getData()) {
            setGridItemLayout((GitHubLabels) labels);
        }

        logger.info("Tab was added in " + this.getClass() + " for " +  super.getModel().getTitle());

    }

    private void setGridItemLayout(GitHubLabels labels) {

        Panel panel = new Panel(labels.getTitle());
        panel.setHeight(100.0f, Sizeable.Unit.PERCENTAGE);
        panel.setStyleName(ValoTheme.PANEL_SCROLL_INDICATOR);

        Label star = new Label(FontAwesome.STAR_O.getHtml() + " " + labels.getCount(), ContentMode.HTML);

        Link link = new Link("",
                new ExternalResource(labels.getUrl()));
        link.setIcon(FontAwesome.valueOf("GITHUB_SQUARE"));

        HorizontalLayout horizontalLayout = new HorizontalLayout(link, star);
        horizontalLayout.setSpacing(true);
        horizontalLayout.setComponentAlignment(star, Alignment.TOP_RIGHT);
        horizontalLayout.setComponentAlignment(link, Alignment.TOP_RIGHT);

        Label label = new Label(labels.getDescription(), ContentMode.HTML);
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth(100, Sizeable.Unit.PERCENTAGE);
        verticalLayout.setSpacing(false);
        verticalLayout.addComponent(horizontalLayout);
        verticalLayout.addComponent(label);
        verticalLayout.setMargin(true);

        panel.setContent(verticalLayout);
        super.getView().addGridComponents(panel);
    }

    String getChartName() {
        return chartName;
    }
}
