package CSanScenGenerator;

import java.sql.*;
import java.util.*;
import java.io.*;

/**
 *
 * @author Peter
 */
public class Treasure {

    public static final int WEAPON = 0;
    public static final int BOOK = 1;
    public static final int HORSE = 2;
    private int id;
    private String name;
    private int pic;
    private int value;
    private boolean appeared;
    private Building hiddenArch;
    private int appearYear;
    private Officer belongTo;
    private Set<Integer> influences;
    private String desc;
    private int type;
    private static List<String> nameList;
    private static int autoId = 10000;

    public Treasure(int type) throws IOException {
        id = autoId;
        autoId++;

        this.type = type;

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
        name = "";
        int nameLen = Utility.randBetween(2, 5);
        for (int i = 0; i < nameLen; ++i) {
            name += Utility.randomPick(nameList);
        }
        
        switch (type) {
            case WEAPON: {
                name += Utility.randomPick(new String[]{"劍", "槍", "弓", "矛", "刀"});
                pic = Utility.randBetween(0, 16);
                break;
            }
            case BOOK: {
                name += Utility.randomPick(new String[]{"書", "卷"});
                pic = Utility.randBetween(100, 110);
                break;
            }
            case HORSE: {
                name += "馬";
                pic = Utility.randBetween(300, 304);
                break;
            }
        }
        
        influences = new HashSet<Integer>();
        desc = "";
    }

    public void randomize(Connection commonData, int value, int year, double priceRate) throws IOException, SQLException {
        this.value = value;
        this.appearYear = year;

        Map<Integer, Integer> influencesProb = null;
        switch (type) {
            case WEAPON: {
                influencesProb = InfluenceRate.getWeaponRates(commonData);
                break;
            }
            case BOOK: {
                influencesProb = InfluenceRate.getBookRates(commonData);
                break;
            }
            case HORSE: {
                influencesProb = InfluenceRate.getHorseRates(commonData);
                break;
            }
        }
        int remainValue = value * 5;
        int trials = 0;
        while (trials < 1000) {
            trials++;
            int influence = Utility.randomCategorize(influencesProb);
            int actualValue = InfluenceRate.getActualValue(commonData, influence);
            if (actualValue <= remainValue){
                remainValue -= actualValue;
                influences.add(influence);
            }
            if (remainValue < 10){
                break;
            }
        }
        
        if (this.type == HORSE){
            influences.add(5110);
        }
    }

    public void place(double someoneHoldProb, int scenYear, List<Officer> allOfficers, List<Building> allBuildings) {
        if (this.appearYear < scenYear) {
            if (Utility.probTestPercentage(someoneHoldProb)){
                this.belongTo = Utility.randomPick(allOfficers);
                this.appeared = true;
            } else {
                this.hiddenArch = Utility.randomPick(allBuildings);
                this.appeared = false;
            }
        } else {
            this.hiddenArch = Utility.randomPick(allBuildings);
            this.appeared = false;
        }
    }
    
    public void writeTreasure(Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement("insert into Treasure (ID, Name, Pic, Worth, Available, HidePlace, AppearYear, BelongedPerson, Influences) "
                    + "values (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setInt(3, pic);
            pstmt.setInt(4, value);
            pstmt.setBoolean(5, appeared);
            pstmt.setInt(6, hiddenArch == null ? -1 : hiddenArch.id);
            pstmt.setInt(7, appearYear);
            pstmt.setInt(8, belongTo == null ? -1 : belongTo.id);
            pstmt.setString(9, Utility.join(influences.toArray(), " "));
            //pstmt.setCharacterStream(10, new StringReader(desc));
            pstmt.executeUpdate();
        } finally {
            pstmt.close();
        }
    }
}
