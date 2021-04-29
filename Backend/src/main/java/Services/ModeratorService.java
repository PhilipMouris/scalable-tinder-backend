package Services;

//import Cache.RedisConf;
import Controller.Controller;
//import Database.ArangoInstance;
//import Interface.ControlService;
import Database.ArangoInstance;

import Database.PostgreSQL;
import Interface.ServiceControl;
import Entities.ErrorLog;
import io.netty.handler.logging.LogLevel;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ModeratorService extends ServiceControl {
    public ModeratorService(int ID) {
        super(ID);
    }

//    private RedisConf redisConf;

    @Override
    public void init() {
        RPC_QUEUE_NAME = conf.getServicesMqModeratorQueue();
////            redisConf  = new RedisConf();
//        } catch (IOException e) {
//            StringWriter errors = new StringWriter();
//            e.printStackTrace(new PrintWriter(errors));
//            Controller.channel.writeAndFlush(new ErrorLog(LogLevel.ERROR, errors.toString()));
//            e.printStackTrace();
//        }
//        liveObjectService = redisConf.getService();
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


//    public static void main(String[] args) {
//        new PostService();
//        //postService.add_command("GetKhara","/home/aboelenien/Desktop/GetKhara.txt");
//    }
}
