package Services;

//import Database.ChatArangoInstance;
import Interface.ServiceControl;

public class ChatService extends ServiceControl{

    @Override
    public void init() {
        RPC_QUEUE_NAME = conf.getServicesMqChatQueue();
        System.out.println(RPC_QUEUE_NAME);
    }

    @Override
    public void initDB() {
        //ChatArangoInstance = new ChatArangoInstance(maxDBConnections);
    }

    @Override
    public void setDBConnections(int connections){
        this.maxDBConnections = connections;
//        ChatArangoInstance.setMaxDBConnections(maxDBConnections);
    }

    public static void main(String[] args) {
        new ChatService().start();
    }
}
