package games.tomcat.ballrollergame1;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import games.tomcat.ballrollergame1.Common.Cube;
import games.tomcat.ballrollergame1.Common.RawResourceReader;
import games.tomcat.ballrollergame1.Common.ShaderHelper;
import games.tomcat.ballrollergame1.Common.Sphere;
import games.tomcat.ballrollergame1.Common.TextureHelper;
import games.tomcat.ballrollergame1.Program.Leaderboard;
import games.tomcat.ballrollergame1.Program.LeaderboardPlayer;


import static games.tomcat.ballrollergame1.Player.initX;
import static games.tomcat.ballrollergame1.Player.jump;
import static games.tomcat.ballrollergame1.Player.jumpHeight;
import static games.tomcat.ballrollergame1.Player.xSpeed;
import static games.tomcat.ballrollergame1.Player.zSpeed;

/**
 * Created by Tom on 25/01/2018.
 */

public class MainRenderer implements GLSurfaceView.Renderer {
    private final Context mActivityContext;

    private PlayerController mPlayerController;

    /**
     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    private float[] mModelMatrix = new float[16];


    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    private float[] mViewMatrix = new float[16];

    /**
     * Store the projection matrix. This is used to project the scene onto a 2D viewport.
     */
    private float[] mProjectionMatrix = new float[16];

    /**
     * Allocate storage for the final combined matrix. This will be passed into the shader program.
     */
    private float[] mMVPMatrix = new float[16];

    /**
     * Stores a copy of the model matrix specifically for the light position.
     */
    private float[] mLightModelMatrix = new float[16];

    private float[] skyBoxMatrix = new float[16];


    /**
     * Used to hold a light centered on the origin in model space. We need a 4th coordinate so we can get translations to work when
     * we multiply this by our transformation matrices.
     */
    private final float[] mLightPosInModelSpace = new float[]{0.0f, 0.0f, 0.0f, 1.0f};

    /**
     * Used to hold the current position of the light in world space (after transformation via model matrix).
     */
    private final float[] mLightPosInWorldSpace = new float[4];

    /**
     * Used to hold the transformed position of the light in eye space (after transformation via modelview matrix)
     */
    private final float[] mLightPosInEyeSpace = new float[4];

    /**
     * This is a handle to our cube shading program.
     */
    private int mProgramHandle;

    /**
     * This is a handle to our light point program.
     */
    private int mPointProgramHandle;

    /**
     * This is a handle to our texture data.
     */
    private int mTextureDataHandle;

    private int mTextureBrick;

    private int mSkyBoxTexture;

    int[] mTextures;

    int textProgramHandle;

    private Cube cube;
    private Sphere sphere;
    private Player player;


    boolean debug = true;


    private SensorFusion mSensorFusion;


    private Camera cam;

    boolean bounceSouth;
    boolean bounceEast, bounceWest, bounceNorth;
    boolean collidedTop;

    float deathLevel;


    private List<float[]> platformList;

    private ArrayList<List<float[]>> objectList;
    private int noPlatforms = 16;

    private Platform[] platforms;

    private boolean onPlatform = false;
    private float platformDestructionPoint;

    private int maxRangeX = 5;
    private int maxRangeY = Math.round(Player.jumpHeight) - 1;
    private int maxRangeZ = 3;


    ObjectPooler Fivex5Pooler;
    ObjectPooler SpirePooler;
    ObjectPooler ThinPooler;
    ObjectPooler SuperThinPooler;

    SoundPool soundPool;
    int mainSound;
    AudioManager amg;
    MediaPlayer mediaPlayer;


    LeaderboardPlayer[] lead;
    Leaderboard leaderboard;
    CurrentPlayer currentPlayer;
    String player1;
    Config config;

    int score;
    float floatscore;
    float fallHeight = 20.f;

    boolean moveZPos, moveZNeg, moveXPos, moveXNeg;

    int speedDivisor;

    float platformLandScore = 20.f;

    int minScore;


    MainActivity activity;

    TextView text;
    boolean moveEast = true;
    boolean moveWest = false;

    boolean music;

    boolean firstJump = false;

    int[] ballTex;


    float maxdist;

