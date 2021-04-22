//import ClientService.Client;
//import Controller.Controller;
import MessageQueue.ServicesMQ;
import MessageQueue.QueueLoadBalance;
import Entities.ServicesType;
import NettyWebServer.NettyServer;
import Controller.Controller;
public class RunBackEnd {

    private static ServicesType[] services;

    public static void main(String[] args) throws InterruptedException {
        
            run("server");
            run("loadBalancer");
            run("mQinstance");
            services = new ServicesType[]{ServicesType.user,ServicesType.moderator};
            run("controller");

    }

    private static void run(String instance) throws InterruptedException {
        Thread t = new Thread(() -> {
            switch (instance.toLowerCase()){
                case "server":
                    NettyServer s = new NettyServer();
                    s.start();
                    break;

                case "loadbalancer":
                    QueueLoadBalance loadBalancer = new QueueLoadBalance();
                    loadBalancer.start();
                    break;
                case "mqinstance":
                    ServicesMQ mQinstance = new ServicesMQ();
                    mQinstance.start();
                    break;
                case "controller":
                    Controller c = new Controller();
                    for(ServicesType type:services) {
                        c.initService(type);
                    }
                    new Thread(() -> {
                        c.start();
                    }).start();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    c.initDBs();
                    c.startServices();

                    break;
            }
        });
        t.start();
        Thread.sleep(200);
    }
}
