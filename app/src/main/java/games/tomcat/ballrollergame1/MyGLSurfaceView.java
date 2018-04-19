package games.tomcat.ballrollergame1;

/**
 * Created by Tom on 28/01/2018.
 */

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView
{
    private MainRenderer mRenderer;
    private PlayerController mPlayerController;
    private  Player player;

    // Offsets for touch events
    private float mPreviousX;
    private float mPreviousY;

    private float mDensity;

    public MyGLSurfaceView(Context context)
    {
        super(context);
        mRenderer = new MainRenderer(context);
        mPlayerController = new PlayerController(context);
        player = new Player();

        //setRenderer(mRenderer , mDensity);

    }

    public MyGLSurfaceView(Context context, AttributeSet attrs)
    {

        super(context, attrs);
        mRenderer = new MainRenderer(context);
        mPlayerController = new PlayerController(context);
        player = new Player();

        //setRenderer(mRenderer , mDensity);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event != null)
        {
            /*
            float x = event.getX();
            float y = event.getY();

            if (event.getAction() == MotionEvent.ACTION_MOVE)
            {
                if (mRenderer != null)
                {
                    float deltaX = (x - mPreviousX) / mDensity / 2f;
                    float deltaY = (y - mPreviousY) / mDensity / 2f;

                    mRenderer.mDeltaX += deltaX;
                    mRenderer.mDeltaY += deltaY;
                }
            }

            mPreviousX = x;
            mPreviousY = y;

            */
            mPlayerController.sphereJump();
            //player.grounded = false;



            return true;
        }
        else
        {
            return super.onTouchEvent(event);
        }
    }

    // Hides superclass method.
    public void setRenderer(MainRenderer renderer, float density)
    {
        mRenderer = renderer;
        mDensity = density;
        super.setRenderer(renderer);
    }


}