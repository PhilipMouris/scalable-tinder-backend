//import ClientService.Client;
//import Controller.Controller;
import MessageQueue.ServicesMQ;
import MessageQueue.QueueLoadBalance;
import Entities.ServicesType;
import NettyWebServer.NettyServer;
import Controller.Controller;
public class RunBackEnd {

    private static ServicesType type;

    public static void main(String[] args) throws InterruptedException {



        if(args.length > 1) {
            if (args[1].toLowerCase().equals("post"))
                type = ServicesType.user_to_user;
            if (args[1].toLowerCase().equals("user"))
                type = ServicesType.user;
            if (args[1].toLowerCase().equals("chat"))
                type = ServicesType.chat;
        }
        if(args.length > 0){
            System.out.println("new Running from args : " + args[0]);
            if(args.length > 1)
                System.out.println("Running from args : " + args[1]);
            run(args[0]);
        } else {
            run("server");
            run("loadBalancer");
            run("mQinstance");
            type = ServicesType.user;
            run("controller");
        }

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
                    c.initService(type);
                    new Thread(() -> {
                        c.start();
                    }).start();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    c.initDB();
                    c.startService();

                    break;
            }
        });
        t.start();
        Thread.sleep(200);
    }
}
