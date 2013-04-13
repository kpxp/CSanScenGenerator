package CSanScenGenerator;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

/**
 *
 * @author Peter
 */
public class TypedOfficer extends Officer {

    public static final int GENERAL = 0;
    public static final int MIGHTY = 1;
    public static final int ADVISOR = 2;
    public static final int POLITICIAN = 3;
    public static final int INTEL_GENERAL = 4;
    public static final int EMPEROR = 5;
    public static final int ALL_ROUNDER = 6;
    public static final int NORMAL = 7;
    public static final int CHEAP = 8;
    public static final int NUMBER_OF_TYPES = 9;
    private int type;
    private static Map<Integer, int[]> skillProb = null;
    private static Map<Integer, Map<String, String>> skillCond = null;
    private static Map<Integer, int[]> stuntProb = null;
    private static Map<Integer, Map<String, String>> stuntCond = null;
    private static Map<Integer, int[]> personalTitleProb = null;
    private static Map<Integer, Map<String, String>> personalTitleCond = null;
    private static Map<Integer, int[]> battleTitleProb = null;
    private static Map<Integer, Map<String, String>> battleTitleCond = null;

    public TypedOfficer(Database indb, int intype, int id, boolean strong, java.sql.Connection commonData) throws SQLException {
        super(indb, id, strong, commonData);
        this.type = intype;
    }

