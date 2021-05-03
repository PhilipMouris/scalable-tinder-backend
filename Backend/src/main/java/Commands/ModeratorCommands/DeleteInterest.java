package Commands.ModeratorCommands;
import Interface.ConcreteCommand;


public class DeleteInterest extends ConcreteCommand {
    @Override
    public void setParameters() {
        storedProcedure = "uspDeleteInterest";
        inputParams = new String[]{"interestinfo.id"};
        outputName = "interest";
    }
}
