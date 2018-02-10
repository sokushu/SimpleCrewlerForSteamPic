package link;

import filter.HTMLs;
import filter.Headers;

import java.io.IOException;
import java.net.HttpURLConnection;

public class Connector2 {

    private HttpURLConnection con;
    private int responseCode;
    private String cookies;
    private String sessionid;
    //private String urlstr = "http://store.steampowered.com/app/292030";

    /*
        思路：
            1.先用GET请求：http://store.steampowered.com/app/292030
                302，取得跳转的链接location （http://store.steampowered.com/app/292030/agecheck）
                取得Response Headers存放在Set-Cookie中的steamCountry,browserid

            2.手动GET请求：location
                200，取得Response Headers存放在Set-Cookie中的sessionid

            3.再次GET请求：
                提交前将 steamCountry;browserid;sessionid;timezoneOffset=28800,0;mature_content=1;（_ga;_gid）添加到Cookie中
                200

     */

    public void sentRequest(String urlstr){

        String location = urlstr;
        try
        {
            con = ToGet.sentGET(location, null);
            responseCode = con.getResponseCode();
            System.out.println(responseCode + "---" + con.getRequestMethod() + " : " + con.getURL());
            if (302 == responseCode){
                location = Headers.getLocation(con);
                cookies = Headers.getCookies(con);

                con = ToGet.sentGET(location,null);
                responseCode = con.getResponseCode();
                System.out.println(responseCode + "---" + con.getRequestMethod() + " : " + con.getURL());
                if (200 == responseCode){
                    String sessionid = Headers.getSessionId(con);
                    cookies += "sessionid=" + sessionid + ";timezoneOffset=28800,0;mature_content=1";
                    location = urlstr;
                    con = ToGet.sentGET(location,cookies);
                    responseCode = con.getResponseCode();
                    System.out.println(responseCode + "---" + con.getRequestMethod() + " : " + con.getURL());
                    if (200 == responseCode){
                        //执行爬取操作
                        HTMLs.catch_2(HTMLs.catch_1(HTMLs.saveToString(con)));
                    }
                    else {
                        System.out.println("return 3");
                        return;
                    }
                }
                else{
                    System.out.println("return 2");
                    return;
                }
            }
            else{
                System.out.println("return 1");
                return;
            }


        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


}
