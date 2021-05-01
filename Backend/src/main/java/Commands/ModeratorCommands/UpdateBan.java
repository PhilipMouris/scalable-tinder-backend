package Commands.ModeratorCommands;

import Controller.ControllerAdapterHandler;
import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;
import Models.BanData;
import Models.Message;
import com.google.gson.JsonObject;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdateBan extends ConcreteCommand {
    @Override
    public void setParameters() {
        storedProcedure = "\"uspUpdateBan\"";
        inputParams = new String[]{"banData.id", "banData.reason", "banData.expiry_date"};
        outputName = "ban";
    }
}

