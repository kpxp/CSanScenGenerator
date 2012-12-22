package CSanScenGenerator;

import java.util.*;
import java.sql.*;

/**
 *
 * @author Peter
 */
public final class ScenOfficerReader {
    
    private static final Map<Integer, Officer> officers = new HashMap<Integer, Officer>();
    private static final Map<Integer, Integer> fatherId = new HashMap<Integer, Integer>();
    private static final Map<Integer, Integer> motherId = new HashMap<Integer, Integer>();
    private static final Map<Integer, Integer> spouseId = new HashMap<Integer, Integer>();
    private static final Map<Integer, Integer> brotherId = new HashMap<Integer, Integer>();
    
    private ScenOfficerReader(){}
    
    public static Officer readOfficer(Connection cdConn, boolean tc, int id) throws ClassNotFoundException, SQLException{
        if (officers.isEmpty()){
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            java.util.Properties prop = new java.util.Properties();
            prop.put("charSet", tc ? "Big5" : "GBK");
            Connection scenConn = DriverManager.getConnection("jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=ScenOfficer.mdb", prop);
            Statement stmt = scenConn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from Person");
            while (rs.next()){
                Officer o = new Officer(cdConn);
                o.id = rs.getInt("id");
                o.surname = rs.getString("SurName");
                o.givenname = rs.getString("GivenName");
                o.calledname = rs.getString("CalledName");
                o.gender = rs.getBoolean("Sex");
                o.face = rs.getInt("Pic");
                o.personAttachment = rs.getInt("Ideal");
                o.personalityConsideration = rs.getInt("IdealTendency");
                o.canStartFaction = rs.getBoolean("LeaderPossibility");
                o.personality = rs.getInt("PCharacter");
                o.debut = rs.getInt("YearAvailable");
                o.born = rs.getInt("YearBorn");
                o.dead = rs.getInt("YearDead");
                o.deadReason = rs.getInt("DeadReason");
                o.might = rs.getInt("Strength");
                o.leadership = rs.getInt("Command");
                o.intelligence = rs.getInt("Intelligence");
                o.politics = rs.getInt("Politics");
                o.glamour = rs.getInt("Glamour");
                o.popularity = rs.getInt("Reputation");
                o.gut = rs.getInt("Braveness");
                o.calm = rs.getInt("Calmness");
                o.loyalty = rs.getInt("Loyalty");
                o.bornLocation = rs.getInt("BornRegion");
                o.debutLocation = rs.getInt("AvailableLocation");
                o.blood = rs.getInt("Strain");
                fatherId.put(o.id, rs.getInt("Father"));
                motherId.put(o.id, rs.getInt("Mother"));
                spouseId.put(o.id, rs.getInt("Spouse"));
                brotherId.put(o.id, rs.getInt("Brother"));
                o.generation = rs.getInt("Generation");
                o.righteous = rs.getInt("PersonalLoyalty");
                o.ambition = rs.getInt("Ambition");
                o.officerTendency = rs.getInt("Qualification");
                o.hanAttitude = rs.getInt("ValuationOnGovernment");
                o.stretagicalAttitude = rs.getInt("StrategyTendency");
                o.skill = Utility.fromIntListToSet(rs.getString("Skills"));
                o.personalTitle = rs.getInt("PersonalTitle");
                o.battleTitle = rs.getInt("CombatTitle");
                o.stunt = Utility.fromIntListToSet(rs.getString("Stunts"));
                officers.put(o.id, o);
            }
            for (Officer o : officers.values()){
                o.father = officers.get(fatherId.get(o.id));
                o.mother = officers.get(motherId.get(o.id));
                o.spouse = officers.get(spouseId.get(o.id));
                o.brother = officers.get(brotherId.get(o.id));
            }
            rs.close();
            stmt.close();
            scenConn.close();
        }
        return officers.get(id);
    }
    
}
