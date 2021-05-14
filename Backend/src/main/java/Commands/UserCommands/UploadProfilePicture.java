package Commands.UserCommands;

import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;
import Models.UserData;
import Models.UserPicture;
import org.json.JSONObject;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UploadProfilePicture extends ConcreteCommand {
    private final Logger LOGGER = Logger.getLogger(UploadProfilePicture.class.getName()) ;

    @Override
    public HttpResponseTypes doCommand() {
        try {
            ArangoInstance.setRedisConnection(redis);
            String filename = MinioInstance.uploadFile(mediaServerRequest.getFile(), mediaServerRequest.getFilename());
            String userID = (String) message.getParameter("userData.id");
            boolean isMain = (boolean) message.getParameter("userData.isMain");
            JSONObject userDataToFind =ArangoInstance.find("users",(Object) userID,"UserData");
            if(userDataToFind==null)
                return HttpResponseTypes._404;
            UserData userDataObject = gson.fromJson(userDataToFind.toString(), UserData.class);
            userDataObject.getProfilePictures().add(new UserPicture(filename, isMain, new Date().toString()));
            JSONObject res = null;
            res = ArangoInstance.update("users", userID, gson.toJson(userDataObject));
            if (res != null) {
                responseJson = new JSONObject();
                responseJson.put("record", res);
                return HttpResponseTypes._200;
            } else {
                return HttpResponseTypes._404;
            }
        }
        catch(Exception e){
            LOGGER.log(Level.SEVERE,e.getMessage(),e);
            return HttpResponseTypes._500;
        }


    }
}
