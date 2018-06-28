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

public class ProfilErstellenActivity extends HilfsActivityClass {

    private Button weiter;
    private EditText nicknameInput;
    private DatePicker dp;
    public static final String jsonTag = "jsonUser";
    private SharedPreferences sharedPref;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);


        sharedPref = getApplicationContext().getSharedPreferences(UebersichtActivity.MY_PREF, Context.MODE_PRIVATE);
        weiter = (Button) findViewById(R.id.weiterButtonID);
        nicknameInput = (EditText) findViewById(R.id.nicknameInputID);
        dp = (DatePicker) findViewById(R.id.dialog_date_datePicker);

        user = new User();

        if (User.fromJSON(sharedPref.getString(jsonTag, "")) != null) {
            user = loadData();
        } else {
            Toast.makeText(getApplicationContext(), "Kein JSONObject gefunden!", Toast.LENGTH_SHORT).show();
        }


        weiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nicknameInput.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Bitte geben Sie einen Nickname ein!", Toast.LENGTH_SHORT).show();
                    return;
                }
                final SharedPreferences.Editor editor = sharedPref.edit();

                user.setNickname(nicknameInput.getText().toString());

                Date newDateOfBirth = new Date(dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
                user.setDateOfBirth(newDateOfBirth);

                String json = User.toJSON(user);
                editor.putString(jsonTag, json);

                editor.commit();

                // Mit diesem Intent wird "TierAuswaehlenActivity" aufgerufen
                Intent myIntent = new Intent(getApplicationContext(), TierAuswaehlenActivity.class);

                startActivity(myIntent);
            }
        });
    }


    /**
     * Hier werden die Werte aus der StartActivity geladen
     *
     * @return der geladene User
     */
    public User loadData() {
        user = User.fromJSON(sharedPref.getString(jsonTag, ""));

        nicknameInput.setText(user.getNickname());

        Date d = new Date(user.getDateOfBirth().getTime());
        d.setYear(d.getYear() + 1900);
        dp.updateDate(d.getYear(), d.getMonth(), d.getDate());

        return user;
    }

}
