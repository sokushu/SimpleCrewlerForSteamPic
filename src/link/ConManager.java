package link;

import filter.Headers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConManager {

    private HttpURLConnection con;
    private String location;
    private int responseCode;

    public void getPic(String urlstr){

        try
        {
            String location = urlstr;
            con = ToGet.sentGET(location, null);
            responseCode = con.getResponseCode();
            System.out.println(responseCode + "---" + con.getRequestMethod() + " : " + con.getURL());
            if (302 == responseCode) {
                location = Headers.getLocation(con);

                String regex = "(?<=.com/)\\w{8}(?=/app)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(location);
                System.out.println(location);
                if (matcher.find()){
                    System.out.println("way1"+matcher.group(0));
                    new Connector1().sentRequest(urlstr);
                }
                else {
                    System.out.println("way2");
                    new Connector2().sentRequest(urlstr);
                }
            }
            else if (200 == responseCode){
                System.out.println("way0");
                new Connector0().sentRequest(urlstr);
            }
            else{
                System.out.println("ConManager : return ");
                return;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
