
import java.io.*;
import java.util.Scanner;
/**
 * COMP90041, Sem1, 2023: Final Project
 * @author: Thuy Linh Nguyen
 * Student ID: 1397876
 * Student Email: linh.nguyen3@student.unimelb.edu.au
 * A class for importing input data and throwing exception where applicable.
 *
 */

public class InputFileHandle {
    private String pathScenario;
    private String pathWelcome;
    private static final String DEFAULT_WELCOME = "welcome.ascii";
    private static final String PRIMARY_DELIMITER = ",";            //Primary delimiter of the scenario file provided by user
    private static final String SECONDARY_DELIMITER = ";";          //Secondary delimiter of the scenario file provided by user

    //Declare column index in input data file
    private enum InputFileColumns {
        SCENARIO_NAME(0),
        LATITUDE(0), LONGITUDE(1), TRESPASSED(2),
        CHARACTER(0), GENDER(1), AGE(2), BODY_TYPE(3), PROFESSION(4), PREGNANCY(5), SPECIES(6), IS_PET(7);
        private int value;
        private InputFileColumns (int value){
            this.value = value;
        }
        public int getValue() {
            return this.value;
        }
    }

    public InputFileHandle() {
        this.pathScenario = RescueBot.NOT_AVAILABLE;
        this.pathWelcome = DEFAULT_WELCOME;
    }

