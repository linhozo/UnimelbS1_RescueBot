import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * COMP90041, Sem1, 2023: Final Project
 * @author: Thuy Linh Nguyen
 * A class for importing log history and calculate statistics based on the historical data.
 * Statistics are calculated for scenarios judged by users and scenarios judged by algorithm separately.
 *
 */

public class Audit {
    //Declare keyword to separate statistics calculation
    private static final String KEYWORD_USER = "Decided by user";
    private static final String KEYWORD_ALGO = "Decided by algorithm";

    /**
     * Import separately judged-by-user scenarios and judged-by-algorithm scenarios from the log file
     * @param log : an instance of Log
     * @param statistic : an instance of Statistics
     * @param keyWord : a flag to identify which scenarios (judged by user or algorithm) in the log file to read in
     * @param counterKeyWord : a flag to identify which scenarios (judged by user or algorithm) in the log file to skip
     */
    private void importLogUserOrAlgo(Log log, Statistic statistic, String keyWord, String counterKeyWord) {
        try {
            File logFile = new File(log.getDefaultLog());
            if (!log.getPathLog().equalsIgnoreCase(RescueBot.NOT_AVAILABLE) && log.isValidPath()) {
                logFile = new File(log.getPathLog());                   //Read in log file provided by user
            } else if (log.defaultLogHasData()){
                logFile = new File(log.getDefaultLog());                //Read in default log file
            }
            BufferedReader inputStream = new BufferedReader(new FileReader(logFile));
            String line = "";

            boolean flag = true;
            String logDelimiter = log.getLogDelimiter();                //Get log delimiter
            String logSpacer = log.getLogSpacer();                      //Get log spacer
            int runCount = 0;
            while ((line = inputStream.readLine()) != null) {
                if (line.startsWith(log.getLogDelimiter())) {
                    String[] tokens = line.replace(logSpacer, "").replace(" ", "").split("[" + logDelimiter + "]", -1);
                    if (tokens[1].startsWith("SCENARIO:") && tokens[2].contains(counterKeyWord)) {
                        //Skip scenario
                        flag = false;
                    }
                    else if (tokens[1].startsWith("SCENARIO:") && tokens[2].contains(keyWord)){
                        //Read Scenario line: match values to scenarioName attributes
                        flag = true;
                        String scenarioName = tokens[1].substring("SCENARIO:".length()).replace(" ", "");
                        Scenario scenario = new Scenario(scenarioName);
                        statistic.addScenario(scenario);
                        runCount += 1;
                    } else if (flag && tokens[1].startsWith("#")) {
                        //Read Location line: match values to latitude, longitude, isTrespassed and isSaved attributes
                        String latitude = tokens[1].replace(" ", "").split(";")[0].substring("Location: ".length());
                        String longitude = tokens[1].replace(" ", "").split(";")[1];
                        boolean isTrespassed = tokens[2].equalsIgnoreCase("Trespassed");
                        boolean isSaved = tokens[3].equalsIgnoreCase("Saved");
                        Location location = new Location(latitude, longitude, isTrespassed);
                        location.setIsSaved(isSaved);
                        Scenario scenario = statistic.getLatestScenario();
                        scenario.addLocation(location);
                        statistic.updateStatistic(scenario);
                    } else if (flag && (!tokens[Log.logHeaderEnum.CHARACTER.ordinal()+1].startsWith("CHARACTER"))) {                  //Skip log header
                        //Read Character line: match values to character common attributes: gender, age, bodyType
                        String gender = tokens[Log.logHeaderEnum.GENDER.ordinal()+1];
                        int age = Integer.parseInt(tokens[Log.logHeaderEnum.AGE.ordinal()+1]);
                        String bodyType = tokens[Log.logHeaderEnum.BODYTYPE.ordinal()+1];

                        Scenario scenario = statistic.getLatestScenario();
                        Location location = scenario.getLatestLocation();
                        if (tokens[1].equalsIgnoreCase("human")) {
                            //Read Human line: match values to human-specific attributes: profession and isPregnant
                            String profession = tokens[Log.logHeaderEnum.PROFESSION.ordinal()+1];
                            boolean pregnant = Boolean.parseBoolean(tokens[Log.logHeaderEnum.PREGNANT.ordinal()+1].toLowerCase());
                            location.addCharacter(new Human(gender, age, bodyType, profession, pregnant));                              //Add human to the latest location read
                        } else {
                            String species = tokens[Log.logHeaderEnum.SPECIES.ordinal()+1];
                            boolean isPet = Boolean.parseBoolean(tokens[Log.logHeaderEnum.ISPET.ordinal()+1].toLowerCase());
                            location.addCharacter(new Animal(gender, age, bodyType, species, isPet));                                   //Add animal to the latest location read
                        }
                        statistic.updateStatistic(scenario);
                    }
                }
            }
            inputStream.close();
            statistic.setRunCount(runCount);
        } catch (IOException e) {
            System.out.println("No history found. Press enter to return to main menu.");
            System.out.print("> ");
        }
    }

    /**
     * Check if historical judged-by-user scenarios and judged-by-algorithm scenarios available in the log.
     * @param log : an instance of Log
     * @param keyword : a flag to identify which scenarios (judged by user or algorithm) in the log file to check
     * @return boolean value, true if available, false otherwise
     */
    private boolean isLogAvail(Log log, String keyword) {
        try {
            File logFile = new File(log.getDefaultLog());
            if (!log.getPathLog().equalsIgnoreCase(RescueBot.NOT_AVAILABLE) && log.isValidPath()) {
                logFile = new File(log.getPathLog());
            } else if (log.defaultLogHasData()) {
                logFile = new File(log.getDefaultLog());
            }
            BufferedReader inputStream = new BufferedReader(new FileReader(logFile));
            String line = "";

            while ((line = inputStream.readLine()) != null) {
                if (line.contains(keyword)) {
                    return true;
                }
            }
            inputStream.close();
            return false;
        } catch (IOException e) {
           return false;
        }
    }

    /**
     * Import the entire log history, calculate and display statistics
     * @param log : an instance of Log
     * @param statistic : an instance of Statistics
     */
    public void importLogHistory(Log log, Statistic statistic) {
        if (isLogAvail(log, KEYWORD_USER)) {
            importLogUserOrAlgo(log, statistic, KEYWORD_USER.replace(" ", ""), KEYWORD_ALGO.replace(" ", ""));
            statistic.displayStatistics(log, KEYWORD_USER);
        }
        if (isLogAvail(log, KEYWORD_USER) && (isLogAvail(log, KEYWORD_ALGO))) {
            System.out.println("");
        }
        if (isLogAvail(log, KEYWORD_ALGO)) {
            importLogUserOrAlgo(log, statistic, KEYWORD_ALGO.replace(" ", ""), KEYWORD_USER.replace(" ", ""));
            statistic.displayStatistics(log, KEYWORD_ALGO);
        }
    }
}
