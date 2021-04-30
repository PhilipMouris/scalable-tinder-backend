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
//    protected void doCommand() {
//        try {
//            dbConn = PostgresInstance.getDataSource().getConnection();
//            dbConn.setAutoCommit(true);
//            String squery ="SELECT * FROM \"uspDeleteBan\"(?)";
//            PreparedStatement query = dbConn.prepareStatement(squery);
//            query.setPoolable(true);
//            // BanData in_banData = message.getBanData();
//            query.setInt(1,in_banData.getId());
//
//            set = query.executeQuery();
//            int deleted_id = -1;
//            while(set.next()){
//                deleted_id = set.getInt(1);
////                System.out.println(set);
//            }
//
//            JsonObject response = new JsonObject();
//            response.add("id", jsonParser.parse(deleted_id+""));
//            //responseJson = response;
//            System.out.println(response + "ALOOO");
//        }catch (SQLException e) {
//            e.printStackTrace();
////            CommandsHelp.handleError(map.get("app"), map.get("method"), e.getMessage(), map.get("correlation_id"), LOGGER);
//            //Logger.log(Level.SEVERE, e.getMessage(), e);
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        finally {
//            PostgresInstance.disconnect(null, proc, dbConn);
//        }
//    }

    //@Override
    public void setMessage(Message message) {

    }
}
