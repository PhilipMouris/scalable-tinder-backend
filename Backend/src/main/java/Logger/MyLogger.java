package Logger;

import Config.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.*;

public class MyLogger {

    private Config config = Config.getInstance();
    // initialize the configurations to create a logger for a micro-service
    public void initialize() {

        try {
            // set the logging properties from a config file
            LogManager.getLogManager().readConfiguration(new FileInputStream(config.getLoggerPropsPath()));
            Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
            logger.setLevel(Level.INFO);
            String path = config.getLoggerPath();
            // create the directory when we will record the logs information
            File file = new File(path);
            file.mkdir();
//            Handler fileHandler = new FileHandler(path+"/logs.log", 100000, 1);
//            logger.addHandler(fileHandler);
//            logger.addHandler(new ConsoleHandler());

        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
    }
    // change the directory against set_log_path command
//    public void setLog_path(String log_path) {
//        this.log_path = log_path;
//        initialize(MyLogger,log_path);
//    }

//    public String getLog_path() {
//        return log_path;
//    }
}
