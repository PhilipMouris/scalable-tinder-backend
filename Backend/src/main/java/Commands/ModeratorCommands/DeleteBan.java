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
    private final Logger LOGGER = Logger.getLogger(DeleteBan.class.getName()) ;

    @Override
    protected HttpResponseTypes doCommand() {
        try {
            dbConn = PostgresInstance.getDataSource().getConnection();
            dbConn.setAutoCommit(true);
            String squery ="SELECT * FROM \"uspDeleteBan\"(?)";
            PreparedStatement query = dbConn.prepareStatement(squery);
            query.setPoolable(true);
            BanData in_banData = message.getBanData();
            query.setInt(1,in_banData.getId());
            
            set = query.executeQuery();
            int deleted_id = -1;
            while(set.next()){
                deleted_id = set.getInt(1);
//                System.out.println(set);
            }
            if(deleted_id==0) {
                return HttpResponseTypes._404;   //Return 404 Not Found if the Ban was not found
            }
            JsonObject response = new JsonObject();
            response.add("id", jsonParser.parse(deleted_id+""));

            responseJson = response;

            return HttpResponseTypes._200;
        }catch (SQLException e) {
            e.printStackTrace();LOGGER.log(Level.SEVERE,e.getMessage(),e);
            return HttpResponseTypes._500;

//            CommandsHelp.handleError(map.get("app"), map.get("method"), e.getMessage(), map.get("correlation_id"), LOGGER);
            //Logger.log(Level.SEVERE, e.getMessage(), e);
        } catch (Exception e){
            e.printStackTrace();LOGGER.log(Level.SEVERE,e.getMessage(),e);
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
