package MessageQueue;

import Config.Config;

import com.rabbitmq.client.*;


import java.io.IOException;

import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ServicesMQ {

    private Config config = Config.getInstance();


    private final String RESPONSE_EXTENSION = "-" +"Response";
    private final String RESPONSE_USER_QUEUE_NAME = config.getServicesMqUserQueue() + RESPONSE_EXTENSION;
    private final String RESPONSE_USER_TO_USER_QUEUE_NAME = config.getServicesMqUserToUserQueue()+ RESPONSE_EXTENSION;
    private final String RESPONSE_MODERATOR_QUEUE_NAME = config.getServicesMqModeratorQueue()+ RESPONSE_EXTENSION;
    private final String RESPONSE_CHAT_QUEUE_NAME = config.getServicesMqChatQueue()+ RESPONSE_EXTENSION;

    private final String REQUEST_EXTENSION = "-" +"Request";
    private final String REQUEST_USER_QUEUE_NAME = config.getServicesMqUserQueue() + REQUEST_EXTENSION;
    private final String REQUEST_USER_TO_USER_QUEUE_NAME = config.getServicesMqUserToUserQueue()+ REQUEST_EXTENSION;
    private final String REQUEST_MODERATOR_QUEUE_NAME = config.getServicesMqModeratorQueue()+ REQUEST_EXTENSION;
    private final String REQUEST_CHAT_QUEUE_NAME = config.getServicesMqChatQueue()+ REQUEST_EXTENSION;

    private final String balancerHost = config.getServerQueueHost();
    private final int balancerPort = config.getServerQueuePort();
    private final String balancerUser = config.getServerQueueUserName();
    private final String balancerPass = config.getServerQueuePass();

    private final Logger LOGGER = Logger.getLogger(ServicesMQ.class.getName()) ;



    public ServicesMQ() {
        establishAllQueues();
    }



    private void establishAllQueues() {
        ConnectionFactory LoadFactory = new ConnectionFactory();
        LoadFactory.setHost(balancerHost);
        LoadFactory.setPort(balancerPort);
        LoadFactory.setUsername(balancerUser);
        LoadFactory.setPassword(balancerPass);
        Connection connection;
        try {
            connection = LoadFactory.newConnection();



            final Channel moderatorChannel_MQ = connection.createChannel();
            final Channel userChannel_MQ = connection.createChannel();
            final Channel chatChannel_MQ = connection.createChannel();
            final Channel userToUserChannel_MQ = connection.createChannel();

            userChannel_MQ.queueDeclare(REQUEST_USER_QUEUE_NAME, true, false, false, null);
            moderatorChannel_MQ.queueDeclare(REQUEST_MODERATOR_QUEUE_NAME, true, false, false, null);
            chatChannel_MQ.queueDeclare(REQUEST_CHAT_QUEUE_NAME, true, false, false, null);
            userToUserChannel_MQ.queueDeclare(REQUEST_USER_TO_USER_QUEUE_NAME,true,false,false,null);

            final Channel moderatorChannel_res = connection.createChannel();
            final Channel userChannel_res = connection.createChannel();
            final Channel chatChannel_res = connection.createChannel();
            final Channel userToUserChannel_res = connection.createChannel();
            userChannel_res.queueDeclare(RESPONSE_USER_QUEUE_NAME, true, false, false, null);
            moderatorChannel_res.queueDeclare(RESPONSE_MODERATOR_QUEUE_NAME, true, false, false, null);
            chatChannel_res.queueDeclare(RESPONSE_CHAT_QUEUE_NAME, true, false, false, null);
            userToUserChannel_res.queueDeclare(RESPONSE_USER_TO_USER_QUEUE_NAME,true,false,false,null);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();LOGGER.log(Level.SEVERE,e.getMessage(),e);
        }
    }


    public static void main(String[] argv) {
        ServicesMQ mq = new ServicesMQ();
        System.out.println("QUEUES CREATED SUCCESSFULLY");
    }
}
