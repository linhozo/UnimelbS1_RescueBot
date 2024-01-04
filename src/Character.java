import java.util.Random;
/**
 * COMP90041, Sem1, 2023: Final Project
 * @author: Thuy Linh Nguyen
 * Student ID: 1397876
 * Student Email: linh.nguyen3@student.unimelb.edu.au
 * A class for defining common characteristics of human and animal and randomly generating characters.
 * It has two inheritors: Animal and Human class.
 */
public class Character {

    //Declare common characteristics: gender, age and bodyType
    protected GenderEnum gender;
    private int age;
    protected BodyTypeEnum bodyType;

    //Declare default age. Used when data imported is invalid (age<0)
    protected static final int DEFAUT_AGE = 0;

    /**
     * Declare constant values for gender. Only values in this list are allowed.
     * The first value in the list is the default value, which will be used when there are discrepancies.
     */
    protected enum GenderEnum {
        UNKNOWN, MALE, FEMALE
    };

    /**
     * Declare constant values for bodyType. Only values in this list are allowed.
     * The first value in the list is the default value, which will be used when there are discrepancies.
     */
    protected enum BodyTypeEnum {
        UNSPECIFIED, AVERAGE, ATHLETIC, OVERWEIGHT
    };

    /**
     * Constructor: define characteristics of a Character object, which can be an instance of Human or Animal class.
     * Set gender and bodyType to default values if there are discrepancies with GenderEnum and BodyTypeEnum.
     * Set age to default value if age < 0.
     * @param gender
     * @param age
     * @param bodyType
     */
    public Character(String gender, int age, String bodyType) {
        for (GenderEnum g : GenderEnum.values()) {
            if (gender.equalsIgnoreCase(g.name())) {
                this.gender = g;
                break;
            } else {
                this.gender = GenderEnum.values()[0];
            }
        }
        this.age = age <= 0 ? DEFAUT_AGE : age;
        for (BodyTypeEnum b : BodyTypeEnum.values()) {
            if (bodyType.equalsIgnoreCase(b.name())) {
                this.bodyType = b;
                break;
            } else {
                this.bodyType = BodyTypeEnum.values()[0];
            }
        }
    }

    public Character() {
        this.gender = GenderEnum.values()[0];
        this.age = DEFAUT_AGE;
        this.bodyType = BodyTypeEnum.values()[0];
    }

    /**
     * Generic method to get random value from an enum list
     * @param enumList : enum class
     * @param rand
     * @return random value within an enum list
     * @param <T> generic class
     */
    public  <T extends Enum<T>> String getRandomTraits(Class<T> enumList, Random rand) {
        T[] list = enumList.getEnumConstants();
        int index = rand.nextInt(list.length);
        return list[index].toString().toUpperCase();
    }

    /**
     * Set random values for common characteristics.
     * Gender is set randomly from GenderEnum. BodyType is set randomly from BodyEnum.
     * Age is set randomly with an upper bound of 101 (exclusive).
     * @param rand
     */
    public void setRandomCharacter(Random rand) {
        this.gender = GenderEnum.valueOf(getRandomTraits(GenderEnum.class, rand));
        this.bodyType = BodyTypeEnum.valueOf(getRandomTraits(BodyTypeEnum.class, rand));
        this.age = rand.nextInt(101);
    }


    public void displayCharacter() {
        System.out.println("- ");
    }

    public String getBodyType() {
        return this.bodyType.name().toLowerCase();
    }

    public String getGender() {
        return this.gender.name().toLowerCase();
    }

    public String getGenderDefault() {
        return GenderEnum.values()[0].name();
    }

    public int getAge() {
        return this.age;
    }

    public String getBodyTypeDefault() {
        return BodyTypeEnum.values()[0].name();
    }

}
