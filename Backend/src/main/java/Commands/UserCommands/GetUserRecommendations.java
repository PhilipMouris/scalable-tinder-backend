package Commands.UserCommands;

import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class GetUserRecommendations extends ConcreteCommand {
        @Override
        public void setParameters(){
            type="generateRecom";
            model = "UserData";
            inputParams = new String[]{
                    "limit",
                    "page",
                    "userData.location.lat",
                    "userData.location.lng",
                    "userData.preferences.location",
                    "userData.age",
                    "userData.preferences.age",
                    "userData.interests"
            };
            collection  = "users";
            useCache = true;
        }
//    {
//        "command":"GetUserRecommendations",
//            "application":"User",
//            "userData":{
//        "age":23,
//                "preferences": {
//            "location": 100,
//                    "age": 2
//        },
//        "location": {
//            "lng": 10,
//                    "addressName": "SK",
//                    "lat": 10
//        },
//        "interests":["volley"]
//    },
//        "limit":200,
//            "page":0,
//            "filter":{
//        "gender":"male"
//    }
//   }
}
