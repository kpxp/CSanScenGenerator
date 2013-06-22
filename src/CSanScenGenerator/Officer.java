package CSanScenGenerator;

import java.sql.*;
import java.util.*;

/**
 *
 * @author Peter
 */
public class Officer {

    private static Map<Integer, Integer> personalConsiderationAttachment;
    public static final boolean MALE = false;
    public static final boolean FEMALE = true;
    protected Random rng = new Random();
    protected Database db;
    protected boolean gender = false;
    protected String surname, givenname, calledname;
    protected int leadership, might, intelligence, politics, glamour;
    protected int face;
    protected int debut, born, dead, debutLocation;
    protected int deadReason;
    protected int personAttachment, loyalty;
    protected int righteous, ambition, popularity, calm, gut, personalityConsideration, personality,
            officerTendency, hanAttitude, stretagicalAttitude, bornLocation;
    protected Set<Integer> skill = new HashSet<Integer>();
    protected Set<Integer> stunt = new HashSet<Integer>();
    protected int personalTitle = -1, battleTitle = -1;
    protected boolean debutted, living;
    protected int id, blood, tightblood;
    protected boolean canStartFaction;
    protected Building building;
    protected boolean employed;
    protected Officer father, mother, spouse, brother;
    protected int generation;
    private boolean isKing = false;
    private Officer king;
    protected boolean strong;
    protected boolean leaderPossibility;
    protected Connection commonData;
    protected List<Officer> imitateOfficer = new ArrayList<Officer>();
    protected List<Officer> hateOfficer = new ArrayList<Officer>();
    
    Officer(Connection commonDataConn) throws SQLException{
        if (personalConsiderationAttachment == null) {
            personalConsiderationAttachment = new HashMap<Integer, Integer>();
            Statement stmt2 = commonDataConn.createStatement();
            ResultSet rs = stmt2.executeQuery("select ID, offset from IdealTendencyKind");
            while (rs.next()) {
                int a = rs.getInt("offset");
                int b = rs.getInt("ID");
                personalConsiderationAttachment.put(b, a);
            }
            rs.close();
            stmt2.close();
        }
    }

    public Officer(Database db, int id, boolean inStrong, Connection commonDataConn) throws SQLException {
        this.db = db;
        this.id = id;
        strong = inStrong;
        blood = id;
        tightblood = id;
        generation = 0;
        if (personalConsiderationAttachment == null) {
            personalConsiderationAttachment = new HashMap<Integer, Integer>();
            Statement stmt2 = commonDataConn.createStatement();
            ResultSet rs = stmt2.executeQuery("select ID, offset from IdealTendencyKind");
            while (rs.next()) {
                int a = rs.getInt("offset");
                int b = rs.getInt("ID");
                personalConsiderationAttachment.put(b, a);
            }
            rs.close();
            stmt2.close();
        }
        commonData = commonDataConn;
    }

    public boolean isStrong() {
        return strong;
    }
    
    public boolean isAlive(){
        return living;
    }

    public void setParent(Officer inFather, Officer inMother) {
        father = inFather;
        mother = inMother;
        if (inFather != null) {
            blood = inFather.blood;
            tightblood = inFather.tightblood;
            generation = inFather.generation + 1;
        } else if (inMother != null) {
            tightblood = inMother.tightblood;
            generation = inMother.generation + 1;
        }
    }

    public void setSpouse(Officer o) {
        spouse = o;
        if (o != null) {
            tightblood = o.tightblood;
        }
    }

    public Officer getSpouse() {
        return spouse;
    }

    public int getGeneration() {
        return generation;
    }

    public int getTightBlood() {
        return tightblood;
    }

    public boolean getGender() {
        return gender;
    }

    public void setRandomGender(double femaleProb) {
        gender = rng.nextFloat() <= femaleProb ? FEMALE : MALE;
    }

