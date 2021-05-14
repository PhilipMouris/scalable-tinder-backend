package Commands.UserCommands;

import Entities.HttpResponseTypes;
import Interface.ConcreteCommand;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.WebServiceClient;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.*;
import org.json.JSONObject;

import java.io.File;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdateLocation extends ConcreteCommand {
    private final Logger LOGGER = Logger.getLogger(UpdateLocation.class.getName());
    
    public void setParameters(){
        type="update";
        model = "UserData";
        inputParams= new String[]{"userData.id","userData"};
        collection  = "users";
        String clientIpAddress = new JSONObject((String)data.get("body")).getJSONObject("Headers").getString("x-forwarded-for");
//        String clientIpAddress = "197.37.184.196";
        String locationsDBPath = "/home/vm/Desktop/scalable-tinder/Backend/src/main/resources/GeoLite2-City.mmdb";
        try {
            File database = new File(locationsDBPath);
            DatabaseReader dbReader = new DatabaseReader.Builder(database).build();
            InetAddress ipAddress = InetAddress.getByName(clientIpAddress);
            CityResponse response = dbReader.city(ipAddress);
            String countryName = response.getCountry().getName();
            String cityName = response.getCity().getName();
            String latitude = response.getLocation().getLatitude().toString();
            String longitude = response.getLocation().getLongitude().toString();

            JSONObject newParameters = message.getParameters();
            newParameters.getJSONObject("userData").put("location", new JSONObject());
            newParameters.getJSONObject("userData").getJSONObject("location").put("lng", longitude).put("lat", latitude).put("addressName", cityName+", "+countryName);
            message.setParameters(newParameters);

        } catch(Exception e) {
            LOGGER.log(Level.SEVERE,e.getMessage(),e);
            status = HttpResponseTypes._500;
        }
    }
}
