package games.tomcat.ballrollergame1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainMenu extends AppCompatActivity {

    CurrentPlayer currentPlayer;
    TextView name;
    String name1;
    Config config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        currentPlayer = new CurrentPlayer(getApplicationContext());
        config = new Config(getApplicationContext());

        Globals g = (Globals)getApplication();
        Button startBTN = (Button)findViewById(R.id.startBTN);
        startBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        Button settingsBTN = (Button)findViewById(R.id.settingsBTN);
        settingsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        Button leaderboardBTN = (Button)findViewById(R.id.leaderboard);
        leaderboardBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LeaderboardMenu.class);
                startActivity(intent);
            }
        });

        Button helpBTN = (Button)findViewById(R.id.helpBTN);
        helpBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HelpMenu.class);
                startActivity(intent);
            }
        });

        name = (TextView)findViewById(R.id.nameText);
        name1 = currentPlayer.getPlayer();
        name.setText("Welcome, " + name1);


    }

    @Override
    protected void onResume()
    {
        // The activity must call the GL surface view's onResume() on activity onResume().
        super.onResume();

        name1 = currentPlayer.getPlayer();
        name.setText("Welcome, " + name1);


    }
}
