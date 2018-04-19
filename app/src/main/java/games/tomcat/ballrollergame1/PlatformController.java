package games.tomcat.ballrollergame1;

public class PlatformController {

    int noPlatforms;
    ObjectPooler Fivex5Pooler;
    ObjectPooler SpirePooler;
    ObjectPooler ThinPooler;
    ObjectPooler SuperThinPooler;

    public PlatformController(){

    }

    public void createPools(int noPlatforms){
        Fivex5Pooler = new ObjectPooler(1, noPlatforms);
        SpirePooler = new ObjectPooler(2, 20);
        ThinPooler = new ObjectPooler(3, 20);
        SuperThinPooler = new ObjectPooler(4, 20);
    }
}
