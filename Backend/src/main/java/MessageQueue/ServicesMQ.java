package MessageQueue;

import Config.Config;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class ServicesMQ {

    private Config config = Config.getInstance();

    private final String HOST = config.getServicesMqQueueHost();
    private final int PORT = config.getServicesMqQueuePort();
    private final String instanceUser = config.getServicesMqQueueUserName();
    private final String instancePassword = config.getServicesMqQueuePass();
    private final String USER_QUEUE_NAME = config.getServicesMqUserQueue();
    private final String USER_TO_USER_QUEUE_NAME = config.getServicesMqUserToUserQueue();
    private final String MODERATOR_QUEUE_NAME = config.getServicesMqModeratorQueue();
    private final String CHAT_QUEUE_NAME = config.getServicesMqChatQueue();

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
    private final String LOAD_USER_TO_USER_QUEUE_NAME = config.getLoadBalancerUserToUserQueue() + LOAD_BALANCER_EXTENSION;
    private final String LOAD_MODERATOR_QUEUE_NAME = config.getLoadBalancerModeratorQueue() + LOAD_BALANCER_EXTENSION;
    private final String LOAD_CHAT_QUEUE_NAME = config.getLoadBalancerChatQueue() + LOAD_BALANCER_EXTENSION;

    private final HashMap<String, Channel> LOAD_CHANNEL_MAP = new HashMap<>();
    private final HashMap<String, Channel> REQUEST_CHANNEL_MAP = new HashMap<>();

    public ServicesMQ() {
        establishConsumeConnections();
//        establishProduceConnections();
    }

    public void start(){
        consumeFromQueue(LOAD_CHAT_QUEUE_NAME, CHAT_QUEUE_NAME);
        consumeFromQueue(LOAD_MODERATOR_QUEUE_NAME, MODERATOR_QUEUE_NAME);
        consumeFromQueue(LOAD_USER_QUEUE_NAME, USER_QUEUE_NAME);
        consumeFromQueue(LOAD_USER_TO_USER_QUEUE_NAME, USER_TO_USER_QUEUE_NAME);

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
            final Channel moderatorChannel = connection.createChannel();
            final Channel userChannel = connection.createChannel();
            final Channel userToUserChannel = connection.createChannel();
            final Channel chatChannel = connection.createChannel();

            userChannel.queueDeclare(LOAD_USER_QUEUE_NAME, true, false, false, null);
            userChannel.basicQos(5);
            moderatorChannel.queueDeclare(LOAD_MODERATOR_QUEUE_NAME, true, false, false, null);
            moderatorChannel.basicQos(5);
            chatChannel.queueDeclare(LOAD_CHAT_QUEUE_NAME, true, false, false, null);
            chatChannel.basicQos(5);
            userToUserChannel.queueDeclare(LOAD_USER_TO_USER_QUEUE_NAME, true, false, false, null);
            userToUserChannel.basicQos(5);

            LOAD_CHANNEL_MAP.put(LOAD_USER_QUEUE_NAME, userChannel);
            LOAD_CHANNEL_MAP.put(LOAD_MODERATOR_QUEUE_NAME, moderatorChannel);
            LOAD_CHANNEL_MAP.put(LOAD_CHAT_QUEUE_NAME, chatChannel);
            LOAD_CHANNEL_MAP.put(LOAD_USER_TO_USER_QUEUE_NAME, userToUserChannel);


            final Channel moderatorChannel_MQ = connection.createChannel();
            final Channel userChannel_MQ = connection.createChannel();
            final Channel chatChannel_MQ = connection.createChannel();
            final Channel userToUserChannel_MQ = connection.createChannel();

            userChannel_MQ.queueDeclare(USER_QUEUE_NAME, true, false, false, null);
            moderatorChannel_MQ.queueDeclare(MODERATOR_QUEUE_NAME, true, false, false, null);
            chatChannel_MQ.queueDeclare(CHAT_QUEUE_NAME, true, false, false, null);
            userToUserChannel_MQ.queueDeclare(USER_TO_USER_QUEUE_NAME,true,false,false,null);

            REQUEST_CHANNEL_MAP.put(USER_QUEUE_NAME, userChannel_MQ);
            REQUEST_CHANNEL_MAP.put(MODERATOR_QUEUE_NAME, moderatorChannel_MQ);
            REQUEST_CHANNEL_MAP.put(CHAT_QUEUE_NAME, chatChannel_MQ);
            REQUEST_CHANNEL_MAP.put(USER_TO_USER_QUEUE_NAME,userToUserChannel_MQ);
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
            final Channel moderatorChannel_MQ = connection.createChannel();
            final Channel userChannel_MQ = connection.createChannel();
            final Channel chatChannel_MQ = connection.createChannel();
            final Channel userToUserChannel_MQ = connection.createChannel();

            userChannel_MQ.queueDeclare(USER_QUEUE_NAME, true, false, false, null);
            moderatorChannel_MQ.queueDeclare(MODERATOR_QUEUE_NAME, true, false, false, null);
            chatChannel_MQ.queueDeclare(CHAT_QUEUE_NAME, true, false, false, null);
            userToUserChannel_MQ.queueDeclare(USER_TO_USER_QUEUE_NAME,true,false,false,null);

            REQUEST_CHANNEL_MAP.put(USER_QUEUE_NAME, userChannel_MQ);
            REQUEST_CHANNEL_MAP.put(MODERATOR_QUEUE_NAME, moderatorChannel_MQ);
            REQUEST_CHANNEL_MAP.put(CHAT_QUEUE_NAME, chatChannel_MQ);
            REQUEST_CHANNEL_MAP.put(USER_TO_USER_QUEUE_NAME,userToUserChannel_MQ);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] argv) {
//        new MQinstance().start();
//    }
}