    /**
     * Initialize the model data.
     */
    public MainRenderer(final Context activityContext) {
        mActivityContext = activityContext;

        player = new Player();
        cube = new Cube();
        sphere = new Sphere();
        cam = new Camera();
        mSensorFusion = new SensorFusion(mActivityContext);
        activity = new MainActivity();
        mTextures = new int[2];
        speedDivisor = 75;
        bounceSouth = false;
        bounceEast = false;
        bounceWest = false;
        bounceNorth = false;
        collidedTop = false;
        ballTex = new int[5];

        maxdist = 3;


        leaderboard = new Leaderboard(activityContext);
        lead = new LeaderboardPlayer[10];
        currentPlayer = new CurrentPlayer(activityContext);
        player1 = currentPlayer.getPlayer();
        config = new Config(activityContext);

        //getboard();

        File namefile;
        File scorefile;


        File configfile = new File(activityContext.getFilesDir(), "config.txt");

        if (configfile.exists()) {
            config.loadConfig();
        } else {
            config.saveConfig();
        }

        mPlayerController = new PlayerController(activityContext);

        if (!config.hard) {

            namefile = new File(activityContext.getFilesDir(), "names.txt");
            scorefile = new File(activityContext.getFilesDir(), "scores.txt");
        } else {
            namefile = new File(activityContext.getFilesDir(), "namesHARD.txt");
            scorefile = new File(activityContext.getFilesDir(), "scoresHARD.txt");
        }

        if (namefile.exists() && scorefile.exists()) {
            leaderboard.loadLeaderboard(lead, config.hard);
            System.out.println("files exist");
        } else {
            System.out.println("getting leaderboard ");
            lead = leaderboard.getLeaderboardPlayers(lead);
            leaderboard.saveLeaderboard(lead, config.hard);
        }
        leaderboard.bubbleSort(lead);
        minScore = lead[9].score;

        Fivex5Pooler = new ObjectPooler(1, noPlatforms);
        SpirePooler = new ObjectPooler(2, 20);
        ThinPooler = new ObjectPooler(3, 20);
        SuperThinPooler = new ObjectPooler(4, 20);

        //plat5x5 = new Plat5x5[10];
        //objectPools(plat5x5, 10);
        //spire = new SpireMiddle[5];
        //objectPools(spire, 5);

        //initPlatforms();
        initPlatforms();

        music = config.music;

        soundPool = new SoundPool.Builder().setMaxStreams(1).build();
        mainSound = soundPool.load(mActivityContext, R.raw.jump, 1);
        amg = (AudioManager) mActivityContext.getSystemService(Context.AUDIO_SERVICE);

        //Drifting 2 by Audionautix on Audio Library - Free Music, licensed under Creative Commons License
        if(music) {
            mediaPlayer = MediaPlayer.create(mActivityContext, R.raw.drifting2);
        }


    }

