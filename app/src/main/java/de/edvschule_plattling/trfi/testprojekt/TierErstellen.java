package de.edvschule_plattling.trfi.testprojekt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Diese Acitivity dient dazu, dass ein neu erstellter User sich ein Tier aus den im Enum festgelegten
 *  Tieren aussuchen kann und dem gewaehlten Tier einen Namen geben kann.
 */
public class TierErstellen extends HilfsActivityClass {

    //private EditText animalNicknameInput;
    private TextView resultTextView;
    private Button weiter;
    public static final String MY_PREF = "MYPREF";
    public static final String jsonTag = "jsonUser";
    private SharedPreferences sharedPref;

    // Zwischenspeicher
    private Animal animal;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_animal);

        sharedPref = getApplicationContext().getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
        resultTextView = (TextView) findViewById(R.id.ueberschriftTextViewID);
        //animalNicknameInput = (EditText) findViewById(R.id.animalNicknameInputID);

        // Dies sind ImageButtons, die mit einem Bild je nach Tier befuellt werden
        final ImageButton btnAnt = (ImageButton) findViewById(R.id.imageBTNAnt);
        final ImageButton btnRabbit = (ImageButton) findViewById(R.id.imageBTNRabbit);
        final ImageButton btnLizard = (ImageButton) findViewById(R.id.imageBTNLizard);

        weiter = (Button) findViewById(R.id.weiterButtonID);

        // Daten vom letzten Speichern laden
        user = new User();
        if(User.fromJSON(sharedPref.getString(jsonTag, "")) != null){
            user = loadData();
        } else {
            Toast.makeText(getApplicationContext(), "Kein JSONObject gefunden!", Toast.LENGTH_SHORT).show();
        }



        btnRabbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setAktuellerBegleiter(user.getAnimals().get("Kaninchen"));
                resultTextView.setText("Dein Tier: " + user.getAktuellerBegleiter().getAnimalNickname());
            }
        });

        btnAnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setAktuellerBegleiter(user.getAnimals().get("Ameise"));
                resultTextView.setText("Dein Tier: " + user.getAktuellerBegleiter().getAnimalNickname());
            }
        });

        btnLizard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setAktuellerBegleiter(user.getAnimals().get("Echse"));
                resultTextView.setText("Dein Tier: " + user.getAktuellerBegleiter().getAnimalNickname());
            }
        });


        weiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SharedPreferences.Editor editor = sharedPref.edit();

                //String animalNickname = animalNicknameInput.getText().toString();
                // Hier wird der Nickname des aktuellen Begleiters neu gesetzt
                //user.replaceAnimal(user.getAktuellerBegleiter().getAnimalNickname(), animalNickname, user.getAktuellerBegleiter());

                String json = User.toJSON(user);

                // Dies braucht man, da der Nickname des Tiers der Schluessel in der Hashmap von Tieren
                //  im User ist. Ohne diesen kann man sich das gewuenschte Tier nicht anzeigen lassen.
                //editor.putString("animalNickname", user.getAktuellerBegleiter().getAnimalNickname());
                editor.putString(jsonTag, json);

                editor.commit();    // Alles-oder-Nichts-Prinzip
                            // Dies braucht man, damit nicht unterm Laden ein Fehler auftritt und somit
                            //  der neue Datensatz unvollstaendig ist, der alte aber unbrauchbar, weil
                            //  stellenweise ueberschrieben

                // Mit diesem Intent wird jetzt nur noch die andere Activity aufgerufen
                Intent myIntent = new Intent(getApplicationContext(), Uebersicht.class);

                startActivity(myIntent);
                }
        });
    }


    // ggf. Standardwerte bzw. gespeicherte Werte vom letzten Oeffnen der App laden. Wird derzeit nicht
    // benoetigt, da sowieso nur neu erstellte user in diese Activity gelangen und somit keine alten
    //  Werte geladen werden koennen.
    public User loadData(){
        User u = new User();
        u = User.fromJSON(sharedPref.getString(jsonTag, ""));

        //String animalMapKey = sharedPref.getString("animalNickname", "");

        //animalNicknameInput.setText(animalMapKey);

        if(u.getAnimals() == null) {
            Toast.makeText(getApplicationContext(), "Tier ist null!", Toast.LENGTH_SHORT).show();
        } else if(u.getAnimals().isEmpty()){
            Toast.makeText(getApplicationContext(), "Es sind noch keine Tiere vorhanden!", Toast.LENGTH_SHORT).show();
        } else {
            animal = u.getAnimals().get(u.getAktuellerBegleiter().getAnimalNickname());
            resultTextView.setText("Dein Tier: " + animal.getAnimalBreed());
        }

        return u;
    }

}
