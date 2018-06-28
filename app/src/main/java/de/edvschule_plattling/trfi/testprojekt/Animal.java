package de.edvschule_plattling.trfi.testprojekt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse dient dazu, dem User verschiedene Tiere zur Auswahl zu stellen. Die Tiere haben eine Rasse
 * und eine Liste der Namen der Bilder zu dem jeweiligen Tier.
 * Außerdem werden die Schritte gespeichert.
 * Created by spfahl on 09.02.2018.
 */

public class Animal {


    private static int LEVELGRENZE = 20;

    private String animalNickname = "";
    private String animalBreed = "";
    private String picName = "";
    private int steps = 0;
    private List<String> fortschrittBilder = new ArrayList<>();   // Bildernamen als String


    public Animal(String pAnimalNickname, String pAnimalBreed, String pPicName, int pSteps, List<String> pFortschrittBilder) {
        this.animalNickname = pAnimalNickname;
        this.animalBreed = pAnimalBreed;
        this.picName = pPicName;
        this.steps = pSteps;
        this.fortschrittBilder = pFortschrittBilder;
    }

    public Animal() {
    }

    /**
     * In dieser Methode wird ein Tier in einen JSON-String umgewandelt.
     *
     * @param animal Das Tier, das in einen JSON-String umgewandelt werden soll
     * @return Das Tier als JSON-String
     */
    public static String toJSON(Animal animal) {
        JSONObject joAnimal = new JSONObject();
        try {
            joAnimal.put("animalNickname", animal.getAnimalNickname());
            joAnimal.put("animalSteps", animal.getSteps());
            joAnimal.put("picName", animal.getPicName());       // aktuelles Bild
            joAnimal.put("animalBreed", animal.getAnimalBreed());
            JSONArray jaBilder = new JSONArray();   // Liste von Bildnamen
            int bildPos = 0;
            for (String bildname : animal.getFortschrittBilder()) {
                jaBilder.put(bildPos++, bildname);
            }
            joAnimal.put("fortschrittBilder", jaBilder);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return joAnimal.toString();
    }

    /**
     * In dieser Methode soll ein JSON-String in ein Tier umgewandelt werden.
     *
     * @param json Der JSON-, der in ein Tier umgewandelt werden soll
     * @return Das Tier, das aus dem JSON-String erzeugt wurde
     */
    public static Animal fromJSON(String json) {
        try {
            JSONObject o = new JSONObject(json);
            Animal a = new Animal();
            a.setAnimalNickname(o.getString("animalNickname"));
            a.setSteps(o.getInt("animalSteps"));
            a.setPicName(o.getString("picName"));
            a.setAnimalBreed(o.getString("animalBreed"));

            List<String> bildnamen = new ArrayList<>();
            JSONArray jaBilder = o.getJSONArray("fortschrittBilder");
            for (int i = 0; i < jaBilder.length(); i++) {
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

    /**
     * In dieser Methode werden die Schritte des Tiers aktualisiert. Gewinnt das Tier ein Level dazu,
     * wird auch das Bild des Tiers geändert.
     *
     * @return ein Wahrheitswert, ob das Tier ein Level dazugewonnen hat
     */
    public boolean isLevelUp() {
        boolean b = false;

        if (this.fortschrittBilder.size() > 0) {
            if (this.getSteps() == 3 * LEVELGRENZE && this.fortschrittBilder.size() >= 2) {
                this.picName = this.fortschrittBilder.get(2);
                b = true;
            } else if (this.getSteps() == LEVELGRENZE && this.fortschrittBilder.size() >= 1) {
                this.picName = this.fortschrittBilder.get(1);
                b = true;
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
