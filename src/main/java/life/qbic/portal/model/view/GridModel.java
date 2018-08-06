package life.qbic.portal.model.view;


import life.qbic.portal.model.components.AComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author fhanssen
 */
public class GridModel implements AModel<AComponent> {

    private static final Logger logger = LogManager.getLogger(GridModel.class);


    private final List<AComponent> data = new ArrayList<>();
    private final String title;
    private final String subtitle;

    public GridModel(String title, String subtitle){
        this.title = title;
        this.subtitle = subtitle;

        logger.info("New grid model for " + title + " was successfully created");

    }



    public List<AComponent> getData() {
        return data;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSubTitle() {
        return subtitle;
    }


    @Override
    public void addData(AComponent... dataItem) {
        data.addAll(Arrays.asList(dataItem));
    }
}