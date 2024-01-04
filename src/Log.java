import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * COMP90041, Sem1, 2023: Final Project
 * @author: Thuy Linh Nguyen
 * Student ID: 1397876
 * Student Email: linh.nguyen3@student.unimelb.edu.au
 * A class for collecting user's consent to save data and writing log.
 *
 */

public class Log {

    private String pathLog;
    private boolean isValidPath;
    private boolean isConsentGiven;
    private boolean toBeContinued;
    private boolean defaultLogHasData;
    private static final String DEFAULT_LOG = "rescuebot.log";
    private static final int LOG_COLUMN_WIDTH = 20;
    private static final int LOG_COLUMN_COUNT = 8;
    private static final String LOG_DELIMITER = "|";
    private static final String LOG_SPACER = "_";
    private static final int LOG_INDENT = 3;
    private static final int LOG_INDENT_EXTRA = 50;
    public enum logHeaderEnum {
        CHARACTER, GENDER, AGE, BODYTYPE, PROFESSION, PREGNANT, SPECIES, ISPET
    }

    public Log() {
        this.pathLog = RescueBot.NOT_AVAILABLE;
        this.isValidPath = false;
        this.isConsentGiven = false;
        this.defaultLogHasData = false;
        this.toBeContinued = true;
    }

    //Collect user's consent to write data to log file
    public void collectUserConsent() {
        while(true) {
            System.out.println("Do you consent to have your decisions saved to a file? (yes/no)");
            System.out.print("> ");
            String userChoice = RescueBot.sc.nextLine();
            if (userChoice.equalsIgnoreCase(RescueBot.YesNoOption.YES_ANS.getValue())) {
                this.isConsentGiven = true;
                break;
            } else if (userChoice.equalsIgnoreCase(RescueBot.YesNoOption.NO_ANS.getValue())) {
                this.isConsentGiven = false;
                break;
            } else {
                System.out.print("Invalid response! ");
            }
        }
    }

