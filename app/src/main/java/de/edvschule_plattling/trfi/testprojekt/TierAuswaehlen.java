package de.edvschule_plattling.trfi.testprojekt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by spfahl on 18.05.2018.
 */

public class TierAuswaehlen extends HilfsActivityClass {

    private Button save;
    private LinearLayout ll;
    private SharedPreferences sharedPref;
    public static final String MY_PREF = "MYPREF";
    public static final String jsonTag = "jsonUser";

    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_animal);

        sharedPref = getApplicationContext().getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);

        save = (Button) findViewById(R.id.btnSaveID);
        ll = (LinearLayout) findViewById(R.id.linearLayoutID);

        // Daten vom letzten Speichern laden
        user = new User();
        if(User.fromJSON(sharedPref.getString(jsonTag, "")) != null){
            user = loadData();
        } else {
            Toast.makeText(getApplicationContext(), "Kein JSONObject gefunden!", Toast.LENGTH_SHORT).show();
        }


        // Hier sollen die ImageButtons dynamisch zur ScrollView hinzugefuegt werden
        for(final String key : user.getAnimals().keySet()){

            ImageButton ib = new ImageButton(this);
            Drawable drawable = getResources().getDrawable(getResources().getIdentifier(user.getAnimals().get(key).getPicName(), "drawable", getPackageName()));

            // Eigenschaften des ImageButtons setzen
            // ib.setImageResource waere besser, da klappt das mit der Groesse, ich weis aber nicht,
            //  was man da uebergeben muss
            //ib.setBackgroundDrawable(drawable);
            int id = getResources().getIdentifier(user.getAnimals().get(key).getPicName(), "drawable", getPackageName());
            ib.setImageResource(id);
            ib.setAdjustViewBounds(true);   // Das muesste eigentlich die Groesse richtig einstellen (?)
            ib.setScaleType(ImageView.ScaleType.FIT_CENTER);
            // TODO xdfklsdfg
            ib.setBackgroundColor(Color.TRANSPARENT);
            //ib.setCropToPadding(true); (geht nicht, braucht API 16 (unsere minSDKVersion ist 15))
            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.setAktuellerBegleiter(user.getAnimals().get(key));
                    Toast.makeText(getApplicationContext(), "Dein neuer Begleiter: " + user.getAnimals().get(key).getAnimalNickname(), Toast.LENGTH_SHORT).show();
                }
            });
            ll.addView(ib);
        }


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SharedPreferences.Editor editor = sharedPref.edit();

                String jsonUser = User.toJSON(user);
                editor.putString(jsonTag, jsonUser);

                editor.commit();
                Intent myIntent = new Intent(getApplicationContext(), Uebersicht.class);

                startActivity(myIntent);
            }
        });

    }


    public User loadData(){
        return User.fromJSON(sharedPref.getString(jsonTag, ""));
    }

}
