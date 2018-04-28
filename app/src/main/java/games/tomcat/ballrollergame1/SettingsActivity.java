package games.tomcat.ballrollergame1;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

    Leaderboard leaderboard;


    String string;

    String name;

    CurrentPlayer currentPlayer;
    Config config;

    int selector = 0;
    int noTextures = 4;




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
        Button setColourBTN = (Button)findViewById(R.id.setColourBTN);
        final Switch hardSwitch = (Switch)findViewById(R.id.hardSwitch);
        final Switch musicSwitch = (Switch)findViewById(R.id.musicSwitch);
        final Switch sfxSwitch = (Switch)findViewById(R.id.SFXSwitch);
        final EditText text = (EditText) findViewById(R.id.name);
        text.setText(currentPlayer.getPlayer());
        hardSwitch.setChecked(config.hard);
        musicSwitch.setChecked(config.music);
        sfxSwitch.setChecked(config.sfx);


        final ImageView texView = (ImageView)findViewById(R.id.imageView) ;
        selector = config.texture;
        texView.setBackgroundColor(getColour(selector));

        texView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(selector < noTextures-1){
                    selector++;
                }
                else{
                    selector = 0;
                }
                texView.setBackgroundColor(getColour(selector));

            }
        });

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
        musicSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musicSwitch.isChecked()){
                    config.music = true;
                    config.saveConfig();
                }
                else{
                    config.music = false;
                    config.saveConfig();
                }

            }

        });
        sfxSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sfxSwitch.isChecked()){
                    config.sfx = true;
                    config.saveConfig();
                }
                else{
                    config.sfx = false;
                    config.saveConfig();
                }

            }

        });
        setColourBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                config.texture = selector;
                config.saveConfig();

            }

        });



    }

    public int getColour(int selector){
        int colour;
        if (selector == 0){
            colour = Color.parseColor("#ff0000");
        }
        else if(selector == 1){
            colour = Color.parseColor("#37ff00");
        }
        else if(selector == 2){
            colour = Color.parseColor("#0033ff");
        }
        else {
            colour = Color.parseColor("#ffd800");
        }

        return colour;
    }


}
