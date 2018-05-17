package de.edvschule_plattling.trfi.testprojekt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static de.edvschule_plattling.trfi.testprojekt.User.fromJSON;

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

                /*final SharedPreferences.Editor editor = sharedPrefUebersicht.edit();
                Animal beg = new Animal("Kaninchen", "Kaninchen", "rabbit", 0);
                u.addAnimal(beg.getAnimalNickname(), beg);
                u.setAktuellerBegleiter(beg);
                Log.e("DEBUG", User.toJSON(u));
                editor.putString(jsonTag, User.toJSON(u));
                editor.commit();*/

                String jsonUser = sharedPrefUebersicht.getString(Uebersicht.jsonTag, "");
                User u = new User();

                u = fromJSON(jsonUser);

                Log.e("DEBUG", u.toString());


                Intent myIntent;

                if (u == null) {
                    // ist noch nicht angemeldet
                    saveNewUser(u);
                    myIntent = new Intent(getApplicationContext(), ProfilErstellen.class);
                    Toast.makeText(getApplicationContext(), "Es ist noch kein User vorhanden!", Toast.LENGTH_SHORT).show();
                } else {
                    if (nicknameInput.equals(u.getNickname())) {
                        // ist schonmal angemeldet gewesen
                        myIntent = new Intent(getApplicationContext(), Uebersicht.class);
                    } else {
                        saveNewUser(u);
                        myIntent = new Intent(getApplicationContext(), ProfilErstellen.class);
                        Toast.makeText(getApplicationContext(), "Leider kein passender User gefunden!", Toast.LENGTH_SHORT).show();
                    }
                }

                startActivity(myIntent);
            }
        });
    }


    public void saveNewUser(User u) {
        final SharedPreferences.Editor editor = sharedPrefUebersicht.edit();
        u = new User();
        u.setNickname(nicknameInputId.getText().toString()); // Eingabe des Nicknameeingefeldes in den neuen User speichern
        u = loadAnimals(u);
        if ((u.getAktuellerBegleiter() == null || u.getAktuellerBegleiter().equals(new Animal())) && !u.getAnimals().isEmpty())
            for (Animal a : u.getAnimals().values()) {
                u.setAktuellerBegleiter(a);
                break;
            }
        Log.e("DEBUG", u.toString());
        String newJsonUser = User.toJSON(u); // User zuruecksetzen
        Log.e("DEBUG", "newjsonuser:" + newJsonUser);
        editor.putString(jsonTag, newJsonUser);
        //editor.putString("animalNickname", "Jane"); // Nickname des Tiers zuruecksetzen
        editor.commit();
    }


    public User loadAnimals(User u) {
        if (u.getAnimals().isEmpty()) {
            u.getAnimals().put("Kaninchen", new Animal("Kaninchen", "Kaninchen", "rabbit", 0));
            u.getAnimals().put("Ameise", new Animal("Ameise", "Ameise", "ant", 0));
            u.getAnimals().put("Echse", new Animal("Echse", "Echse", "lizard", 0));
        }

        return u;
    }

}
