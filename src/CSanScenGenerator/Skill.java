package CSanScenGenerator;

import java.util.*;

/**
 *
 * @author Peter
 */
public class Skill {

    private static Map<Integer, Skill> skills;
    private int level;
    
    private Skill(int level){
        this.level = level;
    }

    public static Map<Integer, Skill> getSkills(java.sql.Connection commonData) throws java.sql.SQLException {
        if (skills == null) {
            java.sql.Statement stmt = commonData.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery("select ID, `Level` from Skill");
            skills = new HashMap<Integer, Skill>();
            while (rs.next()) {
                int id = rs.getInt("ID");
                int level = rs.getInt("Level");
                skills.put(id, new Skill(level));
            }
            rs.close();
            stmt.close();
        }
        return skills;
    }
    
    public int getLevel(){
        return level;
    }

}
