package de.edvschule_plattling.trfi.testprojekt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

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
    public static final String jsonTag = "jsonUser";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        nicknameInputId = (EditText) findViewById(R.id.nicknameInputId);
        weiterButtonID = (Button) findViewById(R.id.weiterButtonID);
        sharedPrefUebersicht = getApplicationContext().getSharedPreferences(UebersichtActivity.MY_PREF, MODE_PRIVATE);
        String jsonUser = sharedPrefUebersicht.getString(UebersichtActivity.jsonTag, "");
        final User u = fromJSON(jsonUser);

        if (u != null) {
            // Falls schon ein User registriert ist, wird sofort die UebersichtActivity aufgerufen,
            // damit der alte Spielstand nicht ueberschrieben werden kann
            Intent intentUebersicht = new Intent(getApplicationContext(), UebersichtActivity.class);
            startActivity(intentUebersicht);
        }

        weiterButtonID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Falls noch kein User registriert war, wird er nun gespeichert und er kann sein Profil erstellen
                saveNewUser(u);
                Intent intent = new Intent(getApplicationContext(), ProfilErstellenActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Hier wird der User in den SharedPreferences als JSON-String gespeichert,
     * außerdem wird der default-Begleiter gesetzt (Hund)
     *
     * @param u der zu speichernde User
     */
    public void saveNewUser(User u) {
        final SharedPreferences.Editor editor = sharedPrefUebersicht.edit();
        u = new User();
        u.setNickname(nicknameInputId.getText().toString()); // Eingabe des Nicknameeingefeldes in den neuen User speichern
        u = loadAnimals(u);  // loadAnimals() liefert einen user zurück, der dann alle drei Tiere hat

        u.setAktuellerBegleiter(u.getAnimals().get("Hund"));    // Der Hund wird als Defaultbegleiter gesetzt
        String newJsonUser = User.toJSON(u);
        editor.putString(jsonTag, newJsonUser);
        editor.commit();
    }


    /**
     * Hier werden zu einem neuen User die drei Tiere, die jeder User zu Beginn hat, hinzugefügt
     * (Hund, Kaninchen, Ameise)
     *
     * @param u der User, zu dem die Tiere hinzugefügt werden sollen
     * @return der aktualisierte User
     */
    public User loadAnimals(User u) {

        if (u.getAnimals().isEmpty()) {
            // Hier werden die Tiere initialisiert
            List<String> rabbitPics = new ArrayList<>();
            rabbitPics.add("rabbit01");
            rabbitPics.add("rabbit02");
            rabbitPics.add("rabbit03");
            u.getAnimals().put("Kaninchen", new Animal("Kaninchen", "Kaninchen", "rabbit01", 0, rabbitPics));

            List<String> antPics = new ArrayList<>();
            antPics.add("ant01");
            antPics.add("ant02");
            antPics.add("ant03");
            u.getAnimals().put("Ameise", new Animal("Ameise", "Ameise", "ant01", 0, antPics));

            List<String> dogPics = new ArrayList<>();
            dogPics.add("dog01");
            dogPics.add("dog02");
            dogPics.add("dog03");
            u.getAnimals().put("Hund", new Animal("Hund", "Hund", "dog01", 0, dogPics));
        }

        return u;
    }

}
