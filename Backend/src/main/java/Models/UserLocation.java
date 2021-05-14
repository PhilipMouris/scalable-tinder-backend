package Models;

public class UserLocation {
    private double lng;
    private double lat;
    private String addressName;

    public UserLocation(double lng,double lat,String addressName){
        this.lng = lng;
        this.lat = lat;
        this.addressName = addressName;
    }

    public UserLocation(){
        super();
    }
}
