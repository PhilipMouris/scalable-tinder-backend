package Commands.ModeratorCommands;

import Interface.ConcreteCommand;
import Models.BanData;
import Models.Message;
import com.google.gson.JsonObject;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class DeleteBan extends ConcreteCommand {
    @Override
    public void setParameters() {
        storedProcedure = "\"uspDeleteBan\"";
        inputParams = new String[]{"banData.id"};
        outputName = "ban";
    }
}
