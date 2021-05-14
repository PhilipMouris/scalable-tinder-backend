package Commands.ChatCommands;

import Interface.ConcreteCommand;

public class GetChat extends ConcreteCommand {
    @Override
    public void setParameters() {
        type = "find";
        model = "Chat";
        inputParams = new String[]{"chatData.id"};
        collection = "chats";
        outputName = "chat";
    }
}
