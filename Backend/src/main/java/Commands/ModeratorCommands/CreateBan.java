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
    private final Logger LOGGER = Logger.getLogger(CreateBan.class.getName()) ;

    @Override
    protected HttpResponseTypes doCommand() {
        try {
            dbConn = PostgresInstance.getDataSource().getConnection();
            dbConn.setAutoCommit(true);
            Statement query = dbConn.createStatement();
            query.setPoolable(true);
            BanData in_banData = message.getBanData();
            set = query.executeQuery(String.format("SELECT * FROM \"uspCreateBan\"('%s','%s','%s','%s')",
                    in_banData.getModerator_id(),
                    in_banData.getUser_id(),
                    in_banData.getReason() ,
                    in_banData.getExpiry_date())
                    );
            BanData out_banData = new BanData();
            while(set.next()){
                out_banData.setId(set.getInt("id"));
                out_banData.setModerator_id(set.getInt("moderator_id"));
                out_banData.setUser_id(set.getInt("user_id"));
                out_banData.setReason(set.getString("reason"));
                out_banData.setCreated_at((set.getTimestamp("created_at")));
                out_banData.setExpiry_date(set.getDate("expiry_date"));
            }

            JsonObject response = new JsonObject();
            response.add("record", jsonParser.parse(gson.toJson(out_banData)));
            responseJson = response;
            LOGGER.log(Level.INFO,"Command: "+CreateBan.class.getName()+" Executed Successfully");
            return HttpResponseTypes._200;
        }catch (SQLException e) {
            e.printStackTrace();
//            CommandsHelp.handleError(map.get("app"), map.get("method"), e.getMessage(), map.get("correlation_id"), LOGGER);
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return HttpResponseTypes._500;
        } catch (Exception e){
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return HttpResponseTypes._500;

        }
        finally {
            PostgresInstance.disconnect(null, proc, dbConn);
        }
    }

    @Override
    public void setMessage(Message message) {

    }
}
