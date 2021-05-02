package Commands.ModeratorCommands;

import Controller.ControllerAdapterHandler;
import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;
import Models.BanData;
import Models.Message;
import com.google.gson.JsonObject;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeleteBan extends ConcreteCommand {
    @Override
    public void setParameters() {
        storedProcedure = "\"uspDeleteBan\"";
        inputParams = new String[]{"banData.id"};
        outputName = "ban";
    }
}
