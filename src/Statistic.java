import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * COMP90041, Sem1, 2023: Final Project
 * @author: Thuy Linh Nguyen
 * Student ID: 1397876
 * Student Email: linh.nguyen3@student.unimelb.edu.au
 * A class for collecting, displaying and calculating statistics.
 *
 */

public class Statistic {
    //Declare a map of (key, value) = (factor, count) that belongs to saved entities (location, human and animal)
    private Map<String, Integer> savedMap = new HashMap<>();

    //Declare a map of (key, value) = (factor, count) that belongs to skipped (not saved) entities (location, human and animal)
    private Map<String, Integer> skippedMap = new HashMap<>();

    //Declare an ArrayList of Scenario object
    private ArrayList<Scenario> scenarioList = new ArrayList<>();

    private int survivalAgeSum;
    private int survivalCount;
    private int runCount;

    public Statistic() {
        this.runCount = 0;
        this.survivalAgeSum = 0;
        this.survivalCount = 0;
    }

    public void refreshStatistics() {
        this.savedMap = new HashMap<>();
        this.skippedMap = new HashMap<>();
        this.runCount = 0;
        this.survivalAgeSum = 0;
        this.survivalCount = 0;
    }

    /**
     * Count the "saved" and the "skipped" by key.
     * Calculate saved ratio and sort the statistics by the ratio in descending order and then by the key in ascending order.
     * @return a sorted list of th ratio and its key
     */
    private ArrayList<String> calcSortRatioList() {
        String ratio;
        ArrayList<String> sortedRatioList = new ArrayList<>();
        ArrayList<String> sortedRatioKey = new ArrayList<>();
        ArrayList<String> sortedKeyRatio = new ArrayList<>();
        for (String key: savedMap.keySet()) {
            if (skippedMap.get(key) != null) {
                ratio = String.format("%.2f", Math.ceil(1.0d*savedMap.get(key)/(skippedMap.get(key) + savedMap.get(key))*100)/100);
            } else {
                ratio = String.format("%.2f", 1.0d);
            }
            sortedRatioKey.add(ratio + "|" + key);
            sortedKeyRatio.add(key + "|" + ratio);
        }
        for (String key: skippedMap.keySet()) {
            if (!savedMap.containsKey(key)) {
                ratio = String.format("%.2f", 0.0d);
                sortedRatioKey.add(ratio + "|" + key);
                sortedKeyRatio.add(key + "|" + ratio);
            }
        }
        Collections.sort(sortedRatioKey);
        Collections.reverse(sortedRatioKey);

        Collections.sort(sortedKeyRatio);
        Collections.reverse(sortedKeyRatio);

        for (int i = 0; i < sortedRatioKey.size(); i++) {
            String iRatio = sortedRatioKey.get(i).split("[|]", -1)[0];
            String iKey = sortedRatioKey.get(i).split("[|]", -1)[1];
            double divisor = Math.pow(10, Integer.toString(sortedKeyRatio.size() - 1).length() - 1);
            for (int j = 0; j < sortedKeyRatio.size(); j++) {
                String jKey = sortedKeyRatio.get(j).split("[|]", -1)[0];
                String jRatio = sortedKeyRatio.get(j).split("[|]", -1)[1];
                if (iKey.equalsIgnoreCase(jKey) && iRatio.equalsIgnoreCase(jRatio)) {
                    sortedRatioList.add(iRatio + "|" + j*1d/divisor + "|" + iKey);
                }
            }
        }

        Collections.sort(sortedRatioList);
        Collections.reverse(sortedRatioList);

        return sortedRatioList;
    }

    //Display accumulated statistics after each 3 runs
    public void displayStatistics() {
        System.out.println("======================================");
        System.out.println("# Statistic");
        System.out.println("======================================");
        System.out.println("- % SAVED AFTER " + this.runCount + " RUNS");

        ArrayList<String> sortedRatioList = calcSortRatioList();

        for (int i = 0; i < sortedRatioList.size(); i++) {
            String keyValueConcat = sortedRatioList.get(i);
            String key = keyValueConcat.split("[|]", -1)[2];
            String value = keyValueConcat.split("[|]", -1)[0];
            System.out.println(key + ": " + value);
        }

        String averageAgeSurvival = "";
        if (this.survivalCount > 0) {
            averageAgeSurvival = String.format("%.2f", Math.ceil(1.0d*this.survivalAgeSum/this.survivalCount*100)/100);
        }

        System.out.println("--");
        System.out.println("average age: " + averageAgeSurvival);
    }

