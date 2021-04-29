package Interface;


import Models.Message;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.TreeMap;

public abstract class Command implements Runnable {

    protected TreeMap<String, Object> data;
    protected Connection dbConn;
    protected CallableStatement proc;
    protected Statement query;
    protected ResultSet set;
    
    final public void init(TreeMap<String, Object> parameters) {
        this.data = parameters;
    }

    protected abstract void execute();
    public abstract void setMessage(Message message);
    public abstract Message getMessage();

    final public void run() {
         this.execute();
    }

}

