package Interface;

//import Cache.RedisConf;
//import Cache.UserCacheController;
//import ClientService.Client;
import Controller.Controller;
import Database.ArangoInstance;
import Entities.ErrorLog;
import Config.Config;
import Config.ConfigTypes;
//import Database.ArangoInstance;
//import Database.ChatArangoInstance;
//import Models.CategoryDBObject;
//import Models.ErrorLog;
//import Models.PostDBObject;
import NettyWebServer.NettyServerInitializer;
import com.rabbitmq.client.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

import static io.netty.buffer.Unpooled.copiedBuffer;

public abstract class ServiceControl {    // This class is responsible for Managing Each Service (Application) Fully (Queue (Reuqest/Response), Controller etc.)

    protected Config conf = Config.getInstance();
    protected int maxDBConnections = conf.getServiceMaxDbConnections();
    protected String RPC_QUEUE_NAME; //set by init
    private String RESPONSE_EXTENSION = "-Response";
    private String REQUEST_EXTENSION = "-Request";
    public int ID;
//    RedisConf redisConf ;
//    protected RLiveObjectService liveObjectService; // For Post Only
    protected ArangoInstance arangoInstance; // For Post Only
//    protected ChatArangoInstance ChatArangoInstance;
//    protected UserCacheController userCacheController; // For UserModel Only
    private int threadsNo = conf.getServiceMaxThreads();
    private ThreadPoolExecutor executor; //Executes the requests of this service

    private String host = conf.getServerQueueHost();    // Define the Queue containing the requests of this service
    private int port = conf.getServerQueuePort();
    private String user = conf.getServerQueueUserName();
    private String pass = conf.getServerQueuePass();
    private Channel requestQueueChannel; //The Channel containing the Queue of this service
    private Channel responseQueueChannel;

    private String requestConsumerTag;
    private String responseConsumerTag;
    private Consumer requestConsumer;
    private Consumer responseConsumer;
    private String REQUEST_QUEUE_NAME;
    private String RESPONSE_QUEUE_NAME;

    public ServiceControl(int ID) {
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadsNo);
        init();
        initDB();
        REQUEST_QUEUE_NAME = RPC_QUEUE_NAME+ REQUEST_EXTENSION;
        RESPONSE_QUEUE_NAME = RPC_QUEUE_NAME+ RESPONSE_EXTENSION;
        this.ID = ID;
    }

    public abstract void init();

    public abstract void initDB();

    public void start() {
        consumeFromRequestQueue();
        consumeFromResponseQueue();
    }

    public void setMaxDBConnections(int connections) {
        setDBConnections(connections);
        conf.setProperty(ConfigTypes.Service, "service.max.db", String.valueOf(connections));

    }
    private void consumeFromResponseQueue(){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(user);
        factory.setPassword(pass);
        Connection connection = null;

        try {
            connection = factory.newConnection();
            responseQueueChannel = connection.createChannel();
            responseQueueChannel.queueDeclare(RESPONSE_QUEUE_NAME, true, false, false, null);
            responseQueueChannel.basicQos(threadsNo);
            System.out.println(" [x] Awaiting RPC RESPONSES on Queue : " + RESPONSE_QUEUE_NAME);
            responseConsumer = new DefaultConsumer(responseQueueChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                    try {
                        //Using Reflection to convert a command String to its appropriate class
//                        Channel receiver = REQUEST_CHANNEL_MAP.get(RESPONSE_MAIN_QUEUE_NAME);

                        System.out.println("Responding to corrID: "+ properties.getCorrelationId() +  ", on Queue : " + RESPONSE_QUEUE_NAME);
                        System.out.println("Request    :   " + new String(body, "UTF-8"));
                        System.out.println("Application    :   " + RPC_QUEUE_NAME);
                        System.out.println("INSTANCE NUM   :   " + ID);
                        System.out.println();
                        String responseMsg = new String(body, "UTF-8");

                        org.json.JSONObject responseJson = new org.json.JSONObject(responseMsg);

                        FullHttpResponse response = new DefaultFullHttpResponse(
                                HttpVersion.HTTP_1_1,
                                HttpResponseStatus.OK,
                                copiedBuffer(responseJson.get("response").toString().getBytes()));

                        org.json.JSONObject headers = (org.json.JSONObject) responseJson.get("Headers");
                        Iterator<String> keys = headers.keys();

                        while (keys.hasNext()) {
                            String key = keys.next();
                            String value = (String) headers.get(key);
                            response.headers().set(key, value);
                        }

                        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
                        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

                        System.out.println("Response   :   " + responseJson.get("response"));
                        System.out.println();

                        ChannelHandlerContext ctxRec = NettyServerInitializer.getUuid().remove(properties.getCorrelationId());
                        ctxRec.writeAndFlush(response);
                        ctxRec.close();

                    } catch (RuntimeException| IOException e) {
                        e.printStackTrace();
                        consumeFromResponseQueue();
                    } finally {
                        synchronized (this) {
                            this.notify();
                        }
                    }
                }
            };
            responseConsumerTag = responseQueueChannel.basicConsume(RESPONSE_QUEUE_NAME, true, responseConsumer);
            // Wait and be prepared to consume the message from RPC client.
        } catch (Exception e) {
            e.printStackTrace();
//            consumeFromQueue(RPC_QUEUE_NAME,QUEUE_TO);
        }
    }
    private void consumeFromRequestQueue(){

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(user);
        factory.setPassword(pass);
        Connection connection = null;

        try {
            connection = factory.newConnection();
            requestQueueChannel = connection.createChannel();
            requestQueueChannel.queueDeclare(REQUEST_QUEUE_NAME, true, false, false, null);
            requestQueueChannel .basicQos(threadsNo);
//            redisConf = new RedisConf();
//            liveObjectService = redisConf.getService();
//
//            Controller.channel.writeAndFlush(new ErrorLog(LogLevel.INFO, " [x] Awaiting RPC requests on Queue : " + RPC_QUEUE_NAME));
            System.out.println(" [x] Awaiting RPC requests on Queue : " + REQUEST_QUEUE_NAME);
            requestConsumer = new DefaultConsumer(requestQueueChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                            .Builder()
                            .correlationId(properties.getCorrelationId())
                            .replyTo(RESPONSE_QUEUE_NAME)
                            .build();
//                    Controller.channel.writeAndFlush(new ErrorLog(LogLevel.INFO, "Responding to corrID: " + properties.getCorrelationId() + ", on Queue : " + RPC_QUEUE_NAME));
                    System.out.println("Responding to corrID: " + properties.getCorrelationId() + ", on Queue : " + REQUEST_QUEUE_NAME);
                    System.out.println("INSTANCE NUM   :   " + ID);
                    try {
                        //Using Reflection to convert a command String to its appropriate class
                        String message = new String(body, "UTF-8");
                        JSONParser parser = new JSONParser();
                        JSONObject command = (JSONObject) parser.parse(message);
                        String className = (String) command.get("command");
                        System.out.println("className:"+className);
                        Class com = Class.forName("Commands."+RPC_QUEUE_NAME + "Commands." + className);
                        Command cmd = (Command) com.newInstance();

                        TreeMap<String, Object> init = new TreeMap<>();
                        init.put("channel",requestQueueChannel);
                        init.put("properties", properties);
                        init.put("replyProps", replyProps);
                        init.put("envelope", envelope);
                        init.put("body", message);
//                        init.put("RLiveObjectService", liveObjectService);
                        init.put("ArangoInstance", arangoInstance);
//                        init.put("ChatArangoInstance", ChatArangoInstance);
//                        init.put("UserCacheController", userCacheController);
                        cmd.init(init);
                        executor.submit(cmd);
                    } catch (RuntimeException | ParseException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                        e.printStackTrace();
                        StringWriter errors = new StringWriter();
                        e.printStackTrace(new PrintWriter(errors));
                        Controller.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR, errors.toString()));
                        start();
                    } finally {
                        synchronized (this) {
                            this.notify();
                        }
                    }
                }
            };
            requestConsumerTag = requestQueueChannel.basicConsume(REQUEST_QUEUE_NAME, true, requestConsumer);


        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            Controller.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR, errors.toString()));