    protected String getVertexShader() {
        return RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.per_pixel_vertex_shader_tex_and_light);
    }

    protected String getFragmentShader() {
        return RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.per_pixel_fragment_shader_tex_and_light);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {


        // Set the background clear color to black.
        // GLES20.glClearColor(0, 0, 0,0f);

        // Use culling to remove back faces.
        GLES20.glEnable(GLES20.GL_CULL_FACE);

        // Enable depth testing
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // The below glEnable() call is a holdover from OpenGL ES 1, and is not needed in OpenGL ES 2.
        // Enable texture mapping
        // GLES20.glEnable(GLES20.GL_TEXTURE_2D);


        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        // Matrix.setLookAtM(mViewMatrix, 0, Camera.eyeX, Camera.eyeY, Camera.eyeZ, Camera.lookX, Camera.lookY, Camera.lookZ, Camera.upX, Camera.upY, Camera.upZ);

        final String vertexShader = RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.per_pixel_vertex_shader_tex_and_light);
        final String fragmentShader = RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.per_pixel_fragment_shader_tex_and_light);

        final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

        mProgramHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                new String[]{"a_Position", "a_Color", "a_Normal", "a_TexCoordinate"});

        // Define a simple shader program for our point.
        final String pointVertexShader = RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.point_vertex_shader);
        final String pointFragmentShader = RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.point_fragment_shader);

        final int pointVertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, pointVertexShader);
        final int pointFragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, pointFragmentShader);
        mPointProgramHandle = ShaderHelper.createAndLinkProgram(pointVertexShaderHandle, pointFragmentShaderHandle,
                new String[]{"a_Position"});

        // Load the texture
        mTextureDataHandle = TextureHelper.loadTexture(mActivityContext, R.drawable.red);
        mTextureBrick = TextureHelper.loadTexture(mActivityContext, R.drawable.brick);
        mSkyBoxTexture = TextureHelper.loadTexture(mActivityContext, R.drawable.sky);

        mTextures[0] = mTextureBrick;
        mTextures[1] = TextureHelper.loadTexture(mActivityContext, R.drawable.titanium);

        ballTex[0] = TextureHelper.loadTexture(mActivityContext, R.drawable.red);
        ballTex[1] = TextureHelper.loadTexture(mActivityContext, R.drawable.green);
        ballTex[2] = TextureHelper.loadTexture(mActivityContext, R.drawable.blue);
        ballTex[3] = TextureHelper.loadTexture(mActivityContext, R.drawable.yellow);


        if (music) {mediaPlayer.start();}
        player1 = currentPlayer.getPlayer();




    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {


        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 250.f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {


        mPlayerController.updateOriValues();

        Stack<float[]> model = new Stack();


        //Matrix.translateM(mViewMatrix, vec3(Player.ballX, Player.ballY, Player.ballZ));
        Matrix.setLookAtM(mViewMatrix, 0, Camera.eyeX, Camera.eyeY, Camera.eyeZ, Camera.lookX, Camera.lookY, Camera.lookZ, Camera.upX, Camera.upY, Camera.upZ);
        Camera.updateCam();

        getDeathLevel();
        System.out.println("Death level = " + deathLevel);

        if (Player.ballY - player.radius <= deathLevel) {
            death();
        }



        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        long slowTime = SystemClock.uptimeMillis() % 100000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);
        float slowAngleInDegrees = (360.0f / 100000.0f) * ((int) slowTime);

        GLES20.glDisable(GLES20.GL_CULL_FACE);

        // Set our per-vertex lighting program.
        GLES20.glUseProgram(mProgramHandle);

        //sphere.generateSphereData(100,100,5.f);

        cube.setupHandles(mProgramHandle);
        sphere.setupHandles(mProgramHandle);




        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        //GLES20.glUniform1i(mTextureUniformHandle, 0);

        // Calculate position of the light. Rotate and then push into the distance.
        Matrix.setIdentityM(mLightModelMatrix, 0);
        //Matrix.translateM(mLightModelMatrix, 0, -1.0f, 10.0f, -5.0f);
        Matrix.translateM(mLightModelMatrix, 0, Player.ballX, Player.ballY + 20.f, Player.ballZ);
        //Matrix.rotateM(mLightModelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
        //Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, 2.0f);
        Matrix.scaleM(mLightModelMatrix, 0, 3.f, 3.f, 3.f);


        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);

        // Draw some cubes.


        ballModel();
        collisionDet();
        updateBall();
        bounce();


        System.out.println("Move Z neg : " + moveZNeg);

        if (zSpeed > 0) {
            //floatscore = floatscore + zSpeed;
        }
        score = Math.round(floatscore);

        if (score < 500) speedDivisor = 75;
        if (score > 500 && speedDivisor > 50) {
            speedDivisor = speedDivisor - (1 / 10);
        }
        if (score > 500 && maxdist < 10) {
            maxdist = maxdist + 0.1f;
            jumpHeight = jumpHeight + 0.2f;
        }

        System.out.println("score = " + score);

        //player.ballX += player.xSpeed;
        //player.ballZ += player.zSpeed;

        platformDestructionPoint = player.ballZ + 15.f;


        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, ballTex[config.texture]);


        //ball
        sphere.drawSphere(mMVPMatrix, mViewMatrix, mModelMatrix, mProjectionMatrix, mLightPosInEyeSpace);

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mSkyBoxTexture);

        Matrix.setIdentityM(skyBoxMatrix, 0);
        Matrix.translateM(skyBoxMatrix, 0, player.ballX * 0.1f, -50 + player.ballY * 0.1f, player.ballZ);
        Matrix.scaleM(skyBoxMatrix, 0, 200, 200, 100);
        sphere.drawSphere(mMVPMatrix, mViewMatrix, skyBoxMatrix, mProjectionMatrix, mLightPosInEyeSpace);

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureBrick);


        renderObjects();
        platformDestroy();
        platformRegen();

        if (config.hard) {
            movePlatforms();
        }



        System.out.println("PlatformList size: " + platformList.size());


        //collisionDet();


        if (onPlatform) {
            System.out.println("in  bounds");
            if (!Player.jumping)
                Player.grounded = true;
            Player.fall = false;
            onPlatform = false;
        } else {
            Player.grounded = false;

            if (!Player.jumping) {
                //Player.fall = true;
            }
            System.out.println("not in x bounds");
        }


        System.out.println("x = " + player.ballX);
        System.out.println("y = " + player.ballY);
        System.out.println("z = " + player.ballZ);


        mPlayerController.checkJump();
        mPlayerController.checkGravity();


        // Draw a point to indicate the light.
        GLES20.glUseProgram(mPointProgramHandle);
        drawLight();

        //text = activity.getScoreText();
        //text.setText("124124124");

        System.out.println("zspeed: " + player.zSpeed);

        collidedTop = false;


        if (debug) {
            System.out.println("Jump: " + player.getjump());
            System.out.println("Jumping: " + Player.jumping);
            System.out.println("FAll: " + player.getFall());
            System.out.println("Grounded: " + player.getGrounded());
        }


    }

    public void bounce() {
        bounceSouth();
        bounceEast();
        bounceWest();
        bounceNorth();

    }


    public void bounceSouth() {
        if (bounceSouth) {
            if (Player.ballZ < player.initZ + (player.initZSpeed * 30) && !collidedTop) {
                Player.ballZ = Player.ballZ + player.initZSpeed * 2;
            } else {
                player.zSpeed = 0;
                bounceSouth = false;
            }
        }
    }

    public void bounceNorth() {
        if (bounceNorth) {
            if (Player.ballZ > player.initZ + (player.initZSpeed * 30)) {
                Player.ballZ = Player.ballZ + player.initZSpeed * 2;
            } else {
                player.zSpeed = 0;
                bounceNorth = false;
            }
        }
    }



    public void bounceEast(){
        if(bounceEast){
            if(Player.ballX < player.initX - (player.initXSpeed * 30)){
                System.out.println("bounce east");
                System.out.println("init X " + player.initX);
                Player.ballX = Player.ballX - (player.initXSpeed * 2);
            }
            else{
                player.xSpeed = 0;
                bounceEast = false;
            }
        }
    }

    public void bounceWest(){
        if(bounceWest){
            if(Player.ballX > player.initX - (player.initXSpeed * 30)){
                System.out.println("bounce west");
                System.out.println("init X " + player.initX);
                Player.ballX = Player.ballX - (player.initXSpeed * 2);
            }
            else{
                player.xSpeed = 0;
                bounceWest = false;
            }
        }
    }


    public int getScore() {
        return score;
    }

    public void updateBall() {

        zSpeed = mPlayerController.pitch / speedDivisor;
        xSpeed = mPlayerController.roll / 75;

        player.ballX = player.ballX + xSpeed;
        if (moveZNeg || (!moveZNeg && mPlayerController.pitch > 0)) {
            player.ballZ = player.ballZ - zSpeed;
        }
        player.pitch = player.pitch + mPlayerController.pitch;
        player.roll = player.roll + mPlayerController.roll;
    }

    public void ballModel() {
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0, 0, 0.0f);

        Matrix.translateM(mModelMatrix, 0, player.ballX, 0, 0);
        Matrix.translateM(mModelMatrix, 0, 0, player.ballY, 0);
        Matrix.translateM(mModelMatrix, 0, 0, 0, player.ballZ);

        Matrix.rotateM(mModelMatrix, 0, -player.pitch, 1.0f, 0.0f, 0.0f); // pitch
        Matrix.rotateM(mModelMatrix, 0, player.roll, 0.0f, 1.0f, 0.0f); // roll
        Matrix.scaleM(mModelMatrix, 0, player.radius, player.radius, player.radius);
    }


    public void collisionDet() {
        // ground collision detection
        for (int i = 0; i < objectList.size(); i++) {
            System.out.println("Object List size: " + objectList.size());
            System.out.println("Object list number " + i + " size : " + objectList.get(i).size());
            for (int j = 0; j < objectList.get(i).size(); j++) {
                if ((player.ballX + (player.radius * 0.667) > platforms[i].platXArray[j] - (platforms[i].widthArray[j])) && (player.ballX - (player.radius * 0.667) < platforms[i].platXArray[j] + (platforms[i].widthArray[j])) && (player.ballZ + (player.radius * 0.667) > platforms[i].platZArray[j] - (platforms[i].depthArray[j])) && (player.ballZ - (player.radius * 0.667) < platforms[i].platZArray[j] + (platforms[i].depthArray[j]))

                        && (player.ballY <= platforms[i].platYArray[j] + (platforms[i].heightArray[j] * 2)) && (player.ballY >= platforms[i].platYArray[j] - (platforms[i].heightArray[j]))) {
                    onPlatform = true;
                    //if(player.firstJump) {
                        System.out.println("bounce1");
                        mPlayerController.sphereJump();
                    //}
                    if (!platforms[i].landed) {
                        platforms[i].landed = true;
                        floatscore = floatscore + (platformLandScore * (zSpeed * 10));
                    }
                }
            }
        }

        //bottom

        for (int i = 0; i < objectList.size(); i++) {

            for (int j = 0; j < objectList.get(i).size(); j++) {
                if (player.ballY - player.radius < platforms[i].platYArray[j] + (platforms[i].heightArray[j]) - 0.1f && player.ballY + player.radius > platforms[i].platYArray[j] - (platforms[i].heightArray[j]) + 0.2f) {

                    System.out.println("INSIDE Y");
                    if ((player.ballX + (player.radius) > platforms[i].platXArray[j] - (platforms[i].widthArray[j])) && (player.ballX - (player.radius) < platforms[i].platXArray[j] + (platforms[i].widthArray[j]))) {
                        //ballmoveX = false;
                        //player.ballX =  plat5.platformX - (plat5.width) - (player.radius);
                        System.out.println("INSIDE X");
                    }
                    //collision left
                    if ((player.ballX + (player.radius) >= platforms[i].platXArray[j] - (platforms[i].widthArray[j])) && (player.ballX + (player.radius) < platforms[i].platXArray[j] - (platforms[i].widthArray[j]) + 0.5f) && (player.ballZ + (player.radius) > platforms[i].platZArray[j] - (platforms[i].depthArray[j])) && (player.ballZ - (player.radius) < platforms[i].platZArray[j] + (platforms[i].depthArray[j]))) {
                        //ballmoveX = false;
                        //player.ballX = platforms[i].platXArray[j] - (platforms[i].widthArray[j]) - (player.radius);
                        player.initX = platforms[i].platXArray[j] - (platforms[i].widthArray[j]) - (player.radius);
                        player.initXSpeed = player.xSpeed;
                        bounceWest = true;
                        if(config.sfx)  mPlayerController.playSound(1);
                        System.out.println("Collision left");
                    }
                    //collision right
                    if ((player.ballX - (player.radius) <= platforms[i].platXArray[j] + (platforms[i].widthArray[j])) && (player.ballX - (player.radius) > platforms[i].platXArray[j] + (platforms[i].widthArray[j]) - 0.5f) && (player.ballZ + (player.radius) > platforms[i].platZArray[j] - (platforms[i].depthArray[j])) && (player.ballZ - (player.radius) < platforms[i].platZArray[j] + (platforms[i].depthArray[j]))) {
                        //ballmoveX = false;
                        //player.ballX = platforms[i].platXArray[j] + (platforms[i].widthArray[j]) + (player.radius);
                        player.initX = platforms[i].platXArray[j] + (platforms[i].widthArray[j]) + (player.radius);
                        player.initXSpeed = player.xSpeed;
                        bounceEast = true;
                        if(config.sfx) mPlayerController.playSound(1);
                        //if (player.xSpeed - player.radius < platforms[i].platformX + (platforms[i].width)) {
                        // player.ballX =  plat5.platformX + (plat5.width) + (player.radius);
                        //}
                        System.out.println("Collision right");
                    }

                    //collision top
                    if ((player.ballZ + (player.radius) >= platforms[i].platZArray[j] - (platforms[i].depthArray[j])) && (player.ballZ + player.radius < platforms[i].platZArray[j] - (platforms[i].depthArray[j]) + 0.5f) && (player.ballX + (player.radius) > platforms[i].platXArray[j] - (platforms[i].widthArray[j])) && (player.ballX - (player.radius) < platforms[i].platXArray[j] + (platforms[i].widthArray[j]))) {
                        System.out.println("Collision top");
                        collidedTop = true;
                        //player.ballZ = platforms[i].platZArray[j] - (platforms[i].depthArray[j]) - (player.radius);
                        player.initZ = platforms[i].platZArray[j] - (platforms[i].depthArray[j]) - (player.radius);
                        player.initZSpeed = player.zSpeed;
                        bounceNorth = true;

                        if(config.sfx) mPlayerController.playSound(1);

                    }

                    //collision bottom
                    if ((player.ballZ - (player.radius) <= platforms[i].platZArray[j] + (platforms[i].depthArray[j])) && (player.ballZ - player.radius > platforms[i].platZArray[j] + (platforms[i].depthArray[j]) - 1.f) && (player.ballX + (player.radius) > platforms[i].platXArray[j] - (platforms[i].widthArray[j])) && (player.ballX - (player.radius) < platforms[i].platXArray[j] + (platforms[i].widthArray[j]))) {
                        System.out.println("Collision bottom");
                        //player.ballZ = platforms[i].platZArray[j] + (platforms[i].depthArray[j]) + (player.radius);
                        player.initZ = platforms[i].platZArray[j] + (platforms[i].depthArray[j]) + (player.radius);
                        player.initZSpeed = player.zSpeed;
                        bounceSouth = true;
                        if(config.sfx) mPlayerController.playSound(1);
                    } else {
                        moveZNeg = true;
                    }


                }
            }
        }


    }


    //get lowest platform
    public void getDeathLevel() {
        for (int i = 0; i < platforms.length; i++) {
            if (i == 0) {
                deathLevel = platforms[i].platYArray[0];
            } else {
                if (platforms[i].platYArray[0] < platforms[i - 1].platYArray[0]) {
                    deathLevel = platforms[i].platYArray[0];
                }
            }

        }

        deathLevel = deathLevel - fallHeight;

    }

    private void initPlatforms() {

        objectList = new ArrayList<List<float[]>>(noPlatforms);

        platforms = new Platform[noPlatforms];


        platforms[0] = new Platform(0, -2, 0, 2.5f, 1.f, 10.f);
        //platforms[1] = new SpireMiddle(0,-2,platforms[0].platZArray[0] - platforms[0].depthArray[0] -5);


        for (int i = 1; i < noPlatforms; i++) {

            Random rX = new Random();
            // platforms[i].platXArray[0] = platforms[i-1].platXArray[0] + (rX.nextInt(2 + 1 + 2) -2);
            Random rY = new Random();
            // platforms[i].platYArray[0] = platforms[i-1].platYArray[0] + (rY.nextInt(2 + 1 + 2) -2);
            Random rZ = new Random();

            Random Rand = new Random();

            float finalRand = Rand.nextFloat() * 2;
            //platforms[i].platZArray[0] = platforms[i-1].platZArray[0] -  (platforms[platforms.length-1].depth * 2) - 3.f + (rX.nextInt(2 + 1 + 2) -2);
            /*
            platforms[i] = new SpireMiddle( platforms[i-1].platXArray[0]+ (rX.nextInt(maxRangeX + 1 + maxRangeX) -maxRangeX),
                    platforms[i-1].platYArray[0] + (rY.nextInt(maxRangeY + 1 + maxRangeY) -maxRangeY),
                    platforms[i-1].platZArray[0] - (platforms[i-1].depthArray[0]) - 3.f + (rZ.nextInt(maxRangeZ + 1 + maxRangeZ) -maxRangeZ)
            );
            */

            platforms[i] = Fivex5Pooler.plat5x5Pool.get(i - 1);
            platforms[i].updateXYZ(platforms[i - 1].platXArray[0] + (rX.nextInt(maxRangeX + 1 + maxRangeX) - maxRangeX),
                    platforms[i - 1].platYArray[0] + (rY.nextInt(maxRangeY + 1 + maxRangeY) - maxRangeY),
                    platforms[i - 1].platZArray[0] - (platforms[i - 1].depthArray[0]) - 5.f - (rZ.nextFloat() * 3));
            platforms[i].active = true;
            platforms[i].landed = false;
            platforms[i].initX = platforms[i].platXArray[0];
            platforms[i].finalRand = finalRand;
            System.out.println("New plat " + i + " num cubes:" + platforms[1].numOfCubes);

        }

        for (int i = 0; i < noPlatforms; i++) {
            System.out.println("Platform2 number " + i + " Y:" + platforms[i].numOfCubes);

        }

        for (int i = 0; i < noPlatforms; i++) {
            platformList = new ArrayList<float[]>();
            for (int j = 0; j < platforms[i].numOfCubes; j++) {
                platformList.add(new float[16]);
            }
            objectList.add(i, platformList);
        }
        System.out.println("object list size " + " : " + objectList.size());


    }

    public void platformDestroy() {
        for (int i = 1; i < platforms.length; i++) {
            if (platforms[i].platZArray[0] > platformDestructionPoint) {
                platforms[i].active = false;
            }
            System.out.println("Plat5[" + i + "] active = " + Fivex5Pooler.plat5x5Pool.get(i).active);
        }
    }


    public void platformRegen() {
        //platform regen
        for (int i = 0; i < platforms.length; i++) {
            if (platforms[i].platZArray[0] > platformDestructionPoint) {

                //for(int j = 1; j <platforms[i].numOfCubes; j++) {
                Random rX = new Random();
                Random rY = new Random();
                Random rZ = new Random();
                Random Rand = new Random();

                float finalRand = Rand.nextFloat() * 2;


                Random selector1 = new Random();
                int selector = selector1.nextInt(10 - 1) + 1;

                if (selector < 6) {

                    int j = 0;
                    while (Fivex5Pooler.plat5x5Pool.get(j).active) {
                        if (j == Fivex5Pooler.plat5x5Pool.size() - 1) {
                            Fivex5Pooler.plat5x5Pool.add(new Plat5x5(0.0f, 0.0f, 0.0f));
                        }
                        j++;

                    }
                    platforms[i] = Fivex5Pooler.plat5x5Pool.get(j);
                    platforms[i].active = true;
                    platforms[i].initX = platforms[i].platXArray[0];
                    platforms[i].finalRand = finalRand;
                    platforms[i].landed = false;
                } else if (selector == 6) {
                    int j = 0;
                    while (SuperThinPooler.SuperThinPool.get(j).active) {
                        if (j == SuperThinPooler.SuperThinPool.size() - 1) {
                            SuperThinPooler.SuperThinPool.add(new PlatformSuperThin(0.0f, 0.0f, 0.0f));
                        }
                        j++;
                    }
                    platforms[i] = SuperThinPooler.SuperThinPool.get(j);
                    platforms[i].active = true;
                    platforms[i].initX = platforms[i].platXArray[0];
                    platforms[i].finalRand = finalRand;
                    platforms[i].landed = false;
                } else if (selector == 7 || selector == 8) {
                    int j = 0;
                    while (ThinPooler.ThinPool.get(j).active) {
                        if (j == ThinPooler.ThinPool.size() - 1) {
                            ThinPooler.ThinPool.add(new PlatformThin(0.0f, 0.0f, 0.0f));
                        }
                        j++;
                    }
                    platforms[i] = ThinPooler.ThinPool.get(j);
                    platforms[i].active = true;
                    platforms[i].initX = platforms[i].platXArray[0];
                    platforms[i].finalRand = finalRand;
                    platforms[i].landed = false;
                } else if (selector == 9 || selector == 10) {
                    int j = 0;
                    while (SpirePooler.SpireMiddlePool.get(j).active) {
                        if (j == SpirePooler.SpireMiddlePool.size() - 1) {
                            SpirePooler.SpireMiddlePool.add(new SpireMiddle(0.0f, 0.0f, 0.0f));
                        }
                        j++;
                    }
                    platforms[i] = SpirePooler.SpireMiddlePool.get(j);
                    platforms[i].active = true;
                    platforms[i].initX = platforms[i].platXArray[0];
                    platforms[i].finalRand = finalRand;
                    platforms[i].landed = false;
                }
                if (i > 1) {
                    platforms[i].updateXYZ(platforms[i - 1].platXArray[0] + (rX.nextInt(maxRangeX + 1 + maxRangeX) - maxRangeX),
                            platforms[i - 1].platYArray[0] + (rY.nextInt(maxRangeY + 1 + maxRangeY) - maxRangeY),
                            platforms[i - 1].platZArray[0] - (platforms[i - 1].depthArray[0]) - 3.f - (rZ.nextFloat() * maxdist));
                }
                //if first platform in list
                else {
                    platforms[i].updateXYZ(platforms[platforms.length - 1].platXArray[0] + (rX.nextInt(maxRangeX + 1 + maxRangeX) - maxRangeX),
                            platforms[platforms.length - 1].platYArray[0] + (rY.nextInt(maxRangeY + 1 + maxRangeY) - maxRangeY),
                            platforms[platforms.length - 1].platZArray[0] - (platforms[platforms.length - 1].depthArray[0]) - 3.f - (rZ.nextFloat() * maxdist));

                }
                System.out.println("Platforms no of cubes " + i + " :  " + platforms[i].numOfCubes);
                if (platforms[i].numOfCubes > 1) {
                    System.out.println("Platforms no of cubes plat z " + platforms[i].platZArray[1]);
                }

                if (platforms[i].numOfCubes > objectList.get(i).size()) {
                    for (int j = objectList.get(i).size() - 1; j < platforms[i].numOfCubes - 1; j++) {
                        objectList.get(i).add(new float[16]);
                    }
                } else if (objectList.get(i).size() > platforms[i].numOfCubes) {
                    for (int j = objectList.get(i).size() - 1; j > platforms[i].numOfCubes - 1; j--) {
                        objectList.get(i).remove(j);
                    }
                }


            }
        }
    }


    public void renderObjects() {
        for (int i = 0; i < objectList.size(); i++) {
            for (int j = 0; j < objectList.get(i).size(); j++) {
                Matrix.setIdentityM(objectList.get(i).get(j), 0);
                Matrix.translateM(objectList.get(i).get(j), 0, platforms[i].platXArray[j], platforms[i].platYArray[j], platforms[i].platZArray[j]);
                Matrix.scaleM(objectList.get(i).get(j), 0, platforms[i].widthArray[j], platforms[i].heightArray[j], platforms[i].depthArray[j]);
                // Set the active texture unit to texture unit 0.
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                // Bind the texture to this unit.
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[platforms[i].texture]);
                cube.drawCube(mMVPMatrix, mViewMatrix, objectList.get(i).get(j), mProjectionMatrix, mLightPosInEyeSpace);
            }
        }
    }

    public void movePlatforms() {


        for (int i = 0; i < platforms.length; i++) {
            Random rMove = new Random();

            if (moveEast) {
                if (platforms[i].platXArray[0] < platforms[i].initX + platforms[i].finalRand) {
                    for (int j = 0; j < platforms[i].numOfCubes; j++) {
                        platforms[i].platXArray[j] = platforms[i].platXArray[j] + 0.03f;
                    }
                } else {
                    moveEast = false;
                    moveWest = true;
                }
            } else if (moveWest) {
                if (platforms[i].platXArray[0] > platforms[i].initX - platforms[i].finalRand) {
                    for (int j = 0; j < platforms[i].numOfCubes; j++) {
                        platforms[i].platXArray[j] = platforms[i].platXArray[j] - 0.03f;
                    }
                } else {
                    moveWest = false;
                    moveEast = true;
                }
            }

        }

    }



    public void destroyPlatforms() {
        objectList.clear();
        platforms = new Platform[noPlatforms];
        restartLevel();
    }

    public void restartLevel() {

        floatscore = 0;
        jumpHeight = 3;
        speedDivisor = 75;


        platforms[0] = new Platform(0, -2, 0, 2.5f, 1.f, 10.f);
        //platforms[1] = new SpireMiddle(0,-2,platforms[0].platZArray[0] - platforms[0].depthArray[0] -5);


        for (int i = 1; i < noPlatforms; i++) {

            Random rX = new Random();
            // platforms[i].platXArray[0] = platforms[i-1].platXArray[0] + (rX.nextInt(2 + 1 + 2) -2);
            Random rY = new Random();
            // platforms[i].platYArray[0] = platforms[i-1].platYArray[0] + (rY.nextInt(2 + 1 + 2) -2);
            Random rZ = new Random();
            //platforms[i].platZArray[0] = platforms[i-1].platZArray[0] -  (platforms[platforms.length-1].depth * 2) - 3.f + (rX.nextInt(2 + 1 + 2) -2);
            /*
            platforms[i] = new SpireMiddle( platforms[i-1].platXArray[0]+ (rX.nextInt(maxRangeX + 1 + maxRangeX) -maxRangeX),
                    platforms[i-1].platYArray[0] + (rY.nextInt(maxRangeY + 1 + maxRangeY) -maxRangeY),
                    platforms[i-1].platZArray[0] - (platforms[i-1].depthArray[0]) - 3.f + (rZ.nextInt(maxRangeZ + 1 + maxRangeZ) -maxRangeZ)
            );
            */

            platforms[i] = Fivex5Pooler.plat5x5Pool.get(i - 1);
            platforms[i].updateXYZ(platforms[i - 1].platXArray[0] + (rX.nextInt(maxRangeX + 1 + maxRangeX) - maxRangeX),
                    platforms[i - 1].platYArray[0] + (rY.nextInt(maxRangeY + 1 + maxRangeY) - maxRangeY),
                    platforms[i - 1].platZArray[0] - (platforms[i - 1].depthArray[0]) - 3.f - (rZ.nextFloat() * 3));
            platforms[i].active = true;
            platforms[i].landed = false;
            System.out.println("New plat " + i + " num cubes:" + platforms[1].numOfCubes);

        }

        for (int i = 0; i < noPlatforms; i++) {
            System.out.println("Platform2 number " + i + " Y:" + platforms[i].numOfCubes);

        }

        for (int i = 0; i < noPlatforms; i++) {
            platformList = new ArrayList<float[]>();
            for (int j = 0; j < platforms[i].numOfCubes; j++) {
                System.out.println("platforms[" + i + "] / " + noPlatforms + " numcubes " + platforms[i].numOfCubes);
                platformList.add(new float[16]);
                System.out.println("platformList size " + i + " : " + platformList.size());
            }
            objectList.add(i, platformList);
        }
        System.out.println("object list size " + " : " + objectList.size());
    }

    private void death() {
        if (score > minScore) {
            leaderboard.addPlayer(new LeaderboardPlayer(player1, score), lead);
            leaderboard.bubbleSort(lead);
            minScore = lead[9].score;
            leaderboard.saveLeaderboard(lead, config.hard);
            System.out.println("Hard = " + config.hard);
            System.out.println("Saving leaderboard");
        }
        destroyPlatforms();
        Player.ballX = 0;
        Player.ballY = 0;
        Player.ballZ = 0;
    }


    /**
     * Draws a point representing the position of the light.
     */
    public void drawLight() {
        final int pointMVPMatrixHandle = GLES20.glGetUniformLocation(mPointProgramHandle, "u_MVPMatrix");
        final int pointPositionHandle = GLES20.glGetAttribLocation(mPointProgramHandle, "a_Position");

        // Pass in the position.
        GLES20.glVertexAttrib3f(pointPositionHandle, mLightPosInModelSpace[0], mLightPosInModelSpace[1], mLightPosInModelSpace[2]);

        // Since we are not using a buffer object, disable vertex arrays for this attribute.
        GLES20.glDisableVertexAttribArray(pointPositionHandle);

        // Pass in the transformation matrix.
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mLightModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Draw the point.
        //sphere.drawSphere(mMVPMatrix, mViewMatrix, mLightModelMatrix, mProjectionMatrix, mLightPosInEyeSpace);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
    }

}
