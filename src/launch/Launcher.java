package launch;

import filter.HTMLs;
import link.Connector;

public class Launcher {

    public static void main(String[] args){

        String urlstr = "http://store.steampowered.com/app/637650";
        //637650
        //524220
        //374320
        //668630
        //570

        new Connector().sentRequest(urlstr);

    }
}
