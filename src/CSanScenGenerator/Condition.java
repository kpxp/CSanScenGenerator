package CSanScenGenerator;

import java.util.*;
import java.io.*;
import java.sql.*;

/**
 *
 * @author Peter
 */
public class Condition {
    
    int kindId;
    String description;
    int param;
    int paramBase;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + this.kindId;
        return hash;
    }
    
    @Override
    public boolean equals(Object o){
        if (!(o instanceof Condition)) return false;
        Condition c = (Condition) o;
        return this.kindId == c.kindId;
    }
    
    private static Map<Integer, Condition> conditions;
            
    public static Map<Integer, Condition> getConditions() throws IOException{
        if (conditions == null) {
            conditions = new HashMap<Integer, Condition>();
            BufferedReader f = new BufferedReader(new FileReader("DATA/condition.txt"));
            while (true){
                Condition c = new Condition();
                String line = f.readLine();
                if (line == null) {
                    break;
                }
                String[] s = line.split("\\s");
                c.kindId = Integer.parseInt(s[0]);
                c.paramBase = Integer.parseInt(s[1]);
                c.description = s[2];
                conditions.put(c.getId(), c);
            }
            f.close();
        }
        return conditions;
    }
    
    private static int autoId = 20000;
    public static List<Integer> writeConditions(Connection cdData, List<Condition> condition) throws SQLException{
        PreparedStatement pstmt = null;
        List<Integer> r = new ArrayList<Integer>();
        try {
            for (Condition c : condition){
                pstmt = cdData.prepareStatement("insert into Condition (ID, Kind, Name, Parameter) "
                        + "values (?, ?, ?, ?)");
                pstmt.setInt(1, autoId);
                pstmt.setInt(2, c.kindId);
                pstmt.setString(3, c.description.replace("$i", Integer.toString(c.param)));
                pstmt.setString(4, Math.round(c.param) - c.param < 0.0001 ? Integer.toString(c.param) : Double.toString(c.param));
                pstmt.executeUpdate();
                autoId++;
                r.add(autoId);
            }
        } finally {
            if (pstmt != null){
                pstmt.close();
            }
        }
        return r;
    }

    public int getId() {
        return kindId;
    }

    public String getDescription() {
        return description;
    }
    
}
