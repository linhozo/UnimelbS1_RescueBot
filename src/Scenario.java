import java.util.ArrayList;
import java.util.Random;

/**
 * COMP90041, Sem1, 2023: Final Project
 * @author: Thuy Linh Nguyen
 * A class for displaying, judging and creating random scenarios.
 *
 */

public class Scenario {

    //Declare scenario name
    private String scenarioName;

    //Declare an array list of location objects that is in the same scenario
    private ArrayList<Location> locationList = new ArrayList<>();

    //Declare default values for the upper bound and the lower bound of the number of random locations to be added to a random scenatio
    public static final int MAX_RANDOM = 100;
    public static final int MIN_RANDOM = 2;

    //Declare the frequency (in number of run) of displaying statistics
    public static final int FREQUENCY = 3;

    //List of scenario name. Used for randomly generating scenario.
    private enum ScenarioNameEnum {
        FLOOD, BUSHFIRE, CYCLONE, AVALANCHE, BLIZZARD, DROUGHT, EARTHQUAKE, WILDFIRE, HURRICANE, ASTEROID_IMPACT,
        ICE_STORM, LANDSLIDE, TSUNAMI, VOLCANIC_ERUPTION, TORNADO_OUTBREAK, SOLAR_STORM, THUNDERSTORM, APOCALYPSE
    }

    /**
     * Display each scenario to be judged
     * @param scenario
     */
    private void displayEachScenario(Scenario scenario) {
        System.out.println("======================================");
        System.out.println("# Scenario: " + scenario.scenarioName);
        System.out.println("======================================");
        for (int i = 0; i < scenario.locationList.size(); i++) {
            Location location = scenario.locationList.get(i);
            System.out.println("[" + (i+1) + "] Location: " + location.getLatitude() + ", " + location.getLongitude());
            System.out.println("Trespassing: " + (location.isTrespassed() == true ? "yes" : "no"));
            System.out.println(location.getCharacterCount() + " Characters: ");
            location.displayAllCharacter();
        }
    }

    /**
     * Keep letting user judge all scenarios available (provided by user or created randomly by the program)
     * or until user chooses to stop. Each judged scenario is written to log file if user's consent is given.
     * @param statistic : in aid of updating statistics and displaying statistics after each 3 runs
     * @param fileHandle : in aid of writing log file (log file contained a part of the welcome ascii file)
     * @param log : in aid of writing log
     * @param scenarioRandom : in aid of creating random scenarios if no scenario is provided
     * @param rand : in aid of creating randomness
     */
    public void judgeAllScenarios(Statistic statistic, InputFileHandle fileHandle, Log log, Scenario scenarioRandom, Random rand) {
        boolean isRandom = false;
        if (!fileHandle.isScenariosProvided()) {
            setRandomScenario(statistic, scenarioRandom, rand);                     //Create random scenario if no scenario is provided by user
            isRandom = true;
        }
        for (int i = 0; i < statistic.getScenarioCount(); i++) {
            Scenario scenario = statistic.getScenario(i);
            judgeEachScenario(scenario, statistic);
            log.writeLog(scenario, fileHandle, true);
            String userChoice = "";
            if (!log.isValidPath() && ((statistic.getScenarioCount() < FREQUENCY && i == statistic.getScenarioCount()-1) || i == FREQUENCY - 1)) {
                statistic.displayStatistics();
                System.out.println("ERROR: could not print results. Target directory does not exist.");
                log.setToBeContinued(false);                                        //Log file provided does not exist, terminate the program
                return;
            } else if (i == statistic.getScenarioCount() - 1 && !isRandom) {
                statistic.displayStatistics();
                while (true) {
                    System.out.println("That's all. Press Enter to return to main menu.");
                    System.out.print("> ");
                    userChoice = RescueBot.sc.nextLine();
                    if (userChoice == "") {
                        statistic.refreshStatistics();
                        break;
                    }
                }
            } else if ((i+1) % FREQUENCY == 0) {
                statistic.displayStatistics();
                while(true) {
                    System.out.println("Would you like to continue? (yes/no)");
                    System.out.print("> ");
                    userChoice = RescueBot.sc.nextLine();
                    if (userChoice.equalsIgnoreCase(RescueBot.YesNoOption.YES_ANS.getValue())) {
                        if (isRandom) {
                            setRandomScenario(statistic, scenarioRandom, rand);
                        }
                        break;
                    } else if (userChoice.equalsIgnoreCase(RescueBot.YesNoOption.NO_ANS.getValue())) {
                        while (true) {
                            System.out.println("That's all. Press Enter to return to main menu.");
                            System.out.print("> ");
                            userChoice = RescueBot.sc.nextLine();
                            if (userChoice == "") {
                                statistic.refreshStatistics();
                                break;
                            }
                        } return;
                    } else {
                        System.out.print("Invalid response! ");
                    }
                }
            } else if (isRandom) {
                setRandomScenario(statistic, scenarioRandom, rand);
            }
            if (userChoice.equalsIgnoreCase(RescueBot.YesNoOption.NO_ANS.getValue())) {
                statistic.refreshStatistics();
                break;
            }
        }
    }

