package de.edvschule_plattling.trfi.testprojekt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by spfahl on 18.05.2018.
 */

public class TierAuswaehlenActivity extends HilfsActivityClass {

    private Button saveBtn;
    private LinearLayout linearLayout;
    private TextView aktuellerBegleiterTV;
    private SharedPreferences sharedPref;
    public static final String jsonTag = "jsonUser";

    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_animal);


        sharedPref = getApplicationContext().getSharedPreferences(UebersichtActivity.MY_PREF, Context.MODE_PRIVATE);
        aktuellerBegleiterTV = (TextView) findViewById(R.id.aktuellerBegleiterLabelID);
        saveBtn = (Button) findViewById(R.id.btnSaveID);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayoutID);

        // Daten vom letzten Speichern laden
        user = new User();
        if (User.fromJSON(sharedPref.getString(jsonTag, "")) != null) {
            user = loadData();
        } else {
            Toast.makeText(getApplicationContext(), "Kein JSONObject gefunden!", Toast.LENGTH_SHORT).show();
        }

        aktuellerBegleiterTV.setText("Dein aktueller Begleiter: " + user.getAktuellerBegleiter().getAnimalNickname());

        // Hier werden die ImageButtons dynamisch zur ScrollView hinzugefuegt
        for (final String key : user.getAnimals().keySet()) {

            ImageButton ib = new ImageButton(this);

            // Eigenschaften des ImageButtons setzen
            int id = getResources().getIdentifier(user.getAnimals().get(key).getPicName(), "drawable", getPackageName());
            ib.setImageResource(id);
            ib.setAdjustViewBounds(true);
            ib.setScaleType(ImageView.ScaleType.FIT_CENTER);

            ib.setBackgroundColor(Color.TRANSPARENT);

            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.setAktuellerBegleiter(user.getAnimals().get(key));
                    aktuellerBegleiterTV.setText("Dein aktueller Begleiter: " + user.getAktuellerBegleiter().getAnimalNickname());

                }
            });
            linearLayout.addView(ib);
        }


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SharedPreferences.Editor editor = sharedPref.edit();

                String jsonUser = User.toJSON(user);
                editor.putString(jsonTag, jsonUser);

                editor.commit();
                Intent myIntent = new Intent(getApplicationContext(), UebersichtActivity.class);

                startActivity(myIntent);
            }
        });

    }


    /**
     * Der User vom letzten Speichern wird geladen
     *
     * @return der geladene User
     */
    public User loadData() {
        return User.fromJSON(sharedPref.getString(jsonTag, ""));
    }

}
