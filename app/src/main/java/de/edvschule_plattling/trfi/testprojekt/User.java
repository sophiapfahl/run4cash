package de.edvschule_plattling.trfi.testprojekt;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Map.Entry;

/**
 * Diese Klasse beschreibt einen User.
 *  Hier wird die ID, der Nickname, das Geburtsdatum, die Schritte, das Vermoegen und die Tiere des
 *  Users gepseichert.
 * Created by spfahl on 08.02.2018.
 */

public class User {

    private int ID; // evtl automatisch generieren lassen

    // Defaultwerte:
    private String nickname = "John Doe";    // Benutzername des Users

    private Date dateOfBirth = new Date();
                // NicknameTier, Enum.Animal
    private HashMap<String, Animal> animals = new HashMap<>();    // HashMap, damit man einen Tiernamen mit einem Tier
                    //  verknuepfen kann. Man koennte auch eine Liste mit Tieren und eine mit Namen
                    //  machen, aber woher weis man dann, welcher Name zu welchem Tier gehoert. Bzw.
                    //  ist eben sehr fehleranfaellig.
                    // Oder jeder User kann nur ein Tier haben, das ist aber eigentlich nicht gewollt.
                    //  Dann koennte man ein Attribut Tier machen und ein Attribut Tiername.
    private int steps = 0;      // Anzahl Schritte
    private int capital = 0;    // Anzahl Kupfermünzen

    private int counter = 0;


    public User(){
        animals.put("Jane", Animal.RABBIT);
    }

    public User(String pNickname, Date pBirthday){
        this.ID = counter;
        counter++;
        this.nickname = pNickname;
        this.dateOfBirth = pBirthday;
        this.animals = new HashMap<String, Animal>();
    }


    /**
     * In dieser Methode wird ein JSONObject erstellt, dem die Attribute des Users hinzugefuegt werden.
     * Am Ende wird aus dem vollstaendigen JSONObject ein String erzeugt.
     * @param user Der User, der gespeichert werden soll
     * @return Der in einen JsonString umgewandelte User
     */
    public static String save(User user){
        JSONObject joUser = new JSONObject();
        try {
            joUser.put("id", user.getID());
            joUser.put("nickname", user.getNickname());
                            // Millisekunden seit 01.01.1970 werden gespeichert
            joUser.put("dateOfBirth", user.getDateOfBirth().getTime());
            joUser.put("steps", user.getSteps());
            joUser.put("capital", user.getCapital());

            JSONArray jaAnimals = new JSONArray();
            int animalPos = 0; // Zaehlvariable (in JSONArrays koennen Elemente nur positionsabhaengig eingefuegt werden)
            // Hier werden alle Tiere des Users durchlaufen (sowohl Key als auch Value)
            for (Entry<String, Animal> es : user.animals.entrySet()) {
                JSONObject joAnimal = new JSONObject();
                joAnimal.put("animalNickname", es.getKey());    // Hier wird der Key gespeichert
                joAnimal.put("ordinal", es.getValue().ordinal());   // Hier wird der Value gespeichert,
                //  aber da Animal ein Enum ist, reicht es, wenn nur der ordinal (int) des Animals
                //  gespeichert wird, statt der komplette Enumwert
                jaAnimals.put(animalPos, joAnimal); // In das Array wird dann das JSONObject mit Key und Value gespeichert
                animalPos++;    // Die Anzahl der Tiere wird erhoeht
            }
            joUser.put("animals", jaAnimals);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return joUser.toString();
    }


    /**
     * In dieser Methode wird aus dem uebergebenen String ein JSONObject erzeugt, welches dann in einen User umgewandelt wird.
     * @param json Dies ist ein String, der ueber die SharedPreferences uebergeben wird. Der String beschreibt ein JSONObjekt.
     * @return Der User, der durch den uebergebenen String erstellt wird
     */
    public static User load(String json) {
        try {
            JSONObject o = new JSONObject(json);
            User u = new User();
            u.setID(o.getInt("id"));
            u.setNickname(o.getString("nickname"));

                // getLong(), weil die Millisekunden des Datums gespeichert werden (seit 01.01.1970)
            Date d = new Date(o.getLong("dateOfBirth"));
            u.setDateOfBirth(d);
            u.setSteps(o.getInt("steps"));
            u.setCapital(o.getInt("capital"));

            HashMap<String, Animal> jsonAnimals = new HashMap<String, Animal>();
            JSONArray jaAnimals = o.getJSONArray("animals");
            if(jaAnimals.length() > 0){
                for(int i=0; i<jaAnimals.length(); i++){
                    JSONObject joAnimal = jaAnimals.getJSONObject(i);
                    String animalNickname = joAnimal.getString("animalNickname");
                    Animal animal = Animal.values()[joAnimal.getInt("ordinal")];
                    jsonAnimals.put(animalNickname, animal);
                }
            }
            u.setAnimals(jsonAnimals);
            return u;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * In dieser Methode werden die bisher verdienten Kupfermuenzen
     * gemäß dem internen Währungssystem verrechnet (10 Kupfer -> 1 Silber, 10 Silber -> 1 Gold)
     * @return String bsp: 12G 3S 4K
     */
    public String umrechnen() {
        String capitalString = "" + capital;
        int capitalStringLaenge = capitalString.length();

        String gold = "0";
        String silber = "0";
        String kupfer = "0";
        if(capitalStringLaenge > 2) {
            gold = capitalString.substring(0, capitalStringLaenge-2);
        }
        if(capitalStringLaenge > 1) {
            silber = capitalString.substring(capitalStringLaenge-2, capitalStringLaenge-1);
        }
        if(capital > 0){
            kupfer = capitalString.substring(capitalStringLaenge-1, capitalStringLaenge);
        }

        return gold + " G  " + silber + " S  " + kupfer + " K";
    }


    // Getter / Setter

    public int getID() { return ID; }
    public void setID(int ID) { this.ID = ID; }

    public String getNickname(){
        return nickname;
    }
    public void setNickname(String newNickname){
        this.nickname = newNickname;
    }

    public Date getDateOfBirth(){
        return dateOfBirth;
    }
    public void setDateOfBirth(Date newDateOfBirth){
        this.dateOfBirth = newDateOfBirth;
    }

    public int getCapital() { return capital; }
    public void setCapital(int capital) { this.capital = capital; }

    public int getSteps() {
        return steps;
    }
    public void setSteps(int steps) {
        this.steps = steps;
    }

    public HashMap<String, Animal> getAnimals() {
        return animals;
    }
    public void setAnimals(HashMap<String, Animal> animals) {
        this.animals = animals;
    }

    /**
     * Methode zum Hinzufuegen eines Tiers. Diese Methode wird benoetigt, damit man auch von anderen
     *  Klassen aus moeglichst einfach Tiere hinzufuegen kann.
     * @param name Der Name des neues Tiers
     * @param a Das neue Tier (Wert aus Enum)
     */
    public void addAnimal(String name, Animal a){
        animals.put(name, a);
    }

    /**
     * Diese Methode leert die Tierliste des Users.
     */
    public void clearAnimals() { this.animals.clear(); }

    /**
     * Mit dieser Methode kann man ein altes Tier durch ein Neues ersetzen.
     * @param oldAnimalName Der Nickname des alten Tiers
     * @param newAnimalName Der Nickname des neuen Tiers
     * @param newAnimal Die Tierart des neuen Tiers (aus Enum)
     */
    public void replaceAnimal(String oldAnimalName, String newAnimalName, Animal newAnimal){
        animals.remove(oldAnimalName);
        animals.put(newAnimalName, newAnimal);
    }

}
