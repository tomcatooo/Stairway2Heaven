package games.tomcat.ballrollergame1.Program;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.logging.Logger;

import games.tomcat.ballrollergame1.Config;

/**
 * Created by Tom on 25/03/2018.
 */

public class Leaderboard {

    String namefile;
    String scorefile;
    String fileContents;
    FileOutputStream outputStream;
    Context context;
    LeaderboardPlayer[] lead = new LeaderboardPlayer[10];
    String[] names = new String[10];
    String[] scores = new String[10];








    public Leaderboard(Context context) {
        this.context = context;
    }

    public LeaderboardPlayer[] getLeaderboardPlayers(LeaderboardPlayer[] leaderboard) {
        for (int i = 0; i < 10; i++) {
            leaderboard[i] = new LeaderboardPlayer("-", 0);
        }
        return  leaderboard;
    }

    public void loadLeaderboard(LeaderboardPlayer[] leaderboard, boolean hard) {
        if (!hard){
            namefile = "names.txt";
            scorefile = "scores.txt";
        }
        else{
            namefile = "namesHARD.txt";
            scorefile = "scoresHARD.txt";
        }

        lead = leaderboard;
        String loadNames = readFromFile(context, namefile);
        String loadScores = readFromFile(context, scorefile);
        names = loadNames.split("~");
        scores = loadScores.split("~");


        for (int i = 0; i < lead.length; i++) {
            LeaderboardPlayer player = new LeaderboardPlayer(names[i],Integer.parseInt(scores[i]) );
            lead[i] = player;

            //lead[i].name = names[i];
            //lead[i].score = Integer.parseInt(scores[i]);
        }


    }

    public void saveLeaderboard(LeaderboardPlayer[] leaderboard, boolean hard) {

        String nametext;
        String scoretext;

        if (!hard){
            namefile = "names.txt";
            scorefile = "scores.txt";
        }
        else{
            namefile = "namesHARD.txt";
            scorefile = "scoresHARD.txt";
        }

        nametext = leaderboard[0].name;
        nametext = nametext.concat("~");
        scoretext = Integer.toString(leaderboard[0].score);
        scoretext = scoretext.concat("~");

        for (int i = 1; i < leaderboard.length; i++) {
            nametext = nametext.concat(leaderboard[i].name);
            nametext = nametext.concat("~");
        }
        //writeToFile(nametext, context, "names.txt");
        writeStringAsFile(nametext, context, namefile);
        System.out.println(nametext);

        for (int i = 1; i < leaderboard.length; i++) {
            scoretext = scoretext.concat(Integer.toString(leaderboard[i].score));
            scoretext = scoretext.concat("~");
        }
        //writeToFile(scoretext, context, "scores.txt");
        writeStringAsFile(scoretext, context, scorefile);
        System.out.println(scoretext);


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

    //https://stackoverflow.com/questions/14376807/how-to-read-write-string-from-a-file-in-android
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

    //public LeaderboardPlayer[] getLeaderboard() {
        //return leaderboard;
    //}

    public void addPlayer(LeaderboardPlayer player, LeaderboardPlayer[] leaderboard) {
        if (player.score > leaderboard[9].score) {
            leaderboard[9] = player;
        }
        bubbleSort(leaderboard);

    }



    public void bubbleSort(LeaderboardPlayer ar[]) {
        for (int i = (ar.length - 1); i >= 0; i--) {
            for (int j = 1; j <= i; j++) {
                if (ar[j - 1].score < ar[j].score) {
                    LeaderboardPlayer temp = ar[j - 1];
                    ar[j - 1] = ar[j];
                    ar[j] = temp;
                }
            }
        }
    }

}
