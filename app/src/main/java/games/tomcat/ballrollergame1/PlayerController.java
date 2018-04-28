package games.tomcat.ballrollergame1;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;


/**
 * Created by Tom on 07/02/2018.
 */


public class PlayerController {

    private SensorFusion mSensorFusion;
    private Player player;
     //float ground = -25.f;
    SoundPool soundPool;
    int mainSound;

    AudioManager amg;


    private float gravity = 0.1f;
    public float zeroPitch = -10.5f;

    float pitch;
    float roll;
    float azi;
    boolean sfx;

    Config config;
    float jumpspeed;




    public PlayerController(Context context) {
        mSensorFusion = new SensorFusion(context);
        player = new Player();
        config = new Config(context);
        config.loadConfig();
        sfx = config.sfx;


        soundPool = new SoundPool.Builder().setMaxStreams(1).build();
        //http://www.orangefreesounds.com/boing-sound-effect/
        mainSound = soundPool.load(context, R.raw.boing, 1);
        amg = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    }

    public void updateOriValues() {
        float divisor = 5.f;
        pitch = ((float) Math.toDegrees(mSensorFusion.fusedOrientation[1]) / divisor) - zeroPitch;
        System.out.println("Pitch = " + pitch);
        roll = ((float) Math.toDegrees(mSensorFusion.fusedOrientation[2]) / divisor);
        azi = ((float) Math.toDegrees(mSensorFusion.fusedOrientation[0]) / 1000.f);
    }

    public void sphereJump() {
        if (player.getGrounded()) {
            Player.jump = true;


            System.out.println("jump!");
        }

    }

    public void playSound(int sound) {

       if(sfx) soundPool.play(sound, 1, 1, 1, 0, 1f);
    }



    public void checkJump() {
        if (Player.grounded) {
            if (Player.jump) {
                if(!player.firstJump){
                    player.firstJump = true;
                }
                playSound(1);
                Player.initY = Player.ballY;
                System.out.println("initY - " + Player.initY);
                Player.jump = false;
                Player.jumping = true;
                Player.grounded = false;
            }
        } else {

            if (Player.jumping) {
                jumpspeed = player.zSpeed;
                //in case of negative speed (in reverse)
                if(jumpspeed < 0){
                    jumpspeed = jumpspeed * -1;
                }
                if(jumpspeed < 0.05){
                    jumpspeed = 0.05f;
                }
                System.out.println("jumpspeed = " +jumpspeed);
                if (Player.ballY < Player.initY + (25 * jumpspeed)) {
                    Player.ballY = Player.ballY + 0.10000000000000000f;
                } else {
                    Player.jumping = false;
                    Player.fall = true;
                }
            }
        }
    }



            //if (!Player.grounded && !Player.jumping) {
                 //   Player.ballY = Player.ballY - 0.10000000000000000f;
           // } else {
                //Player.grounded = true;
               // Player.fall = false;
            //}
        //}




    public void checkGravity(){
        if (!Player.grounded && !Player.jumping) {
            //if(Player.ballY > ground) {
                Player.ballY = Player.ballY - gravity;
                Player.fall = true;
           // }
            }
            else {
                //Player.grounded = true;
                Player.fall = false;
            }

        }
    }

