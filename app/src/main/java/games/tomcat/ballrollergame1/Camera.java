package games.tomcat.ballrollergame1;

/**
 * Created by Tom on 11/02/2018.
 */

public class Camera {

    public static float eyeX = 0.0f, eyeY = 7.5f, eyeZ = 8.f;
    static float lookX = 0.0f;
    static float lookY = 5.0f;
    static float lookZ = -5.0f;
    // Set our up vector. This is where our head would be pointing were we holding the camera.
    static float upX = 0.0f;
    static float upY = 1.0f;
    static float upZ = 0.0f;

    public Camera(){

    }

    public static void updateCam(){
        eyeX = Player.ballX;
        eyeY = Player.ballY + 7.5f;
        eyeZ =  Player.ballZ + 8.f;
        lookX = Player.ballX;
        lookY = Player.ballY + 5.0f;
        lookZ = Player.ballZ - 5.0f;
    }
}
