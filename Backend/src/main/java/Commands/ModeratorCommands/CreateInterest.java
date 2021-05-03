package Commands.ModeratorCommands;

import Interface.ConcreteCommand;


public class CreateInterest extends ConcreteCommand {

    @Override
    public void setParameters() {
        storedProcedure = "uspCreateInterest";
        inputParams = new String[]{ "interestinfo.name" };
        outputName = "interest";
    }
}
