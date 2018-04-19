package games.tomcat.ballrollergame1;

/**
 * Created by Tom on 07/03/2018.
 */

public class PlatformThin extends Platform {

    float width = 1f;
    float height = 1f;
    float depth = 2.5f;

    public PlatformThin(float platformX, float platformY, float platformZ) {
        widthArray[0] = width;
        heightArray[0] = height;
        depthArray[0] = depth;
        platXArray[0] = platformX;
        platYArray[0] = platformY;
        platZArray[0] = platformZ;
        numOfCubes = 1;
        texture = 1;
        new Platform(widthArray, heightArray, depthArray, platXArray, platYArray, platZArray, 1, texture);
    }
}
