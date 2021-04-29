package Services;


//import Cache.UserCacheController;
import Database.ArangoInstance;
import Interface.ServiceControl;
import MediaServer.MinioInstance;

import java.io.PrintWriter;
import java.io.StringWriter;

public class UserService extends ServiceControl {

    public UserService(int ID) {
        super(ID);
    }

    @Override
    public void init() {
        RPC_QUEUE_NAME = conf.getServicesMqUserQueue();

    }

    @Override
    public void initDB() {
        try {
                arangoInstance=new ArangoInstance(15);
                minioInstance =new MinioInstance();

        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
//            Controller.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR, errors.toString()));
            e.printStackTrace();
        }
    }

    @Override
    public void setDBConnections(int connections) {
        // TODO @soudian
    }


//    public static void main(String[] argv) {
//        new UserService();
//    }

}



