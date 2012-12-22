package CSanScenGenerator;

import java.util.*;

/**
 *
 * @author Peter
 */
public class Technique {

    private int preId;
    private static Map<Integer, Technique> techniques;

    private Technique(int inPreId) {
        preId = inPreId;
    }

    public static Map<Integer, Technique> getTechniques(java.sql.Connection commonData) throws java.sql.SQLException {
        if (techniques == null) {
            java.sql.Statement stmt = commonData.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery("select ID, PreID from Technique");
            techniques = new HashMap<Integer, Technique>();
            while (rs.next()) {
                int id = rs.getInt("ID");
                int preId = rs.getInt("PreID");
                techniques.put(id, new Technique(preId));
            }
            rs.close();
            stmt.close();
        }
        return techniques;
    }

    /**
     * @return the preId
     */
    public int getPreId() {
        return preId;
    }
}
