package launch;

import download.Download;
import link.ConManager;

public class Launcher {

    public static void main(String[] args){

//        String urlstr = "http://store.steampowered.com/app/220";
//        //637650
//        //524220
//        //374320
//        //668630
//        //570
//        //292030
//        new ConManager().getPic(urlstr);
        new Download(new String[]{"http://cdn.steamstatic.com.8686c.com/steam/apps/220/0000001869.1920x1080.jpg?t=1515390717"},"E:\\Work\\MyProject\\SimpleCrewlerForSteamPic");

    }
}
