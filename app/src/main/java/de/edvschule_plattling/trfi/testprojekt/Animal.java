package de.edvschule_plattling.trfi.testprojekt;

/**
 * Dieser Enum dient dazu, dem User verschiedene Tiere zur Auswahl zu stellen. Die Tiere haben eine Rasse
 *  (z.B. "Kaninchen") und den Namen des Bildes zu dem jeweiligen Tier ("rabbit").
 *  Es gibt Kaninchen, Ameisen und Echsen.
 * Created by spfahl on 09.02.2018.
 */

public enum Animal {


    // Die Bezeichnung des Enumwertes (z.B. ANT) muss IMMER genauso heissen wie der Name des Bildes (ant)!
    // Sonst tritt ein Fehler auf, wenn das Tier im TierErstellen geladen wird!
    RABBIT("rabbit", "Kaninchen"),
    ANT("ant", "Ameise"),
    LIZARD("lizard", "Echse");


    private String picName;
    private String animalBreed; // Tierrasse

    private Animal(String pPicName, String pAnimalBreed){
        this.picName = pPicName;
        this.animalBreed = pAnimalBreed;
    }



    public String getPicName(){
        return picName;
    }
    public String getAnimalBreed(){
        return animalBreed;
    }

    @Override
    public String toString() {
        return "Animal{ " +
                "picName='" + picName + '\'' +
                ", animalBreed='" + animalBreed + '\'' +
                '}';
    }
}
