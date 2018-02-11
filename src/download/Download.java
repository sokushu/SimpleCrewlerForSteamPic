package download;

import link.ToGet;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Download {
    private String[] imgUrl;
    private String FilePath;
    public Download(String[] imgUrl, String FilePath){
        this.imgUrl = imgUrl;
        this.FilePath = FilePath;
        File file = new File(FilePath);
        if (!file.exists()){
            file.mkdir();
        }
        for(int i = 0; i < imgUrl.length; i++){
            saveImg(imgUrl[i].toString());
        }
    }

    private InputStream inputStream;

    private void saveImg(String imgUrl){
        try {
            URL url = new URL(imgUrl);
            HttpURLConnection httpcon = ToGet.sentGET(imgUrl,null);

            inputStream = httpcon.getInputStream();

            int a = 0;
            byte[] bData = new byte[1024];
            ByteArrayOutputStream bytearr = new ByteArrayOutputStream();
            while ((a = inputStream.read(bData)) != -1){
                bytearr.write(bData, 0, a);
            }
            bytearr.close();

            byte[] data = bytearr.toByteArray();
            String filename = filename(imgUrl);
            File savefile = new File(FilePath + File.separator + filename);
            FileOutputStream fileout = new FileOutputStream(savefile);
            fileout.write(data);
            fileout.close();
            inputStream.close();
            System.out.print("OK( ^v^ )b " + FilePath + File.separator + filename);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private static String filename(String imgUrl){
        String[] pic = {".jpg",".png",".jpeg",".JPG",".JPEG",".PNG",".gif",".GIF",".bmp",".BMP"};
        String filename = "";
        for (int i = 0; i < pic.length; i++) {
            if (imgUrl.indexOf(pic[i].toString()) > -1) {
                filename = imgUrl.substring(imgUrl.lastIndexOf("/") + 1, imgUrl.indexOf(pic[i])) + pic[i];
            }
            break;
        }
        return filename;
    }
}
