import java.util.Random;

/**
 * COMP90041, Sem1, 2023: Final Project
 * @author: Thuy Linh Nguyen
 * Student ID: 1397876
 * Student Email: linh.nguyen3@student.unimelb.edu.au
 * A class for defining human characteristics and randomly generating human.
 * It is an inheritance of Character class.
 */

public class Human extends Character {
    //Declare human-specific characteristics: their age category, profession and whether they're pregnant or not
    private AgeCategoryEnum ageCategory;
    private String profession;
    private boolean pregnant;

    /**
     * Categorize age groups: baby [0-4], child [5-16], adult[17-68], senior (68,~)
     * Set value for ageCategory attribute
     */
    public enum AgeCategoryEnum {
        BABY(0), CHILD(5), ADULT(17), SENIOR(69);
        private int age;
        AgeCategoryEnum(int age){
            this.age = age;
        }
        public static AgeCategoryEnum getName(int age) {
            AgeCategoryEnum name = BABY;
            for (AgeCategoryEnum a: values()) {
                if (a.age <= age) {
                    name = a;
                }
            }
            return name;
        }
    }

    /**
     * List of constant values of profession. Only values in this list are allowed.
     * The first value in the list is the default value, which will be used when there are discrepancies.
     */
    protected enum ProfessionEnum {
        NONE, DOCTOR, CEO, CRIMINAL, HOMELESS, UNEMPLOYED, MUGGLE, CODER, SINGER, FILMMAKER, ENGINEER, BEEKEEPER, BABYSITTER,
        COMEDIAN, ARTIST, STUDENT, PROFESSOR, ASTRONAUT, FIREFIGHTER, CHEF, PHOTOGRAPHER, TEACHER, SKYWALKER, ATHLETE,
        PAINTER, NURSE, LAWYER, DRIVER, FARMER, PILOT, DANCER, FISHER, CLERK, BAKER, MAGICIAN, WIZARD, DENTIST,
        DETECTIVE, POTTER, FLORIST, GUARD, CARETAKER, GEOLOGIST, GARDENER, LIBRARIAN, COMPOSER, PIANIST, DESIGNER,
        SURGEON, MINER, ACCOUNTANT, SCIENTIST, EDITOR, ELECTRICIAN, ILLUSTRATOR, BOTANIST, PHARMACIST, VLOGGER, POLITICIAN
    }


    /**
     * Constructor: define characteristics of a Human object
     * Constructor of gender, age and bodyType are inherited from the parent class Character.
     * This constructor is invoked when Human object is created in the program, except for random creation.
     * @param gender : refer to GenderEnum in parent class Character
     * @param age    : how old is he/she?
     * @param bodyType : refer to BodyTypeEnum in parent class Character
     * @param profession : refer to ProfessionEnum in this class. Only adults are allowed to have profession. Use default value for others.
     * @param pregnant : only adult female are allowed to be pregnant
     */
    public Human(String gender, int age, String bodyType, String profession, boolean pregnant) {
        super(gender, age, bodyType);
        this.ageCategory = AgeCategoryEnum.getName(age);
        if (this.ageCategory == AgeCategoryEnum.ADULT){
            this.profession = profession;
        } else {
            this.profession = ProfessionEnum.values()[0].name();
        }
        if (this.ageCategory == AgeCategoryEnum.ADULT && this.gender == GenderEnum.FEMALE) {
            this.pregnant = pregnant;
        } else {
            this.pregnant = false;
        }
    }

    /**
     * Constructor: define characteristics of an Human object.
     * This constructor is invoked when human is created randomly.
     * @param gender : male or female or unknown?
     * @param age    : how old is he/she?
     * @param bodyType : refer to BodyTypeEnum in parent class Character
     */
    public Human(String gender, int age, String bodyType) {
        super(gender, age, bodyType);
        this.ageCategory = AgeCategoryEnum.values()[0];
        this.profession = ProfessionEnum.values()[0].toString();
        this.pregnant =false;

    }

    /**
     * Set random values for human characteristics.
     * It calls setRandomCharacter function from the parent class Character to
     * randomly set values for gender, age and bodyType.
     * Only adults are allowed to have profession. Only adult female are allowed to be pregnant.
     * @param rand
     */
    public void setRandomHuman(Random rand) {
        setRandomCharacter(rand);
        this.profession = ProfessionEnum.valueOf(getRandomTraits(ProfessionEnum.class, rand)).toString();
        this.pregnant = rand.nextBoolean();
        if (this.ageCategory != AgeCategoryEnum.ADULT){
            this.profession = ProfessionEnum.values()[0].name();
        }
        if (this.ageCategory != AgeCategoryEnum.ADULT || this.gender != GenderEnum.FEMALE) {
            this.pregnant = false;
        }
    }

    @Override
    public void displayCharacter() {
        System.out.println("- "
                + (this.bodyType == BodyTypeEnum.values()[0] ? "" : this.bodyType.name().toLowerCase() + " ")
                + this.ageCategory.name().toLowerCase() +  " "
                + (this.profession.equalsIgnoreCase(ProfessionEnum.values()[0].name()) ? "" : this.profession.toLowerCase() + " ")
                + (this.gender == GenderEnum.values()[0] ? "" : this.gender.name().toLowerCase())
                + (this.pregnant ? " pregnant" : ""));
    }

    public String getAgeCategory () {
        return this.ageCategory.name().toLowerCase();
    }

    public String getProfession () {
        return this.profession.toLowerCase();
    }

    public boolean isPregnant() {
        return this.pregnant;
    }

    public String getProfessionDefault() {
        return ProfessionEnum.values()[0].name();
    }
}