    /**
     * Let user judge scenario one by one
     * @param scenario : in aid of judging scenario
     * @param statistic : in aid of updating statistics each time a scenario is judged
     */
    private void judgeEachScenario(Scenario scenario, Statistic statistic) {
        displayEachScenario(scenario);
        System.out.println("To which location should RescueBot be deployed?");
        System.out.print("> ");
        boolean done = false;
        while (!done) {
            try {
                int locationChoice = RescueBot.sc.nextInt();
                RescueBot.sc.nextLine();
                if (locationChoice >= 1 && locationChoice <= scenario.locationList.size()) {
                    done = true;
                    Location location = scenario.locationList.get(locationChoice - 1);
                    location.setIsSaved(true);
                    statistic.updateStatistic(scenario);
                    statistic.setRunCount(statistic.getRunCount() + 1);
                } else {
                    System.out.println("Invalid response! To which location should RescueBot be deployed?");
                    System.out.print("> ");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid response! To which location should RescueBot be deployed?");
                System.out.print("> ");
            }
        }
    }

    /**
     * Create random scenario with random number of locations with the minimum of 2 locations in each scenario.
     * @param statistic
     * @param scenario
     * @param rand
     */
    public void setRandomScenario(Statistic statistic, Scenario scenario, Random rand) {
        int scenarioNameIndex;
        int locationCount;
        int characterCount;

        scenarioNameIndex = rand.nextInt(ScenarioNameEnum.values().length);
        scenario.scenarioName = ScenarioNameEnum.values()[scenarioNameIndex].toString();

        Location location = new Location();
        locationCount = rand.nextInt(MAX_RANDOM - MIN_RANDOM + 1) + MIN_RANDOM;
        for (int j = 0; j < locationCount; j++) {
            location.setRandomLocation(rand);
            addLocation(location);
            Character character = new Character();
            characterCount = rand.nextInt(MAX_RANDOM) + 1;
            for (int k = 0; k < characterCount; k++) {
                if (rand.nextBoolean()) {
                    Human human = new Human(character.getGender(), character.getAge(), character.getBodyType());
                    human.setRandomHuman(rand);
                    location.addCharacter(human);
                } else {
                    Animal animal = new Animal(character.getGender(), character.getAge(), character.getBodyType());
                    animal.setRandomAnimal(rand);
                    location.addCharacter(animal);
                }
            }
        }
        statistic.addScenario(scenario);
    }


    public Location getLocation(int i) {
        return this.locationList.get(i);
    }

    public int getLocationListSize() {
        return this.locationList.size();
    }

    public int getLocationCount() {
        return this.locationList.size();
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void refreshSurvival() {
        for (int i = 0; i < getLocationListSize(); i++) {
            Location location = getLocation(i);
            location.setIsSaved(false);
        }
    }

    public Scenario(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public Scenario() {
        this.scenarioName = ScenarioNameEnum.values()[0].toString();
    }

    public void addLocation(Location location) {
        locationList.add(location);
    }

    public Location getLatestLocation() {
        return locationList.get(locationList.size()-1);
    }
}