    //Create log file if it's not already available and not provided by user
    public void createLogFile(InputFileHandle fileHandle) {
        File welcomeText = new File(fileHandle.getPathWelcome());
        try {
            BufferedReader inputStream = new BufferedReader(new FileReader(welcomeText));       //The log contains a part of the welcome text, for the sake of consistency and coolness :)
            String line = "";
            ArrayList<String> lineArray = new ArrayList<>();
            while ((line = inputStream.readLine()) != null) {
                if (line.startsWith("Welcome")) {
                    lineArray.add("Let's take a moment of reflection...");
                    lineArray.add("I know it seems unfair to prioritize one's life over an other's. We have no choice but to choose.");
                    lineArray.add("We've put forth our best effort to save lives. I thank you for your service, hooman.");
                    lineArray.add("Below are the scenarios that have been judged so far. I'll leave statistics calculation to you since I'm not good at Maths.");
                    lineArray.add("Just kidding...Let me handle that boring part. Please go to the Main Menu and choose Audit to get all the figures.");
                    lineArray.add("Have a nice life, hooman. Live long and prosper! To infinity and beyond! :)");
                    lineArray.add("");
                    lineArray.add("");
                    break;
                } else {
                    lineArray.add(line);
                }
            }
            inputStream.close();
            try {
                File file = new File(DEFAULT_LOG);

                if (!file.exists()) {
                    file.createNewFile();
                    BufferedWriter outputWriter = null;
                    outputWriter = new BufferedWriter(new FileWriter(file));
                    for (int i = 0; i < lineArray.size(); i++) {
                        outputWriter.write(lineArray.get(i));
                        outputWriter.newLine();
                    }
                    outputWriter.flush();
                    outputWriter.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write log each time a scenario is judged. Record whether the judgement is made by user or algorithm.
     * The process contains 4 main sub-parts: writing Scenario, writing Headers, writing Locations and writing Characters
     * @param scenario
     * @param fileHandle
     * @param decidedByUser
     */
    public void writeLog(Scenario scenario, InputFileHandle fileHandle, boolean decidedByUser) {
        if (this.isConsentGiven) {
            try {
                File file = new File(DEFAULT_LOG);
                if (this.pathLog.equalsIgnoreCase(RescueBot.NOT_AVAILABLE) && !this.defaultLogHasData) {
                    createLogFile(fileHandle);
                    file = new File(DEFAULT_LOG);
                } else if (!this.pathLog.equalsIgnoreCase(RescueBot.NOT_AVAILABLE)) {
                    file = new File(this.pathLog);
                }
                BufferedWriter outputWriter = null;
                outputWriter = new BufferedWriter(new FileWriter(file, true));

                outputWriter.newLine();
                writeLogScenarioPart(scenario, outputWriter, decidedByUser);
                writeLogHeader(outputWriter);

                for (int i = 0; i < scenario.getLocationListSize(); i++) {
                    Location location = scenario.getLocation(i);
                    writeLogLocationPart(location, outputWriter, i);

                    for (int j = 0; j < location.getCharacterCount(); j++) {
                        Character character = location.getCharacter(j);
                        if (character instanceof Human) {
                            Human human = (Human) character;
                            writeLogHumanPart(human, outputWriter);
                        } else {
                            Animal animal = (Animal) character;
                            writeLogAnimalPart(animal, outputWriter);
                        }
                    }
                }
                this.defaultLogHasData = true;
                outputWriter.flush();
                outputWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeLogHeader(BufferedWriter outputWriter) {
        try {
            String[] tempArr = new String[logHeaderEnum.values().length];
            for (int i = 0; i < tempArr.length; i++) {
                tempArr[i] = logHeaderEnum.values()[i].name();
            }
            int count = 0;
            int arrIndex = 0;
            while (count < LOG_COLUMN_COUNT * LOG_COLUMN_WIDTH + 1) {
                if (count % LOG_COLUMN_WIDTH == 0 && count != LOG_COLUMN_COUNT * LOG_COLUMN_WIDTH) {
                    outputWriter.write(LOG_DELIMITER);
                    count += 1;
                } else if (count % LOG_COLUMN_WIDTH == LOG_INDENT + 2 && count < LOG_COLUMN_WIDTH * tempArr.length) {
                    arrIndex = (count - (LOG_INDENT + 2)) / LOG_COLUMN_WIDTH;
                    outputWriter.write(tempArr[arrIndex]);
                    count += tempArr[arrIndex].length();
                } else if (count == LOG_COLUMN_COUNT * LOG_COLUMN_WIDTH) {
                    outputWriter.write(LOG_DELIMITER);
                    outputWriter.newLine();
                    count += 1;
                } else {
                    outputWriter.write(" ");
                    count += 1;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeLogScenarioPart(Scenario scenario, BufferedWriter outputWriter, boolean decidedByUser) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date timeStamp = new Date();
            String scenarioStr = "SCENARIO: " + scenario.getScenarioName().toUpperCase() + " " + LOG_DELIMITER + " "
                    + (decidedByUser ? "Decided by user at " : "Decided by algorithm at ") + dateFormat.format(timeStamp);
            int count = 0;
            while (count < LOG_COLUMN_COUNT * LOG_COLUMN_WIDTH + 1) {
                if (count == 0) {
                    outputWriter.write(LOG_DELIMITER);
                    count += 1;
                } else if (count == LOG_INDENT_EXTRA + 1) {
                    outputWriter.write(scenarioStr);
                    count += scenarioStr.length();
                } else if (count == LOG_COLUMN_COUNT * LOG_COLUMN_WIDTH) {
                    outputWriter.write(LOG_DELIMITER);
                    outputWriter.newLine();
                    count += 1;
                } else {
                    outputWriter.write(" ");
                    count += 1;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeLogLocationPart(Location location, BufferedWriter outputWriter, int locationIndex) {
        try {
            String line = "#" + (locationIndex + 1) + " Location: " + location.getLatitude() + "; " + location.getLongitude();
            String locationFlags = (location.isTrespassed() ? "_Trespassed_" : "___Legal____")
                    + LOG_DELIMITER + (location.isSaved() ? "___Saved____" : "___Skipped__") ;
            int count = 0;
            while (count < LOG_COLUMN_COUNT * LOG_COLUMN_WIDTH + 1) {
                if (count == 0) {
                    outputWriter.write(LOG_DELIMITER);
                    count += 1;
                } else if (count == 1) {
                    outputWriter.write(line);
                    count += line.length();
                } else if (count == LOG_COLUMN_COUNT * LOG_COLUMN_WIDTH - (LOG_DELIMITER + locationFlags).length()) {
                    outputWriter.write(LOG_DELIMITER + locationFlags);
                    count += (LOG_DELIMITER + locationFlags).length();
                } else if (count == LOG_COLUMN_COUNT * LOG_COLUMN_WIDTH) {
                    outputWriter.write(LOG_DELIMITER);
                    outputWriter.newLine();
                    count += 1;
                } else {
                    outputWriter.write(LOG_SPACER);
                    count += 1;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeLogHumanPart(Human human, BufferedWriter outputWriter) {
        try {
            String gender = human.getGender().toLowerCase();
            String age = Integer.toString(human.getAge());
            String bodyType = human.getBodyType().toLowerCase();
            String profession = human.getProfession().toLowerCase();
            String pregnancy = Boolean.toString(human.isPregnant()).toLowerCase();
            String[] tempArr = {"human", gender, age, bodyType, profession, pregnancy};

            int count = 0;
            int arrIndex = 0;
            while (count < LOG_COLUMN_COUNT * LOG_COLUMN_WIDTH + 1) {
                if (count % LOG_COLUMN_WIDTH == 0 && count != LOG_COLUMN_COUNT * LOG_COLUMN_WIDTH) {
                    outputWriter.write(LOG_DELIMITER);
                    count += 1;
                } else if (count % LOG_COLUMN_WIDTH == LOG_INDENT + 1 && count < LOG_COLUMN_WIDTH * tempArr.length) {
                    arrIndex = (count - (LOG_INDENT + 1)) / LOG_COLUMN_WIDTH;
                    outputWriter.write(tempArr[arrIndex]);
                    count += tempArr[arrIndex].length();
                } else if (count == LOG_COLUMN_COUNT * LOG_COLUMN_WIDTH) {
                    outputWriter.write(LOG_DELIMITER);
                    outputWriter.newLine();
                    count += 1;
                } else {
                    outputWriter.write(LOG_SPACER);
                    count += 1;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeLogAnimalPart(Animal animal, BufferedWriter outputWriter) {
        try {
            String gender = animal.getGender().toLowerCase();
            String age = Integer.toString(animal.getAge());
            String bodyType = animal.getBodyType().toLowerCase();
            String species = animal.getSpecies().toLowerCase();
            String isPet = Boolean.toString(animal.isPet()).toLowerCase();
            String[] tempArr = {"animal", gender, age, bodyType, species, isPet};

            int count = 0;
            int arrIndex = 0;
            while (count < LOG_COLUMN_COUNT * LOG_COLUMN_WIDTH + 1) {
                if (count % LOG_COLUMN_WIDTH == 0 && count != LOG_COLUMN_COUNT * LOG_COLUMN_WIDTH) {
                    outputWriter.write(LOG_DELIMITER);
                    count += 1;
                } else if (count % LOG_COLUMN_WIDTH == LOG_INDENT + 1 && ((count - (LOG_INDENT + 1)) / LOG_COLUMN_WIDTH) < 4) {
                    arrIndex = (count - (LOG_INDENT + 1)) / LOG_COLUMN_WIDTH;
                    outputWriter.write(tempArr[arrIndex]);
                    count += tempArr[arrIndex].length();
                } else if (count % LOG_COLUMN_WIDTH == LOG_INDENT + 1 && count > (LOG_COLUMN_COUNT - 1) * LOG_COLUMN_WIDTH) {
                    arrIndex = tempArr.length - 1;
                    outputWriter.write(tempArr[arrIndex]);
                    count += tempArr[arrIndex].length();
                } else if (count % LOG_COLUMN_WIDTH == LOG_INDENT + 1 && count > (LOG_COLUMN_COUNT - 2) * LOG_COLUMN_WIDTH) {
                    arrIndex = tempArr.length - 2;
                    outputWriter.write(tempArr[arrIndex]);
                    count += tempArr[arrIndex].length();
                } else if (count == LOG_COLUMN_COUNT * LOG_COLUMN_WIDTH) {
                    outputWriter.write(LOG_DELIMITER);
                    outputWriter.newLine();
                    count += 1;
                } else {
                    outputWriter.write(LOG_SPACER);
                    count += 1;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPathLog(String pathLog) {
        this.pathLog = pathLog;
    }

    public String getPathLog() {
        return pathLog;
    }

    public boolean isValidPath() {
        return isValidPath;
    }

    public void setValidPath(boolean validPath) {
        isValidPath = validPath;
    }

    public boolean toBeContinued() {
        return toBeContinued;
    }

    public void setToBeContinued(boolean toBeContinued) {
        this.toBeContinued = toBeContinued;
    }

    public void setConsentGiven(boolean consentGiven) {
        isConsentGiven = consentGiven;
    }

    public String getDefaultLog() {
        return this.DEFAULT_LOG;
    }

    public boolean defaultLogHasData() {
        return this.defaultLogHasData;
    }

    public String getLogDelimiter() {
        return this.LOG_DELIMITER;
    }

    public String getLogSpacer() {
        return this.LOG_SPACER;
    }
}
