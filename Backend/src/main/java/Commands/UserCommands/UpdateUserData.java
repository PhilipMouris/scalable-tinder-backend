package Commands.UserCommands;

import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;
//import Models.User;
import Models.Message;
import com.arangodb.entity.DocumentEntity;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.json.JSONArray;
import org.json.JSONObject;
import Database.ArangoInstance;
import Models.UserData;
public class UpdateUserData extends ConcreteCommand {
    public void setParameters(){
        type="update";
        model = "UserData";
        inputParams= new String[]{"userData._key","userData"};
        collection  = "users";
    }

    @Override
    public void doCustomCommand(){
        if(status!= HttpResponseTypes._200) return;
        storedProcedure =    "\"uspSeeMatchesChronological\"";
        inputParams=     new String[]{"userData._key"};
        outputName = "matches";
        String responseJsonTemp = responseJson.toString();
        handleSQLCommand();
        JSONArray matches = responseJson.getJSONArray("matches");
        for (int i=0;i<matches.length();i++){
            int targetID = matches.getJSONObject(i).getInt("user_1_id");
            this.ArangoInstance.createNotificaiton(targetID,
                    "profileChange",
                    "Profile Update",
                    "A matched user updated their profile"
            );

        }

        responseJson = new JSONObject(responseJsonTemp);




        }
    }

