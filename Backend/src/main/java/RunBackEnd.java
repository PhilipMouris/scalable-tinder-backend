//import ClientService.Client;
//import Controller.Controller;
import Config.Config;
import Interface.ServiceControl;
import MessageQueue.ServicesMQ;
import MessageQueue.QueueLoadBalance;
import Entities.ServicesType;
import NettyWebServer.NettyServer;
import Controller.Controller;
public class RunBackEnd {

    private static ServicesType[] services;
    private static int[] ports;


    public static void main(String[] args) throws InterruptedException {
            ports = new int[]{8020,8021};
            run("server");
            run("mQinstance");
            services = new ServicesType[]{ServicesType.user,ServicesType.moderator};
            run("controller");

    }
    private static int getInitialInstanceNum(ServicesType serviceName){
        Config config = Config.getInstance();
        switch (serviceName){
            case user:
                return config.getUserServiceNumInstances();
            case chat:
                return config.getChatServiceNumInstances();
            case moderator:
                return config.getModeratorServiceNumInstances();
            case user_to_user:
                return config.getUserToUserServiceNumInstances();
        }
        return 0;
    }
    private static void run(String instance) throws InterruptedException {
        Thread t = new Thread(() -> {
            switch (instance.toLowerCase()){
                case "server":
                    for(int port :ports){
                        NettyServer s = new NettyServer(port);
                        new Thread(() -> {
                            s.start();
                        }).start();
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case "mqinstance":
                    ServicesMQ mQinstance = new ServicesMQ();
                    break;
                case "controller":
                    Controller c = new Controller();
                    for(ServicesType type:services) {
                        for(int i =0;i<getInitialInstanceNum(type);i++) {
                            ServiceControl s = c.initService(type);
                            new Thread(() -> {
                                s.start();
                            }).start();
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    new Thread(() -> {
                        c.start();
                    }).start();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    c.initDBs();
//                    c.startServices();

                    break;
            }
        });
        t.start();
        Thread.sleep(200);
    }
}