    //Read welcome ascii file and print welcome message to terminal
    public void displayWelcomeMessage() {
        File welcomeFile = new File(this.pathWelcome);
        try {
            Scanner inputStream = new Scanner(welcomeFile);
            while (inputStream.hasNextLine()) {
                String line = inputStream.nextLine();
                System.out.println(line);
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            System.out.println("Welcome message not found!");
        }
    }

    /**
     * Import scenario file, read data and allocate value to applicable attributes of corresponding object.
     * Exception is thrown in this method but error handling (aka assigning default value) will be implemented by
     * the constructor of each relevant object.
     * @param statistic : this object is passed in aid of updating statistics each time a value of an attribute is set.
     */
    public void importScenarios(Statistic statistic) {
        File scenarioFile = new File(this.pathScenario);
        try {
            BufferedReader inputStream = new BufferedReader(new FileReader(scenarioFile));      //Read in scenario file
            String line = "";
            int i = 1;
            while ((line = inputStream.readLine()) != null) {
                if (i != 1) {                                                                   //Skip first line (file header)
                    String[] tokens = line.split(PRIMARY_DELIMITER, -1);                   //Split value by delimiter
                    try {
                        if (tokens.length != 8) {                                               //Scenario file must have exactly 8 fields
                            throw new InvalidDataFormatException("WARNING: invalid data format in scenarios file in line " + i);
                        }
                    } catch (InvalidDataFormatException e) {
                        System.out.println(e.getMessage());
                        continue;
                    }
                    if (tokens[InputFileColumns.SCENARIO_NAME.getValue()].startsWith("scenario:")) {
                        //Read Scenario line: match value to scenarioName attributes
                        String scenarioName = tokens[InputFileColumns.SCENARIO_NAME.getValue()].substring("scenario:".length());
                        Scenario scenario = new Scenario(scenarioName);
                        statistic.addScenario(scenario);
                    } else if (tokens[0].startsWith("location:")) {
                        //Read Location line: match value to latitude, longitude and isTrespassed attributes
                        String latitude = tokens[0].split(SECONDARY_DELIMITER)[InputFileColumns.LATITUDE.getValue()].substring("location:".length());
                        String longitude = tokens[0].split(SECONDARY_DELIMITER)[InputFileColumns.LONGITUDE.getValue()];
                        String trespassedValue = tokens[0].split(SECONDARY_DELIMITER)[InputFileColumns.TRESPASSED.getValue()];
                        boolean isTrespassed = trespassedValue.equalsIgnoreCase("trespassing");
                        try {
                            if (!trespassedValue.equalsIgnoreCase("trespassing")         //Value must be "trespassing" or "legal"
                                    && !trespassedValue.equalsIgnoreCase("legal")) {
                                throw new InvalidCharacteristicException("WARNING: invalid characteristic in scenarios file in line " + i);
                            }
                        } catch (InvalidCharacteristicException e) {
                            System.out.println(e.getMessage());
                            isTrespassed = false;
                        }

                        Location location = new Location(latitude, longitude, isTrespassed);
                        Scenario scenario = statistic.getLatestScenario();
                        scenario.addLocation(location);
                    } else {
                        //Read Character line: match value to gender, age, bodyType attributes
                        String gender = tokens[InputFileColumns.GENDER.getValue()];
                        try {
                            boolean flag = false;
                            for (Character.GenderEnum g : Character.GenderEnum.values()) {          //Value must be in GenderEnum list
                                if (gender.equalsIgnoreCase(g.name())) {
                                    flag = true;
                                    break;
                                }
                            }
                            if (!flag) {
                                throw new InvalidCharacteristicException("WARNING: invalid characteristic in scenarios file in line " + i);
                            }
                        } catch (InvalidCharacteristicException e) {
                            System.out.println(e.getMessage());
                        }

                        int age;
                        try {
                            age = Integer.parseInt(tokens[InputFileColumns.AGE.getValue()]);
                            try {
                                if (age < 0) {                                                      //Value must be >=0
                                    throw new InvalidCharacteristicException("WARNING: invalid characteristic in scenarios file in line " + i);
                                }
                            } catch (InvalidCharacteristicException e) {
                                System.out.println(e.getMessage());
                            }
                        } catch (NumberFormatException e) {                                         //Value must be integer
                            System.out.println("WARNING: invalid number format in scenarios file in line " + i);
                            age = Character.DEFAUT_AGE;
                        }

                        String bodyType = tokens[InputFileColumns.BODY_TYPE.getValue()];
                        try {
                            boolean flag = false;
                            for (Character.BodyTypeEnum b : Character.BodyTypeEnum.values()) {      //Value must be in BodyTypeEnum list
                                if (bodyType.equalsIgnoreCase(b.name())) {
                                    flag = true;
                                    break;
                                }
                            }
                            if (!flag) {
                                throw new InvalidCharacteristicException("WARNING: invalid characteristic in scenarios file in line " + i);
                            }
                        } catch (InvalidCharacteristicException e) {
                            System.out.println(e.getMessage());
                        }

                        Scenario scenario = statistic.getLatestScenario();
                        Location location = scenario.getLatestLocation();
                        if (tokens[InputFileColumns.CHARACTER.getValue()].equalsIgnoreCase("human")) {
                            //Read Character line: match value to human-specific attributes: profession and isPregnant
                            String profession = tokens[InputFileColumns.PROFESSION.getValue()];
                            try {
                                boolean flag = false;
                                for (Human.ProfessionEnum p : Human.ProfessionEnum.values()) {      //Value must be in ProfessionEnum list
                                    if (profession.equalsIgnoreCase(p.name())) {
                                        flag = true;
                                        break;
                                    }
                                }
                                if (!flag
                                        || (!profession.equalsIgnoreCase(Human.ProfessionEnum.values()[0].name())       //Only adults are allowed to have profession
                                            && Human.AgeCategoryEnum.getName(age)!=Human.AgeCategoryEnum.ADULT)) {
                                    throw new InvalidCharacteristicException("WARNING: invalid characteristic in scenarios file in line " + i);
                                }
                            } catch (InvalidCharacteristicException e) {
                                System.out.println(e.getMessage());
                            }

                            boolean isPregnant;
                            try {
                                String pregnancyValue = tokens[InputFileColumns.PREGNANCY.getValue()];
                                if ((!pregnancyValue.equalsIgnoreCase("true") && !pregnancyValue.equalsIgnoreCase("false"))                 //Value must be "true" or "false"
                                    ||(pregnancyValue.equalsIgnoreCase("true") && gender.equalsIgnoreCase(Character.GenderEnum.MALE.name()))            //Only female can be pregnant
                                    ||(pregnancyValue.equalsIgnoreCase("true") && Human.AgeCategoryEnum.getName(age)!=Human.AgeCategoryEnum.ADULT ))    //Only adults should be pregnant
                                {
                                    throw new InvalidCharacteristicException("WARNING: invalid characteristic in scenarios file in line " + i);
                                }
                                isPregnant = Boolean.parseBoolean(pregnancyValue.toLowerCase());
                            } catch (InvalidCharacteristicException e) {
                                System.out.println(e.getMessage());
                                isPregnant = false;
                            }
                            //Add character to its location
                            location.addCharacter(new Human(gender, age, bodyType, profession, isPregnant));
                        } else {
                            //Read Character line: match value to animal-specific attributes: species and isPet
                            String species = tokens[InputFileColumns.SPECIES.getValue()];
                            boolean isPet;
                            try {
                                String isPetValue = tokens[InputFileColumns.IS_PET.getValue()];
                                if ((!isPetValue.equalsIgnoreCase("true") && !isPetValue.equalsIgnoreCase("false"))                 //Value must be "true" or "false"
                                    ||(isPetValue.equalsIgnoreCase("true")                                                                     //Only dog, cat and ferret are allowed to be pet
                                        && !species.equalsIgnoreCase(Animal.SpeciesEnum.values()[0].name())
                                        && !species.equalsIgnoreCase(Animal.SpeciesEnum.values()[1].name())
                                        && !species.equalsIgnoreCase(Animal.SpeciesEnum.values()[2].name()))) {
                                    throw new InvalidCharacteristicException("WARNING: invalid characteristic in scenarios file in line " + i);
                                }
                                isPet = Boolean.parseBoolean(isPetValue.toLowerCase());
                            } catch (InvalidCharacteristicException e) {
                                System.out.println(e.getMessage());
                                isPet = false;
                            }
                            //Add character to its location
                            location.addCharacter(new Animal(gender, age, bodyType, species, isPet));
                        }
                    }
                }
                i += 1;
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();;
        }
    }

    //Exception for handling invalid format of data imported
    class InvalidDataFormatException extends Exception {
        String message;
        InvalidDataFormatException (String message) {
            super(message);
            this.message = message;
        }
        @Override
        public String toString() {
            return message;
        }
    }

    //Exception for handling inapplicable value of data imported
    class InvalidCharacteristicException extends Exception {
        String message;
        InvalidCharacteristicException (String message) {
            super(message);
            this.message = message;
        }
        @Override
        public String toString() {
            return message;
        }
    }

    public void setPathScenario(String pathScenario) {
        this.pathScenario = pathScenario;
    }

    public boolean isScenariosProvided() {
        return (!this.pathScenario.equalsIgnoreCase(RescueBot.NOT_AVAILABLE));
    }

    public String getPathScenario() {
        return pathScenario;
    }

    public String getPathWelcome() {
        return pathWelcome;
    }
}
