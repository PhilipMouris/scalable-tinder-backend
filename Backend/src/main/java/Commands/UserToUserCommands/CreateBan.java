package Commands.UserToUserCommands;

import Interface.ConcreteCommand;

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

    @Override
      public void doCustomCommand(){
          int userID = (int) message.getParameter("banData.user_id");
           this.ArangoInstance.createNotificaiton(userID,
                   "ban",
                   "You have been banned",
                   (String) message.getParameter("banData.reason")
                   );
    }
}
