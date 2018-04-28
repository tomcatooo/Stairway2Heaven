package games.tomcat.ballrollergame1;

import java.util.ArrayList;

/**
 * Created by Tom on 19/02/2018.
 */

public class ObjectPooler {


    ArrayList<Plat5x5> plat5x5Pool;
    ArrayList<SpireMiddle> SpireMiddlePool;
    ArrayList<PlatformThin> ThinPool;
    ArrayList<PlatformSuperThin> SuperThinPool;




    //changed from arrays to list to allow growth
    public ObjectPooler(int id, int number){
        if ( id == 1) {
            plat5x5Pool = new ArrayList<Plat5x5>(number);
            for (int i = 0; i < number; i++) {
                plat5x5Pool.add(i, new Plat5x5(0.0f, 0.0f, 0.0f));
            }
        }
        else if (id == 2){
            SpireMiddlePool = new ArrayList<SpireMiddle>(number);
            for (int i = 0; i < number; i++){
                SpireMiddlePool.add(i, new SpireMiddle(0.0f, 0.0f, 0.0f));
            }
        }
        else if (id ==3){
            ThinPool = new ArrayList<PlatformThin>(number);
            for (int i = 0; i < number; i++){
                ThinPool.add(i, new PlatformThin(0.0f, 0.0f, 0.0f));
            }
        }
        else if(id ==4){
            SuperThinPool = new ArrayList<PlatformSuperThin>(number);
            for(int i = 0; i < number; i++){
                SuperThinPool.add(i, new PlatformSuperThin(0.0f, 0.0f, 0.0f));
            }
        }

    }




}
