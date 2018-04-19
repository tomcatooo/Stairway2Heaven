package games.tomcat.ballrollergame1;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CurrentPlayer {

    String player;

    Context context;

    String filename = "currentPlayer.txt";

    public CurrentPlayer(Context context){
        this.context = context;
        if(fileExist(filename)){
            player = getPlayer();
        }
        else{
            setPlayer("Player");
            writeStringAsFile(player, context, filename );
        }
    }

    public String getPlayer(){

        String player = readFromFile(context, filename);

        return player;
    }

    public void setPlayer(String player){
        this.player = player;
    }

    public void savePlayer(String player){
        writeStringAsFile(player, context, filename );
    }

    public boolean fileExist(String fname){
        File file = context.getFileStreamPath(fname);
        return file.exists();
    }

    public static void writeStringAsFile(final String fileContents, Context context, String fileName) {
        try {
            FileWriter out = new FileWriter(new File(context.getFilesDir(), fileName));
            out.write(fileContents);
            System.out.println("saving " + fileName + " at " + context.getFilesDir());
            out.close();
        } catch (IOException e) {
            //Logger.logError(TAG, e);
        }
    }

    private String readFromFile(Context context, String filename) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
                System.out.println("loading " + filename);
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }



        return ret;
    }
}
