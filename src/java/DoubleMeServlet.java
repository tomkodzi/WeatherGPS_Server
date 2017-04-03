/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
 
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
 
@WebServlet("/DoubleMeServlet")
public class DoubleMeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    //Tworzenie obiektu 
    GPSData gpsData = new GPSData();
    
    //Podanie adresu do bazy danych MSQL
    String polaczenieURL = "jdbc:mysql://localhost:3306/gps";
    //podanie loginu i hasła do bazy danych
    String uzytkownik = "root";
    String haslo = "root";  
    //Zmienne pomocnicze
    Connection conn = null;
    Connection conn_get = null;
    Statement stmt = null;
    Statement stmt_get = null;
    ResultSet rs = null;

    String recievedString;
    
    public DoubleMeServlet() {
        super();
    }
 
    //Obsługa żadania GET
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        
            //Odczytywanie parametrów z żądania
            String id = request.getParameter("id");  
            System.out.println("parametr1: " + id);
            String data = request.getParameter("data");  
            System.out.println("parametr2: " + data);
            
            // Tworzenie strumienia do wysyłania wiadomości do klienta
            OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
        
            // Połączenie z bazą i pobieranie z niej danych
            try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            conn_get = DriverManager.getConnection(polaczenieURL,uzytkownik,haslo);
            Class.forName("com.mysql.jdbc.Driver");
            stmt_get = conn_get.createStatement();
            
            
            //Zapytania do bazy:
            //Średnia temperatura
            ResultSet aveTemp = stmt_get.executeQuery("Select AVG(temp) from danegps where telID = '" + id +"' AND dzien = '" + data +"' AND place <> 'Earth';");
            if (aveTemp.next()) {
            float aveTempFinal = (float) (Math.round(aveTemp.getFloat(1) * 100d) / 100d);
            System.out.println("Srednia temperatura wynosi:  " + aveTempFinal + "\n"); 
            writer.write("Srednia temperatura:  " + aveTempFinal + " C\n");
            }
            
            //Najczęstsza lokalizacja
             ResultSet comLoc = stmt_get.executeQuery("SELECT place FROM danegps WHERE telID = '" + id +"' AND dzien = '" + data +"' AND place <> 'Earth' GROUP BY place ORDER BY COUNT(*) DESC LIMIT 1;");
            if (comLoc.next()) {
            System.out.println("Najpopularniejsza lokalizacja:  " + comLoc.getString(1) + "\n"); 
            writer.write("Najpopularniejsza lokalizacja:  " + comLoc.getString(1) + "\n");
            }
            
            //Najczęstsza pogoda
            ResultSet comWea = stmt_get.executeQuery("SELECT weather FROM danegps WHERE telID = '" + id +"' AND dzien = '" + data +"' AND place <> 'Earth' GROUP BY weather ORDER BY COUNT(*) DESC LIMIT 1;");
            if (comWea.next()) {
            System.out.println("Najczęstsza pogoda:  " + comWea.getString(1) + "\n"); 
            writer.write("Najczestsza pogoda:  " + comWea.getString(1) + "\n \n \n");
            }
            
            //pobranie zawartości bazy danych dla danego ID telefonu
            String query = "Select * from danegps where telID = '" + id +"' AND dzien = '" + data +"' AND place <> 'Earth' ORDER BY data ASC;";
            rs = stmt_get.executeQuery(query);
            SQLWarning wynik = stmt_get.getWarnings();
             
            writer.write("Logowania z ostatniego dnia: \n");
            // Zmienne pomocnicze
            float oldSzer = 0;
            float oldDlug = 0;
            String przemieszczenie = "";
            
            while(rs.next()) {
                writer.flush();
                //Sprawdzanie cz nastąpiło przemieszczenie
                float szer = Float.valueOf(rs.getString("szerGeo"));
                float dlug = Float.valueOf(rs.getString("dlugGeo"));
                boolean zmianaSzer;
                boolean zmianaSzerDlug;
               if(Math.abs(oldSzer - szer)>0.0005){
                zmianaSzer = true;
                }
                else zmianaSzer = false;
				
                if (Math.abs(oldDlug - dlug)>0.0005){
                zmianaSzerDlug = true;
                }
                zmianaSzerDlug = false;
                if (zmianaSzer || zmianaSzerDlug){
                    przemieszczenie = "Wykryto przemieszczenie"; 
                }
                else { 
                    przemieszczenie = "Nie wykryto przemieszczenia";   
                }
                //Wysłanie przetworzonych danych do klienta
                response.getOutputStream().println(przemieszczenie+"\nData: \n    " + rs.getString("data") + " \nPolozenie: \n    Szerokosc: " + rs.getString("szerGeo")+ "\n    Dlugosc: " + rs.getString("dlugGeo")+ "\n    Miasto: " +rs.getString("place") + "\nPogoda: \n    Temperatura: " +rs.getString("temp") + "\n    Cisnienie: " +rs.getString("pressure") + "\n    Niebo: " +rs.getString("weather") + "\n    Pora dnia: "+rs.getString("pora")+ "\n\n");
                System.out.println(przemieszczenie+"\nData: \n    " + rs.getString("data") + " \nPolozenie: \n    Szerokosc: " + rs.getString("szerGeo")+ "\n    Dlugosc: " + rs.getString("dlugGeo")+ "\n    Miasto: " +rs.getString("place") + "\nPogoda: \n    Temperatura: " +rs.getString("temp") + "\n    Cisnienie: " +rs.getString("pressure") + "\n    Niebo: " +rs.getString("weather") + "\n    Pora dnia: "+rs.getString("pora")+ "\n\n");
             	 
		oldSzer= szer;
		oldDlug=dlug;
            }
         
            writer.close();
            rs.close();
            stmt_get.close();
            conn_get.close();

            
            } catch (SQLException ex) {
            Logger.getLogger(DoubleMeServlet.class.getName()).log(Level.SEVERE, null, ex);
            response.getOutputStream().println("Jednak nie dziala1");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DoubleMeServlet.class.getName()).log(Level.SEVERE, null, ex);
            response.getOutputStream().println("Jednak nie dziala2");
        }
        
    }
 
    //Obsługa żadania POST
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
    //Odebranie danych przychodzących od klienta
    StringBuilder sb = new StringBuilder();
    BufferedReader reader = request.getReader();
    try {
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append('\n');
        }
    } finally {
        reader.close();
    }
    System.out.println(sb.toString());
 
            recievedString = new String(sb);
            System.out.println(recievedString);
            response.setStatus(HttpServletResponse.SC_OK);
            
            OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
            writer.write("Odebrałem dane");
            writer.write(recievedString);
            writer.flush();
            writer.close();
        // Tworzenie obiektu JSON na podstawie otrzymanch danych
        JSONObject json = new JSONObject(recievedString);
        gpsData = gpsData.fromJSON(json);
     
        
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            //Ustawiamy dane dotyczące podłączenia
            conn = DriverManager.getConnection(polaczenieURL,uzytkownik,haslo);
        } catch (SQLException ex) {
            Logger.getLogger(DoubleMeServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
                       
        try {
            //Ustawiamy sterownik MySQL
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DoubleMeServlet.class.getName()).log(Level.SEVERE, null, ex);
        }        
                        
                //Zapisanie danch do baz danych MySQL        
        try {
           String query1= "INSERT INTO danegps (telID, data, accu, SzerGeo, dlugGeo, temp, place, pressure, weather, sunrise, sunset, pora, dzien) VALUES ('"+gpsData.getTelID()+"','"+gpsData.getData() +"','"+gpsData.getAccu() +"',"
                  + "'"+gpsData.getSzerGeo()+"','"+gpsData.getDlugGeo()+"','"+gpsData.getTemp()+"','"+gpsData.getPlace()+"','"+gpsData.getPressure()+"','"+gpsData.getSky()+"','"+gpsData.getSunrise()+"','"+gpsData.getSunset()+"','"+gpsData.getDay()+"','"+gpsData.getDzien()+"');";
            stmt = conn.createStatement();
           int executeUpdate = stmt.executeUpdate(query1);
         //  OutputStreamWriter writerResponse = new OutputStreamWriter(response.getOutputStream());
         // writerResponse.write("zapisano");
         // writer.write("Zapisano");
         // writerResponse.flush();
         // writerResponse.close();
           System.out.println("Zapisano");
           
        } catch (SQLException ex) {
            Logger.getLogger(DoubleMeServlet.class.getName()).log(Level.SEVERE, null, ex);
              System.out.println("SQLException: " + ex.getMessage());
              System.out.println("SQLState: " + ex.getSQLState());
              System.out.println("VendorError: " + ex.getErrorCode());
        }
       
        try {
           stmt.close();
           conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(DoubleMeServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
      
       }
 
}