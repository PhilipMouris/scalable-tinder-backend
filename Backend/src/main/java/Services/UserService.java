package Services;


//import Cache.UserCacheController;
import Controller.Controller;
import Database.ArangoInstance;
import Database.PostgreSQL;
import Interface.ServiceControl;
import Entities.ErrorLog;
import io.netty.handler.logging.LogLevel;

import java.io.IOException;
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
                postgresDB= new PostgreSQL();
                postgresDB.initSource();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
//            Controller.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR, errors.toString()));
            e.printStackTrace();
        }
    }


    @Override
    public void setDBConnections(int connections){
        postgresDB.setDbMaxConnections(connections+"");
//        ChatArangoInstance.setMaxDBConnections(maxDBConnections);
    }
    


//    public static void main(String[] argv) {
//        new UserService();
//    }

}