//            start();
        }
    }
    protected abstract void setDBConnections(int connections);

    public void setMaxThreadsSize(int threads) {
        threadsNo = threads;
        executor.setMaximumPoolSize(threads);
        conf.setProperty(ConfigTypes.Service, "service.max.thread", String.valueOf(threads));
    }

    public void resume() {
        try {
            requestConsumerTag = requestQueueChannel.basicConsume(REQUEST_QUEUE_NAME, false, requestConsumer);
            responseConsumerTag =responseQueueChannel.basicConsume(RESPONSE_QUEUE_NAME, false, responseConsumer);
        } catch (IOException e) {
            e.printStackTrace();
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            Controller.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR, errors.toString()));
        }
//        Controller.channel.writeAndFlush(new ErrorLog(LogLevel.INFO, "Service Resumed"));
    }

    public void freeze() {
        try {
            requestQueueChannel.basicCancel(requestConsumerTag);
            responseQueueChannel.basicCancel(responseConsumerTag);
            System.out.println("FREEZING");
        } catch (IOException e) {
            e.printStackTrace();
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
//            Controller.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR, errors.toString()));
        }
//        Controller.channel.writeAndFlush(new ErrorLog(LogLevel.INFO, "Service Freezed"));
    }

    //TODO CHECK IF FILE EXISTS FIRST IF THERE THEN LOG AN ERROR
    public void add_command(String commandName, String source_code) {
        FileWriter fileWriter;
        try {
            File idea = new File("/target/classes/" + RPC_QUEUE_NAME + "Commands/" + commandName + ".class");
            if (idea.exists()) {
//                Controller.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR, commandName + " Already exists please use update"));
                return;
            }
            fileWriter = new FileWriter(idea);
            BufferedWriter bufferedWriter =
                    new BufferedWriter(fileWriter);
            bufferedWriter.write(source_code);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            Controller.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR, errors.toString()));
        }

    }

    public boolean delete_command(String commandName) {
        try {
            Files.deleteIfExists(Paths.get("/target/classes/" + RPC_QUEUE_NAME + "Commands/" + commandName + ".class"));
        } catch (NoSuchFileException e) {
            Controller.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR, "No such file/directory exists"));
            return false;
        } catch (DirectoryNotEmptyException e) {
            Controller.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR, "Directory is not empty."));
            return false;
        } catch (IOException e) {
            Controller.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR, "Invalid permissions."));
            return false;
        }
//        Controller.channel.writeAndFlush(new ErrorLog(LogLevel.INFO, "Deletion successful."));
        return true;
    }

    public void update_command(String commandName, String filePath) {
        if(delete_command(commandName))
            add_command(commandName, filePath);
    }

    public void setArangoInstance(ArangoInstance arangoInstance) {
        this.arangoInstance = arangoInstance;
    }

    public void createNoSQLDB(){

        arangoInstance.initializeDB();
    }

    public void dropNoSQLDB(){

        arangoInstance.dropDB();
    }
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

}
