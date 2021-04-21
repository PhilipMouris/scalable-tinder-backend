package Config;

//import Controller.Controller;

import java.io.*;
import java.util.Properties;

public class Config {

    private static Config instance = new Config();

    private final Properties arangoConfig = new Properties();
    private final Properties controllerConfig = new Properties();
    private final Properties loadBalancerConfig = new Properties();
    private final Properties mediaServerConfig = new Properties();
    private final Properties mqInstanceConfig = new Properties();
    private final Properties serviceConfig = new Properties();
    private final Properties webServerConfig = new Properties();

    private final String arangoPath = "/home/vm/Desktop/scalable-tinder/Backend/src/main/resources/arango.conf";
    private final String controllerPath = "/home/vm/Desktop/scalable-tinder/Backend/src/main/resources/controller.conf";
    private final String loadBalancerPath = "/home/vm/Desktop/scalable-tinder/Backend/src/main/resources/load.balancer.conf";
    private final String mediaServerPath = "/home/vm/Desktop/scalable-tinder/Backend/src/main/resources/media.server.conf";
    private final String mqInstancePath = "/home/vm/Desktop/scalable-tinder/Backend/src/main/resources/mq.instance.conf";
    private final String servicePath = "/home/vm/Desktop/scalable-tinder/Backend/src/main/resources/service.conf";
    private final String webServerPath = "/home/vm/Desktop/scalable-tinder/Backend/src/main/resources/web.server.conf";

    private Config() {
        loadConfig(arangoConfig, arangoPath);
        loadConfig(controllerConfig, controllerPath);
        loadConfig(loadBalancerConfig, loadBalancerPath);
        loadConfig(mediaServerConfig, mediaServerPath);
        loadConfig(mqInstanceConfig, mqInstancePath);
        loadConfig(serviceConfig, servicePath);
        loadConfig(webServerConfig, webServerPath);
//        readSystemVariables(loadBalancerConfig,"load_balancer_rabbitmq_host");
    }

    private void loadConfig(Properties config, String path){
        try {
            FileInputStream file = new FileInputStream(path);
            config.load(file);
            file.close();
        } catch (IOException e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
//            Controller.logger.error(errors);
            e.printStackTrace();
        }
    }

    public static void readSystemVariables(Properties conf, String param){
        System.out.println(System.getenv(param));
        if(System.getenv(param) != null)
            conf.setProperty(param, System.getenv(param));
    }

    public static Config getInstance(){
        return instance;
    }

    public void setProperty(ConfigTypes config, String key, String val){
        Properties props = null;
        String path = null;
        switch (config){
            case Arango:
                props = arangoConfig;
                path = arangoPath;
                break;
            case Controller:
                props = controllerConfig;
                path = controllerPath;
                break;
            case LoadBalancer:
                props = loadBalancerConfig;
                path = loadBalancerPath;
                break;
            case MediaServer:
                props = mediaServerConfig;
                path = mediaServerPath;
                break;
            case MqInstance:
                props = mqInstanceConfig;
                path = mqInstancePath;
                break;
            case PostSql:
//                props = postSqlConf;
//                path = postSqlPath;
                break;
            case Service:
                props = serviceConfig;
                path = servicePath;
                break;
            case WebServer:
                props = webServerConfig;
                path = webServerPath;
                break;
        }
        props.setProperty(key,val);
        writeConfig(props, path);
    }

    private void writeConfig(Properties config, String path){
        OutputStream out;
        try {
            out = new FileOutputStream(path, false);
            config.store(out, "");
            out.close();
        } catch (IOException e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
//            Controller.logger.error(errors);
            e.printStackTrace();
        }
    }


    //Web WebServer Configs

    public int getWebServerPort() {
        return Integer.parseInt(webServerConfig.getProperty("server_port"));
    }

    public String getServerQueueHost() {
        return webServerConfig.getProperty("server_rabbitmq_host");
    }

    public int getServerQueuePort() {
        return Integer.parseInt(webServerConfig.getProperty("server_rabbitmq_port"));
    }

