package Commands.ChatCommands;

import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UploadMedia extends ConcreteCommand {
    private final Logger LOGGER = Logger.getLogger(UploadMedia.class.getName());

    @Override
    public void setParameters() {
        type = "find";
        model = "Chat";
        inputParams = new String[]{"chatData.id"};
        collection = "chats";
        outputName = "chat";
    }

    public void doCustomCommand() {
        if (responseJson.length() == 0)
            return;
        try {
            ArangoInstance.setRedisConnection(redis);
            String filename = MinioInstance.uploadFile(mediaServerRequest.getFile(), mediaServerRequest.getFilename());
            JSONArray oldMessages = responseJson.getJSONObject("chat").getJSONArray("messages");
            JSONObject newMessage = (JSONObject) message.getParameter("chatData.message");
            newMessage.put("media", filename);
            oldMessages.put(newMessage);
            JSONObject newParameters = message.getParameters();
            newParameters.getJSONObject("chatData").put("messages", oldMessages).remove("message");
            message.setParameters(newParameters);
            type = "update";
            inputParams = new String[]{"chatData.id", "chatData"};
            status = handleNoSQLCommand();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            status = HttpResponseTypes._500;
        }
    }
}
