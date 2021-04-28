package Services;


//import Cache.UserCacheController;
import Interface.ServiceControl;

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
//        try {
////            userCacheController = new UserCacheController();
//        } catch (IOException e) {
//            StringWriter errors = new StringWriter();
//            e.printStackTrace(new PrintWriter(errors));
//            Controller.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR, errors.toString()));
//            e.printStackTrace();
//        }
    }

    @Override
    public void setDBConnections(int connections) {
        // TODO @soudian
    }


//    public static void main(String[] argv) {
//        new UserService();
//    }

}



