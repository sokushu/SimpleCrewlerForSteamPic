package link;

import filter.HTMLs;
import filter.Headers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

public class Connector1 {

    private HttpURLConnection con;
    private int responseCode;
    private String cookies;
    private String sessionid;

    /*
        思路：
            1.先用GET请求：http://store.steampowered.com/app/637650
                302，取得跳转的链接location （http://store.steampowered.com/agecheck/app/637650）

            2.手动GET请求：location
                200，取得Response Headers存放在Set-Cookie中的steamCountry,browserid,sessionid

            3.用POST请求：location
                参数有：snr,sessionid,ageDay,ageMonth,ageYear，编码封装到content中用流发送
                提交前将 steamCountry;browserid;sessionid;timezoneOffset=28800,0;（_ga;_gid）添加到Cookie中
                302，取得跳转的链接location

            4.手动GET请求：location
                Cookie除上之外再加上 birthtime=725817601; lastagecheckage=1-January-1993
                200
     */
    public void sentRequest(String urlstr) {

        String location = urlstr;
        try
        {
            con = ToGet.sentGET(location, null);
            responseCode = con.getResponseCode();
            System.out.println(responseCode + "---" + con.getRequestMethod() + " : " + con.getURL());
            if (302 == responseCode){
                location = Headers.getLocation(con);

                con = ToGet.sentGET(location,null);
                responseCode = con.getResponseCode();
                System.out.println(responseCode + "---" + con.getRequestMethod() + " : " + con.getURL());
                if (200 == responseCode){
                    //获得cookies（添加到Cookie）和sessionid（添加到表单），设置连接参数并连接，将表单写到POST输出流中
                    cookies = Headers.getCookies(con);
                    cookies += "timezoneOffset=28800,0";//不加也没事，最好加上
                    sessionid = Headers.getSessionId(con);

                    String content = URLEncoder.encode("snr","UTF-8") + "=" + URLEncoder.encode("1_agecheck_agecheck__age-gate","UTF-8");
                    content += "&" + URLEncoder.encode("sessionid","UTF-8") + "=" + URLEncoder.encode(sessionid,"UTF-8");
                    content += "&" + URLEncoder.encode("ageDay","UTF-8") + "=" + URLEncoder.encode("1","UTF-8");
                    content += "&" + URLEncoder.encode("ageMonth","UTF-8") + "=" + URLEncoder.encode("January","UTF-8");
                    content += "&" + URLEncoder.encode("ageYear","UTF-8") + "=" + URLEncoder.encode("1993","UTF-8");

                    con = ToPost.sentPOST(location, cookies, content);
                    responseCode = con.getResponseCode();
                    System.out.println(responseCode + "---" + con.getRequestMethod() + " : " + con.getURL());
                    if (302 == responseCode){
                        //处理302重定向,保存302重定向地址，以及Cookies,然后GET重新发送请求(模拟请求)
                        location = con.getHeaderField("Location");
                        cookies += ";birthtime=725817601;lastagecheckage=1-January-1993";

                        con = ToGet.sentGET(location, cookies);
                        responseCode = con.getResponseCode();
                        System.out.println(responseCode + "---" + con.getRequestMethod() + " : " + con.getURL());
                        if(200 == responseCode){
                            //执行爬取操作
                            HTMLs.catch_2(HTMLs.catch_1(HTMLs.saveToString(con)));
                        }
                        else{
                            System.out.println("return 4");
                            return;
                        }
                    }
                    else {
                        System.out.println("return 3");
                        return;
                    }
                }
                else {
                    System.out.println("return 2");
                    return;
                }
            }
            else {
                System.out.println("return 1");
                return;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