    //Display accumulated statistics of the entire log history for auditing
    public void displayStatistics(Log log, String keyword) {
        System.out.println("======================================");
        System.out.println("# " + keyword + " audit");
        System.out.println("======================================");
        System.out.println("- % SAVED AFTER " + this.runCount + " RUNS");

        ArrayList<String> sortedRatioList = calcSortRatioList();

        for (int i = 0; i < sortedRatioList.size(); i++) {
            String keyValueConcat = sortedRatioList.get(i);
            String key = keyValueConcat.split("[" + log.getLogDelimiter() + "]")[2];
            String value = keyValueConcat.split("[|]")[0];
            System.out.println(key + ": " + value);
        }

        String averageAgeSurvival = "";
        if (this.survivalCount > 0) {
            averageAgeSurvival = String.format("%.2f", Math.ceil(1.0d*this.survivalAgeSum/this.survivalCount*100)/100);
        }

        System.out.println("--");
        System.out.println("average age: " + averageAgeSurvival);
    }

    public void updateStatistic(Scenario scenario) {
        for (int i = 0; i < scenario.getLocationCount(); i++) {
            Location location = scenario.getLocation(i);
            for (int j = 0; j < location.getCharacterCount(); j++) {
                updateLocationDimension(location.isSaved(), location);
                Character character = location.getCharacter(j);
                updateCharacterDimension(location.isSaved(), character);
                if (location.isSaved() && character instanceof Human) {
                    this.survivalAgeSum += character.getAge();
                    this.survivalCount += 1;
                }
            }
        }
    }

    private void updateLocationDimension(boolean isSaved, Location location) {
        if (location.isTrespassed()) {
            updateMapItem(isSaved,"trespassing");
        } else {
            updateMapItem(isSaved,"legal");
        }
    }

    private void updateCharacterDimension(boolean isSaved, Character character) {
        if (character instanceof Human) {
            Human human = (Human) character;
            updateMapItem(isSaved, "human");
            updateMapItem(isSaved, human.getAgeCategory());

            if (!human.getGender().equalsIgnoreCase(human.getGenderDefault())) {
                updateMapItem(isSaved, human.getGender());
            }
            if (!human.getBodyType().equalsIgnoreCase(human.getBodyTypeDefault())) {
                updateMapItem(isSaved, human.getBodyType());
            }
            if (!human.getProfession().equalsIgnoreCase(human.getProfessionDefault())) {
                updateMapItem(isSaved, human.getProfession());
            }
            if (human.isPregnant()) {
                updateMapItem(isSaved, "pregnant");
            }
        } else {
            Animal animal = (Animal) character;
            updateMapItem(isSaved, "animal");
            updateMapItem(isSaved, animal.getSpecies());
            if (animal.isPet()) {
                updateMapItem(isSaved, "pet");
            }
        }
    }

    private void updateMapItem(boolean isSaved, String val) {
        if (isSaved) {
            if (savedMap.containsKey(val)) {
                savedMap.put(val, savedMap.get(val) + 1);
            } else {
                savedMap.put(val, 1);
            }
        } else {
            if (skippedMap.containsKey(val)) {
                skippedMap.put(val, skippedMap.get(val) + 1);
            } else {
                skippedMap.put(val, 1);
            }
        }
    }


    public void addScenario(Scenario scenario) {
        this.scenarioList.add(scenario);
    }

    public Scenario getLatestScenario() {
        return this.scenarioList.get(this.scenarioList.size()-1);
    }

    public int getScenarioCount() {
        return this.scenarioList.size();
    }

    public Scenario getScenario (int i) {
        return this.scenarioList.get(i);
    }

    public void setRunCount(int runCount) {
        this.runCount = runCount;
    }

    public int getRunCount() {
        return runCount;
    }
}
