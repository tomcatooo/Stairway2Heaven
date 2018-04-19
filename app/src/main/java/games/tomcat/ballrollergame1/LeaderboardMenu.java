package games.tomcat.ballrollergame1;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;

import games.tomcat.ballrollergame1.Program.Leaderboard;
import games.tomcat.ballrollergame1.Program.LeaderboardPlayer;

/**
 * Created by Tom on 26/03/2018.
 */



public class LeaderboardMenu extends AppCompatActivity {

    private TextView name1, name2, name3, name4, name5, name6, name7, name8, name9, name10;
    private TextView score1, score2,score3,score4,score5,score6,score7,score8,score9,score10;

    String filename = "leaderboard";
    FileOutputStream outputStream;

    Context activityContext;



    Leaderboard leaderboard;

    LeaderboardPlayer[] lead;

    String string;

    Config config;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard);

        activityContext = this.getApplicationContext();
        leaderboard = new Leaderboard(activityContext);
        lead = new LeaderboardPlayer[10];
        config = new Config(activityContext);


        //getboard();
        File namefile;
        File scorefile;


        File configfile = new File(activityContext.getFilesDir(), "config.txt");

        if (configfile.exists()){
            config.loadConfig();
        }
        else{
            config.saveConfig();
        }

        if(!config.hard) {

            namefile = new File(activityContext.getFilesDir(), "names.txt");
            scorefile = new File(activityContext.getFilesDir(), "scores.txt");
        }
        else{
            namefile = new File(activityContext.getFilesDir(), "namesHARD.txt");
            scorefile = new File(activityContext.getFilesDir(), "scoresHARD.txt");
        }


        if(namefile.exists() && scorefile.exists()){
            System.out.println("files exist");
            leaderboard.loadLeaderboard(lead, config.hard);
            System.out.println("Hard = " + config.hard);

        }
        else {
            lead = leaderboard.getLeaderboardPlayers(lead);
            leaderboard.saveLeaderboard(lead, config.hard);
            System.out.println("new leaderboard");
        }

        leaderboard.bubbleSort(lead);


        name1 = (TextView) findViewById(R.id.name1);
        name2 = (TextView) findViewById(R.id.name2);
        name3 = (TextView) findViewById(R.id.name3);
        name4 = (TextView) findViewById(R.id.name4);
        name5 = (TextView) findViewById(R.id.name5);
        name6 = (TextView) findViewById(R.id.name6);
        name7 = (TextView) findViewById(R.id.name7);
        name8 = (TextView) findViewById(R.id.name8);
        name9 = (TextView) findViewById(R.id.name9);
        name10 = (TextView) findViewById(R.id.name10);

        score1 = (TextView) findViewById(R.id.score1);
        score2 = (TextView) findViewById(R.id.score2);
        score3 = (TextView) findViewById(R.id.score3);
        score4 = (TextView) findViewById(R.id.score4);
        score5 = (TextView) findViewById(R.id.score5);
        score6 = (TextView) findViewById(R.id.score6);
        score7 = (TextView) findViewById(R.id.score7);
        score8 = (TextView) findViewById(R.id.score8);
        score9 = (TextView) findViewById(R.id.score9);
        score10 = (TextView) findViewById(R.id.score10);

        name1.setText(lead[0].name);
        score1.setText(String.valueOf(lead[0].score));
        name2.setText(lead[1].name);
        score2.setText(String.valueOf(lead[1].score));
        name3.setText(lead[2].name);
        score3.setText(String.valueOf(lead[2].score));
        name4.setText(lead[3].name);
        score4.setText(String.valueOf(lead[3].score));
        name5.setText(lead[4].name);
        score5.setText(String.valueOf(lead[4].score));
        name6.setText(lead[5].name);
        score6.setText(String.valueOf(lead[5].score));
        name7.setText(lead[6].name);
        score7.setText(String.valueOf(lead[6].score));
        name8.setText(lead[7].name);
        score8.setText(String.valueOf(lead[7].score));
        name9.setText(lead[8].name);
        score9.setText(String.valueOf(lead[8].score));
        name10.setText(lead[9].name);
        score10.setText(String.valueOf(lead[9].score));





    }

    public boolean fileExist(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }

    public  void bubbleSort(LeaderboardPlayer ar[])
    {
        for (int i = (ar.length - 1); i >= 0; i--)
        {
            for (int j = 1; j <= i; j++)
            {
                if (ar[j-1].score> ar[j].score)
                {
                    LeaderboardPlayer temp = ar[j-1];
                    ar[j-1] = ar[j];
                    ar[j] = temp;
                } } } }

   private void getboard(){
        lead = new LeaderboardPlayer[10];
        lead[0] = new LeaderboardPlayer("Tom", 420);
       lead[1] = new LeaderboardPlayer("Tom", 135);
       lead[2] = new LeaderboardPlayer("Tom", 1235);
        for (int i=3; i < 10; i++){
            lead[i] = new LeaderboardPlayer("Emptyboi", 0);
        }
        bubbleSort(lead);
    }

    public void saveLeaderboad(){
        try {
            outputStream = openFileOutput(filename, Context.MODE_APPEND);
            //for (String s : numbers) {
              //  outputStream.write(s.getBytes());
            //}
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
