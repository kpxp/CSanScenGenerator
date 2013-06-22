package CSanScenGenerator;

import java.util.*;
import java.io.*;
import java.sql.*;
import java.text.DecimalFormat;

/**
 *
 * @author Peter
 */
public class InfluenceKindRate {
 
    private static Map<Integer, InfluenceKindRate> influences;
    private int id;
    private int weaponRate;
    private int bookRate;
    private int horseRate;
    private int[] titleRate = new int[TypedOfficer.NUMBER_OF_TYPES];
    private boolean isBattle;
    private double leaderProb;
    private double baseValue;
    private boolean baseValueInverse;
    private double buildingRate;
    private boolean buildingRateInverse;
    private double paramMin;
    private double paramMax;
    private double costExponent;
    private boolean integralParam;
    private List<Integer> conditionKind;
    private double conditionParam;
    private boolean noParam;
    private boolean unusable;
    
    private String description;

    private static Map<Integer, InfluenceKindRate> getInfluenceKindRates() throws IOException, SQLException {
        if (influences == null) {
            influences = new HashMap<Integer, InfluenceKindRate>();
            BufferedReader f = new BufferedReader(new InputStreamReader(new FileInputStream("DATA/influenceKind.txt"), "UTF-8"));
            while (true) {
                //read rates
                InfluenceKindRate r = new InfluenceKindRate();
                String line = f.readLine();
                if (line == null) {
                    break;
                }
                String[] s = line.split("\\s");
                r.id = Integer.parseInt(s[0]);
                r.weaponRate = Integer.parseInt(s[1]);
                r.bookRate = Integer.parseInt(s[2]);
                r.horseRate = Integer.parseInt(s[3]);
                r.baseValue = Double.parseDouble(s[4]);
                r.baseValueInverse = s[5].equals("1") ? true : false;
                r.noParam = s[5].equals("2") ? true : false;
                r.unusable = s[5].equals("3") ? true : false;
                r.costExponent = Double.parseDouble(s[6]);
                r.buildingRate = Double.parseDouble(s[7]);
                r.buildingRateInverse = s[8].equals("1") ? true: false;
                try {
                    r.paramMin = Integer.parseInt(s[9]);
                    r.paramMax = Integer.parseInt(s[10]);
                    r.integralParam = true;
                } catch (NumberFormatException ex){
                    r.paramMin = Double.parseDouble(s[9]);
                    r.paramMax = Double.parseDouble(s[10]);
                    r.integralParam = false;
                }
                
                for (int i = 0; i < TypedOfficer.NUMBER_OF_TYPES; ++i) {
                    r.titleRate[i] = Integer.parseInt(s[i + 11]);
                }
                r.isBattle = s[TypedOfficer.NUMBER_OF_TYPES + 11].equals("1") ? true : false;
                r.leaderProb = Double.parseDouble(s[TypedOfficer.NUMBER_OF_TYPES + 12]);
                String condKindStr = s[TypedOfficer.NUMBER_OF_TYPES + 13];
                String[] cksp = condKindStr.split(",");
                r.conditionKind = new ArrayList<Integer>();
                for (String i : cksp){
                    r.conditionKind.add(Integer.parseInt(i));
                }
                r.description = s[TypedOfficer.NUMBER_OF_TYPES + 14];
                influences.put(r.id, r);
            }
            f.close();
        }
        return influences;
    }
    
    public static List<Integer> getConditionKind(int id) throws IOException, SQLException{
        if (influences == null) {
            getInfluenceKindRates();
        }
        return influences.get(id).conditionKind;
    }

