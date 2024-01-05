
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Random;
import java.util.Scanner;


/**
 * COMP90041, Sem1, 2023: Final Project
 * @author: Thuy Linh Nguyen
 *
 * <h1>RescueBot - A decision engine designed to explore different scenarios, prioritize
 * and determine the order of rescue operations in life-threatening situations</h1>
 * RescueBot implements an application that let users explore different scenarios,
 * make critical decisions about whom to save, run simulation with the built-in algorithm
 * and audit the entire decision history as well as its corresponding statistics.
 */
public class RescueBot {

    //A Scanner object used for every class of the application to read input from users
    public static final Scanner sc = new Scanner(System.in);

    //Constant for N/A flag
    public static final String NOT_AVAILABLE = "N/A";

    //Constant for yes/no answer options
    public enum YesNoOption {
        YES_ANS("yes"), NO_ANS("no");
        private String value;
        private YesNoOption (String value){
            this.value = value;
        }
        public String getValue() {
            return this.value;
        }
    }

    //Constants for Setup Menu options
    private enum HelpMenu {
        SCENARIO(new String[] {"-s", "--scenario"}),
        HELP(new String[] {"-h", "--help"}),
        LOG(new String[] {"-l", "--log"});

        private String[] val;

        HelpMenu(String[] val) {
            this.val = val;
        }

        private Boolean isTrue(String val) {
            return val.equalsIgnoreCase(this.val[0]) || val.equalsIgnoreCase(this.val[1]);
        }
    }

    //Constants for Main Menu options
    private enum MainMenu {
        JUDGE(new String[] {"judge", "j"}),
        RUN(new String[] {"run", "r"}),
        AUDIT(new String[] {"audit", "a"}),
        QUIT(new String[] {"quit", "q"});

        private String[] val;

        MainMenu(String[] val) {
            this.val = val;
        }

        private Boolean isTrue(String val) {
            return val.equalsIgnoreCase(this.val[0]) || val.equalsIgnoreCase(this.val[1]);
        }
    }

    /**
     * Decides whether to save the passengers or the pedestrians by potential scoring based on various factors.
     * For each scenario, location with the highest total points will be saved.
     * @param Scenario scenario: the ethical dilemma
     * @return Decision: which group to save
     */
    public static Location decide(Scenario scenario) {
        for (int i = 0; i < scenario.getLocationListSize(); i++) {
            Location location = scenario.getLocation(i);

            //Each character in the location will receive 1 point for respecting the rules, aka not trespassing the area
            if (!location.isTrespassed()) {
                location.setPotentialPoint(location.getCharacterCount());
            }
            for (int j = 0; j < location.getCharacterCount(); j++) {
                Character character = location.getCharacter(j);
                if (character instanceof Human) {
                    Human human = (Human) character;

                    //RescueBot will not be needed in the presence of a wizard, who can save the whole area with a spell
                    if (human.getProfession().equalsIgnoreCase("WIZARD")) {
                        location.setPotentialPoint(-1000);
                    } else {
                        //RescueBot favors human over animal...well, only innocent human who does not commit any crime
                        if (!human.getProfession().equalsIgnoreCase("CRIMINAL")) {
                            location.setPotentialPoint(location.getPotentialPoint() + 1);
                        }
                        //Younger people should be prioritized for their potential for future contribution
                        if (human.getAgeCategory().equalsIgnoreCase("BABY")
                                ||human.getAgeCategory().equalsIgnoreCase("CHILD")
                                ||human.getAgeCategory().equalsIgnoreCase("ADULT")) {
                            location.setPotentialPoint(location.getPotentialPoint() + 1);
                        }
                        //RescueBot is quite curvy but he must sacrifice overweight adults to save more lives
                        if (human.getBodyType().equalsIgnoreCase("OVERWEIGHT")
                                && human.getAgeCategory().equalsIgnoreCase("ADULT")) {
                            location.setPotentialPoint(location.getPotentialPoint() - 1);
                        }
                    }
                } else {
                    Animal animal = (Animal) character;
                    //RescueBot loves dragon and dinosaur. He thinks they can work together in the rescue missions. He also claims to be a big fan of Harry Potter and has a secret crush on Hedwig.
                    if (animal.getSpecies().equalsIgnoreCase("DRAGON")
                            || animal.getSpecies().equalsIgnoreCase("HEDWIG")
                            || animal.getSpecies().equalsIgnoreCase("DINOSAUR")
                            || animal.isPet()) {
                        location.setPotentialPoint(location.getPotentialPoint() + 5);
                    }
                }
            }
        }
        //Get the location with the highest points
        int locationChosenIndex = 0;
        for (int i = 1; i < scenario.getLocationListSize(); i++) {
            if (scenario.getLocation(i).getPotentialPoint() > scenario.getLocation(locationChosenIndex).getPotentialPoint()) {
                locationChosenIndex = i;
            }
        }
        return scenario.getLocation(locationChosenIndex);
    }

