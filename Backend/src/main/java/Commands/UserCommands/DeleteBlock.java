package Commands.UserCommands;
import Controller.ControllerAdapterHandler;
import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;

public class DeleteBlock extends ConcreteCommand{
    @Override
    public void setParameters() {
        storedProcedure = "\"uspDeleteBlock\"";
        inputParams = new String[]{"blockData.source_user_id",
                "blockData.target_user_id"};
        outputName = "block";
    }
}
