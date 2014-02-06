package CSanScenGenerator;

import java.util.*;
import java.io.*;
import java.sql.*;

/**
 *
 * @author Peter
 */
public class InfluenceRate {

    private static Map<Integer, InfluenceRate> influences;
    int id;
    int kind;
    int weaponRate;
    int bookRate;
    int horseRate;
    int[] titleRate = new int[TypedOfficer.NUMBER_OF_TYPES];
    boolean isBattle;
    double leaderProb;
    int actualValue;
    int buildingRate;
    String description;
    
    Map<Integer, Integer> conditions;
    //FIXME influence.txt needs pre-defined conditions

    public static void addInfluences(Connection commonData, Map<Integer, InfluenceRate> rates) throws IOException, SQLException{
        if (influences == null){
            getInfluenceRates(commonData);
        }
        influences.putAll(rates);
    }

    private static Map<Integer, InfluenceRate> getInfluenceRates(Connection commonData) throws IOException, SQLException {
        if (influences == null) {
            influences = new HashMap<Integer, InfluenceRate>();
            BufferedReader f = new BufferedReader(new FileReader("DATA/influence.txt"));
            Statement stmt = commonData.createStatement();
            ResultSet rs = null;
            while (true) {
                //read rates
                InfluenceRate r = new InfluenceRate();
                String line = f.readLine();
                if (line == null) {
                    break;
                }
                String[] s = line.split("\\s");
                r.id = Integer.parseInt(s[0]);
                r.weaponRate = Integer.parseInt(s[1]);
                r.bookRate = Integer.parseInt(s[2]);
                r.horseRate = Integer.parseInt(s[3]);
                r.actualValue = Integer.parseInt(s[4]);
                r.buildingRate = Integer.parseInt(s[5]);
                for (int i = 0; i < TypedOfficer.NUMBER_OF_TYPES; ++i) {
                    r.titleRate[i] = Integer.parseInt(s[i + 6]);
                }
                r.isBattle = s[15].equals("1") ? true : false;
                r.leaderProb = Double.parseDouble(s[16]);
                influences.put(r.id, r);
                //read description
                rs = stmt.executeQuery("select Kind, Description from Influence where ID = " + r.id);
                if (rs.next()){
                    r.kind = rs.getInt(1);
                    r.description = rs.getString(2);
                } else {
                    throw new IllegalArgumentException("influnece.txt中的影響編號" + r.id + "不存在");
                }
            }
            f.close();
            rs.close();
            stmt.close();
        }
        return influences;
    }

    public static Map<Integer, Integer> getWeaponRates(Connection cdData) throws SQLException, IOException {
        Map<Integer, Integer> r = new HashMap<Integer, Integer>();
        for (InfluenceRate i : getInfluenceRates(cdData).values()) {
            r.put(i.id, i.weaponRate);
        }
        return r;
    }

    public static Map<Integer, Integer> getBookRates(Connection cdData) throws SQLException, IOException {
        Map<Integer, Integer> r = new HashMap<Integer, Integer>();
        for (InfluenceRate i : getInfluenceRates(cdData).values()) {
            r.put(i.id, i.bookRate);
        }
        return r;
    }

    public static Map<Integer, Integer> getHorseRates(Connection cdData) throws SQLException, IOException {
        Map<Integer, Integer> r = new HashMap<Integer, Integer>();
        for (InfluenceRate i : getInfluenceRates(cdData).values()) {
            r.put(i.id, i.horseRate);
        }
        return r;
    }

    public static Map<Integer, Integer> getBuildingRates(Connection cdData) throws SQLException, IOException {
        Map<Integer, Integer> r = new HashMap<Integer, Integer>();
        for (InfluenceRate i : getInfluenceRates(cdData).values()) {
            r.put(i.id, i.buildingRate);
        }
        return r;
    }

    private static Map<Integer, Map<Integer, Integer>> titleRates = new HashMap<Integer, Map<Integer, Integer>>();
    public static Map<Integer, Integer> getTitleRates(Connection cdData, int type) throws IOException, SQLException {
        if (!titleRates.containsKey(type)){
            titleRates.put(type, new HashMap<Integer, Integer>());
            for (InfluenceRate i : getInfluenceRates(cdData).values()) {
                titleRates.get(type).put(i.id, i.titleRate[type]);
            }
        }
        return titleRates.get(type);
    }

    public static boolean isBattle(Connection cdData, int influenceId) throws IOException, SQLException {
        return getInfluenceRates(cdData).get(influenceId).isBattle;
    }
    
    public static double getLeaderProb(Connection cdData, int influenceId) throws IOException, SQLException {
        return getInfluenceRates(cdData).get(influenceId).leaderProb;
    }

    public static int getActualValue(Connection cdData, int influenceId) throws IOException, SQLException {
        return getInfluenceRates(cdData).get(influenceId).actualValue;
    }
    
    public static String getDescription(Connection cdData, int influenceId) throws IOException, SQLException {
        return getInfluenceRates(cdData).get(influenceId).description;
    }

    public static List<Integer> getConditions(Connection cdData, int id) throws IOException, SQLException {
        if (influences == null){
            getInfluenceRates(cdData);
        }
        return InfluenceKindRate.getConditionKind(influences.get(id).kind);
    }
}
