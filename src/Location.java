import java.util.ArrayList;
import java.util.Random;
/**
 * COMP90041, Sem1, 2023: Final Project
 * @author: Thuy Linh Nguyen
 * Student ID: 1397876
 * Student Email: linh.nguyen3@student.unimelb.edu.au
 * A class for defining characteristics of location and randomly generating location.
 *
 */

public class Location {
    //Declare location characteristics: latitude, longitude and whether it is trespassed or not
    private String latitude;
    private String longitude;
    private boolean isTrespassed;

    //Declare location status: whether it is saved or not
    private boolean isSaved;

    //Declare location's potential point given by the algorithm
    private int potentialPoint;

    //Declare an array list of character objects that gather in the location
    private ArrayList<Character> characterList = new ArrayList<>();

    /**
     * Constructor: define characteristics of a Location object: latitude, longitude and iTresspassed
     * @param latitude
     * @param longitude
     * @param isTrespassed
     */
    public Location(String latitude, String longitude, boolean isTrespassed) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.isTrespassed = isTrespassed;
        this.potentialPoint = 0;
        this.isSaved = false;
    }

    public Location() {
        this.latitude = " ";
        this.longitude = " ";
        this.isTrespassed = false;
        this.potentialPoint = 0;
        this.isSaved = false;
    }

    //Display all characters in the location
    public void displayAllCharacter() {
        for (int i = 0; i < this.characterList.size(); i++) {
            characterList.get(i).displayCharacter();
        }
    }

    //Set random value for location's characteristics
    public void setRandomLocation(Random rand) {
        this.latitude = rand.nextInt(91) + "." + (rand.nextInt(9000) + 1000) + (rand.nextBoolean() ? " N" : " S");
        this.longitude = rand.nextInt(181) + "." + (rand.nextInt(9000) + 1000) + (rand.nextBoolean() ? " E" : " W");
        this.isTrespassed = rand.nextBoolean();
    }

    public void addCharacter(Character character) {
        characterList.add(character);
    }

    public int getCharacterCount() {
        return this.characterList.size();
    }


    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public Character getCharacter(int i) {
        return this.characterList.get(i);
    }

    public boolean isTrespassed() {
        return isTrespassed;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setIsSaved(boolean isSaved) {
        this.isSaved = isSaved;
    }

    public int getPotentialPoint() {
        return potentialPoint;
    }

    public void setPotentialPoint(int potentialPoint) {
        this.potentialPoint = potentialPoint;
    }
}
