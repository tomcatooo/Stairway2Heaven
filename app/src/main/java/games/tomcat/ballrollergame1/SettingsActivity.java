package games.tomcat.ballrollergame1;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;

import games.tomcat.ballrollergame1.Program.Leaderboard;
import games.tomcat.ballrollergame1.Program.LeaderboardPlayer;

/**
 * Created by Tom on 26/03/2018.
 */



public class SettingsActivity extends AppCompatActivity {

    private TextView name1, name2, name3, name4, name5, name6, name7, name8, name9, name10;
    private TextView score1, score2,score3,score4,score5,score6,score7,score8,score9,score10;

    String filename = "leaderboard";
    FileOutputStream outputStream;

    Context activityContext;


    MainMenu mainMenu;



    Leaderboard leaderboard;

    LeaderboardPlayer[] lead;

    String string;

    String name;

    CurrentPlayer currentPlayer;
    Config config;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Globals g = (Globals)getApplication();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);


        config = new Config(getApplicationContext());
        currentPlayer = new CurrentPlayer(getApplicationContext());
        File configfile = new File(getApplicationContext().getFilesDir(), "config.txt");
        if(configfile.exists()) {
            config.loadConfig();
        }
        else{
            config.saveConfig();
        }
        Button setNameBTN = (Button)findViewById(R.id.setNameBtn);
        final Switch hardSwitch = (Switch)findViewById(R.id.hardSwitch);
        final EditText text = (EditText) findViewById(R.id.name);
        text.setText(currentPlayer.getPlayer());
        hardSwitch.setChecked(config.hard);

        setNameBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    name = text.getText().toString();
                    g.setName(name);
                    currentPlayer.setPlayer(name);
                    currentPlayer.savePlayer(name);

                }

            });

        hardSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hardSwitch.isChecked()){
                    config.hard = true;
                    config.saveConfig();
                }
                else{
                    config.hard = false;
                    config.saveConfig();
                }

            }

        });



    }


}