    public void setRandomNames(double doubleNameProb) {
        List<String> surnameList = db.getSurname();
        List<String> givennameList = gender == MALE ? db.getMalegivenname() : db.getFemalegivenname();
        String picked;

        picked = surnameList.get(rng.nextInt(surnameList.size()));
        surname = picked;

        picked = givennameList.get(rng.nextInt(givennameList.size()));
        givenname = picked;

        if (picked.length() < 2 && rng.nextFloat() <= doubleNameProb) {
            picked = givennameList.get(rng.nextInt(givennameList.size()));
            if (picked.length() == 1) {
                givenname += picked;
            }
        }

        calledname = "";

        if (father != null) {
            surname = father.surname;
        }
    }

    public String getName() {
        return surname + givenname;
    }

    public int getId() {
        return id;
    }

    public void randomFaceImage() {
        List<Integer> list = gender == MALE ? db.getMaleface() : db.getFemaleface();
        face = list.get(rng.nextInt(list.size()));
    }

    public void setFace(int x) {
        face = x;
    }

    /**
     * Return the greatest value of all abilities
     */
    public int maxAbility() {
        int r = leadership;
        if (might > r) {
            r = might;
        }
        if (intelligence > r) {
            r = intelligence;
        }
        if (politics > r) {
            r = politics;
        }
        return r;
    }

    public void tweakAbility(double mul, int abs) {
        leadership = (int) (leadership * mul + abs);
        might = (int) (might * mul + abs);
        intelligence = (int) (intelligence * mul + abs);
        politics = (int) (politics * mul + abs);
        glamour = (int) (glamour * mul + abs);
    }

    public void randomAbility(double mul, double abs, int loCap, int hiCap) {
        leadership = Utility.randBetween_f(1 * mul + abs, 100 * mul + abs);
        might = Utility.randBetween_f(1 * mul + abs, 100 * mul + abs);
        intelligence = Utility.randBetween_f(1 * mul + abs, 100 * mul + abs);
        politics = Utility.randBetween_f(1 * mul + abs, 100 * mul + abs);
        glamour = Utility.randBetween_f(1 * mul + abs, 100 * mul + abs);
        if (!strong) {
            leadership = Utility.cap(leadership, loCap, hiCap);
            might = Utility.cap(might, loCap, hiCap);
            intelligence = Utility.cap(intelligence, loCap, hiCap);
            politics = Utility.cap(politics, loCap, hiCap);
            glamour = Utility.cap(glamour, loCap, hiCap);
        }
    }

    public void abilityFromParent(int loCap, int hiCap, int base, int var) {
        if (father != null && mother != null) {
            leadership = Utility.randBetween(Math.min(father.leadership, mother.leadership) - var, Math.max(father.leadership, mother.leadership) + var) + base;
            might = Utility.randBetween(Math.min(father.might, mother.might) - var, Math.max(father.might, mother.might) + var) + base;
            intelligence = Utility.randBetween(Math.min(father.intelligence, mother.intelligence) - var, Math.max(father.intelligence, mother.intelligence) + var) + base;
            politics = Utility.randBetween(Math.min(father.politics, mother.politics) - var, Math.max(father.politics, mother.politics) + var) + base;
            glamour = Utility.randBetween(Math.min(father.glamour, mother.glamour) - var, Math.max(father.glamour, mother.glamour) + var) + base;
        } else {
            leadership = (int) Utility.randGaussian((father != null ? father : mother).leadership, var) + base;
            might = (int) Utility.randGaussian((father != null ? father : mother).might, var) + base;
            intelligence = (int) Utility.randGaussian((father != null ? father : mother).intelligence, var) + base;
            politics = (int) Utility.randGaussian((father != null ? father : mother).politics, var) + base;
            glamour = (int) Utility.randGaussian((father != null ? father : mother).glamour, var) + base;
        }
        if (!strong) {
            leadership = Utility.cap(leadership, loCap, hiCap);
            might = Utility.cap(might, loCap, hiCap);
            intelligence = Utility.cap(intelligence, loCap, hiCap);
            politics = Utility.cap(politics, loCap, hiCap);
            glamour = Utility.cap(glamour, loCap, hiCap);
        }
    }

    public int getAbilitySum() {
        return leadership + might + intelligence + politics + glamour;
    }
    
