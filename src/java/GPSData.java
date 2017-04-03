
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tomasz on 22.10.2016.
 */
public class GPSData {

    //Deklaracje zmiennych opisujące pola obiektu GPSData
    String place; //miejscowość
    String telID; //id telefonu
    String data; //data bez godziny
    String sky; //pogoda
    String day; //widno czy ciemno
    double accu; //dokładosc
    double szerGeo; //szerokosc geograficzna
    double dlugGeo; //dlugosc geograficzna
    double pressure; //cisnienie
    int temp; // temperatura
    int sunrise; //wschód
    int sunset; //zachód
    int unixT; //timestamp unixa do sprawdzania czy dzień czy noc
    int main; // id pogody
    
    
    //Funkcja zwracająca datę, po usunięciu godziny
    public String getDzien() {
        String currentDateandTime = getData();
        String[] currentDateandTimeSplit = currentDateandTime.split("-");
        dzien = currentDateandTimeSplit[0];
        
        return dzien;
    }

    String dzien;

    //Funkcja określająca warunki meteorologiczne na podstawie pobramych danych
    public String getSky() {
        int main = getMain()/100;
        
        // Zamiana 
        switch(main){    
            case 2:  sky = "Burza";
                     break;
            case 3:  sky = "Mżawka";
                     break;
            case 5:  sky = "Deszcz";
                     break;
            case 6:  sky = "Śnieg";
                     break;
            case 7:  sky = "Mgla";
                     break;
            case 8:  sky = "Zachmurzenie";
                     break;                     
            case 9:  sky = "Warunki ekstremalne";
                     break;
                                
        }
        
        if (getMain()==800){
        sky = "Czyste niebo";
        }
        
        return sky;
    }


    public int getMain() {
        return main;
    }

    public void setMain(int main) {
        this.main = main;
    }

    public int getUnixT() {
        return unixT;
    }

    public void setUnixT(int unixT) {
        this.unixT = unixT;
    }

    // Funkcja określająca czy jest widno cz ciemno
    public String getDay() {
        
        int r = getSunrise();
        int s = getSunset();
        int u = getUnixT();
        
        if(r > u || u > s){
            day = "Ciemno"; 
        }
        else day = "Widno";
       
       
        
        return day;
    }

// Funkcje pozwalające na pobieranie lub ustawianie wartości zmiennych obiektu GPSData
    public int getSunrise() {
        return sunrise;
    }

    public void setSunrise(int sunrise) {
        this.sunrise = sunrise;
    }


    public int getSunset() {
        return sunset;
    }

    public void setSunset(int sunset) {
        this.sunset = sunset;
    }




    public Double getPressure() {
        return pressure;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }


    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }



    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public String getTelID() {
        return telID;
    }

    public void setTelID(String telID) {
        this.telID = telID;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public double getAccu() {
        return accu;
    }

    public void setAccu(double accu) {
        this.accu = accu;
    }

    public double getSzerGeo() {
        return szerGeo;
    }

    public void setSzerGeo(double szerGeo) {
        this.szerGeo = szerGeo;
    }

    public double getDlugGeo() {
        return dlugGeo;
    }

    public void setDlugGeo(double dlugGeo) {
        this.dlugGeo = dlugGeo;
    }
    //Funkcja odpowiedzialna za tworzenie obiektu JSON
    public JSONObject toJSON()throws JSONException{
        JSONObject js= new JSONObject();

        js.put("telID",getTelID());
        js.put("data",getData());
        js.put("accu",getAccu());
        js.put("szerGeo",getSzerGeo());
        js.put("dlugGeo",getDlugGeo());
        js.put("temp",getTemp());
        js.put("place",getPlace());
        js.put("pressure",getPressure());
        js.put("sunrise",getSunrise());
        js.put("sunset",getSunset());
        js.put("unixT",getUnixT());
        js.put("main",getMain());
        js.put("dzien",getDzien());

        return js;
    }
    
    //Tworzenie obiektu GPSData na podstawie obiektu JSON
    public static GPSData fromJSON(JSONObject object) throws JSONException {
        GPSData d= new GPSData();

    d.setTelID(object.getString("telID"));
        d.setData(object.getString("data"));
        d.setAccu(object.getDouble("accu"));
        d.setSzerGeo(object.getDouble("szerGeo"));
        d.setDlugGeo(object.getDouble("dlugGeo"));
        d.setTemp(object.getInt("temp"));
        d.setPlace(object.getString("place"));
        d.setPressure(object.getDouble("pressure"));
        d.setSunrise(object.getInt("sunrise"));
        d.setSunset(object.getInt("sunset"));
        d.setUnixT(object.getInt("unixT"));
        d.setMain(object.getInt("main"));


        return d;
    }


}