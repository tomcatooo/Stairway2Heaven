package games.tomcat.ballrollergame1;

import android.app.Application;

public class Globals extends Application {

    String name;

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }
}
