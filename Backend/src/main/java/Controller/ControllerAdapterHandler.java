package Controller;

import Interface.ServiceControl;
import Entities.ControlMessage;
import Entities.ErrorLog;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.logging.LogLevel;

public class ControllerAdapterHandler extends ChannelInboundHandlerAdapter {

    private final ServiceControl service;

    public ControllerAdapterHandler(ServiceControl service) {
        this.service = service;
    }

    @Override
    public void channelRead(ChannelHandlerContext arg0, Object arg1) {
        Channel currentChannel = arg0.channel();

        currentChannel.writeAndFlush(new ErrorLog(LogLevel.DEBUG,"Client Reading Channel"));
        if(arg1 instanceof String) {
            currentChannel.writeAndFlush(new ErrorLog(LogLevel.INFO,"[INFO] - " + currentChannel.remoteAddress() + " - " + arg1.toString()));
        }
        else if(arg1 instanceof ControlMessage)
            controlService((ControlMessage) arg1);
        currentChannel.writeAndFlush("[Controller] - Success" + "\r\n");
    }

    private void controlService(ControlMessage m){
        switch (m.getControlCommand()){
            case maxDbConnections:  service.setMaxDBConnections(Integer.parseInt(m.getParam()));
                break;
            case maxThreadPool : service.setMaxThreadsSize(Integer.parseInt(m.getParam()));
                break;
            case resume : service.resume();
                break;
            case freeze : service.freeze();
                break;
            case addCommand : service.add_command(m.getParam(), m.getPath());
                break;
            case deleteCommand : service.delete_command(m.getParam());
                break;
            case updateCommand : service.update_command(m.getParam(), m.getPath());
                break;
//            case dropPostDB: service.dropPostDB();
//                break;
//            case createPostDB: service.createPostDB();
//                break;
//            case seedPostDB: service.seedPostDB();
//                break;

        }
        Controller.channel.writeAndFlush(new ErrorLog(LogLevel.DEBUG,"ControlService is executing : " + m.getControlCommand()));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext arg0) {

    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext arg0) {

    }

}