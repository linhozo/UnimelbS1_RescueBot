import java.util.Random;

/**
 * COMP90041, Sem1, 2023: Final Project
 * @author: Thuy Linh Nguyen
 * A class for defining animal characteristics and randomly generating animals.
 * It is an inheritance of Character class.
 */

public class Animal extends Character{

    //Declare animal-specific characteristics: their species and whether they're pet or not
    private String species;
    private boolean isPet;

    /**
     * List of constant values of species, used for randomly generating animal.
     * Only 3 first species in the list are allowed to be pet (isPet = true) but need not to be.
     */
    public enum SpeciesEnum {
        DOG, CAT, FERRET, DONKEY, LEOPARD, PANDA, RABBIT, PIGEON, DOLPHIN, EAGLE, GOAT, LIZARD, DINOSAUR,
        RHINOCEROS, WOMBAT, SHEEP, SLOTH, RACOON, LYNX, LEMUR, WHALE, TIGER, CHEETAH, TURTLE, FROG, HAMSTER, JACKAL,
        GOOSE, GORILLA, ORANGUTAN, GIRAFFE, COBRA, DEER, ANT, BEE, KOALA, HEDGEHOG, BISON, HEDWIG, MOLE, MULE, RAT,
        LION, BEAR, TORTOISE, HARE, CROW, OSTRICH, EMU, PEACOCK, HARK, CHAMELEON, KANGAROO, UNICORN, BABOON,
        SNAKE, PANTHER, ELEPHANT, CROCODILE, FISH, ALLIGATOR, FOX, ARMADILLO, WOLF, MONKEY, HORSE, CAMEL, PLATYPUS,
        HIPPOPOTAMUS, IBEX, IGUANA, PHOENIX, DRAGON, BUFFALO, OTTER, FLAMINGO, SWAN, PYTHON, DINGO, WALLABY, POSSUM
        }

    /**
     * Constructor: define characteristics of an Animal object.
     * Constructor of gender, age and bodyType are inherited from the parent class Character.
     * This constructor is invoked when Animal object is created in the program, except for random creation.
     * @param gender : refer to GenderEnum in parent class Character
     * @param age    : how old is he/she?
     * @param bodyType : refer to BodyTypeEnum in parent class Character
     * @param species : refer to SpeciesEnum in this class. Set the 3 first species in SpeciesEnum to be pet
     */
    public Animal(String gender, int age, String bodyType, String species, boolean isPet) {
        super(gender, age, bodyType);
        this.species = species;
        if (!this.species.equalsIgnoreCase(SpeciesEnum.values()[0].name())
                &&!this.species.equalsIgnoreCase(SpeciesEnum.values()[1].name())
                &&!this.species.equalsIgnoreCase(SpeciesEnum.values()[2].name())) {
            this.isPet = false;
        } else {
            this.isPet = isPet;
        }
    }

    /**
     * Constructor: define characteristics of an Animal object.
     * This constructor is invoked when animal is created randomly.
     * @param gender : male or female or unknown?
     * @param age    : how old is he/she?
     * @param bodyType : refer to BodyTypeEnum in parent class Character
     */
    public Animal(String gender, int age, String bodyType) {
        super(gender, age, bodyType);
        this.species = SpeciesEnum.values()[0].toString();
        this.isPet = true;
    }

    /**
     * Set random values for animal's characteristics.
     * It calls setRandomCharacter function from the parent class Character to
     * randomly set values for gender, age and bodyType.
     * Only the 3 first species in SpeciesEnum are allowed to be pet.
     * @param rand
     */
    public void setRandomAnimal(Random rand) {
        setRandomCharacter(rand);
        this.species = SpeciesEnum.valueOf(getRandomTraits(SpeciesEnum.class, rand)).toString();
        if (this.species.equalsIgnoreCase(SpeciesEnum.values()[0].name())
                ||this.species.equalsIgnoreCase(SpeciesEnum.values()[1].name())
                ||this.species.equalsIgnoreCase(SpeciesEnum.values()[2].name())) {
            this.isPet = true;
        } else {
            this.isPet = false;
        }
    }


    @Override
    public void displayCharacter() {
        System.out.println("- " + this.species.toLowerCase() + (this.isPet ? " is pet" : ""));
    }

    public String getSpecies() {
        return this.species.toLowerCase();
    }

    public boolean isPet() {
        return this.isPet;
    }

}
