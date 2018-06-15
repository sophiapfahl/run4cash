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
import java.util.Random;
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
    private Animal aktuellerBegleiter = new Animal();
    private int steps = 0;      // Anzahl Schritte
    private int capital = 0;    // Anzahl Kupfermünzen

    private int counter = 0;


    public User(){

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
    public static String toJSON(User user){
        JSONObject joUser = new JSONObject();
        try {
            joUser.put("id", user.getID());
            joUser.put("nickname", user.getNickname());
                            // Millisekunden seit 01.01.1970 werden gespeichert
            joUser.put("dateOfBirth", user.getDateOfBirth().getTime());
            joUser.put("steps", user.getSteps());
            joUser.put("capital", user.getCapital());
            joUser.put("aktuellerBegleiter", user.getAktuellerBegleiter().getAnimalNickname());

            JSONArray jaAnimals = new JSONArray();
            int animalPos = 0; // Zaehlvariable (in JSONArrays koennen Elemente nur positionsabhaengig eingefuegt werden)
            // Hier werden alle Tiere des Users durchlaufen (sowohl Key als auch Value)
            for (Entry<String, Animal> es : user.animals.entrySet()) {
                JSONObject joAnimal = new JSONObject(Animal.toJSON(es.getValue()));
//                joAnimal.put("animalNickname", es.getKey());    // Hier wird der Key gespeichert
//                joAnimal.put("animalSteps", es.getValue().getSteps());   // Hier wird der Value gespeichert
//                joAnimal.put("picName", es.getValue().getPicName());
//                joAnimal.put("animalBreed", es.getValue().getAnimalBreed());

                jaAnimals.put(animalPos++, joAnimal); // In das Array wird dann das JSONObject mit Key und Value gespeichert
                  // Die Anzahl der Tiere wird erhoeht
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
    public static User fromJSON(String json) {
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
                    Animal animal = Animal.fromJSON(joAnimal.toString());
                    jsonAnimals.put(animal.getAnimalNickname(), animal);
                }
            }
            u.setAnimals(jsonAnimals);
            Log.e("DEBUG", "HashMap: " + u.getAnimals().toString());

            u.setAktuellerBegleiter(u.getAnimals().get(o.getString("aktuellerBegleiter")));
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

    public Animal getAktuellerBegleiter() {
        return aktuellerBegleiter;
    }

    public void setAktuellerBegleiter(Animal aktuellerBegleiter) {
        this.aktuellerBegleiter = aktuellerBegleiter;
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

    @Override
    public String toString() {
        return "User{" +
                "ID=" + ID +
                ", nickname='" + nickname + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", animals=" + animals +
                ", aktuellerBegleiter=" + aktuellerBegleiter +
                ", steps=" + steps +
                ", capital=" + capital +
                ", counter=" + counter +
                '}';
    }

    public void goldZufaelling() {

        if(Math.random() > 0.9) {
            setCapital(getCapital() + 100); // + 100 Kupfer --> 1 Gold
        }
    }
}
