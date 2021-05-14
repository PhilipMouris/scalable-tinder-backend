package Commands.ChatCommands;

import Interface.ConcreteCommand;

public class DeleteChat extends ConcreteCommand {
    @Override
    public void setParameters(){
        type = "delete";
        model = "Chat";
        inputParams = new String[] {"chatData.id"};
        collection = "chats";
        outputName = "chat";
    }
}