    @Override
    public void randomAbility(double mul, double abs, int loCap, int hiCap) {
        switch (type) {
            case GENERAL: {
                /*leadership = Utility.randBetween(70, 100);
                might = Utility.randBetween(70, 100);
                intelligence = Utility.randBetween(30, 60);
                politics = Utility.randBetween(10, 40);
                glamour = Utility.randBetween(30, 90);*/
                leadership = (int) Utility.randGaussian(85, 15);
                might = (int) Utility.randGaussian(85, 15);
                intelligence = (int) Utility.randGaussian(50, 20);
                politics = (int) Utility.randGaussian(40, 20);
                glamour = (int) Utility.randGaussian(60, 30);
                break;
            }
            case MIGHTY: {
                /*leadership = Utility.randBetween(40, 70);
                might = Utility.randBetween(80, 100);
                intelligence = Utility.randBetween(10, 40);
                politics = Utility.randBetween(1, 30);
                glamour = Utility.randBetween(1, 50);*/
                leadership = (int) Utility.randGaussian(55, 15);
                might = (int) Utility.randGaussian(90, 10);
                intelligence = (int) Utility.randGaussian(25, 15);
                politics = (int) Utility.randGaussian(15, 15);
                glamour = (int) Utility.randGaussian(30, 30);
                break;
            }
            case ADVISOR: {
                /*leadership = Utility.randBetween(70, 100);
                might = Utility.randBetween(1, 70);
                intelligence = Utility.randBetween(80, 100);
                politics = Utility.randBetween(70, 100);
                glamour = Utility.randBetween(60, 100);*/
                leadership = (int) Utility.randGaussian(80, 20);
                might = (int) Utility.randGaussian(35, 35);
                intelligence = (int) Utility.randGaussian(90, 10);
                politics = (int) Utility.randGaussian(85, 15);
                glamour = (int) Utility.randGaussian(80, 20);
                break;
            }
            case POLITICIAN: {
                /*leadership = Utility.randBetween(10, 40);
                might = Utility.randBetween(1, 40);
                intelligence = Utility.randBetween(70, 100);
                politics = Utility.randBetween(80, 100);
                glamour = Utility.randBetween(1, 100);*/
                leadership = (int) Utility.randGaussian(25, 25);
                might = (int) Utility.randGaussian(20, 20);
                intelligence = (int) Utility.randGaussian(85, 15);
                politics = (int) Utility.randGaussian(90, 10);
                glamour = (int) Utility.randGaussian(50, 50);
                break;
            }
            case INTEL_GENERAL: {
                /*leadership = Utility.randBetween(70, 100);
                might = Utility.randBetween(70, 100);
                intelligence = Utility.randBetween(70, 100);
                politics = Utility.randBetween(1, 50);
                glamour = Utility.randBetween(30, 90);*/
                leadership = (int) Utility.randGaussian(85, 15);
                might = (int) Utility.randGaussian(85, 15);
                intelligence = (int) Utility.randGaussian(85, 15);
                politics = (int) Utility.randGaussian(25, 25);
                glamour = (int) Utility.randGaussian(60, 30);
                break;
            }
            case EMPEROR: {
                /*leadership = Utility.randBetween(70, 100);
                might = Utility.randBetween(50, 70);
                intelligence = Utility.randBetween(60, 100);
                politics = Utility.randBetween(60, 100);
                glamour = Utility.randBetween(80, 100);*/
                leadership = (int) Utility.randGaussian(80, 20);
                might = (int) Utility.randGaussian(50, 20);
                intelligence = (int) Utility.randGaussian(80, 20);
                politics = (int) Utility.randGaussian(80, 20);
                glamour = (int) Utility.randGaussian(90, 10);
                break;
            }
            case ALL_ROUNDER: {
                /*leadership = Utility.randBetween(70, 100);
                might = Utility.randBetween(70, 100);
                intelligence = Utility.randBetween(70, 100);
                politics = Utility.randBetween(70, 100);
                glamour = Utility.randBetween(70, 100);*/
                leadership = (int) Utility.randGaussian(85, 15);
                might = (int) Utility.randGaussian(85, 15);
                intelligence = (int) Utility.randGaussian(85, 15);
                politics = (int) Utility.randGaussian(85, 15);
                glamour = (int) Utility.randGaussian(85, 15);
                break;
            }
            case NORMAL: {
                /*leadership = Utility.randBetween(50, 70);
                might = Utility.randBetween(50, 70);
                intelligence = Utility.randBetween(50, 70);
                politics = Utility.randBetween(50, 70);
                glamour = Utility.randBetween(50, 70);*/
                leadership = (int) Utility.randGaussian(60, 15);
                might = (int) Utility.randGaussian(60, 15);
                intelligence = (int) Utility.randGaussian(60, 15);
                politics = (int) Utility.randGaussian(60, 15);
                glamour = (int) Utility.randGaussian(60, 15);
                break;
            }
            case CHEAP: {
                /*leadership = Utility.randBetween(1, 50);
                might = Utility.randBetween(1, 50);
                intelligence = Utility.randBetween(1, 50);
                politics = Utility.randBetween(1, 50);
                glamour = Utility.randBetween(1, 50);*/
                leadership = (int) Utility.randGaussian(25, 25);
                might = (int) Utility.randGaussian(25, 25);
                intelligence = (int) Utility.randGaussian(25, 25);
                politics = (int) Utility.randGaussian(25, 25);
                glamour = (int) Utility.randGaussian(25, 25);
                break;
            }
        }
        leadership = (int) (leadership * mul + abs);
        might = (int) (might * mul + abs);
        intelligence = (int) (intelligence * mul + abs);
        politics = (int) (politics * mul + abs);
        glamour = (int) (glamour * mul + abs);
        //cap the values
        if (!strong) {
            leadership = Utility.cap(leadership, loCap, hiCap);
            might = Utility.cap(might, loCap, hiCap);
            intelligence = Utility.cap(intelligence, loCap, hiCap);
            politics = Utility.cap(politics, loCap, hiCap);
            glamour = Utility.cap(glamour, loCap, hiCap);
        }
    }

    private static void readSetting(String fileName, Map<Integer, int[]> probs, Map<Integer, Map<String, String>> conds) {
        BufferedReader file = null;
        try {
            file = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return;
        }

        try {
            String s;
            while ((s = file.readLine()) != null) {
                String[] temps = s.split("\\s");

                int id = Integer.parseInt(temps[0]);

                int[] p = new int[NUMBER_OF_TYPES];
                for (int j = 1; j <= NUMBER_OF_TYPES; ++j) {
                    p[j - 1] = Integer.parseInt(temps[j]);
                }
                probs.put(id, p);

                Map<String, String> map = new HashMap<String, String>(6);
                for (int j = NUMBER_OF_TYPES + 1; j < temps.length; j += 2) {
                    map.put(temps[j], temps[j + 1]);
                }

                conds.put(id, map);


            }
        } catch (IOException ex) {
            //Do not touch anything if file reading fails.
            ex.printStackTrace();
            return;
        }
    }

