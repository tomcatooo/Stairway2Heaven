package games.tomcat.ballrollergame1;

/**
 * Created by Tom on 28/02/2018.
 */

public class Plat5x5 extends Platform {

    float width = 2.5f;
    float height = 1f;
    float depth = 2.5f;




    public Plat5x5(float platformX, float platformY, float platformZ){
        widthArray[0] = width;
        heightArray[0] = height;
        depthArray[0] = depth;
        platXArray[0] = platformX;
        platYArray[0] = platformY;
        platZArray[0] = platformZ;
        numOfCubes = 1;
        texture = 0;
        new Platform(widthArray, heightArray, depthArray, platXArray, platYArray, platZArray, 1, texture);

    }




}
