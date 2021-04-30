package Models;

import com.arangodb.velocypack.annotations.SerializedName;
import org.json.JSONObject;

import java.io.Serializable;

public class Message implements Serializable {
    private JSONObject parameters;

    public JSONObject getParameters() {
        return parameters;
    }

    public void setParameters(JSONObject parameters){
        this.parameters = parameters;
    }

    public Object getParameter(String key){
        System.out.println(key + " INITIAL");
        if(key.contains(".")) return getParameter(key, parameters);
        return parameters.has(key)? parameters.get(key): null;
    }

    public Object getParameter(String key, JSONObject inputParams){
       String[] splitKeys = key.split("\\.");
       if(!inputParams.has(splitKeys[0])) return null;
       Object currentObject = inputParams.get(splitKeys[0]);
       if(splitKeys.length==1) return  currentObject;
       JSONObject newParams = new JSONObject(currentObject.toString());
       int spliceIndex = splitKeys[0].length() + 1;
       return getParameter(key.substring(spliceIndex), newParams);
    }

    public String getStringParameters(){
        return parameters.toString();
    }
}