    public int getMerit() throws SQLException {
        int personalTitleLevel = 0;
        if (this.personalTitle >= 0){
            personalTitleLevel = Title.getPersonalTitles(commonData).get(this.personalTitle).getLevel();
            personalTitleLevel = (int) (Math.pow(personalTitleLevel, 1.5) * 15);
        }
        
        int battleTitleLevel = 0;
        if (this.battleTitle >= 0){
            battleTitleLevel = Title.getBattleTitles(commonData).get(this.battleTitle).getLevel();
            battleTitleLevel = (int) (Math.pow(battleTitleLevel, 1.5) * 15);
        }
        
        int allSkillMerit = 0;
        for (int i : this.skill) {
            Skill s = Skill.getSkills(commonData).get(i);
            allSkillMerit += 5 * s.getLevel();
        }
        
        return (this.might + this.leadership + this.intelligence + this.politics + this.glamour) * 
                (100 + personalTitleLevel + battleTitleLevel + allSkillMerit);
    }

    public int getAbilityMin() {
        return Collections.min(Arrays.asList(leadership, might, intelligence, politics, glamour));
    }

    public int getAbilityMax() {
        return Collections.max(Arrays.asList(leadership, might, intelligence, politics, glamour));
    }

    public void setAvailability(int scenYear, List<Building> allBuildings){
        debutted = debut <= scenYear;
        living = scenYear <= dead;
        if (!debutted) {
            debutLocation = Utility.randomPick(allBuildings).id;
        }
    }
    
    public void completeRandomYears(int scenYear, int lo, int hi, int bornLo, int bornHi, int livingLo, int livingHi,
            boolean isDebutted, List<Building> allBuildings) {
        debut = Utility.randBetween(lo, debutted ? scenYear : hi);
        born = debut - Utility.randBetween(bornLo, bornHi);
        dead = Math.max(debut, scenYear) + Utility.randBetween(livingLo, livingHi);
        setAvailability(scenYear, allBuildings);
    }

    public void yearFromParent(int scenYear, int bornLo, int bornHi, int livingLo, int livingHi, List<Building> allBuildings,
            int parentLo, int parentHi) {
        if (mother != null) {
            born = mother.born + Utility.randBetween(parentLo, parentHi);
        } else {
            born = father.born + Utility.randBetween(parentLo, parentHi);
        }
        debut = born + Utility.randBetween(bornLo, bornHi);
        dead = Math.max(debut, scenYear) + Utility.randBetween(livingLo, livingHi);
        setAvailability(scenYear, allBuildings);
    }

    public boolean isAvailable() {
        return debutted && living;
    }

    public int getBornYear() {
        return born;
    }

    public int getPersonAttachmentDifference(Officer anotherOfficer, int cap) {
        int result = Math.abs(anotherOfficer.personAttachment - this.personAttachment);
        if (result > cap / 2) {
            result = cap - result;
        }
        return result;
    }

    public void randomDeathReason(double naturalDeathProb) {
        deadReason = rng.nextFloat() <= naturalDeathProb ? 0 : 1;
    }

    public void randomPersonAttachment(int lo, int hi) {
        personAttachment = Utility.randBetween(lo, hi);
    }

    public void attachmentFromParent(int lo, int hi, int var) {
        if (father == null) {
            personAttachment = (int) Utility.randGaussian(mother.personAttachment, var);
        } else if (mother == null) {
            personAttachment = (int) Utility.randGaussian(father.personAttachment, var);
        } else {
            personAttachment = (int) Utility.randGaussian((Utility.probTestPercentage(50) ? father : mother).personAttachment, var);
        }

        if (personAttachment < lo) {
            personAttachment += hi - lo;
        } else if (personAttachment > hi) {
            personAttachment -= hi - lo;
        }
    }

    public void randomPersonAttachmentWithinDiff(Officer reference, int value, int hi) {
        personAttachment = Utility.randBetween(reference.personAttachment - value, reference.personAttachment + value);
        if (personAttachment >= hi) {
            personAttachment -= hi;
        }
        if (personAttachment < 0) {
            personAttachment += hi;
        }
    }

