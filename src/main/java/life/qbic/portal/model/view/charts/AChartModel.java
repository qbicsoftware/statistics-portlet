package life.qbic.portal.model.view.charts;

import com.vaadin.addon.charts.model.*;
import life.qbic.portal.model.view.AModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * @author fhanssen
 * This abstract class holds all fields and methods needed for a general Chart, mainly the configuration file. When
 * adding a new chart type (such as Scatter Plot or so) a new chart model needs be added. A comprehensive
 * guide on Vaadin charts can be found here: https://demo.vaadin.com/charts/
 */


public abstract class AChartModel<T extends AbstractSeries> implements AModel<T> {

    private static final Logger logger = LogManager.getLogger(AChartModel.class);

    final Configuration configuration;

    public AChartModel(Configuration configuration, String title, String subtitle,
                       Tooltip tooltip, Legend legend, AbstractPlotOptions plotOptions){

        this.configuration = configuration;
        this.configuration.setTitle(title);
        this.configuration.setSubTitle(subtitle);

        this.configuration.setTooltip(tooltip);

        this.configuration.setLegend(legend);

        this.configuration.setPlotOptions(plotOptions);

        logger.info("New chart model for " + title + " was successfully created");
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    abstract public void addData(T... item);

    //TODO 2: If your chart TYPE does not exists yet, extend this class
}