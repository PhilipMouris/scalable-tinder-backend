package Commands.ModeratorCommands;

import Interface.ConcreteCommand;
import Models.BanData;
import Models.Message;
import com.google.gson.JsonObject;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class UpdateBan extends ConcreteCommand {
    @Override
    public void setParameters() {
        storedProcedure = "\"uspUpdateBan\"";
        inputParams = new String[]{"banData.id", "banData.reason", "banData.expiry_date"};
        outputName = "ban";
    }
}

