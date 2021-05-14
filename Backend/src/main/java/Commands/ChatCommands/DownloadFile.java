package Commands.ChatCommands;

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
