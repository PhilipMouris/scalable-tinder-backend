package Commands.UserCommands;

import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;
import MediaServer.MinioInstance;
import Models.UserData;
import com.arangodb.ArangoDB;
import com.arangodb.entity.DocumentEntity;
import com.arangodb.model.DocumentUpdateOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UploadFile extends ConcreteCommand {

    @Override
    public HttpResponseTypes doCommand() {
        String filename=MinioInstance.uploadFile(mediaServerRequest.getFile(),mediaServerRequest.getFilename());
//        ArangoDB arangoDB= ArangoInstance.getArangoDB();
//        JSONObject object=message.getParameters()
//        String userID=message.getParameter("userData");
//        UserData userData= message.();
//        UserData userDataToFind = ArangoInstance.getUserData(userID);
//        DocumentEntity res = null;
//        System.out.println("UserData Is" + new Gson().toJson(userData));
//        if (userDataToFind != null) {
//            System.out.println(userData);
//            res = arangoDB.db(ArangoInstance.getDbName()).collection("users").updateDocument(userID, gson.toJson(userData), new DocumentUpdateOptions().returnNew(true));
//        } else {
//            return HttpResponseTypes._404;
//        }
//        JsonObject response = new JsonObject();
//        JSONObject userDataJSON=new JSONObject(res);
//        String newDocumentString=userDataJSON.get("new").toString();
//        response.add("userData", jsonParser.parse(newDocumentString));
//        responseJson = jsonParser.parse(response.toString());
        return HttpResponseTypes._200;
    }
}