    public void randomHiddenValues() {
        //righteous = 0 - 4, higher = more righteous
        righteous = Utility.randBetween(0, 4);

        ambition = Utility.randBetween(0, 4);

        //officer tendency = 0 - 4
        officerTendency = Utility.randBetween(0, 4);

        gut = Utility.randBetween(1, 9);

        calm = Utility.randBetween(1, 9);

        //attitude towards han = 0 - 2, higher = better attitude towards Han
        hanAttitude = Utility.randBetween(0, 2);

        //stretagical attitude = 0 - 3, higher = less ambitious
        stretagicalAttitude = Utility.randBetween(0, 3);

        personalityConsideration = Utility.randBetween(0, 6);

        bornLocation = Utility.randBetween(0, 17);

        personality = Utility.randBetween(1, 6);

    }

    public void randomLeaderPossibility(int factor){
        leaderPossibility = Utility.probTestPercentage(factor * ambition) ? true : false;
    }

    public void hiddenValuesFromParent() {
        if (father != null && mother != null) {

            righteous = (Utility.probTestPercentage(50) ? father : mother).righteous + Utility.randomCategorize(0.1, 0.2, 0.4, 0.2, 0.1) - 2;

            ambition = (Utility.probTestPercentage(50) ? father : mother).ambition + Utility.randomCategorize(0.1, 0.2, 0.4, 0.2, 0.1) - 2;

            //0.84 = 1 - 0.6 x 0.6
            officerTendency = Utility.probTestPercentage(84) ? (Utility.probTestPercentage(50) ? father : mother).officerTendency : Utility.randBetween(0, 4);

            gut = (Utility.probTestPercentage(50) ? father : mother).gut + Utility.randomCategorize(0.05, 0.05, 0.1, 0.2, 0.3, 0.2, 0.1, 0.05, 0.05) - 4;

            calm = (Utility.probTestPercentage(50) ? father : mother).calm + Utility.randomCategorize(0.05, 0.05, 0.1, 0.2, 0.3, 0.2, 0.1, 0.05, 0.05) - 4;

            hanAttitude = (Utility.probTestPercentage(50) ? father : mother).hanAttitude + Utility.randomCategorize(0.2, 0.6, 0.2) - 1;

            stretagicalAttitude = (Utility.probTestPercentage(50) ? father : mother).stretagicalAttitude + Utility.randomCategorize(0.2, 0.6, 0.2) - 1;

            personalityConsideration = (Utility.probTestPercentage(50) ? father : mother).personalityConsideration + Utility.randomCategorize(0.1, 0.2, 0.4, 0.2, 0.1) - 2;

            bornLocation = Utility.probTestPercentage(80) ? (Utility.probTestPercentage(25) ? father : mother).bornLocation : Utility.randBetween(0, 17);

            personality = Utility.probTestPercentage(50) ? (Utility.probTestPercentage(50) ? father : mother).personality : Utility.randBetween(1, 6);
            
            leaderPossibility = Utility.probTestPercentage(50) ? father.leaderPossibility : mother.leaderPossibility;

        } else {

            righteous = (father != null ? father : mother).righteous + Utility.randomCategorize(0.1, 0.2, 0.4, 0.2, 0.1) - 2;

            ambition = (father != null ? father : mother).righteous + Utility.randomCategorize(0.1, 0.2, 0.4, 0.2, 0.1) - 2;

            officerTendency = Utility.probTestPercentage(60) ? (father != null ? father : mother).officerTendency : Utility.randBetween(0, 4);

            gut = (father != null ? father : mother).gut + Utility.randomCategorize(0.05, 0.05, 0.1, 0.2, 0.3, 0.2, 0.1, 0.05, 0.05) - 4;

            calm = (father != null ? father : mother).calm + Utility.randomCategorize(0.05, 0.05, 0.1, 0.2, 0.3, 0.2, 0.1, 0.05, 0.05) - 4;

            hanAttitude = (father != null ? father : mother).hanAttitude + Utility.randomCategorize(0.2, 0.6, 0.2) - 1;

            stretagicalAttitude = (father != null ? father : mother).stretagicalAttitude + Utility.randomCategorize(0.2, 0.6, 0.2) - 1;

            personalityConsideration = (father != null ? father : mother).personalityConsideration + Utility.randomCategorize(0.1, 0.2, 0.4, 0.2, 0.1) - 2;

            bornLocation = Utility.probTestPercentage(80) ? (father != null ? father : mother).bornLocation : Utility.randBetween(0, 17);

            personality = Utility.probTestPercentage(50) ? (father != null ? father : mother).personality : Utility.randBetween(1, 6);
            
            leaderPossibility = (father != null ? father : mother).leaderPossibility;

        }

        righteous = Utility.cap(righteous, 0, 4);

        gut = Utility.cap(gut, 1, 9);

        calm = Utility.cap(calm, 1, 9);

        hanAttitude = Utility.cap(hanAttitude, 0, 2);

        stretagicalAttitude = Utility.cap(stretagicalAttitude, 0, 3);

        personalityConsideration = Utility.cap(personalityConsideration, 0, 6);

    }
    
