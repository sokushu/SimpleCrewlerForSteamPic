package link;

import filter.HTMLs;
import filter.Headers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Connector {

    //private String urlstr = "http://store.steampowered.com/agecheck/app/";
    private URL url;
    private HttpURLConnection con;
    private int responseCode;

    /*
        思路：
            1.先用GET请求：http://store.steampowered.com/agecheck/app/637650
                取得Response Headers存放在Set-Cookie中的steamCountry,browserid,sessionid

            2.用POST请求：http://store.steampowered.com/agecheck/app/637650
                参数有：snr,sessionid,ageDay,ageMonth,ageYear
                提交前将 steamCountry;browserid;sessionid;timezoneOffset=28800,0;（_ga;_gid）添加到Cookie中
                头属性 Referer:http://store.steampowered.com/agecheck/app/637650

            3.用GET请求302重定向的页面
                Cookie除上之外再加上 birthtime=725817601; lastagecheckage=1-January-1993
                头属性 Referer:http://store.steampowered.com/agecheck/app/637650
     */
    public void sentRequest(String urlstr) {

        try
        {
            con = ToGet.sentGET(urlstr, null);
            responseCode = con.getResponseCode();
            System.out.println(responseCode + "---" + con.getRequestMethod() + " : " + con.getURL());
            if (200 != responseCode){
                return;
            }
            //获得cookies（添加到Cookie）和sessionid（添加到表单），设置连接参数并连接，将表单写到POST输出流中
            String cookies = Headers.getCookies(con);
            cookies += "timezoneOffset=28800,0";//不加也没事，最好加上
            String sessionid = Headers.getSessionId(cookies);

            String content = URLEncoder.encode("snr","UTF-8") + "=" + URLEncoder.encode("1_agecheck_agecheck__age-gate","UTF-8");
            content += "&" + URLEncoder.encode("sessionid","UTF-8") + "=" + URLEncoder.encode(sessionid,"UTF-8");
            content += "&" + URLEncoder.encode("ageDay","UTF-8") + "=" + URLEncoder.encode("1","UTF-8");
            content += "&" + URLEncoder.encode("ageMonth","UTF-8") + "=" + URLEncoder.encode("January","UTF-8");
            content += "&" + URLEncoder.encode("ageYear","UTF-8") + "=" + URLEncoder.encode("1993","UTF-8");

            con = ToPost.sentPOST(urlstr, cookies, content);
            responseCode = con.getResponseCode();
            System.out.println(responseCode + "---" + con.getRequestMethod() + " : " + con.getURL());
            if (200 != responseCode){
                if (302 != responseCode){
                    System.out.println("responseCode = " + responseCode);
                    return;
                }
            }
            //处理302重定向,保存302重定向地址，以及Cookies,然后GET重新发送请求(模拟请求)
            if (302 == responseCode){

                String location = con.getHeaderField("Location");
                cookies += ";birthtime=725817601;lastagecheckage=1-January-1993";

                con = ToGet.sentGET(location, cookies);
                System.out.println(responseCode + "---" + con.getRequestMethod() + " : " + con.getURL());
                responseCode = con.getResponseCode();
                if (200 != responseCode){
                    if (302 != responseCode){
                        return;
                    }
                }
            }
            //写入文件
            HTMLs.saveToFile(con);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
