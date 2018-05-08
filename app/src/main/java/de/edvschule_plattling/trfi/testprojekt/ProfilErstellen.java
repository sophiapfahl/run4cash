package de.edvschule_plattling.trfi.testprojekt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Date;

/**
 * Diese Activity dient dazu, dass der User ggf. ein neues Profil erstellen kann, sofern er noch keines besitzt.
 * Created by spfahl on 23.02.2018.
 */

public class ProfilErstellen extends HilfsActivityClass {

    private Button weiter;
    private EditText nicknameInput;
    private DatePicker dp;
    public static final String MY_PREF = "MYPREF";
    public static final String jsonTag = "jsonUser";
    private SharedPreferences sharedPref;

    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);


        sharedPref = getApplicationContext().getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
        weiter = (Button) findViewById(R.id.weiterButtonID);
        nicknameInput = (EditText) findViewById(R.id.animalNicknameInputID);
        dp = (DatePicker) findViewById(R.id.dialog_date_datePicker);

        user = new User();
        // Moeglicher Fehler: Hier ist schon oft nicht in den if-Zweig gekommen. Das geht jetzt aber
        if(User.load(sharedPref.getString(jsonTag, "")) != null){
            user = loadData();
        } else {
            Toast.makeText(getApplicationContext(), "Kein JSONObject gefunden!", Toast.LENGTH_SHORT).show();
        }


        weiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SharedPreferences.Editor editor = sharedPref.edit();

                user.setNickname(nicknameInput.getText().toString());

                            // year + 1900, weil DatePicker das Jahr - 1900 speichert
                Date newDateOfBirth = new Date(dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
                user.setDateOfBirth(newDateOfBirth);

                String json = User.save(user);
                editor.putString(jsonTag, json);

                editor.commit();

                // Mit diesem Intent wird "TierErstellen" aufgerufen
                Intent myIntent = new Intent(getApplicationContext(), TierErstellen.class);

                startActivity(myIntent);
            }
        });
    }


    // ggf. Standardwerte bzw. gespeicherte Werte vom letzten Oeffnen der App laden.
    public User loadData(){
        user = User.load(sharedPref.getString(jsonTag, ""));

        nicknameInput.setText(user.getNickname());

        Date d = new Date(user.getDateOfBirth().getTime());
        d.setYear(d.getYear() + 1900);
        dp.updateDate(d.getYear(), d.getMonth(), d.getDate());

        return user;
    }

}
