package life.qbic.portal.io;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import submodule.data.MainConfig;

import java.io.FileInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * @author fhanssen
 * Read given config file. DON'T change the DumperOptions, unless you are absolutely sure of what you are doing.
 * This has to be synced with the Writer in the data portlet.
 */
public final class YAMLParser {

    private final static Logger logger = LogManager.getLogger(YAMLParser.class);


    public static MainConfig parseConfig(String inputFile) {

        logger.info("Parse file " + inputFile + "...");

        DumperOptions options = new DumperOptions();

        //this option needs to agree with the respective option set in the writer
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        Yaml yaml = new Yaml(options);

        return yaml.loadAs(YAMLParser.class.getResourceAsStream(inputFile), MainConfig.class);
    }

    public static MainConfig parseConfig(FileInputStream inputStream, String inputFilename){

        logger.info("Parse file " + inputFilename + "...");

        DumperOptions options = new DumperOptions();

        //this option needs to agree with the respective option set in the writer
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        Yaml yaml = new Yaml(options);

        return yaml.loadAs(inputStream, MainConfig.class);
    }
}


