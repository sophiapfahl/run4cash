package de.edvschule_plattling.trfi.testprojekt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static de.edvschule_plattling.trfi.testprojekt.User.load;

/**
 * Diese Activity wird direkt beim oeffnen der App geladen. Ein Benutzer muss seinen Namen eingeben und
 * wird ggf. auf die Uebersichtsseite weitergeleitet, wenn bereits ein Account existiert. wenn nicht,
 * kann er ein neues Profil erstellen.
 * Created by spfahl on 26.04.2018.
 */

public class StartActivity extends HilfsActivityClass {

    private EditText nicknameInputId;
    private Button weiterButtonID;
    private SharedPreferences sharedPrefUebersicht;
    public static final String MY_PREF = "MYPREF";
    public static final String jsonTag = "jsonUser";




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        nicknameInputId = (EditText) findViewById(R.id.nicknameInputId);
        weiterButtonID = (Button) findViewById(R.id.weiterButtonID);
        sharedPrefUebersicht = getApplicationContext().getSharedPreferences(Uebersicht.MY_PREF, MODE_PRIVATE);

        weiterButtonID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nicknameInput = nicknameInputId.getText().toString();

                String jsonUser = sharedPrefUebersicht.getString(Uebersicht.jsonTag, "");

                User u = new User();
                u = load(jsonUser);

                Intent myIntent;

                if(u == null){
                    // ist noch nicht angemeldet
                    myIntent = new Intent(getApplicationContext(), ProfilErstellen.class);
                    Toast.makeText(getApplicationContext(), "Es ist noch kein User vorhanden!", Toast.LENGTH_SHORT).show();
                } else {
                    if(nicknameInput.equals(u.getNickname())){
                        // ist schonmal angemeldet gewesen
                        myIntent = new Intent(getApplicationContext(), Uebersicht.class);
                    } else {
                        myIntent = new Intent(getApplicationContext(), ProfilErstellen.class);
                        final SharedPreferences.Editor editor = sharedPrefUebersicht.edit();
                        u = new User();
                        u.setNickname(nicknameInput);
                        String newJsonUser = User.save(u); // User zuruecksetzen
                        editor.putString(jsonTag, newJsonUser);
                        editor.putString("animalNickname", "Jane"); // Nickname des Tiers zuruecksetzen
                        editor.commit();
                        Toast.makeText(getApplicationContext(), "Leider kein passender User gefunden!", Toast.LENGTH_SHORT).show();
                    }
                }

                // Evtl. gleich den vorher eingegebenen Nickname reinladen
                startActivity(myIntent);
            }
        });
    }
}
