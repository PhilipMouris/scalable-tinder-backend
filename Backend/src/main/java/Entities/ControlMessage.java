package Entities;

import java.io.Serializable;
import java.util.Objects;

public class ControlMessage implements Serializable {



    @Override
    public int hashCode() {
        return Objects.hash(controlCommand, param, path);
    }

    private ControlCommand controlCommand;
    private String param;
    private String path;

    public ControlMessage(ControlCommand controlCommand){
        this.controlCommand = controlCommand;
    }

    public ControlMessage(ControlCommand controlCommand, String param){
        this.controlCommand = controlCommand;
        this.param = param;
    }

    public ControlMessage(ControlCommand controlCommand, String param, String path) {
        this.controlCommand = controlCommand;
        this.param = param;
        this.path = path;
    }

    public ControlCommand getControlCommand() {
        return controlCommand;
    }

    public String getParam() {
        return param;
    }

    public String getPath() {
        return path;
    }
}
