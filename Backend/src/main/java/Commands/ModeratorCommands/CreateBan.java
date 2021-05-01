package Commands.ModeratorCommands;

import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;
import Models.BanData;
import Models.Message;
import com.google.gson.JsonObject;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateBan extends ConcreteCommand {

    @Override
    public void setParameters() {
        storedProcedure = "\"uspCreateBan\"";
        inputParams = new String[]{"banData.moderator_id",
                "banData.user_id",
                "banData.reason",
                "banData.expiry_date"};
        outputName = "ban";
    }
}
