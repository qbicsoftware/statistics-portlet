package life.qbic.portal.io;

import java.io.InputStream;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import submodule.data.MainConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * @author fhanssen
 * Read given config file. DON'T change the DumperOptions, unless you are absolutely sure of what you are doing.
 * This has to be synced with the Writer in the data portlet.
 */
public final class YAMLParser {

    private final static Logger logger = LogManager.getLogger(YAMLParser.class);


    /**
     * Loads configuration from the given path, which is then loaded as a resource stream.
     * @param resourceLocation
     * @return
     */
    public static MainConfig parseConfig(final String resourceLocation) {
        logger.info("Parsing file " + resourceLocation + "...");
        return parseConfig(YAMLParser.class.getResourceAsStream(resourceLocation));
    }

    /**
     * Loads configuration from the given input stream.
     * @param inputStream
     * @return
     */
    public static MainConfig parseConfig(final InputStream inputStream) {

        logger.info("Parsing from input stream ");

        DumperOptions options = new DumperOptions();

        //this option needs to agree with the respective option set in the writer
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        Yaml yaml = new Yaml(options);

        return yaml.loadAs(inputStream, MainConfig.class);
    }
}