    private static int maxIdealTendencyId = -1;
    private static int findMaxIdealTendencyId(){
        int min = Integer.MAX_VALUE;
        int r = -1;
        for (Map.Entry<Integer, Integer> i : personalConsiderationAttachment.entrySet()){
            if (i.getValue() < min){
                min = i.getValue();
                r = i.getKey();
            }
        }
        return r;
    }
    
    public void setMaxIdealTendency(){
        if (maxIdealTendencyId == -1){
            maxIdealTendencyId = findMaxIdealTendencyId();
        }
        personalityConsideration = maxIdealTendencyId;
    }
    
    public void setMaxAmbition(){
        ambition = 4;
        stretagicalAttitude = 0;
    }

    public void randomPopularity(int lo, int hi) {
        popularity = Utility.randBetween(lo, hi) * 10;
    }

    public void popularityFromParent(double lo, double hi) {
        if (father == null) {
            popularity = (int) (mother.popularity * Utility.randBetween(lo, hi));
        } else if (mother == null) {
            popularity = (int) (father.popularity * Utility.randBetween(lo, hi));
        } else {
            popularity = (int) (Math.max(father.popularity, mother.popularity) * Utility.randBetween(lo, hi));
        }
    }

    public void specialFromParent(int base, int var) throws SQLException {
        skill.clear();
        Set<Integer> skillIds = Skill.getSkills(commonData).keySet();
        for (Integer i : skillIds) {
            double prob;
            if (father != null && mother != null) {
                if (father.skill.contains(i) && mother.skill.contains(i)) {
                    prob = base * 2 - var;
                } else if (father.skill.contains(i) || mother.skill.contains(i)) {
                    prob = base;
                } else {
                    prob = var;
                }
            } else {
                if ((father != null ? father : mother).skill.contains(i)) {
                    prob = base * 2 - var;
                } else {
                    prob = var;
                }
            }
            if (Utility.probTestPercentage(prob)) {
                skill.add(i);
            }
        }
    }

    public void randomSpecials(double rate, int abyThreshold, double abyRate) throws SQLException {
        skill.clear();
        Set<Integer> skillIds = Skill.getSkills(commonData).keySet();
        for (Integer i : skillIds) {
            if (Utility.probTestPercentage(rate * (maxAbility() <= abyThreshold ? 1 : ((maxAbility() - abyThreshold) / (100.0 - abyThreshold) * (abyRate - 1) + 1)))) {
                skill.add(i);
            }
        }
    }

    public void stuntFromParent(int base, int var) throws SQLException {
        stunt.clear();
        Set<Integer> stuntIds = Stunt.getStunts(commonData).keySet();
        for (Integer i : stuntIds) {
            double prob;
            if (father != null && mother != null) {
                if (father.skill.contains(i) && mother.skill.contains(i)) {
                    prob = base * 2 - var;
                } else if (father.skill.contains(i) || mother.skill.contains(i)) {
                    prob = base;
                } else {
                    prob = var;
                }
            } else {
                if ((father != null ? father : mother).skill.contains(i)) {
                    prob = base * 2 - var;
                } else {
                    prob = var;
                }
            }
            if (Utility.probTestPercentage(prob)) {
                stunt.add(i);
            }
        }
    }

    public void randomStunts(double rate, int abyThreshold, double abyRate) throws SQLException {
        stunt.clear();
        Set<Integer> stuntIds = Stunt.getStunts(commonData).keySet();
        for (Integer i : stuntIds) {
            if (Utility.probTestPercentage(rate * (maxAbility() <= abyThreshold ? 1 : ((maxAbility() - abyThreshold) / (100.0 - abyThreshold) * (abyRate - 1) + 1)))) {
                stunt.add(i);
            }
        }
    }

