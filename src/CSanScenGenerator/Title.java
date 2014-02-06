package CSanScenGenerator;

import java.util.*;
import java.sql.*;
import java.io.*;

/**
 *
 * @author Peter
 */
public class Title {

    private static Map<Integer, Title> titles = new HashMap<Integer, Title>();
    private int id;
    private int level;
    private String name, description, conditionString;
    private List<Integer> influences, conditions;
    
    private static final int CREATED_TITLE_ID_ABOVE = 20000;

    private Title(int inId, int inLevel, String inName, String inDescription, String inConditionString, String inInfluences,
            String inConditions) {
        id = inId;
        level = inLevel;
        name = inName;
        description = inDescription;
        conditionString = inConditionString;
        influences = Utility.fromIntListToList(inInfluences);
        conditions = Utility.fromIntListToList(inConditions);
    }

    private Title(int inId, int inLevel, String inName, String inDescription, String inConditionString, List<Integer> inInfluences,
            List<Integer> inConditions) {
        id = inId;
        level = inLevel;
        name = inName;
        description = inDescription;
        conditionString = inConditionString;
        influences = inInfluences;
        conditions = inConditions;
    }
    
    public int getLevel(){
        return level;
    }

    public static Map<Integer, Title> getTitles() {
        return titles;
    }

    private static int autoId = CREATED_TITLE_ID_ABOVE;
    private static List<String> nameList;
    public static int getCreatedTitle(Connection commonData, int type, int level, double learnableRate) throws IOException, SQLException {

        int id = autoId;
        autoId++;

        int lv = Math.max(1, level);

        if (nameList == null) {
            nameList = new ArrayList<String>();
            String s;
            BufferedReader f = new BufferedReader(new InputStreamReader(new FileInputStream("DATA/" + (CSanScenGenerator.frontend.Frontend.isTc() ? "" : "GBK/") + "treasureName.txt"), "UTF8"));

            while (true) {
                s = f.readLine();
                if (s == null) {
                    break;
                }
                nameList.add(s);
            }
        }
        String name = "";
        int nameLen = Utility.randBetween(2, 5);
        for (int i = 0; i < nameLen; ++i) {
            name += Utility.randomPick(nameList);
        }
        switch (type) {
            case TypedOfficer.GENERAL:
            case TypedOfficer.MIGHTY:
                name += "將";
                break;
            case TypedOfficer.ADVISOR:
                name += "計";
                break;
            case TypedOfficer.POLITICIAN:
                name += "吏";
                break;
            case TypedOfficer.EMPEROR:
                name += "雄";
                break;
        }

        StringBuilder descriptionBuilder = new StringBuilder();

        List<Integer> influences = new ArrayList<Integer>();
        List<Integer> leaderInfluences = new ArrayList<Integer>();
        boolean combat = false;
        int remainValue = (int) (2.5 * (5 * level * level - 10 * level + 11));
        int trials = 0;
        while (trials < 1000) {
            trials++;
            Map<Integer, Integer> influencesProb = InfluenceRate.getTitleRates(commonData, type);
            int influence = Utility.randomCategorize(influencesProb);
            int actualValue = InfluenceRate.getActualValue(commonData, influence);
            boolean leaderOnly = Utility.probTestPercentage(InfluenceRate.getLeaderProb(commonData, influence)) 
                    && InfluenceRate.isBattle(commonData, influence);
            if (actualValue <= remainValue && (leaderOnly || actualValue * 1.5 <= remainValue) && !influences.contains(influence)) {
                remainValue -= actualValue;
                if (leaderOnly){
                    leaderInfluences.add(influence);
                } else {
                    influences.add(influence);
                }
                if (InfluenceRate.isBattle(commonData, influence)) {
                    combat = true;
                }
                if (!leaderOnly) {
                    remainValue -= actualValue / 2;
                }
                descriptionBuilder.append("。").append(InfluenceRate.getDescription(commonData, influence));
            }
            if (remainValue <= 10) {
                break;
            }
        }

        Set<Condition> conditionsToWrite = new HashSet<Condition>();
        List<Integer> condition;
        String prereq;
        if (Utility.probTestPercentage(learnableRate)){
            condition = new ArrayList<Integer>();
            StringBuilder sb = new StringBuilder();
            for (Integer i : influences){
                List<Integer> r = InfluenceRate.getConditions(commonData, i);
                for (Integer j : r){
                    Condition c = Condition.getConditions().get(j);
                    conditionsToWrite.add(c);
                }
            }
            for (Integer i : leaderInfluences){
                List<Integer> r = InfluenceRate.getConditions(commonData, i);
                for (Integer j : r){
                    Condition c = Condition.getConditions().get(j);
                    conditionsToWrite.add(c);
                }
            }
            for (Condition c : conditionsToWrite){
                if (c.paramBase >= 0){
                    c.param = (int) (Math.pow(lv, 1.5) / conditionsToWrite.size() * 10 * c.paramBase * Utility.randBetween(0.8, 1.2));
                } else {
                    c.param = (int) (-c.paramBase / (Math.pow(lv, 1.5) / conditionsToWrite.size() * 10 * Utility.randBetween(0.8, 1.2)));
                }
                sb.append("。").append(c.description.replace("$i", Integer.toString(c.param)));
            }
            List<Integer> cid = Condition.writeConditions(commonData, new ArrayList<Condition>(conditionsToWrite));
            condition.addAll(cid);
            prereq = sb.toString();
        } else {
            condition = Collections.singletonList(900);
            prereq = "天生";
        }
        
        if (!leaderInfluences.isEmpty()){
            influences.add(281);
            influences.addAll(leaderInfluences);
        }

        titles.put(id, new Title(id, lv, name, descriptionBuilder.toString(), prereq, influences, condition));

        return id;
    }

    public static void writeTitles(Connection commonData) throws SQLException {
        Statement stmt = commonData.createStatement();
        stmt.executeUpdate("delete from Title where ID >= " + CREATED_TITLE_ID_ABOVE);
        stmt.close();
        PreparedStatement pstmt = null;
        try {
            for (Title t : titles.values()) {
                pstmt = commonData.prepareStatement("insert into Title (ID, Kind, \"Level\", Combat, Name, Description, Prerequisite, Influences, Conditions) "
                        + "values (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                pstmt.setInt(1, t.id);
                pstmt.setInt(2, 1);
                pstmt.setInt(3, t.level);
                pstmt.setInt(4, 1);
                pstmt.setString(5, t.name);
                pstmt.setString(6, t.description);
                pstmt.setString(7, t.conditionString);
                pstmt.setString(8, Utility.join(t.influences.toArray(), " "));
                pstmt.setString(9, Utility.join(t.conditions.toArray(), " "));
                //pstmt.setCharacterStream(10, new StringReader(desc));
                pstmt.executeUpdate();
                pstmt.close();
            }
        } finally {
            if (pstmt != null){
                pstmt.close();
            }
        }
    }
}

