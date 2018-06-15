package de.edvschule_plattling.trfi.testprojekt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Diese Activity dient dazu, dem User sein komplettes Profil anzuzeigen. Es werden laufend Schritte
 *  und Vermoegen des Benutzers aktualisiert.
 * Created by spfahl on 12.01.2018.
 */

public class Uebersicht extends HilfsActivityClass implements SensorEventListener, StepListener {

    private User user;

    private TextView nickname;
    private TextView dateOfBirth;
    private TextView animalNickname;
    private TextView steps;
    private TextView capital;
    private TextView animalSteps;
    private ImageView animalPic;
    private Button zurTierauswahl;
    private SharedPreferences sharedPref;
    public static final String MY_PREF = "MYPREF";
    public static final String jsonTag = "jsonUser";

    private SimpleStepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uebersicht);

        steps = (TextView) findViewById(R.id.stepsId);
        nickname = (TextView) findViewById(R.id.nicknameID);
        dateOfBirth = (TextView) findViewById(R.id.gebdatID);
        animalNickname = (TextView) findViewById(R.id.tierNameID);
        animalPic = (ImageView) findViewById(R.id.tierBildID);
        capital = (TextView) findViewById(R.id.capitalID) ;
        zurTierauswahl = (Button) findViewById(R.id.btnTierauswahlID);
        animalSteps = (TextView) findViewById(R.id.animalStepsID);

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

        zurTierauswahl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCompleteUser();
                Intent myIntent = new Intent(getApplicationContext(), TierAuswaehlen.class);
                startActivity(myIntent);
            }
        });

    }


    /**
     * Diese Methode wird verwendet, um die zuletzt gespeicherten Daten aus den SharedPreferences zu laden.
     * @return Der User mit den Werten, die zuletzt gespeichert wurden.
     */
    public User loadData(){
        try {
            return User.fromJSON((sharedPref.getString(jsonTag, "")));
        } catch(Exception e){
            return new User();
        }
    }

    /**
     * Diese Methode wird verwendet, um die gespeicherten Daten aus dem User für den Benutzer anzeigen zu lassen.
     * Es werden diverse Textfelder und ImageViews befüllt.
     */
    public void showData(){
        nickname.setText(user.getNickname());

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date userGebDat = new Date(user.getDateOfBirth().getTime());
        userGebDat.setYear(user.getDateOfBirth().getYear() - 1900);
        dateOfBirth.setText(sdf.format(userGebDat));
        steps.setText("" + user.getSteps());
        capital.setText(user.umrechnen());

        //String animalMapKey = sharedPref.getString("animalNickname", "");
        animalNickname.setText(user.getAktuellerBegleiter().getAnimalNickname());

        updateAnimalPic(user.getAktuellerBegleiter().getPicName());

        animalSteps.setText("Schritte: " + user.getAktuellerBegleiter().getSteps());
    }

    public void updateAnimalPic(String animalPicName) {
        // Holt Bild ueber eine ID, nicht direkt ueber den Pfad (Endung ist so auch irrelevant!)
        Drawable drawable = getResources().getDrawable(getResources().getIdentifier(animalPicName, "drawable", getPackageName()));
        animalPic.setImageDrawable(drawable);
    }

    /**
     * Hier wird der aktualisierte User (ggf. mit mehr Schritten / Vermoegen als zuvor) als JSONObject
     *  in den SharedPreferences gespeichert.
     *  Diese Methode wird aufgerufen, wenn die App geschlossen wird.
     */
    public void saveCompleteUser(){
        final SharedPreferences.Editor editor = sharedPref.edit();
        String jsonUser = User.toJSON(user);
        editor.putString(jsonTag, jsonUser);
        editor.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        steps.setText("Schritte: " + user.getSteps());
        capital.setText(user.umrechnen());
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
        saveCompleteUser();
    }


    @Override
    public void step(long timeNs) {
        user.setSteps(user.getSteps() + 1);
        steps.setText("Schritte: " + user.getSteps());

        if(user.getSteps()%10 == 0) {
            user.setCapital(user.getCapital() + 1);
            capital.setText(user.umrechnen());
            user.goldZufaelling();


        }



        animalSteps.setText("Schritte: " + user.getAktuellerBegleiter().getSteps());


        if(user.getAktuellerBegleiter().updateSteps(user.getAktuellerBegleiter().getSteps() +1)) {
            updateAnimalPic(user.getAktuellerBegleiter().getPicName());
            MediaPlayer level = MediaPlayer.create(Uebersicht.this, R.raw.levelup);

            level.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {

                    mediaPlayer.reset();
                    mediaPlayer.release();

                }
            });

            level.start();


        }





    }


}
