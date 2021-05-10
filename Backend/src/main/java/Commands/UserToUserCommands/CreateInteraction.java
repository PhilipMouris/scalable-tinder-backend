package Commands.UserToUserCommands;

import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;
import org.json.JSONArray;
import org.json.JSONObject;


public class CreateInteraction extends ConcreteCommand {
    @Override
    public void setParameters() {
        storedProcedure = "uspCreateInteraction";
        inputParams = new String[]{"interactionData.source_user_id", "interactionData.target_user_id", "interactionData.type"};
        outputName = "interaction";
    }


    @Override
    public void doCustomCommand(){
        if(status!= HttpResponseTypes._200) return;
        String type = responseJson.getJSONArray(outputName).getJSONObject(0).getString("type") ;
        if(type.equals("dislike")) return;
        String responseJSONTemp = responseJson.toString();
        storedProcedure = "checkUserPremiumMirrorInteraction";
        inputParams = new String[]{"interactionData.source_user_id", "interactionData.target_user_id"};
        handleSQLCommand();
        JSONArray interactions = responseJson.getJSONArray("interaction");
        for (int i=0;i<interactions.length();i++){
            JSONObject interaction = interactions.getJSONObject(i);
            int targetID = interaction.getInt("target_user_id");
            int sourceID = interaction.getInt("source_user_id");
            boolean isSourcePremium = interaction.getBoolean("is_source_premium");
            boolean  isTargetPremium = interaction.getBoolean("is_target_premium");
            if(isSourcePremium){
                this.ArangoInstance.createNotificaiton(sourceID,
                        "newMatch",
                        "New Match",
                        "You have a new match"
                );
            }

            if(isTargetPremium){
                this.ArangoInstance.createNotificaiton(targetID,
                        "newMatch",
                        "New Match",
                        "You have a new match"
                );
            }
            break;
        }
        responseJson = new JSONObject(responseJSONTemp);
    }

}
