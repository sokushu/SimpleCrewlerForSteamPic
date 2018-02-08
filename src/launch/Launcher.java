package launch;

import filter.HTMLs;
import link.Connector;

public class Launcher {

    public static void main(String[] args){

        //524220
        //374320
        //668630
        //570

        new Connector().sentRequest(524220);
        //HTMLs.readFile();
        //HTMLs.catch_1(HTMLs.readFile());
        HTMLs.catch_2(HTMLs.catch_1(HTMLs.readFile()));
    }
}