    public String getServerQueueUserName() {
        return webServerConfig.getProperty("server_rabbitmq_username");
    }

    public String getServerQueuePass() {
        return webServerConfig.getProperty("server_rabbitmq_password");
    }

    public String getServerQueueName() {
        return webServerConfig.getProperty("server_rabbitmq_queue");
    }


    //LoadBalancer Configs

    public String getLoadBalancerQueueHost() {
        return loadBalancerConfig.getProperty("load_balancer_rabbitmq_host");
    }

    public int getLoadBalancerQueuePort() {
        return Integer.parseInt(loadBalancerConfig.getProperty("load_balancer_rabbitmq_port"));
    }

    public String getLoadBalancerQueueUserName() {
        return loadBalancerConfig.getProperty("load_balancer_rabbitmq_username");
    }

    public String getLoadBalancerQueuePass() {
        return loadBalancerConfig.getProperty("load_balancer_rabbitmq_password");
    }

    public String getLoadBalancerQueueName() {
        return loadBalancerConfig.getProperty("load_balancer_rabbitmq_queue");
    }

    public String getLoadBalancerUserQueue() {
        return loadBalancerConfig.getProperty("load_balancer_rabbitmq_user_queue");
    }

    public String getLoadBalancerPostQueue() {
        return loadBalancerConfig.getProperty("load_balancer_rabbitmq_post_queue");
    }

    public String getLoadBalancerChatQueue() {
        return loadBalancerConfig.getProperty("load_balancer_rabbitmq_chat_queue");
    }


    //MediaServer Configs

    public String getMediaServerPath() {
        return mediaServerConfig.getProperty("media_server_file_path");
    }

    public int getMediaServerPort() {
        return Integer.parseInt(mediaServerConfig.getProperty("media_server_port"));
    }

    public int getMediaServerThreads() {
        return Integer.parseInt(mediaServerConfig.getProperty("media_server_threads"));
    }


    //MqInstance Configs

    public String getMqInstanceQueueHost() {
        return mqInstanceConfig.getProperty("mq_instance_rabbitmq_host");
    }

    public int getMqInstanceQueuePort() {
        return Integer.parseInt(mqInstanceConfig.getProperty("mq_instance_rabbitmq_port"));
    }

    public String getMqInstanceQueueUserName() {
        return mqInstanceConfig.getProperty("mq_instance_rabbitmq_username");
    }

    public String getMqInstanceQueuePass() {
        return mqInstanceConfig.getProperty("mq_instance_rabbitmq_password");
    }

    public String getMqInstanceUserQueue() {
        return mqInstanceConfig.getProperty("mq_instance_rabbitmq_user_queue");
    }

    public String getMqInstancePostQueue() {
        return mqInstanceConfig.getProperty("mq_instance_rabbitmq_post_queue");
    }

    public String getMqInstanceChatQueue() {
        return mqInstanceConfig.getProperty("mq_instance_rabbitmq_chat_queue");
    }


    //Controller Configs

    public String getControllerHost() {
        return controllerConfig.getProperty("controller_host");
    }

    public int getControllerPort() {
        return Integer.parseInt(controllerConfig.getProperty("controller_port"));
    }


    //Service Configs

   public int getServiceMaxThreads() {
        return Integer.parseInt(serviceConfig.getProperty("service_max_thread"));
    }

    public int getServiceMaxDbConnections() {
        return Integer.parseInt(serviceConfig.getProperty("service_max_db"));
    }


    //Arango Configs

    public String getArangoUserName() {
        return arangoConfig.getProperty("arango_username");
    }

    public String getArangoHost() {
        return arangoConfig.getProperty("arango_host");
    }

    public int getArangoPort() {
        return Integer.parseInt(arangoConfig.getProperty("arango_port"));
    }

    public String getArangoQueuePass() {
        return arangoConfig.getProperty("arango_password");
    }

    public String getArangoPostDbName() {
        return arangoConfig.getProperty("arango_post_db_name");
    }

    public String getArangoChatDbName() {
        return arangoConfig.getProperty("arango_chat_db_name");
    }

}
