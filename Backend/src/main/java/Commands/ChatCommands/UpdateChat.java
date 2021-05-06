package Commands.ChatCommands;

import Interface.ConcreteCommand;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class UpdateChat extends ConcreteCommand {
    @Override
    public void setParameters(){
        type = "find";
        model = "Chat";
        inputParams= new String[]{"chatData.id"};
        collection  = "chats";
        outputName = "chat";
    }

    public void doCustomCommand() {
        if(responseJson.length() == 0)
            return;
        JSONArray oldMessages = responseJson.getJSONObject("chat").getJSONArray("messages");
        JSONObject newMessage = (JSONObject) message.getParameter("chatData.message");
        oldMessages.put(newMessage);
        JSONObject newParameters = message.getParameters();
        newParameters.getJSONObject("chatData").put("messages", oldMessages).remove("message");
        message.setParameters(newParameters);
        type = "update";
        inputParams= new String[]{"chatData.id", "chatData"};
        status = handleNoSQLCommand();
    }
}
