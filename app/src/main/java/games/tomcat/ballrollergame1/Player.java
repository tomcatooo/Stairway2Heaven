package games.tomcat.ballrollergame1;

/**
 * Created by Tom on 07/02/2018.
 */

public class Player {

    public static float ballX = 0.0f, ballY= 0.0f, ballZ = 0.0f;

    public static float pitch = 0.0f, roll = 0.0f;

    public static float distanceToCam = -4.f;

    public static float jumpHeight = 3.f;


    public static boolean jump = false, jumping = false, fall = false, grounded = true;

    public static float xSpeed, zSpeed;

    public  float radius = 1;

    public static float initY;

    public static float initZ, initX;

    public static float initZSpeed, initXSpeed;


    public Player(){

    }



    public boolean getGrounded(){
        return grounded;
    }

    public void setGrounded(boolean bool){grounded = bool;}

    public boolean getFall(){
        return fall;
    }
    public void setFall(boolean bool){fall = bool;}


    public boolean getjump(){
        return jump;
    }
    public void setJump(boolean bool){jump = bool;}
}