    /**
     * This is the main method of the application
     * @param args are used to initialize path to scenarios file, log file or invoke help menu,
     */
    public static void main(String[] args) {
        int countArgsScenario = 0;
        int countArgsLog = 0;
        int countArgsHelp = 0;
        int countArgsError = 0;
        
        InputFileHandle fileHandle = new InputFileHandle();
        Log log = new Log();
        Random rand = new Random();
        
        //Check argument syntax. Repeated arguments are deemed to be invalid.
        for (int i = 0; i < args.length; i++) {
            if (HelpMenu.SCENARIO.isTrue(args[i])) {
                countArgsScenario += 1;
                if (i+1 < args.length && (!HelpMenu.HELP.isTrue(args[i + 1]) || !HelpMenu.LOG.isTrue(args[i + 1]))) {
                    fileHandle.setPathScenario(args[i+1]);
                }
            } else if (HelpMenu.HELP.isTrue(args[i])) {
                countArgsHelp += 1;
            } else if (HelpMenu.LOG.isTrue(args[i])) {
                countArgsLog += 1;
                if (i+1 < args.length && (!HelpMenu.HELP.isTrue(args[i + 1]) || !HelpMenu.SCENARIO.isTrue(args[i + 1]))) {
                    log.setPathLog(args[i+1]);
                }
            } else if (!HelpMenu.SCENARIO.isTrue(args[i-1])
                    && !HelpMenu.LOG.isTrue(args[i-1])) {
                countArgsError += 1;
            }
        }
        
        //Show Help Menu and terminate the program if invalid arguments are provided
        if (countArgsHelp > 0 || countArgsError > 0 || countArgsScenario > 1 || countArgsLog > 1 ) {
            System.out.println("RescueBot - COMP90041 - Final Project");
            System.out.println("");
            System.out.println("Usage: java RescueBot [arguments]");
            System.out.println("");
            System.out.println("Arguments:");
            System.out.println("-s or --scenarios\tOptional: path to scenario file");
            System.out.println("-h or --help\t\tOptional: Print Help (this message) and exit");
            System.out.println("-l or --log\t\tOptional: path to data log file");
            return;
        }

        //Check availability of path to scenario file provided by user. If not available, throw error, show Help Menu and terminate the program
        if (!fileHandle.getPathScenario().equalsIgnoreCase(NOT_AVAILABLE)) {
            File fileScenario = new File(fileHandle.getPathScenario());
            try {
                BufferedReader inputStream = new BufferedReader(new FileReader(fileScenario));
            } catch (FileNotFoundException e) {
                System.out.println("java.io.FileNotFoundException: could not find scenarios file.");
                System.out.println("RescueBot - COMP90041 - Final Project");
                System.out.println("");
                System.out.println("Usage: java RescueBot [arguments]");
                System.out.println("");
                System.out.println("Arguments:");
                System.out.println("-s or --scenarios\tOptional: path to scenario file");
                System.out.println("-h or --help\t\tOptional: Print Help (this message) and exit");
                System.out.println("-l or --log\t\tOptional: path to data log file");
                return;
            }
        }

        //Check availability of path to log file provided by user
        if (!log.getPathLog().equalsIgnoreCase(NOT_AVAILABLE)) {
            File fileLog = new File(fileHandle.getPathScenario());
            try {
                BufferedReader inputStream = new BufferedReader(new FileReader(fileLog));
                log.setValidPath(true);
            } catch (FileNotFoundException e) {
                log.setValidPath(false);
            }
        } else {
            log.setValidPath(true);
        }

        //Create Statistic object
        Statistic statistic = new Statistic();

        //Display welcome message
        fileHandle.displayWelcomeMessage();

        //Import scenario file if provided
        if (fileHandle.isScenariosProvided()) {
            fileHandle.importScenarios(statistic);
        }

        //Count number of scenario imported. If none, below message will not be shown.
        if (statistic.getScenarioCount() > 0) {
            System.out.println(statistic.getScenarioCount() +  " scenarios imported.");
        }

        //Run the program based on user's choice of Main Menu options
        while (log.toBeContinued()) {       //Stop if user chooses [judge] option, gives consent to data collection but path to log file provided does not exist.
            System.out.println("Please enter one of the following commands to continue:");
            System.out.println("- judge scenarios: [judge] or [j]");
            System.out.println("- run simulations with the in-built decision algorithm: [run] or [r]");
            System.out.println("- show audit from history: [audit] or [a]");
            System.out.println("- quit the program: [quit] or [q]");
            System.out.print("> ");

            String menuChoice = sc.nextLine();

            if (MainMenu.JUDGE.isTrue(menuChoice)) {
                //User chooses to judge scenarios
                log.collectUserConsent();                                                   //Collect user's consent to log data
                Scenario scenario = new Scenario();
                scenario.judgeAllScenarios(statistic, fileHandle, log, scenario, rand);     //If no scenario files is provided, generate random scenarios for user to judge.

            } else if (MainMenu.RUN.isTrue(menuChoice)) {
                //User chooses to run simulations with decide(Scenario scenario) algorithm
                log.setConsentGiven(true);                                                  //Data is always written to log file when simulations are run
                statistic.refreshStatistics();
                if (fileHandle.isScenariosProvided()) {
                    //Run simulations on scenarios provided by user
                    for (int i = 0; i < statistic.getScenarioCount(); i++) {
                        Scenario scenario = statistic.getScenario(i);
                        scenario.refreshSurvival();                                         //Reset life-saving decision before running simulation on each scenario
                        Location location = decide(scenario);                               //Run simulation with decide(Scenario scenario) algorithm
                        statistic.setRunCount(statistic.getRunCount() + 1);                 //Count number of run for statistics collection
                        location.setIsSaved(true);                                          //Location saved by algorithm
                        statistic.updateStatistic(scenario);                                //Update statistics
                        log.writeLog(scenario, fileHandle, false);              //Write to log file with decided-by-algo flag
                    }
                    statistic.displayStatistics();

                } else {
                    //Run simulations on scenarios generated randomly by RescueBot
                    int numberOfScenario = 0;
                    while (true) {
                        System.out.println("How many scenarios should be run?");            //Ask user to input number of scenarios to be run
                        System.out.print("> ");
                        try {
                            numberOfScenario = sc.nextInt();
                            sc.nextLine();
                            break;
                        } catch (NumberFormatException e) {                                 //User inputted invalid number format
                            System.out.print("Invalid input! ");
                        }
                    }
                    if (numberOfScenario > 0) {
                        Scenario scenario = new Scenario();
                        for (int i = 0; i < numberOfScenario; i++) {
                            scenario.setRandomScenario(statistic, scenario, rand);          //Generate random scenario
                            scenario.refreshSurvival();                                     //Reset life-saving decision before running simulation on each scenario
                            Location location = decide(scenario);                           //Run simulation with decide(Scenario scenario) algorithm
                            location.setIsSaved(true);                                      //Location saved by algorithm
                            statistic.updateStatistic(scenario);                            //Update statistics
                            log.writeLog(scenario, fileHandle, false);          //Write to log file with decided-by-algo flag
                        }
                        statistic.setRunCount(numberOfScenario);                            //Set number of run for statistics collection
                        statistic.displayStatistics();
                    }
                }
                while (true) {
                    System.out.println("That's all. Press Enter to return to main menu.");
                    System.out.print("> ");
                    String userChoice = sc.nextLine();
                    if (userChoice == "") {
                        break;
                    }
                }
            } else if (MainMenu.AUDIT.isTrue(menuChoice)) {
                //User chooses to audit log history
                Audit audit = new Audit();
                statistic = new Statistic();
                audit.importLogHistory(log, statistic);                                     //Import history from log file and calculate statistics
                statistic = new Statistic();                                                //Refresh statistics
                if (fileHandle.isScenariosProvided()) {                                     //Restore scenarios if scenarios file is provided by users
                    fileHandle.importScenarios(statistic);
                }
                while (true) {
                    System.out.println("That's all. Press Enter to return to main menu.");
                    System.out.print("> ");
                    String userChoice = sc.nextLine();
                    if (userChoice == "") {
                        break;
                    }
                }
            } else if (MainMenu.QUIT.isTrue(menuChoice)) {
                //User chooses to quit
                break;
            } else {
                System.out.print("Invalid command! ");
            }
        }
    }
}