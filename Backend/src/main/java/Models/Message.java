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
        return parameters.has(key)? parameters.get(key): null;
    }

    public String getStringParameters(){
        return parameters.toString();
    }
}
