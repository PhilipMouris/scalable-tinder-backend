package MessageQueue;

import Config.Config;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class ServicesMQ {

    private Config config = Config.getInstance();

    private final String HOST = config.getMqInstanceQueueHost();
    private final int PORT = config.getMqInstanceQueuePort();
    private final String instanceUser = config.getMqInstanceQueueUserName();
    private final String instancePassword = config.getMqInstanceQueuePass();
    private final String USER_QUEUE_NAME = config.getMqInstanceUserQueue();
    private final String POST_QUEUE_NAME = config.getMqInstancePostQueue();
    private final String CHAT_QUEUE_NAME = config.getMqInstanceChatQueue();

    private final String LOAD_BALANCER_EXTENSION = "-" +config.getLoadBalancerQueueName();
//    private final String balancerHost = config.getLoadBalancerQueueHost();
//    private final int balancerPort = config.getLoadBalancerQueuePort();
//    private final String balancerUser = config.getLoadBalancerQueueUserName();
//    private final String balancerPass = config.getLoadBalancerQueuePass();
    private final String balancerHost = config.getServerQueueHost();
    private final int balancerPort = config.getServerQueuePort();
    private final String balancerUser = config.getServerQueueUserName();
    private final String balancerPass = config.getServerQueuePass();
    private final String LOAD_USER_QUEUE_NAME = config.getLoadBalancerUserQueue() + LOAD_BALANCER_EXTENSION;
    private final String LOAD_POST_QUEUE_NAME = config.getLoadBalancerPostQueue() + LOAD_BALANCER_EXTENSION;
    private final String LOAD_CHAT_QUEUE_NAME = config.getLoadBalancerChatQueue() + LOAD_BALANCER_EXTENSION;

    private final HashMap<String, Channel> LOAD_CHANNEL_MAP = new HashMap<>();
    private final HashMap<String, Channel> REQUEST_CHANNEL_MAP = new HashMap<>();

    public ServicesMQ() {
        establishConsumeConnections();
//        establishProduceConnections();
    }

    public void start(){
        consumeFromQueue(LOAD_CHAT_QUEUE_NAME, CHAT_QUEUE_NAME);
        consumeFromQueue(LOAD_POST_QUEUE_NAME, POST_QUEUE_NAME);
        consumeFromQueue(LOAD_USER_QUEUE_NAME, USER_QUEUE_NAME);
    }

    private void consumeFromQueue(String RPC_QUEUE_NAME, String QUEUE_TO){

        try {
            Channel balancer = LOAD_CHANNEL_MAP.get(RPC_QUEUE_NAME);
            System.out.println(" [x] Awaiting RPC requests on Queue : " + RPC_QUEUE_NAME);
            Consumer consumer = new DefaultConsumer(balancer) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                    try {
                        //Using Reflection to convert a command String to its appropriate class
                        Channel receiver = REQUEST_CHANNEL_MAP.get(QUEUE_TO);

                        System.out.println("Responding to corrID: "+ properties.getCorrelationId() +  ", on Queue : " + RPC_QUEUE_NAME);
                        System.out.println("Request    :   " + new String(body, "UTF-8"));
                        System.out.println("Application    :   " + QUEUE_TO);
                        System.out.println();

                        receiver.basicPublish("", QUEUE_TO, properties, body);

                    } catch (RuntimeException| IOException e) {
                        e.printStackTrace();
                        consumeFromQueue(RPC_QUEUE_NAME,QUEUE_TO);
                    } finally {
                        synchronized (this) {
                            this.notify();
                        }
                    }
                }
            };
            balancer.basicConsume(RPC_QUEUE_NAME, true, consumer);
            // Wait and be prepared to consume the message from RPC client.
        } catch (Exception e) {
            e.printStackTrace();
//            consumeFromQueue(RPC_QUEUE_NAME,QUEUE_TO);
        }
    }

    private void establishConsumeConnections() {
        ConnectionFactory LoadFactory = new ConnectionFactory();
        LoadFactory.setHost(balancerHost);
        LoadFactory.setPort(balancerPort);
        LoadFactory.setUsername(balancerUser);
        LoadFactory.setPassword(balancerPass);
        Connection connection;
        try {
            connection = LoadFactory.newConnection();
            final Channel postChannel = connection.createChannel();
            final Channel userChannel = connection.createChannel();
            final Channel chatChannel = connection.createChannel();

            userChannel.queueDeclare(LOAD_USER_QUEUE_NAME, true, false, false, null);
            userChannel.basicQos(5);
            postChannel.queueDeclare(LOAD_POST_QUEUE_NAME, true, false, false, null);
            postChannel.basicQos(5);
            chatChannel.queueDeclare(LOAD_CHAT_QUEUE_NAME, true, false, false, null);
            postChannel.basicQos(5);

            LOAD_CHANNEL_MAP.put(LOAD_USER_QUEUE_NAME, userChannel);
            LOAD_CHANNEL_MAP.put(LOAD_POST_QUEUE_NAME, postChannel);
            LOAD_CHANNEL_MAP.put(LOAD_CHAT_QUEUE_NAME, chatChannel);

            final Channel postChannel2 = connection.createChannel();
            final Channel userChannel2 = connection.createChannel();
            final Channel chatChannel2 = connection.createChannel();

            userChannel.queueDeclare(USER_QUEUE_NAME, true, false, false, null);
            postChannel.queueDeclare(POST_QUEUE_NAME, true, false, false, null);
            chatChannel.queueDeclare(CHAT_QUEUE_NAME, true, false, false, null);

            REQUEST_CHANNEL_MAP.put(USER_QUEUE_NAME, userChannel2);
            REQUEST_CHANNEL_MAP.put(POST_QUEUE_NAME, postChannel2);
            REQUEST_CHANNEL_MAP.put(CHAT_QUEUE_NAME, chatChannel2);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void establishProduceConnections() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);
        factory.setUsername(instanceUser);
        factory.setPassword(instancePassword);
        Connection connection;
        try {
            connection = factory.newConnection();
            final Channel postChannel = connection.createChannel();
            final Channel userChannel = connection.createChannel();
            final Channel chatChannel = connection.createChannel();

            userChannel.queueDeclare(USER_QUEUE_NAME, true, false, false, null);
            postChannel.queueDeclare(POST_QUEUE_NAME, true, false, false, null);
            chatChannel.queueDeclare(CHAT_QUEUE_NAME, true, false, false, null);

            REQUEST_CHANNEL_MAP.put(USER_QUEUE_NAME, userChannel);
            REQUEST_CHANNEL_MAP.put(POST_QUEUE_NAME, postChannel);
            REQUEST_CHANNEL_MAP.put(CHAT_QUEUE_NAME, chatChannel);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] argv) {
//        new MQinstance().start();
//    }
}
