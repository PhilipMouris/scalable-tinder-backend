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

    //@Override
//    protected void doCommand() {
//        try {
//            dbConn = PostgresInstance.getDataSource().getConnection();
//            dbConn.setAutoCommit(true);
//            String squery ="SELECT * FROM \"uspUpdateBan\"(?,?,?)";
//            PreparedStatement query = dbConn.prepareStatement(squery);
//            query.setPoolable(true);
//            BanData in_banData = message.getBanData();
//            query.setInt(1,in_banData.getId());
//            if(in_banData.getReason()==null) query.setNull(2, Types.LONGNVARCHAR);else query.setString(2,in_banData.getReason());
//            if(in_banData.getExpiry_date()==null) query.setNull(3, Types.DATE);else query.setDate(2,in_banData.getExpiry_date());
//            set = query.executeQuery();
//            BanData out_banData = new BanData();
//            while(set.next()){
//                out_banData.setId(set.getInt("id"));
//                out_banData.setModerator_id(set.getInt("moderator_id"));
//                out_banData.setUser_id(set.getInt("user_id"));
//                out_banData.setReason(set.getString("reason"));
//                out_banData.setCreated_at((set.getTimestamp("created_at")));
//                out_banData.setExpiry_date(set.getDate("expiry_date"));
//            }
//
//            JsonObject response = new JsonObject();
//            System.out.println("BEFORE"+gson.toJson(out_banData));
//            response.add("record", jsonParser.parse(gson.toJson(out_banData)));
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

    @Override
    public void setMessage(Message message) {

    }
}