    private boolean testConds(Map<String, String> conds) {
        boolean ok = true;
        if (conds != null) {
            Iterator<Map.Entry<String, String>> it = conds.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                if (entry.getKey().equals("leadership")) {
                    if (leadership < Integer.parseInt(entry.getValue())) {
                        ok = false;
                    }
                }
                if (entry.getKey().equals("might")) {
                    if (might < Integer.parseInt(entry.getValue())) {
                        ok = false;
                    }
                }
                if (entry.getKey().equals("intelligence")) {
                    if (intelligence < Integer.parseInt(entry.getValue())) {
                        ok = false;
                    }
                }
                if (entry.getKey().equals("politics")) {
                    if (politics < Integer.parseInt(entry.getValue())) {
                        ok = false;
                    }
                }
                if (entry.getKey().equals("glamour")) {
                    if (glamour < Integer.parseInt(entry.getValue())) {
                        ok = false;
                    }
                }
                if (entry.getKey().equals("total")) {
                    if (leadership + might + intelligence + politics + glamour < Integer.parseInt(entry.getValue())) {
                        ok = false;
                    }
                }
                if (entry.getKey().equals("popularity")) {
                    if (popularity < Integer.parseInt(entry.getValue())) {
                        ok = false;
                    }
                }
                if (entry.getKey().equals("gender")) {
                    if (entry.getValue().equals("male")) {
                        if (gender != MALE) {
                            ok = false;
                        }
                    } else if (entry.getValue().equals("female")) {
                        if (gender != FEMALE) {
                            ok = false;
                        }
                    }
                }
            }
        }
        return ok;
    }

    @Override
    public void randomSpecials(double rate, int abyThreshold, double abyRate) {
        randomSpecials(rate, abyThreshold, abyRate, type);
    }

    @SuppressWarnings("element-type-mismatch")
    public void randomSpecials(double rate, int abyThreshold, double abyRate, int officerType) {
        skill.clear();

        if (skillProb == null) {
            skillProb = new HashMap<Integer, int[]>();
            skillCond = new HashMap<Integer, Map<String, String>>();
            readSetting("DATA/special.txt", skillProb, skillCond);
        }

        Map<Integer, Integer> prob = new HashMap<Integer, Integer>();
        for (Map.Entry<Integer, int[]> i : skillProb.entrySet()) {
            prob.put(i.getKey(), i.getValue()[officerType]);
        }

        for (Map.Entry<Integer, Integer> i : prob.entrySet()) {
            if (Utility.probTestPercentage(i.getValue() * rate / 5.0 * (maxAbility() <= abyThreshold ? 1 : ((maxAbility() - abyThreshold) / (100.0 - abyThreshold) * (abyRate - 1) + 1)))) {
                if (testConds(skillCond.get(i.getKey()))) {
                    skill.add(i.getKey());
                }
            }
        }

        //3x, 4x, 5x, 6x army type related skill adjust
        boolean[][] armySkill = new boolean[4][7];
        int[] cnt = new int[4];
        for (int i = 3; i <= 6; ++i) {
            for (int j = 0; j < 7; ++j) {
                if (skill.contains(i * 10 + j)) {
                    armySkill[i - 3][j] = true;
                    cnt[i - 3]++;
                }
            }
        }
        if (cnt[0] > cnt[1] && cnt[0] > cnt[2] && cnt[0] > cnt[3]) {
            while (Utility.probTestPercentage(60)) {
                int swapArmy = Utility.randBetween(4, 6);
                List<Integer> candidate = new ArrayList<Integer>();
                for (int i = 0; i < 7; ++i) {
                    if (armySkill[swapArmy - 3][i] && !armySkill[0][i]) {
                        candidate.add(i);
                    }
                }
                if (!candidate.isEmpty()) {
                    //force call the "Object" overloaded version of remove
                    skill.remove((Object) (swapArmy * 10 + Utility.randomPick(candidate)));
                    skill.add(30 + Utility.randomPick(candidate));
                }
            }
        } else if (cnt[1] > cnt[0] && cnt[1] > cnt[2] && cnt[1] > cnt[3]) {
            while (Utility.probTestPercentage(75)) {
                int swapArmy = Utility.randBetween(4, 6);
                if (swapArmy == 4) {
                    swapArmy = 3;
                }
                List<Integer> candidate = new ArrayList<Integer>();
                for (int i = 0; i < 7; ++i) {
                    if (armySkill[swapArmy - 3][i] && !armySkill[0][i]) {
                        candidate.add(i);
                    }
                }
                if (!candidate.isEmpty()) {
                    //force call the "Object" overloaded version of remove
                    skill.remove((Object) (swapArmy * 10 + Utility.randomPick(candidate)));
                    skill.add(40 + Utility.randomPick(candidate));
                }
            }
        } else if (cnt[2] > cnt[0] && cnt[2] > cnt[1] && cnt[2] > cnt[3]) {
            while (Utility.probTestPercentage(75)) {
                int swapArmy = Utility.randBetween(4, 6);
                if (swapArmy == 5) {
                    swapArmy = 3;
                }
                List<Integer> candidate = new ArrayList<Integer>();
                for (int i = 0; i < 7; ++i) {
                    if (armySkill[swapArmy - 3][i] && !armySkill[0][i]) {
                        candidate.add(i);
                    }
                }
                if (!candidate.isEmpty()) {
                    //force call the "Object" overloaded version of remove
                    skill.remove((Object) (swapArmy * 10 + Utility.randomPick(candidate)));
                    skill.add(50 + Utility.randomPick(candidate));
                }
            }
        } else if (cnt[3] > cnt[0] && cnt[3] > cnt[1] && cnt[3] > cnt[2]) {
            while (Utility.probTestPercentage(75)) {
                int swapArmy = Utility.randBetween(3, 5);
                List<Integer> candidate = new ArrayList<Integer>();
                for (int i = 0; i < 7; ++i) {
                    if (armySkill[swapArmy - 3][i] && !armySkill[0][i]) {
                        candidate.add(i);
                    }
                }
                if (!candidate.isEmpty()) {
                    //force call the "Object" overloaded version of remove
                    skill.remove((Object) (swapArmy * 10 + Utility.randomPick(candidate)));
                    skill.add(60 + Utility.randomPick(candidate));
                }
            }
        }

    }

    @Override
    public void randomStunts(double rate, int abyThreshold, double abyRate) {
        randomStunts(rate, abyThreshold, abyRate, type);
    }

    public void randomStunts(double rate, int abyThreshold, double abyRate, int officerType) {
        stunt.clear();

        if (stuntProb == null) {
            stuntProb = new HashMap<Integer, int[]>();
            stuntCond = new HashMap<Integer, Map<String, String>>();
            readSetting("DATA/stunt.txt", stuntProb, stuntCond);
        }

        Map<Integer, Integer> prob = new HashMap<Integer, Integer>();
        for (Map.Entry<Integer, int[]> i : stuntProb.entrySet()) {
            prob.put(i.getKey(), i.getValue()[officerType]);
        }

        for (Map.Entry<Integer, Integer> i : prob.entrySet()) {
            if (Utility.probTestPercentage(i.getValue() * rate / 5.0 * (maxAbility() <= abyThreshold ? 1 : ((maxAbility() - abyThreshold) / (100.0 - abyThreshold) * (abyRate - 1) + 1)))) {
                if (testConds(stuntCond.get(i.getKey()))) {
                    stunt.add(i.getKey());
                }
            }
        }

    }

    @Override
    public void randomPersonalTitles(double noSpecProb) {
        randomPersonalTitles(noSpecProb, type);
    }

    public void randomPersonalTitles(double noSpecProb, int officerType) {
        if (personalTitleProb == null) {
            personalTitleProb = new HashMap<Integer, int[]>();
            personalTitleCond = new HashMap<Integer, Map<String, String>>();
            readSetting("DATA/personalTitle.txt", personalTitleProb, personalTitleCond);
        }

        Map<Integer, Integer> prob = new HashMap<Integer, Integer>();
        for (Map.Entry<Integer, int[]> i : personalTitleProb.entrySet()) {
            prob.put(i.getKey(), i.getValue()[officerType]);
        }

        if (rng.nextFloat() < noSpecProb) {
            personalTitle = -1;
        } else {
            boolean ok;
            int trials = 0;
            do {
                trials++;
                personalTitle = Utility.randomCategorize(prob);
                ok = testConds(personalTitleCond.get(personalTitle));
            } while (!ok && trials < 1000);
            if (!ok) {
                personalTitle = -1;
            }
        }
    }
    
    @Override
    public void createUniquePersonalTitle(int lo, int hi, double learnableRate) throws IOException, SQLException{
        createPersonalTitle((int) Utility.randGaussian((lo + hi) / 2.0, hi - lo), type, learnableRate);
    }
    
    public void createPersonalTitle(int level, int officerType, double learnableRate) throws IOException, SQLException{
        personalTitle = Title.getCreatedTitle(commonData, officerType, level, false, learnableRate);
    }

    @Override
    public void randomBattleTitles(double noSpecProb) {
        randomBattleTitles(noSpecProb, type);
    }

    public void randomBattleTitles(double noSpecProb, int officerType) {
        if (battleTitleProb == null) {
            battleTitleProb = new HashMap<Integer, int[]>();
            battleTitleCond = new HashMap<Integer, Map<String, String>>();
            readSetting("DATA/battleTitle.txt", battleTitleProb, battleTitleCond);
        }

        Map<Integer, Integer> prob = new HashMap<Integer, Integer>();
        for (Map.Entry<Integer, int[]> i : battleTitleProb.entrySet()) {
            prob.put(i.getKey(), i.getValue()[officerType]);
        }

        if (rng.nextFloat() < noSpecProb) {
            battleTitle = -1;
        } else {
            boolean ok;
            int trials = 0;
            do {
                trials++;
                battleTitle = Utility.randomCategorize(prob);
                ok = testConds(battleTitleCond.get(battleTitle));
            } while (!ok && trials < 1000);
            if (!ok) {
                battleTitle = -1;
            }
        }

        //3x, 4x, 5x, 6x army type battle title adjust
        boolean[][] armySkill = new boolean[5][7];
        int[] cnt = new int[5];
        for (int i = 3; i <= 7; ++i) {
            for (int j = 0; j < 7; ++j) {
                if (skill.contains(i * 10 + j)) {
                    armySkill[i - 3][j] = true;
                    cnt[i - 3]++;
                }
            }
        }
        int max = cnt[0];
        int maxType = 0;
        for (int i = 1; i < 5; ++i) {
            if (cnt[i] > max) {
                max = cnt[i];
                maxType = i;
            }
        }
        if (battleTitle / 10 == 30 || battleTitle / 10 == 32) {
            battleTitle = battleTitle / 10 * 10 + maxType;
        }

    }
    
    @Override
    public void createUniqueBattleTitle(int lo, int hi, double learnableRate) throws IOException, SQLException{
        createBattleTitle((int) Utility.randGaussian((lo + hi) / 2.0, hi - lo), type, learnableRate);
    }
    
    public void createBattleTitle(int level, int officerType, double learnableRate) throws IOException, SQLException{
        battleTitle = Title.getCreatedTitle(commonData, officerType, level, true, learnableRate);
    }

    @Override
    public void randomHiddenValues() {

        //attitude towards han = 0 - 2, higher = better attitude towards Han
        hanAttitude = Utility.randBetween(0, 2);

        //officer tendency = 0 - 4
        officerTendency = Utility.randBetween(0, 4);
        switch (type) {
            case GENERAL: {
                righteous = Utility.randomCategorize(0.1, 0.2, 0.4, 0.2, 0.1);
                ambition = Utility.randomCategorize(0.1, 0.25, 0.3, 0.25, 0.1);
                gut = Utility.randomCategorize_i(1, 2, 3, 4, 6, 9, 12, 9, 6) + 1;
                calm = Utility.randomCategorize_i(4, 6, 9, 12, 9, 6, 4, 3, 2) + 1;
                personality = Utility.randomCategorize(0.15, 0.05, 0.15, 0.2, 0.3, 0.15) + 1;
                break;
            }
            case MIGHTY: {
                righteous = Utility.randomCategorize(0.05, 0.1, 0.25, 0.3, 0.3);
                ambition = Utility.randomCategorize(0.15, 0.25, 0.3, 0.2, 0.1);
                gut = Utility.randomCategorize_i(1, 1, 1, 1, 3, 6, 9, 12, 9, 1) + 1;
                calm = Utility.randomCategorize_i(12, 9, 6, 4, 2, 1, 1, 1, 1) + 1;
                personality = Utility.randomCategorize(0.5, 0.05, 0.1, 0.3, 0.05, 0.1) + 1;
                break;
            }
            case ADVISOR: {
                righteous = Utility.randomCategorize(0.1, 0.2, 0.4, 0.2, 0.1);
                ambition = Utility.randomCategorize(0.1, 0.25, 0.3, 0.25, 0.1);
                gut = Utility.randomCategorize_i(2, 4, 9, 6, 4, 2, 1, 1, 1) + 1;
                calm = Utility.randomCategorize_i(1, 1, 1, 1, 3, 6, 9, 12, 9, 1) + 1;
                personality = Utility.randomCategorize(0.05, 0.15, 0.1, 0.1, 0.15, 0.45) + 1;
                break;
            }
            case POLITICIAN: {
                righteous = Utility.randomCategorize(0.1, 0.2, 0.4, 0.2, 0.1);
                ambition = Utility.randomCategorize(0.15, 0.25, 0.3, 0.2, 0.1);
                gut = Utility.randomCategorize_i(6, 9, 6, 4, 2, 1, 1, 1, 1) + 1;
                calm = Utility.randomCategorize_i(1, 1, 1, 1, 3, 6, 9, 12, 9) + 1;
                personality = Utility.randomCategorize(0.05, 0.4, 0.1, 0.05, 0.1, 0.4) + 1;
                break;
            }
            case INTEL_GENERAL: {
                righteous = Utility.randomCategorize(0.1, 0.2, 0.4, 0.2, 0.1);
                ambition = Utility.randomCategorize(0.1, 0.2, 0.3, 0.25, 0.15);
                gut = Utility.randomCategorize_i(1, 1, 1, 2, 4, 6, 9, 6, 4) + 1;
                calm = Utility.randomCategorize_i(1, 1, 1, 2, 4, 6, 9, 6, 4) + 1;
                personality = Utility.randomCategorize(0.05, 0.05, 0.15, 0.25, 0.25, 0.25) + 1;
                break;
            }
            case EMPEROR: {
                righteous = Utility.randomCategorize(0.1, 0.2, 0.4, 0.2, 0.1);
                ambition = Utility.randomCategorize(0.05, 0.1, 0.2, 0.3, 0.35);
                gut = Utility.randomCategorize_i(1, 2, 4, 6, 9, 6, 4, 2, 1) + 1;
                calm = Utility.randomCategorize_i(1, 1, 1, 2, 4, 6, 9, 6, 4) + 1;
                personality = Utility.randomCategorize(0.05, 0.05, 0.3, 0.15, 0.15, 0.3) + 1;
                break;
            }
            case ALL_ROUNDER: {
                righteous = Utility.randomCategorize(0.1, 0.2, 0.4, 0.2, 0.1);
                ambition = Utility.randomCategorize(0.1, 0.2, 0.3, 0.25, 0.15);
                gut = Utility.randomCategorize_i(1, 1, 2, 3, 4, 6, 9, 12, 6, 1) + 1;
                calm = Utility.randomCategorize_i(1, 1, 2, 3, 4, 6, 9, 12, 6, 1) + 1;
                personality = Utility.randomCategorize(0.025, 0.025, 0.05, 0.3, 0.3, 0.3) + 1;
                break;
            }
            case NORMAL: {
                righteous = Utility.randomCategorize(0.1, 0.2, 0.4, 0.2, 0.1);
                ambition = Utility.randomCategorize(0.2, 0.3, 0.3, 0.1, 0.1);
                gut = Utility.randomCategorize_i(1, 2, 4, 6, 9, 6, 4, 2, 1) + 1;
                calm = Utility.randomCategorize_i(1, 2, 4, 6, 9, 6, 4, 2, 1) + 1;
                personality = Utility.randomCategorize(0.2, 0.2, 0.1, 0.15, 0.15, 0.2) + 1;
                break;
            }
            case CHEAP: {
                righteous = Utility.randomCategorize(0.35, 0.3, 0.2, 0.1, 0.05);
                ambition = Utility.randomCategorize(0.35, 0.3, 0.2, 0.1, 0.05);
                gut = Utility.randomCategorize_i(12, 9, 6, 4, 1, 1, 1, 1, 1) + 1;
                calm = Utility.randomCategorize_i(12, 9, 6, 4, 1, 1, 1, 1, 1) + 1;
                personality = Utility.randomCategorize(0.05, 0.6, 0.2, 0.05, 0.05, 0.05) + 1;
                break;
            }
        }

        personalityConsideration = Utility.randBetween(0, 6);

        bornLocation = Utility.randBetween(0, 17);

        int adjust = Utility.randomCategorize(0.05, 0.15, 0.6, 0.15, 0.05) - 2;
        stretagicalAttitude = Utility.cap(3 - (ambition + adjust), 0, 3);

    }

    public int getType() {
        return type;
    }
}