    public void personalTitleFromParent(double noSpecProb, int inherit) throws SQLException {
        randomPersonalTitles(noSpecProb);
        if (father != null && mother != null) {
            if (Utility.probTestPercentage(inherit * 2)) {
                personalTitle = (Utility.probTestPercentage(50) ? father : mother).personalTitle;
                return;
            }
        } else {
            if (Utility.probTestPercentage(inherit)) {
                personalTitle = (father != null ? father : mother).personalTitle;
                return;
            }
        }
    }

    public void randomPersonalTitles(double noSpecProb) throws SQLException {
        if (rng.nextFloat() < noSpecProb) {
            personalTitle = -1;
        } else {
            Set<Integer> personalTitles = Title.getPersonalTitles(commonData).keySet();
            List<Integer> ids = new ArrayList<Integer>();
            for (Integer i : personalTitles) {
                ids.add(i);
            }
            personalTitle = Utility.randomPick(ids);
        }
    }
    
    public void createUniquePersonalTitle(int lo, int hi, double learnableRate) throws java.io.IOException, SQLException{
        double mean = (lo + hi) / 2.0;
        double var = hi - mean;
        personalTitle = Title.getCreatedTitle(commonData, TypedOfficer.NORMAL, (int) Utility.randGaussian(mean, var), false, learnableRate);
    }

    public void battleTitleFromParent(double noSpecProb, int inherit) throws SQLException {
        randomBattleTitles(noSpecProb);
        if (father != null && mother != null) {
            if (Utility.probTestPercentage(inherit * 2)) {
                battleTitle = (Utility.probTestPercentage(50) ? father : mother).battleTitle;
                return;
            }
        } else {
            if (Utility.probTestPercentage(inherit)) {
                battleTitle = (father != null ? father : mother).battleTitle;
                return;
            }
        }
    }

    public void randomBattleTitles(double noSpecProb) throws SQLException {
        if (rng.nextFloat() < noSpecProb) {
            battleTitle = -1;
        } else {
            Set<Integer> battleTitles = Title.getBattleTitles(commonData).keySet();
            List<Integer> ids = new ArrayList<Integer>();
            for (Integer i : battleTitles) {
                ids.add(i);
            }
            battleTitle = Utility.randomPick(ids);
        }
    }
    
    public void createUniqueBattleTitle(int lo, int hi, double learnableRate) throws java.io.IOException, SQLException{
        double mean = (lo + hi) / 2.0;
        double var = hi - mean;
        battleTitle = Title.getCreatedTitle(commonData, TypedOfficer.NORMAL, (int) Utility.randGaussian(mean, var), true, learnableRate);
    }

    public void clearSpecial() {
        skill.clear();
    }

    public void placeInBuildingUnemployed(List<Building> allBuildings) {
        building = Utility.randomPick(allBuildings);
        building.unemployedOfficers.add(this);
    }

    public void joinFaction(Building b, Officer king, int loyaltyLo, int loyaltyHi, boolean strongHiLoyalty) {
        joinFaction(b, king, loyaltyLo, loyaltyHi, strongHiLoyalty, true);
    }

    public void joinFaction(Building b, Officer king, int loyaltyLo, int loyaltyHi, boolean strongHiLoyalty, boolean employ) {
        this.loyalty = Utility.randBetween(loyaltyLo, loyaltyHi);
        this.king = king;
        //remove unemployedOfficers from the original
        if (building != null){
            building.unemployedOfficers.remove(this);
        }
        //and change building
        building = b;
        //and joins in new faction
        if (employ) {
            building.officers.add(this);
            employed = true;
        } else {
            building.unemployedOfficers.add(this);
        }
        if (king != null) {
            ensureIdealTendency(king);
        }
        if (this.strong && strongHiLoyalty){
            this.loyalty = 999;
        }
    }

