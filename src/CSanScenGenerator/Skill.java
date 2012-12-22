package CSanScenGenerator;

import java.util.*;

/**
 *
 * @author Peter
 */
public class Skill {

    private static Map<Integer, Skill> skills;
    
    private Skill(){}

    public static Map<Integer, Skill> getSkills(java.sql.Connection commonData) throws java.sql.SQLException {
        if (skills == null) {
            java.sql.Statement stmt = commonData.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery("select ID from Skill");
            skills = new HashMap<Integer, Skill>();
            while (rs.next()) {
                int id = rs.getInt("ID");
                skills.put(id, new Skill());
            }
            rs.close();
            stmt.close();
        }
        return skills;
    }

}
