package games.tomcat.ballrollergame1.Common;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;
import java.util.Vector;

/**
 * Created by Tom on 28/01/2018.
 */

public class Sphere {

    /** Store our model data in a float buffer. */
    private final FloatBuffer mVerticesBuffer;
    private final CharBuffer mIndicesBuffer;
    private final FloatBuffer mNormalsBuffer;
    private final FloatBuffer mTextureCoordinatesBuffer;

    /** This will be used to pass in the transformation matrix. */
    private int mMVPMatrixHandle;

    /** This will be used to pass in the modelview matrix. */
    private int mMVMatrixHandle;

    /** This will be used to pass in the light position. */
    private int mLightPosHandle;

    /** This will be used to pass in the texture. */
    private int mTextureUniformHandle;

    /** This will be used to pass in model position information. */
    private int mPositionHandle;

    /** This will be used to pass in model color information. */
    private int mColorHandle;

    /** This will be used to pass in model normal information. */
    private int mNormalHandle;

    /** This will be used to pass in model texture coordinate information. */
    private int mTextureCoordinateHandle;

    /** How many bytes per float. */
    private final int mBytesPerFloat = 4;

    /** Size of the position data in elements. */
    private final int mPositionDataSize = 3;

    /** Size of the color data in elements. */
    private final int mColorDataSize = 4;

    /** Size of the normal data in elements. */
    private final int mNormalDataSize = 3;

    /** Size of the texture coordinate data in elements. */
    private final int mTextureCoordinateDataSize = 2;

    public float[] mVertices;
    public float[] mNormals;
    public float[] mTexture;
    public char[] mIndexes;

    private float x, y, z;

    // rings defines how many circles exists from the bottom to the top of the sphere
// sectors defines how many vertexes define a single ring
// radius defines the distance of every vertex from the center of the sphere.
    public void generateSphereData(int totalRings, int totalSectors, float radius)
    {
        mVertices = new float[totalRings * totalSectors * 3];
        mNormals = new float[totalRings * totalSectors * 3];
        mTexture = new float[totalRings * totalSectors * 2];
        mIndexes = new char[totalRings * totalSectors * 6];

        float R = 1f / (float)(totalRings-1);
        float S = 1f / (float)(totalSectors-1);
        int r, s;

        int vertexIndex = 0, textureIndex = 0, indexIndex = 0, normalIndex = 0;

        for(r = 0; r < totalRings; r++)
        {
            for(s = 0; s < totalSectors; s++)
            {
                y = (float)Math.sin((-Math.PI / 2f) + Math.PI * r * R );
                x = (float)Math.cos(2f * Math.PI * s * S) * (float)Math.sin(Math.PI * r * R );
                z = (float)Math.sin(2f * Math.PI * s * S) * (float)Math.sin(Math.PI * r * R );

                if (mTexture != null)
                {
                    mTexture[textureIndex] = s * S;
                    mTexture[textureIndex + 1] = r * R;

                    textureIndex += 2;
                }

                mVertices[vertexIndex] = x * radius;
                mVertices[vertexIndex + 1] = y * radius;
                mVertices[vertexIndex + 2] = z * radius;

                vertexIndex += 3;

                mNormals[normalIndex] = x;
                mNormals[normalIndex + 1] = y;
                mNormals[normalIndex + 2] = z;

                normalIndex += 3;
            }

        }


        int r1, s1;
        for(r = 0; r < totalRings ; r++)
        {
            for(s = 0; s < totalSectors ; s++)
            {
                r1 = (r + 1 == totalRings) ? 0 : r + 1;
                s1 = (s + 1 == totalSectors) ? 0 : s + 1;

                mIndexes[indexIndex] = (char)(r * totalSectors + s);
                mIndexes[indexIndex + 1] = (char)(r * totalSectors + (s1));
                mIndexes[indexIndex + 2] = (char)((r1) * totalSectors + (s1));

                mIndexes[indexIndex + 3] = (char)((r1) * totalSectors + s);
                mIndexes[indexIndex + 4] = (char)((r1) * totalSectors + (s1));
                mIndexes[indexIndex + 5] = (char)(r * totalSectors + s);
                indexIndex += 6;
            }
        }
    }

            public Sphere() {

            generateSphereData(50,50, 1.f);

                // Initialize the buffers.
                mVerticesBuffer = ByteBuffer.allocateDirect(mVertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                mVerticesBuffer.put(mVertices).position(0);

                mIndicesBuffer = ByteBuffer.allocateDirect(mIndexes.length * 4).order(ByteOrder.nativeOrder()).asCharBuffer();
                mIndicesBuffer.put(mIndexes).position(0);

                mTextureCoordinatesBuffer = ByteBuffer.allocateDirect(mTexture.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                mTextureCoordinatesBuffer.put(mTexture).position(0);

                mNormalsBuffer = ByteBuffer.allocateDirect(mNormals.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                mNormalsBuffer.put(mNormals).position(0);

            }


    public void setupHandles(int mProgramHandle){
        // Set program handles for cube drawing.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVMatrix");
        mLightPosHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_LightPos");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color");
        mNormalHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Normal");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate");
    }

    public void drawSphere(float[] mMVPMatrix, float[] mViewMatrix, float[] mModelMatrix, float[] mProjectionMatrix, float[] mLightPosInEyeSpace){
        // Pass in the position information
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
                0, mVerticesBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);



        // Pass in the color information
        //mCubeColors.position(0);
        //GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
         //       0, mCubeColors);

      //  GLES20.glEnableVertexAttribArray(mColorHandle);

        // Pass in the normal information
        mNormalsBuffer.position(0);
        GLES20.glVertexAttribPointer(mNormalHandle, mNormalDataSize, GLES20.GL_FLOAT, false,
                0, mNormalsBuffer);

        GLES20.glEnableVertexAttribArray(mNormalHandle);

        // Pass in the texture coordinate information
        mTextureCoordinatesBuffer.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false,
                0, mTextureCoordinatesBuffer);

        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        // Pass in the modelview matrix.
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Pass in the light position in eye space.
        GLES20.glUniform3f(mLightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndexes.length, GLES20.GL_UNSIGNED_SHORT, mIndicesBuffer);
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public float getZ(){
        return z;
    }
}
