package CSanScenGenerator;

import java.util.*;

/**
 *
 * @author Peter
 */
public class Stunt {

    private static Map<Integer, Stunt> stunts;

    private Stunt() {
    }

    public static Map<Integer, Stunt> getStunts(java.sql.Connection commonData) throws java.sql.SQLException {
        if (stunts == null) {
            java.sql.Statement stmt = commonData.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery("select ID from Stunt");
            stunts = new HashMap<Integer, Stunt>();
            while (rs.next()) {
                int id = rs.getInt("ID");
                stunts.put(id, new Stunt());
            }
            rs.close();
            stmt.close();
        }
        return stunts;
    }

}