    public void ensureIdealTendency(Officer king) {
        if (getPersonAttachmentDifference(king, 150) > personalConsiderationAttachment.get(personalityConsideration)) {
            int maxVar = personalConsiderationAttachment.get(personalityConsideration);
            personAttachment = (Utility.randBetween(king.personAttachment - maxVar, king.personAttachment + maxVar) + 150) % 150;
        }
    }

    public void setLocationFromParent(int lo, int hi) {
        if (this.isAvailable()) {
            if (father != null) {
                if (father.building != null) {
                    joinFaction(father.building, father.king, lo, hi, father.employed);
                }
            } else {
                if (mother.building != null) {
                    joinFaction(mother.building, mother.king, lo, hi, mother.employed);
                }
            }
        }
    }
    private static int maleFaceIndex = 0;
    private static int femaleFaceIndex = 0;
    private static int maleFaceAIndex = 0;
    private static int maleFaceMIndex = 0;
    private static int maleFaceUIndex = 0;
    private static int femaleFaceAIndex = 0;
    private static int femaleFaceMIndex = 0;

    public void setFace(Database db) {
        if (getGender() == Officer.MALE) {
            List<Integer> list = db.getMaleface();
            setFace(list.get(maleFaceIndex % list.size()));
            maleFaceIndex++;
        } else {
            List<Integer> list = db.getFemaleface();
            setFace(list.get(femaleFaceIndex % list.size()));
            femaleFaceIndex++;
        }
    }

    public void setTypedFace(Database db) {
        if (getGender() == Officer.MALE) {
            if (getLeadership() < 50 && getMight() < 50 && getIntelligence() < 50 && getPolitics() < 50 && getGlamour() < 50) {
                List<Integer> list = db.getMaleFaceU();
                setFace(list.get(maleFaceUIndex % list.size()));
                maleFaceUIndex++;
            } else if (getLeadership() + getMight() > getIntelligence() + getPolitics()) {
                List<Integer> list = db.getMaleFaceM();
                setFace(list.get(maleFaceMIndex % list.size()));
                maleFaceMIndex++;
            } else {
                List<Integer> list = db.getMaleFaceA();
                setFace(list.get(maleFaceAIndex % list.size()));
                maleFaceAIndex++;
            }
        } else {
            if (getLeadership() + getMight() > getIntelligence() + getPolitics()) {
                List<Integer> list = db.getFemaleFaceM();
                setFace(list.get(femaleFaceMIndex % list.size()));
                femaleFaceMIndex++;
            } else {
                List<Integer> list = db.getFemaleFaceA();
                setFace(list.get(femaleFaceAIndex % list.size()));
                femaleFaceAIndex++;
            }
        }
    }
    
    public void randomImitateOfficers(List<Officer> allOfficers, double prob){
        while (Utility.probTestPercentage(prob)){
            imitateOfficer.add(Utility.randomPick(allOfficers));
        }
    }
    
    public void randomHateOfficers(List<Officer> allOfficers, double prob){
        while (Utility.probTestPercentage(prob)){
            hateOfficer.add(Utility.randomPick(allOfficers));
        }
    }
    
    public void imitateFamilyOfficers(List<Officer> allOfficers, double spouse, double close, double blood){
        if (Utility.probTestPercentage(spouse)){
            imitateOfficer.add(this.spouse);
        }
        for (Officer o : allOfficers){
            if ((o.father != null && o.father.id == this.id) || 
                    (o.mother != null && o.mother.id == this.id) || 
                    (this.father != null && this.father.id == o.id) || 
                    (this.mother != null && this.mother.id == o.id) || 
                    (o.father != null && this.father != null && o.father.id == this.father.id) || 
                    (o.mother != null && this.mother != null && o.mother.id == this.mother.id)){
                if (Utility.probTestPercentage(close)){
                    imitateOfficer.add(o);
                }
            }
            if (o.blood == this.blood && Utility.probTestPercentage(blood)){
                imitateOfficer.add(o);
            }
        }
    }

