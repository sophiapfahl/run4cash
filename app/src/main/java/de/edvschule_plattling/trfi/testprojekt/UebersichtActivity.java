package de.edvschule_plattling.trfi.testprojekt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Diese Activity dient dazu, dem User sein komplettes Profil anzuzeigen. Es werden laufend Schritte
 * und Vermoegen des Benutzers aktualisiert.
 * Created by spfahl on 12.01.2018.
 */

public class UebersichtActivity extends HilfsActivityClass implements SensorEventListener, StepListener {

    private User user;

    private TextView nicknameTV;
    private TextView dateOfBirthTV;
    private TextView animalNicknameTV;
    private TextView stepsTV;
    private TextView capitalTV;
    private TextView animalStepsTV;
    private ImageView animalPic;
    private Button zurTierauswahlBtn;
    private SharedPreferences sharedPref;
    public static final String MY_PREF = "MYPREF";
    public static final String jsonTag = "jsonUser";

    private SimpleStepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;

    private AsyncPlayer moneyPlayer = new AsyncPlayer("MoneyPlayer");
    private AsyncPlayer levelPlayer = new AsyncPlayer("LevelPlayer");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uebersicht);

        stepsTV = (TextView) findViewById(R.id.stepsId);
        nicknameTV = (TextView) findViewById(R.id.nicknameID);
        dateOfBirthTV = (TextView) findViewById(R.id.gebdatID);
        animalNicknameTV = (TextView) findViewById(R.id.tierNameID);
        animalPic = (ImageView) findViewById(R.id.tierBildID);
        capitalTV = (TextView) findViewById(R.id.capitalID);
        zurTierauswahlBtn = (Button) findViewById(R.id.btnTierauswahlID);
        animalStepsTV = (TextView) findViewById(R.id.animalStepsID);

        sharedPref = getApplicationContext().getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);

        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        simpleStepDetector = new SimpleStepDetector();
        simpleStepDetector.registerListener(this);

        user = new User();
        // Hier werden die Daten geladen
        user = loadData();


        // Hier werden die Daten angezeigt (in die entsprechenden Textfelder geladen etc.)
        showData();

        // Hier wird der "fertige" User im UebersichtSharedPref gespeichert
        saveCompleteUser();

        zurTierauswahlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCompleteUser();
                Intent myIntent = new Intent(getApplicationContext(), TierAuswaehlenActivity.class);
                startActivity(myIntent);
            }
        });

    }

    /**
     * Diese Methode wird verwendet, um die zuletzt gespeicherten Daten aus den SharedPreferences zu laden.
     *
     * @return Der User mit den Werten, die zuletzt gespeichert wurden.
     */
    public User loadData() {
        try {
            return User.fromJSON((sharedPref.getString(jsonTag, "")));
        } catch (Exception e) {
            return new User();
        }
    }

    /**
     * Diese Methode wird verwendet, um die gespeicherten Daten aus dem User für den Benutzer anzeigen zu lassen.
     * Es werden diverse Textfelder und ImageViews befüllt.
     */
    public void showData() {
        nicknameTV.setText(user.getNickname());

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date userGebDat = new Date(user.getDateOfBirth().getTime());
        userGebDat.setYear(user.getDateOfBirth().getYear() - 1900);
        dateOfBirthTV.setText(sdf.format(userGebDat));
        stepsTV.setText("Deine Schritte: " + user.getSteps());
        capitalTV.setText(user.umrechnen());
        animalNicknameTV.setText(user.getAktuellerBegleiter().getAnimalNickname());

        updateAnimalPic(user.getAktuellerBegleiter().getPicName());

        animalStepsTV.setText("Schritte: " + user.getAktuellerBegleiter().getSteps());
    }

    /**
     * Holt Bild ueber eine ID, nicht direkt ueber den Pfad (Endung ist so auch irrelevant!)
     *
     * @param animalPicName der Name des Tierbildes
     */
    public void updateAnimalPic(String animalPicName) {

        Drawable drawable = getResources().getDrawable(getResources().getIdentifier(animalPicName, "drawable", getPackageName()));
        animalPic.setImageDrawable(drawable);
    }

    /**
     * Hier wird der aktualisierte User (ggf. mit mehr Schritten / Vermoegen als zuvor) als JSONObject
     * in den SharedPreferences gespeichert.
     * Diese Methode wird aufgerufen, wenn die App geschlossen wird oder auf eine andere Activity navigiert wird.
     */
    public void saveCompleteUser() {
        final SharedPreferences.Editor editor = sharedPref.edit();
        String jsonUser = User.toJSON(user);
        editor.putString(jsonTag, jsonUser);
        editor.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        stepsTV.setText("Deine Schritte: " + user.getSteps());
        capitalTV.setText(user.umrechnen());
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
        simpleStepDetector.registerListener(null);
        saveCompleteUser();
    }


    /**
     * Schritte des Users und des aktuellen Begleiters werden erhöht und die entsprechenden Textfelder aktualisiert.
     * Gegebenenfalls wird das Level des Tiers erhöht, der User hat außerdem die Möglichkeit, Geld zu gewinnen.
     *
     * @param timeNs
     */
    @Override
    public void step(long timeNs) {
        user.setSteps(user.getSteps() + 1);
        stepsTV.setText("Deine Schritte: " + user.getSteps());
        user.getAktuellerBegleiter().setSteps(user.getAktuellerBegleiter().getSteps() + 1);
        animalStepsTV.setText("Schritte: " + user.getAktuellerBegleiter().getSteps());


        if (user.getSteps() % 10 == 0) {
            user.setCapital(user.getCapital() + 1);
            capitalTV.setText(user.umrechnen());


            Uri path = Uri.parse("android.resource://de.edvschule_plattling.trfi.testprojekt/" + R.raw.money);
            moneyPlayer.stop();
            moneyPlayer.play(this, path, false, AudioManager.STREAM_MUSIC);

            user.goldZufaelling();
        }

        // isLevelUp returned boolean ob aktueller Begleiter levelup erhält oder nicht
        if (user.getAktuellerBegleiter().isLevelUp()) {


            updateAnimalPic(user.getAktuellerBegleiter().getPicName());  // Benutzeroberfläche aktualiseren
            Uri path = Uri.parse("android.resource://de.edvschule_plattling.trfi.testprojekt/" + R.raw.levelup);

            levelPlayer.stop();
            levelPlayer.play(this, path, false, AudioManager.STREAM_MUSIC);

        }


    }

}
