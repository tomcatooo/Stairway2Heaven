package games.tomcat.ballrollergame1.Platforms;

import java.util.Random;

/**
 * Created by Tom on 28/02/2018.
 */

public class Platform {

    boolean active = false;
    boolean landed = false;



    float width, height, depth;

    int numOfCubes;
    int texture;
    float randomRange = 3.f;

    float[] widthArray = new float[2];
    float[] heightArray= new float[2];
    float[] depthArray= new float[2];

    float[] platXArray = new float[2];
    float[] platYArray = new float[2];
    float[] platZArray = new float[2];

    float initX;

    boolean moveEast;
    boolean moveWest;

    Random rand = new Random();
    float finalRand;


public Platform(){

}

    public Platform(float platformX, float platformY, float platformZ, float width, float height, float depth){
        this.width = width;
        this.height = height;
        this.depth = depth;
        numOfCubes = 1;
        widthArray[0] = width;
        heightArray[0] = height;
        depthArray[0] = depth;
        platXArray[0] = platformX;
        platYArray[0] = platformY;
        platZArray[0] = platformZ;
        landed = false;
        Random rand = new Random();
        finalRand = rand.nextFloat() * randomRange;
        initX = platXArray[0];
        moveEast = true;
        moveWest = false;
        //this.platformX = platformX;
        //this.platformY = platformY - (height*2) ;
       // this.platformZ = platformZ ;
    }


    public Platform(float[] widthArray, float[] heightArray, float[] depthArray, float[] platXArray, float[] platYArray, float[] platZArray, int numcubes, int texture){
        this.widthArray = widthArray;
        this.heightArray = heightArray;
        this.depthArray = depthArray;
        this.platXArray = platXArray;
        this.platYArray = platYArray;
        this.platZArray = platZArray;
        this.numOfCubes = numcubes;
        this.texture = texture;
        landed = false;

    }

    public void init(){
        float[] widthArray = new float[numOfCubes];
        float[] heightArray= new float[numOfCubes];
        float[] depthArray= new float[numOfCubes];

        float[] platXArray = new float[numOfCubes];
        float[] platYArray = new float[numOfCubes];
        float[] platZArray = new float[numOfCubes];
    }

    public void updateXYZ(float platformX, float platformY, float platformZ){
        platXArray[0] = platformX;
        platYArray[0] = platformY;
        platZArray[0] = platformZ;
    }

    public void movePlatformEast(){
    if (platXArray[0] < initX + finalRand && moveEast) {
        for (int i = 0; i < numOfCubes; i++) {
            platXArray[i] = platXArray[i] + 0.1f;
        }
    }
    else{
        moveEast = false;
        moveWest = true;
    }
    }

    public void movePlatformWest(){
        if (platXArray[0] > initX - finalRand && moveWest) {
            for (int i = 0; i < numOfCubes; i++) {
                platXArray[i] = platXArray[i] - 0.1f;
            }
        }
        else{
            moveWest = false;
            moveEast = true;
        }
    }
}
