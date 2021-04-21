//package Interface;
//
//import Cache.RedisConf;
//import Cache.UserCacheController;
//import ClientService.Client;
//import Config.Config;
//import Config.ConfigTypes;
//import Database.ArangoInstance;
//import Database.ChatArangoInstance;
//import Models.CategoryDBObject;
//import Models.ErrorLog;
//import Models.PostDBObject;
//import com.rabbitmq.client.*;
//import io.netty.handler.logging.LogLevel;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
//import org.redisson.api.RLiveObjectService;
//
//import java.io.*;
//import java.nio.file.DirectoryNotEmptyException;
//import java.nio.file.Files;
//import java.nio.file.NoSuchFileException;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.TreeMap;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeoutException;
//
//public abstract class ControlService {
//
//    protected Config conf = Config.getInstance();
//    protected int maxDBConnections = conf.getServiceMaxDbConnections();
//    protected String RPC_QUEUE_NAME; //set by init
//    RedisConf redisConf ;
//    protected RLiveObjectService liveObjectService; // For Post Only
//    protected ArangoInstance arangoInstance; // For Post Only
//    protected ChatArangoInstance ChatArangoInstance;
//    protected UserCacheController userCacheController; // For UserModel Only
//    private int threadsNo = conf.getServiceMaxThreads();
//    private ThreadPoolExecutor executor;
//    private String host = conf.getMqInstanceQueueHost();
//    private int port = conf.getMqInstanceQueuePort();
//    private String user = conf.getMqInstanceQueueUserName();
//    private String pass = conf.getMqInstanceQueuePass();
//    private Channel channel;
//    private String consumerTag;
//    private Consumer consumer;
//
//    public ControlService() {
//        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadsNo);
//        init();
//    }
//
//    public abstract void init();
//
//    public abstract void initDB();
//
//    public void start() {
//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost(host);
//        factory.setPort(port);
//        factory.setUsername(user);
//        factory.setPassword(pass);
//        Connection connection = null;
//
//        try {
//            connection = factory.newConnection();
//            channel = connection.createChannel();
//            channel.queueDeclare(RPC_QUEUE_NAME, true, false, false, null);
//            channel.basicQos(threadsNo);
//            redisConf = new RedisConf();
//            liveObjectService = redisConf.getService();
//
//            Client.channel.writeAndFlush(new ErrorLog(LogLevel.INFO, " [x] Awaiting RPC requests on Queue : " + RPC_QUEUE_NAME));
//            consumer = new DefaultConsumer(channel) {
//                @Override
//                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
//                    AMQP.BasicProperties replyProps = new AMQP.BasicProperties
//                            .Builder()
//                            .correlationId(properties.getCorrelationId())
//                            .build();
//                    Client.channel.writeAndFlush(new ErrorLog(LogLevel.INFO, "Responding to corrID: " + properties.getCorrelationId() + ", on Queue : " + RPC_QUEUE_NAME));
//
//
//                    try {
//                        //Using Reflection to convert a command String to its appropriate class
//                        String message = new String(body, "UTF-8");
//                        JSONParser parser = new JSONParser();
//                        JSONObject command = (JSONObject) parser.parse(message);
//                        String className = (String) command.get("command");
//                        System.out.println("className:"+className);
//                        Class com = Class.forName(RPC_QUEUE_NAME + "Commands." + className);
//                        Command cmd = (Command) com.newInstance();
//
//                        TreeMap<String, Object> init = new TreeMap<>();
//                        init.put("channel", channel);
//                        init.put("properties", properties);
//                        init.put("replyProps", replyProps);
//                        init.put("envelope", envelope);
//                        init.put("body", message);
//                        init.put("RLiveObjectService", liveObjectService);
//                        init.put("ArangoInstance", arangoInstance);
//                        init.put("ChatArangoInstance", ChatArangoInstance);
//                        init.put("UserCacheController", userCacheController);
//                        cmd.init(init);
//                        executor.submit(cmd);
//                    } catch (RuntimeException | ParseException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
//                        e.printStackTrace();
//                        StringWriter errors = new StringWriter();
//                        e.printStackTrace(new PrintWriter(errors));
//                        Client.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR, errors.toString()));
//                        start();
//                    } finally {
//                        synchronized (this) {
//                            this.notify();
//                        }
//                    }
//                }
//            };
//            consumerTag = channel.basicConsume(RPC_QUEUE_NAME, true, consumer);
//
//
//        } catch (IOException | TimeoutException e) {
//            e.printStackTrace();
//            StringWriter errors = new StringWriter();
//            e.printStackTrace(new PrintWriter(errors));
//            Client.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR, errors.toString()));
////            start();
//        }
//    }
//
//    public void setMaxDBConnections(int connections) {
//        setDBConnections(connections);
//        conf.setProperty(ConfigTypes.Service, "service.max.db", String.valueOf(connections));
//
//    }
//
//    protected abstract void setDBConnections(int connections);
//
//    public void setMaxThreadsSize(int threads) {
//        threadsNo = threads;
//        executor.setMaximumPoolSize(threads);
//        conf.setProperty(ConfigTypes.Service, "service.max.thread", String.valueOf(threads));
//    }
//
//    public void resume() {
//        try {
//            consumerTag = channel.basicConsume(RPC_QUEUE_NAME, false, consumer);
//        } catch (IOException e) {
//            e.printStackTrace();
//            StringWriter errors = new StringWriter();
//            e.printStackTrace(new PrintWriter(errors));
//            Client.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR, errors.toString()));
//        }
//        Client.channel.writeAndFlush(new ErrorLog(LogLevel.INFO, "Service Resumed"));
//    }
//
//    public void freeze() {
//        try {
//            channel.basicCancel(consumerTag);
//        } catch (IOException e) {
//            e.printStackTrace();
//            StringWriter errors = new StringWriter();
//            e.printStackTrace(new PrintWriter(errors));
//            Client.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR, errors.toString()));
//        }
//        Client.channel.writeAndFlush(new ErrorLog(LogLevel.INFO, "Service Freezed"));
//    }
//
//    //TODO CHECK IF FILE EXISTS FIRST IF THERE THEN LOG AN ERROR
//    public void add_command(String commandName, String source_code) {
//        FileWriter fileWriter;
//        try {
//            File idea = new File("/target/classes/" + RPC_QUEUE_NAME + "Commands/" + commandName + ".class");
//            if (idea.exists()) {
//                Client.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR, commandName + " Already exists please use update"));
//                return;
//            }
//            fileWriter = new FileWriter(idea);
//            BufferedWriter bufferedWriter =
//                    new BufferedWriter(fileWriter);
//            bufferedWriter.write(source_code);
//            bufferedWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//            StringWriter errors = new StringWriter();
//            e.printStackTrace(new PrintWriter(errors));
//            Client.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR, errors.toString()));
//        }
//
//    }
//
//    public boolean delete_command(String commandName) {
//        try {
//            Files.deleteIfExists(Paths.get("/target/classes/" + RPC_QUEUE_NAME + "Commands/" + commandName + ".class"));
//        } catch (NoSuchFileException e) {
//            Client.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR, "No such file/directory exists"));
//            return false;
//        } catch (DirectoryNotEmptyException e) {
//            Client.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR, "Directory is not empty."));
//            return false;
//        } catch (IOException e) {
//            Client.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR, "Invalid permissions."));
//            return false;
//        }
//        Client.channel.writeAndFlush(new ErrorLog(LogLevel.INFO, "Deletion successful."));
//        return true;
//    }
//
//    public void update_command(String commandName, String filePath) {
//        if(delete_command(commandName))
//            add_command(commandName, filePath);
//    }
//
//    public void setArangoInstance(ArangoInstance arangoInstance) {
//        this.arangoInstance = arangoInstance;
//    }
//
//    public void createPostDB(){
//
//        arangoInstance.initializeDB();
//    }
//
//    public void dropPostDB(){
//
//        arangoInstance.dropDB();
//    }
//
//
//    public void seedPostDB(){
//        ArrayList<String> catid = new ArrayList<String>();
//        ArrayList<String> postid = new ArrayList<String>();
//        for(int i=0;i<2;i++){
//            CategoryDBObject cat = new CategoryDBObject("category"+i,new ArrayList<>());
//            arangoInstance.insertNewCategory(cat);
//            catid.add(cat.getId());
//        }
//        for(int i=0;i<10;i++){
//            PostDBObject post= new PostDBObject("Kefa7y"+i,new ArrayList<>(), new ArrayList<>(), "45b1f6ff-cc8a-43e7-bb6d-6f96d9b9f3a1.jpeg");
//            arangoInstance.insertNewPost(post);
//            arangoInstance.addNewPostToCategory(catid.get(i%2),post.getId());
//            postid.add(post.getId());
//        }
//        Client.channel.writeAndFlush(new ErrorLog(LogLevel.INFO,"Database seeded: Post"));
//
//    }
//
//}
