package Commands.UserCommands;

import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;
//import Models.User;
import Models.Message;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.BaseDocument;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import Database.ArangoInstance;
import Models.UserData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;


public class GetUserDataPaginated extends ConcreteCommand {

    @Override
    public void setParameters(){
        type = "findAll";
        model = "UserData";
        inputParams = new String[]{"limit","page"};
        collection  = "users";
        outputName="users";
    }

}
