package Services;


//import Cache.UserCacheController;
import Database.ArangoInstance;
import Interface.ServiceControl;
import MediaServer.MinioInstance;

import java.io.PrintWriter;
import java.io.StringWriter;

public class UserToUserService extends ServiceControl {

    public UserToUserService(int ID) {
        super(ID);
    }

    @Override
    public void init() {
        RPC_QUEUE_NAME = conf.getServicesMqUserToUserQueue();

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
    public void setDBConnections(int connections) {
        // TODO @soudian
    }


//    public static void main(String[] argv) {
//        new UserService();
//    }

}