    public static Map<Integer, InfluenceRate> generateInfluences(Connection commonData, int cnt) throws IOException, SQLException{
        //delete generated things
        Statement dstmt = commonData.createStatement();
        dstmt.execute("delete from Influence where ID >= 10000");
        dstmt.close();
        
        Map<Integer, InfluenceKindRate> kinds = getInfluenceKindRates();
        Map<Integer, InfluenceRate> result = new HashMap<Integer, InfluenceRate>();
        for (int i = 0; i < cnt; ++i){
            InfluenceRate r = new InfluenceRate();
            r.id = 10000 + i;
            
            //randomly pick a kind
            InfluenceKindRate k;
            do {
                k = Utility.randomPick(kinds.values());
            } while (k.unusable || k.noParam || k.baseValue == 0);
            r.kind = k.id;
            
            //copy over prob data
            r.bookRate = k.bookRate;
            r.horseRate = k.horseRate;
            r.weaponRate = k.weaponRate;
            r.leaderProb = k.leaderProb;
            r.isBattle = k.isBattle;
            r.titleRate = Arrays.copyOf(k.titleRate, k.titleRate.length);

            //generate parameter
            String desc = k.description;
            double param;
            if (k.noParam){
                param = 0;
            } else if (k.integralParam){
                int lo = (int) Math.round(k.paramMin);
                int hi = (int) Math.round(k.paramMax);
                //params are int
                int paramI = Utility.randBetween(lo, hi);
                desc = desc.replace("$i", Integer.toString(paramI))
                    .replace("$d", Integer.toString(paramI)).replace("$f", Integer.toString(paramI * 100))
                    .replace("$e", Integer.toString((1 - paramI) * 100)).replace("$m", Double.toString(paramI * 10000));
                param = paramI;
            } else {
                //params are double
                double lo = k.paramMin;
                double hi = k.paramMax;
                param = Utility.randBetween(lo, hi);
                param = Utility.roundToSignificantFigures(param, 2);
                //put params into string
                DecimalFormat df2 = new DecimalFormat("0.00");
                DecimalFormat df = new DecimalFormat("0");
                desc = desc.replace("$i", df2.format(param))
                    .replace("$d", df.format(param * 100)).replace("$f", df.format((param - 1) * 100))
                    .replace("$e", df.format((1 - param) * 100)).replace("$m", df.format(param * 10000));
            }
            
            //compute cost
            if (k.noParam){
                r.actualValue = (int) k.baseValue;
            } else {
                if (k.baseValueInverse){
                    r.actualValue = (int) Math.round(k.baseValue / Math.pow(param, k.costExponent));
                } else {
                    r.actualValue = (int) Math.round(k.baseValue * Math.pow(param, k.costExponent));
                }
                if (k.buildingRateInverse){
                    r.buildingRate = (int) Math.round(k.buildingRate / Math.pow(param, k.costExponent));
                } else {
                    r.buildingRate = (int) Math.round(k.buildingRate / Math.pow(param, k.costExponent));
                }
            }
            
            PreparedStatement stmt = commonData.prepareStatement(
                        "insert into Influence (ID, Kind, Name, Description, Parameter, Parameter2) values (?, ?, ?, ?, ?, ?)");
            stmt.setInt(1, r.id);
            stmt.setInt(2, k.id);
            
            //handle special things and write to db
            if (desc.contains("$t")){
                //terrain
                int terrain = Utility.randBetween(1, 9);
                if (terrain >= 7) terrain++; //skip mountains
                String terrainString = "";
                double costMod = 1;
                switch (terrain){
                    case 1: terrainString = "平原"; costMod = 2; break;
                    case 2: terrainString = "草原"; costMod = 2; break;
                    case 3: terrainString = "森林"; break;
                    case 4: terrainString = "湿地"; costMod = 0.5; break;
                    case 5: terrainString = "山地"; break;
                    case 6: terrainString = "水域"; costMod = 1.5; break;
                    case 8: terrainString = "荒地"; break;
                    case 9: terrainString = "沙漠"; costMod = 0.5; break;
                    case 10: terrainString = "雪地"; costMod = 0.5; break;
                }
                r.actualValue *= costMod;
                desc = desc.replace("$t", terrainString);
                
                //prepare parameters
                stmt.setString(3, desc);
                stmt.setString(4, desc);
                stmt.setInt(5, terrain);
                stmt.setDouble(6, param);
            } else if (desc.contains("$s")){
                //military kind
                int military = Utility.randBetween(0, 4);
                String militaryString = "";
                switch (military){
                    case 0: militaryString = "步兵"; break;
                    case 1: militaryString = "弩兵"; break;
                    case 2: militaryString = "騎兵"; break;
                    case 3: militaryString = "水軍"; break;
                    case 4: militaryString = "器械"; break;
                }
                desc = desc.replace("$s", militaryString);
                
                //prepare parameters
                stmt.setString(3, desc);
                stmt.setString(4, desc);
                stmt.setInt(5, military);
                stmt.setDouble(6, param);
            } else if (desc.contains("$z")){
                //disaster
                int disaster = Utility.randBetween(0, 5);
                String disasterString = "";
                switch (disaster){
                    case 0: disasterString = "水災"; break;
                    case 1: disasterString = "旱災"; break;
                    case 2: disasterString = "地震"; break;
                    case 3: disasterString = "蝗災"; break;
                    case 4: disasterString = "瘟疫"; break;
                    case 5: disasterString = "風災"; break;
                }
                desc = desc.replace("$z", disasterString);
                
                //prepare parameters
                stmt.setString(3, desc);
                stmt.setString(4, desc);
                stmt.setInt(5, disaster);
                stmt.setDouble(6, param);
            } else {
                //standard
                stmt.setString(3, desc);
                stmt.setString(4, desc);
                stmt.setDouble(5, param);
                //for Kind 352 capped water capability and 6140 decrease loyalty capped by loyalty
                if (k.id == 352 || k.id == 6140 || (k.id >= 6700 && k.id <= 6745) || k.id == 6760){
                    stmt.setInt(6, 1);
                } else if (k.id == 6360){
                    stmt.setInt(6, 150);
                } else if (k.id == 6350) {
                    stmt.setInt(6, 3);
                } else if (k.id == 6750) {
                    stmt.setInt(6, 100);
                } else {
                    stmt.setString(6, "");
                }
            }
            
            r.conditions = new HashMap<Integer, Integer>();
            for (Integer j : k.conditionKind){
                int requiredValue = (int) Math.round(k.baseValueInverse ? k.conditionParam / param : param / k.conditionParam);
                switch (j){
                    case 0:
                    case 100:
                    case 110:
                    case 120:
                    case 130:
                    case 140:
                    case 200:
                    case 210:
                    case 220:
                    case 230:
                    case 240:
                    case 400:
                    case 410:
                        requiredValue *= 100;
                        break;
                    case 300:
                        requiredValue *= 200;
                        break;
                }
                r.conditions.put(j, requiredValue);
            }
            
            r.description = desc;
            
            //finally, put to db
            stmt.executeUpdate();
            
            result.put(r.id, r);
            
            stmt.close();
        }
        
        return result;
    }
    
}
