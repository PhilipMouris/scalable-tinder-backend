package Services;

//import Cache.RedisConf;
import Controller.Controller;
//import Database.ArangoInstance;
//import Interface.ControlService;
import Interface.ServiceControl;
import Entities.ErrorLog;
import io.netty.handler.logging.LogLevel;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ModeratorService extends ServiceControl {

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
        //arangoInstance = new ArangoInstance(maxDBConnections);
    }

    @Override
    public void setDBConnections(int connections){
        this.maxDBConnections = connections;
       // arangoInstance.setMaxDBConnections(maxDBConnections);
    }


//    public static void main(String[] args) {
//        new PostService();
//        //postService.add_command("GetKhara","/home/aboelenien/Desktop/GetKhara.txt");
//    }
}
