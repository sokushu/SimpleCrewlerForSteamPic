package link;

import filter.HTMLs;

import java.io.IOException;
import java.net.HttpURLConnection;

public class Connector0 {

    private HttpURLConnection con;
    private int responseCode;

    /*
        思路：
            1.直接GET获取无须验证的游戏/应用的大图
     */

    public void sentRequest(String urlstr){

        try
        {
            con = ToGet.sentGET(urlstr, null);
            responseCode = con.getResponseCode();
            System.out.println(responseCode + "---" + con.getRequestMethod() + " : " + con.getURL());
            if (200 == responseCode){
                //执行爬取操作
                HTMLs.catch_2(HTMLs.catch_1(HTMLs.saveToString(con)));
            }
            else {
                System.out.println("return");
                return;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
