package link;

import filter.HTMLs;
import filter.Headers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Connector {

    private String urlstr = "http://store.steampowered.com/agecheck/app/";
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
    public void sentRequest(int appid) {

        try
        {
            urlstr += appid;
            //设置首次连接的连接参数
            url = new URL(urlstr);
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(20000);
            con.setReadTimeout(20000);
            con.setUseCaches(false);
            con.setInstanceFollowRedirects(false);
            con.connect();
            responseCode = con.getResponseCode();
            if (200 != responseCode){
                System.out.println("responseCode = " + responseCode);
                return;
            }
            //获得cookies（添加到Cookie）和sessionid（添加到表单），设置连接参数并连接，将表单写到POST输出流中
            String cookies = Headers.getCookies(con);
            cookies += "timezoneOffset=28800,0";//不加也没事，最好加上
            String sessionid = Headers.getSessionId(cookies);
            con = (HttpURLConnection) url.openConnection();
            //写入cookies
            con.setRequestProperty("Cookie",cookies);
            //设置连接参数
            con.setConnectTimeout(20000);
            con.setReadTimeout(20000);
            con.setUseCaches(false);
            con.setInstanceFollowRedirects(false);
            //POST请求所必需
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestMethod("POST");

            con.connect();
            //编码后写入流
            OutputStream os = con.getOutputStream();
            String content = URLEncoder.encode("snr","UTF-8") + "=" + URLEncoder.encode("1_agecheck_agecheck__age-gate","UTF-8");
            content += "&" + URLEncoder.encode("sessionid","UTF-8") + "=" + URLEncoder.encode(sessionid,"UTF-8");
            content += "&" + URLEncoder.encode("ageDay","UTF-8") + "=" + URLEncoder.encode("1","UTF-8");
            content += "&" + URLEncoder.encode("ageMonth","UTF-8") + "=" + URLEncoder.encode("January","UTF-8");
            content += "&" + URLEncoder.encode("ageYear","UTF-8") + "=" + URLEncoder.encode("1993","UTF-8");
            os.write(content.getBytes());
            os.flush();
            os.close();
            responseCode = con.getResponseCode();
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

                URL realurl = new URL(location);
                con = (HttpURLConnection) realurl.openConnection();
                //设置重定向后的连接参数
                con.setConnectTimeout(10000);
                con.setReadTimeout(10000);
                con.setUseCaches(false);
                con.setInstanceFollowRedirects(false);
                con.setRequestProperty("Referer","Referer:http://store.steampowered.com/agecheck/app/637650");
                con.setRequestMethod("GET");
                //设置cookies
                con.setRequestProperty("Cookie", cookies);

                //发起连接
                con.connect();
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
