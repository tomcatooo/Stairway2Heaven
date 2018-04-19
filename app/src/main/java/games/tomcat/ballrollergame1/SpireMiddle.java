package games.tomcat.ballrollergame1;

/**
 * Created by Tom on 28/02/2018.
 */

public class SpireMiddle extends Platform {

    float height = 1.f;
    float width = 5.f;
    float depth = 5.f;





    public SpireMiddle(float platformX, float platformY, float platformZ) {
        widthArray[0] = width;
        heightArray[0] = height;
        depthArray[0] = depth;
        platXArray[0] = platformX;
        platYArray[0] = platformY;
        platZArray[0] = platformZ;
        widthArray[1] = width/3;
        heightArray[1] = height*5;
        depthArray[1] = depth/3;
        platXArray[1] = platformX;
        platYArray[1] = platformY+(height*5);
        platZArray[1] = platformZ;
        numOfCubes = 2;
        texture = 1;
        new Platform(widthArray, heightArray, depthArray, platXArray, platYArray, platZArray, 2, texture);
    }

    public void updateXYZ(float platformX, float platformY, float platformZ){
        platXArray[0] = platformX;
        platYArray[0] = platformY;
        platZArray[0] = platformZ;
        platXArray[1] = platformX;
        platYArray[1] = platformY+(height*5);
        platZArray[1] = platformZ;

    }

    public void init(){
        widthArray[0] = width;
        heightArray[0] = height;
        depthArray[0] = depth;
       // platXArray[0] = platformX;
       // platYArray[0] = platformY;
       // platZArray[0] = platformZ;
        widthArray[1] = width/3;
        heightArray[1] = height*2;
        depthArray[1] = depth/3;
       // platXArray[1] = platformX;
        //platYArray[1] = platformY;
       // platZArray[1] = platformZ;
    }
}
