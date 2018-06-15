package de.edvschule_plattling.trfi.testprojekt;

import android.media.MediaPlayer;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Dieser Enum dient dazu, dem User verschiedene Tiere zur Auswahl zu stellen. Die Tiere haben eine Rasse
 *  (z.B. "Kaninchen") und den Namen des Bildes zu dem jeweiligen Tier ("rabbit").
 *  Es gibt Kaninchen, Ameisen und Echsen.
 * Created by spfahl on 09.02.2018.
 */

public class Animal {


    // Die Bezeichnung des Enumwertes (z.B. ANT) muss IMMER genauso heissen wie der Name des Bildes (ant)!
    // Sonst tritt ein Fehler auf, wenn das Tier im TierErstellen geladen wird!
//    RABBIT("rabbit", "Kaninchen"),
//    ANT("ant", "Ameise"),
//    LIZARD("lizard", "Echse");

    private static int LEVELGRENZE = 20;

    // TODO animalNickname (animalBreed = animalNickname)
    private String animalNickname = "";
    private String animalBreed = ""; // Tierrasse
    private String picName = "";
    private int steps = 0;
    private List<String> fortschrittBilder = new ArrayList<>();   // Bildernamen als String


    public Animal(String pAnimalNickname, String pAnimalBreed, String pPicName, int pSteps, List<String> pFortschrittBilder){
        this.animalNickname = pAnimalNickname;
        this.animalBreed = pAnimalBreed;
        this.picName = pPicName;
        this.steps = pSteps;
        this.fortschrittBilder = pFortschrittBilder;
    }

    public Animal() {
    }


    public static String toJSON(Animal animal){
        JSONObject joAnimal = new JSONObject();
        try{
            joAnimal.put("animalNickname", animal.getAnimalNickname());
            joAnimal.put("animalSteps", animal.getSteps());
            joAnimal.put("picName", animal.getPicName());       // aktuelles Bild
            joAnimal.put("animalBreed", animal.getAnimalBreed());
            JSONArray jaBilder = new JSONArray();   // Liste von Bildnamen
            int bildPos = 0;
            for(String bildname : animal.getFortschrittBilder()){
                jaBilder.put(bildPos++, bildname);
            }
            joAnimal.put("fortschrittBilder", jaBilder);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return joAnimal.toString();
    }

    public static Animal fromJSON(String json){
        try {
            Log.e("DEBUG", json);
            JSONObject o = new JSONObject(json);
            Animal a = new Animal();
            a.setAnimalNickname(o.getString("animalNickname"));
            a.setSteps(o.getInt("animalSteps"));
            a.setPicName(o.getString("picName"));
            a.setAnimalBreed(o.getString("animalBreed"));

            List<String> bildnamen = new ArrayList<>();
            JSONArray jaBilder = o.getJSONArray("fortschrittBilder");
            for(int i=0; i<jaBilder.length(); i++){
                bildnamen.add(jaBilder.getString(i));
            }
            a.setFortschrittBilder(bildnamen);

            return a;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    public String getPicName() {
        return picName;
    }

    public void setPicName(String picName) {
        this.picName = picName;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public boolean updateSteps(int steps) {
        boolean b = false;

        this.setSteps(steps);

        // TODO fortschrittbilder befüllen
        if(this.fortschrittBilder.size() > 0 ) {
            if (steps > 3*LEVELGRENZE) {
                this.picName = this.fortschrittBilder.get(2);    // potentieller nullpointer
                b = true;
            } else {
                if(steps > LEVELGRENZE) {
                    this.picName = this.fortschrittBilder.get(1);
                    b = true;
                }
            }
        }

        return b;


    }




    public String getAnimalBreed() {
        return animalBreed;
    }

    public void setAnimalBreed(String animalBreed) {
        this.animalBreed = animalBreed;
    }

    public String getAnimalNickname() {
        return animalNickname;
    }

    public void setAnimalNickname(String animalNickname) {
        this.animalNickname = animalNickname;
    }

    public List<String> getFortschrittBilder() {
        return fortschrittBilder;
    }

    public void setFortschrittBilder(List<String> fortschrittBilder) {
        this.fortschrittBilder = fortschrittBilder;
    }

    public static int getLEVELGRENZE() {
        return LEVELGRENZE;
    }

    public static void setLEVELGRENZE(int LEVELGRENZE) {
        Animal.LEVELGRENZE = LEVELGRENZE;
    }

    @Override
    public String toString() {
        return "Animal{" +
                "animalNickname='" + animalNickname + '\'' +
                ", animalBreed='" + animalBreed + '\'' +
                ", picName='" + picName + '\'' +
                ", steps=" + steps +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Animal animal = (Animal) o;

        if (steps != animal.steps) return false;
        if (animalNickname != null ? !animalNickname.equals(animal.animalNickname) : animal.animalNickname != null)
            return false;
        if (animalBreed != null ? !animalBreed.equals(animal.animalBreed) : animal.animalBreed != null)
            return false;
        return picName != null ? picName.equals(animal.picName) : animal.picName == null;
    }

    @Override
    public int hashCode() {
        int result = animalNickname != null ? animalNickname.hashCode() : 0;
        result = 31 * result + (animalBreed != null ? animalBreed.hashCode() : 0);
        result = 31 * result + (picName != null ? picName.hashCode() : 0);
        result = 31 * result + steps;
        return result;
    }
}
