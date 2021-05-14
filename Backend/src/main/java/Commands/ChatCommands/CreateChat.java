package Commands.ChatCommands;

import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;
import org.json.JSONObject;

public class CreateChat extends ConcreteCommand {
    @Override
    public void setParameters(){
        storedProcedure = "\"uspCheckIfUsersMatched\"";
        inputParams = new String[]{"chatData.userAId", "chatData.userBId"};
        outputName = "interactions";
    }

    public void doCustomCommand(){
        if (responseJson.getJSONArray("interactions").length() == 2) {
            type = "create";
            model = "Chat";
            inputParams = new String[] {"chatData"};
            collection = "chats";
            outputName = "chat";
            storedProcedure = null;
            status = handleNoSQLCommand();
        }
        else {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Cannot create chat between unmatched users");
            responseJson = errorResponse;
            status = HttpResponseTypes._400;
        }
    }
}
