package games.tomcat.ballrollergame1;

/**
 * Created by Tom on 07/03/2018.
 */

public class Spirex2 extends Platform {

    float height = 2.f;
    float width = 5.f;
    float depth = 5.f;





    public Spirex2(float platformX, float platformY, float platformZ) {
        widthArray[0] = width;
        heightArray[0] = height;
        depthArray[0] = depth;
        platXArray[0] = platformX;
        platYArray[0] = platformY;
        platZArray[0] = platformZ;
        widthArray[1] = width/3;
        heightArray[1] = height*2;
        depthArray[1] = depth/3;
        platXArray[1] = platformX - 2.f;
        platYArray[1] = platformY+(height*5);
        platZArray[1] = platformZ;
        platXArray[2] = platformX + 2.f;
        platYArray[2] = platformY+(height*5);
        platZArray[2] = platformZ;
        numOfCubes = 3;
        texture = 0;
        new Platform(widthArray, heightArray, depthArray, platXArray, platYArray, platZArray, 3, texture);
    }

    public void updateXYZ(float platformX, float platformY, float platformZ){
        platXArray[0] = platformX;
        platYArray[0] = platformY;
        platZArray[0] = platformZ;
        platXArray[1] = platformX - 2.f;
        platYArray[1] = platformY+(height*5);
        platZArray[1] = platformZ;
        platXArray[2] = platformX + 2.f;
        platYArray[2] = platformY+(height*5);
        platZArray[2] = platformZ;
        System.out.println("yoe");

    }

}
