package CSanScenGenerator;

import java.util.*;
import java.sql.*;

/**
 *
 * @author Peter
 */
public class Army {
    
    protected int id, type;
    protected String name;
    protected int soldier;
    
    protected int morale, combativity, experience;
    protected Officer leader = null;
    protected Officer followedLeader = null;
    protected int leaderExperience;
    
    private static int autoId = 1;
    
    public Army(){
        this.id = autoId;
        autoId++;
    }
    
    public void randomType(boolean water, Set<Integer> troopKinds){
        if (water && Utility.probTestPercentage((50))){
            type = 30;
            name = CSanScenGenerator.frontend.Frontend.isTc() ? "走舸隊" : "走舸队";
        } else {
            type = Utility.randBetween(0, 2);
            name = "部隊";
            /*if (CSanScenGenerator.frontend.Frontend.isTc()){
                name = (type == 0 ? "輕步兵" : (type == 1 ? "輕弩兵" : "輕騎兵")) + "隊";
            } else {
                name = (type == 0 ? "轻步兵" : (type == 1 ? "轻弩兵" : "轻骑兵")) + "队";
            }*/
        }
    }
    
    public void randomMorale(int moraleLo, int moraleHi){
        morale = Utility.randBetween(moraleLo, moraleHi);
        combativity = Utility.randBetween(moraleLo, moraleHi);
    }
    
    public void randomLeader(Set<Officer> officers, int expLo, int expHi){
        leader = Utility.randomPick(officers);
        experience = Utility.randBetween(expLo, expHi);
        leaderExperience = Utility.randBetween(expLo, expHi);
        if (leaderExperience >= 1000){
            followedLeader = leader;
            leaderExperience = 0;
        }
    }
    
    public void randomSoldier(int lo, int hi){
        soldier = Utility.randBetween(lo, hi) * 1000;
    }
    
    public void writeArmy(Connection conn) throws SQLException{
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement("insert into Military (ID, Name, KindID, Quantity, Morale, Combativity, Experience, FollowedLeaderID, LeaderID, LeaderExperience) values "
                    + "(?, ?, ?, ?, ?, ?,  ?, ?, ?, ?)");
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setInt(3, type);
            pstmt.setInt(4, soldier);
            pstmt.setInt(5, morale);
            pstmt.setInt(6, combativity);
            pstmt.setInt(7, experience);
            pstmt.setInt(8, followedLeader == null ? -1 : followedLeader.id);
            pstmt.setInt(9, leader == null ? -1 : leader.id);
            pstmt.setInt(10, leaderExperience);
            pstmt.executeUpdate();
        } finally {
            pstmt.close();
        }
    }
    
    public static String toListStr(Collection<Army> t) {
        int[] r = new int[t.size()];
        Iterator<Army> it = t.iterator();
        int i = 0;
        while (it.hasNext()){
            r[i++] = it.next().id;
        }
        return Utility.join(r, " ");
    }
    
}