    public void writeOfficer(Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement("insert into Person (ID, Available, Alive, SurName, GivenName, CalledName, Sex, Pic, "
                    + "Ideal, IdealTendency, PCharacter, YearAvailable, YearBorn, YearDead, DeadReason, Strength, Command, Intelligence, "
                    + "Politics, Glamour, Reputation, Braveness, Calmness, Loyalty, BornRegion, AvailableLocation, PersonalLoyalty, Ambition,"
                    + "Qualification, ValuationOnGovernment, StrategyTendency, Skills, PersonalTitle, CombatTitle, Stunts, "
                    + "Strain, Father, Mother, Spouse, Brother, Generation, LeaderPossibility, ClosePersons, HatedPersons) values"
                    + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            pstmt.setInt(1, id);
            pstmt.setBoolean(2, debutted);
            pstmt.setBoolean(3, living);
            pstmt.setString(4, surname);
            pstmt.setString(5, givenname);
            pstmt.setString(6, calledname);
            /*pstmt.setCharacterStream(4, new StringReader(surname), surname.length());
            pstmt.setCharacterStream(5, new StringReader(givenname), givenname.length());
            if (calledname.length() > 0){
            pstmt.setCharacterStream(6, new StringReader(calledname), calledname.length());
            } else {
            pstmt.setString(6, "");
            }*/
            pstmt.setBoolean(7, gender);
            pstmt.setInt(8, face);
            pstmt.setInt(9, personAttachment);
            pstmt.setInt(10, personalityConsideration);
            pstmt.setInt(11, personality);
            pstmt.setInt(12, debut);
            pstmt.setInt(13, born);
            pstmt.setInt(14, dead);
            pstmt.setInt(15, deadReason);
            pstmt.setInt(16, might);
            pstmt.setInt(17, leadership);
            pstmt.setInt(18, intelligence);
            pstmt.setInt(19, politics);
            pstmt.setInt(20, glamour);
            pstmt.setInt(21, popularity);
            pstmt.setInt(22, gut);
            pstmt.setInt(23, calm);
            pstmt.setInt(24, loyalty);
            pstmt.setInt(25, bornLocation);
            pstmt.setInt(26, debutLocation);
            pstmt.setInt(27, righteous);
            pstmt.setInt(28, ambition);
            pstmt.setInt(29, officerTendency);
            pstmt.setInt(30, hanAttitude);
            pstmt.setInt(31, stretagicalAttitude);
            pstmt.setString(32, Utility.join(skill.toArray(), " "));
            pstmt.setInt(33, personalTitle);
            pstmt.setInt(34, battleTitle);
            pstmt.setString(35, Utility.join(stunt.toArray(), " "));
            pstmt.setInt(36, blood);
            pstmt.setInt(37, father == null ? -1 : father.id);
            pstmt.setInt(38, mother == null ? -1 : mother.id);
            pstmt.setInt(39, spouse == null ? -1 : spouse.id);
            pstmt.setInt(40, brother == null ? -1 : brother.id);
            pstmt.setInt(41, generation);
            pstmt.setBoolean(42, leaderPossibility);
            pstmt.setString(43, Utility.join(Officer.officerIds(imitateOfficer).toArray(), " "));
            pstmt.setString(44, Utility.join(Officer.officerIds(hateOfficer).toArray(), " "));
            pstmt.executeUpdate();
        } finally {
            pstmt.close();
        }
    }
    
    public static List<Integer> officerIds(List<Officer> o){
        List<Integer> r = new ArrayList<Integer>();
        for (Officer i : o){
            if (i != null){
                r.add(i.id);
            }
        }
        return r;
    }

    /**
     * @return the leadership
     */
    public int getLeadership() {
        return leadership;
    }

    /**
     * @return the might
     */
    public int getMight() {
        return might;
    }

    /**
     * @return the intelligence
     */
    public int getIntelligence() {
        return intelligence;
    }

    /**
     * @return the politics
     */
    public int getPolitics() {
        return politics;
    }

    /**
     * @return the glamour
     */
    public int getGlamour() {
        return glamour;
    }

    public static String toListStr(Collection<Officer> t) {
        int[] r = new int[t.size()];
        Iterator<Officer> it = t.iterator();
        int i = 0;
        while (it.hasNext()) {
            r[i++] = it.next().id;
        }
        return Utility.join(r, " ");
    }

    public void setKing(boolean b) {
        isKing = b;
    }

    public boolean isKing() {
        return isKing;
    }
}
