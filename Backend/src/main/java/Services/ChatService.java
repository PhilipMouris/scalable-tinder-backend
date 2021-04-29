package Services;

//import Database.ChatArangoInstance;
import Database.ArangoInstance;
import Interface.ServiceControl;
import MediaServer.MinioInstance;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ChatService extends ServiceControl{

    public ChatService(int ID) {
        super(ID);
    }

    @Override
    public void init() {
        RPC_QUEUE_NAME = conf.getServicesMqChatQueue();
        System.out.println(RPC_QUEUE_NAME);
    }

    @Override
    public void initDB() {
        try {
            minioInstance =new MinioInstance();
            arangoInstance=new ArangoInstance(15);
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
//            Controller.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR, errors.toString()));
            e.printStackTrace();
        }
    }

    @Override
    public void setDBConnections(int connections){
        this.maxDBConnections = connections;
//        ChatArangoInstance.setMaxDBConnections(maxDBConnections);
    }

//    public static void main(String[] args) {
//        new ChatService().start();
//    }
}
