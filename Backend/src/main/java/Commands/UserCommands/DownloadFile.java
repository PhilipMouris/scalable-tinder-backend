package Commands.UserCommands;

import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;
import Models.UserData;
import Models.UserPicture;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DownloadFile extends ConcreteCommand {
    private final Logger LOGGER = Logger.getLogger(DownloadFile.class.getName());

    @Override
    public HttpResponseTypes doCommand() {
        try {

            ArangoInstance.setRedisConnection(redis);
            String fileName=(String) message.getParameter("fileName");
            String userID = (String) message.getParameter("userID");
            JSONObject userDataToFind = ArangoInstance.find("users", userID, "UserData");
            if (userDataToFind == null)
                return HttpResponseTypes._404;
            UserData userDataObject = gson.fromJson(userDataToFind.toString(), UserData.class);
            boolean found=false;
            if(userDataObject.getVideos()!=null &&!userDataObject.getVideos().contains(fileName)) {
                for (UserPicture picture:userDataObject.getProfilePictures()){
                    if(picture.getUrl().equals(fileName)){
                        found=true;
                    }
                }
                if(!found) {    // Filename was not found in videos or pictures of that user
                    return HttpResponseTypes._401; // Should be 401 unauthroized when we implement authorizations and add its Res type
                }
            }

            byte[] fileToSend = MinioInstance.downloadFile(fileName);
            if(fileToSend.length==0)
                return HttpResponseTypes._401; // File not found
            JSONObject res = new JSONObject();
            res.put("isFile", true);
            responseJson = res;
            file = fileToSend;
            return HttpResponseTypes._200;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return HttpResponseTypes._500;
        }


    }
}
