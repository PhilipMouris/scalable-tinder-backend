package Commands.ModeratorCommands;

import Interface.ConcreteCommand;


public class UpdateInterest extends ConcreteCommand {

    @Override
    public void setParameters() {
        storedProcedure = "uspEditInterest";
        inputParams = new String[]{ "interestinfo.id",
                "interestinfo.name" };
        outputName = "interest";
    }
}
