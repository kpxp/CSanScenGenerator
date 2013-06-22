package CSanScenGenerator;

import java.util.*;
import java.sql.*;
import java.io.*;

/**
 *
 * @author Peter
 */
public class Faction {

    protected int id, color, capital, reputation;
    protected Officer king;
    protected String name;
    protected List<Building> buildings;
    protected List<Team> teams;
    protected Set<Integer> troopKinds;
    protected Set<Integer> techniques;
    private Connection commonData;
    
    protected boolean strong;

    public Faction(int id, boolean isStrong, Connection commonDataConn) {
        this.id = id;
        buildings = new ArrayList<Building>();
        teams = new ArrayList<Team>();
        troopKinds = new HashSet<Integer>();
        techniques = new HashSet<Integer>();
        strong = isStrong;
        commonData = commonDataConn;
    }
    
    private static Set<Integer> usableTroopKinds;

    public void randomReputation(int lo, int hi) {
        reputation = Utility.randBetween(lo, hi) * 10;
    }

    private boolean tryToGetTechnique(Map<Integer, Technique> allTech, Integer toGetId, double prob) {
        if (techniques.contains(toGetId)) {
            return true;
        }
        Integer requiredTechId = allTech.get(toGetId).getPreId();
        if (Utility.probTestPercentage(prob * 100)) {
            if (requiredTechId == -1) {
                techniques.add(toGetId);
                return true;
            } else {
                if (tryToGetTechnique(allTech, requiredTechId, prob)) {
                    techniques.add(toGetId);
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    public void randomTechniques(double prob) throws SQLException {
        techniques.clear();
        Map<Integer, Technique> allTech = Technique.getTechniques(commonData);
        for (Map.Entry<Integer, Technique> i : allTech.entrySet()) {
            Integer requiredTechId = i.getValue().getPreId();
            if (Utility.probTestPercentage(prob * 100)) {
                if (requiredTechId == -1) {
                    techniques.add(i.getKey());

                } else {
                    if (tryToGetTechnique(allTech, requiredTechId, prob)) {
                        techniques.add(i.getKey());
                    }
                }
            }
        }
    }

    public void defaultTroopKinds(boolean mustHaveBasic) {
        if (mustHaveBasic) {
            troopKinds.addAll(Arrays.asList(0, 1, 2, 25, 29, 30));
        } else {
            troopKinds.addAll(Arrays.asList(25, 29, 30));
        }
    }
    
    public void randomTroopKinds(double prob, boolean mustHaveBasic) throws IOException{
        if (usableTroopKinds == null){
            usableTroopKinds = new HashSet<Integer>();
            String s;
            BufferedReader f = new BufferedReader(new FileReader("DATA/factionSpecialMilitary.txt"));
            
            while (true) {
                s = f.readLine();
                if (s == null) break;
                usableTroopKinds.add(Integer.parseInt(s));
            }
        }
        troopKinds.clear();
        defaultTroopKinds(mustHaveBasic);
        while (Utility.probTestPercentage(prob)){
            troopKinds.add(Utility.randomPick(usableTroopKinds));
        }
        if (troopKinds.size() < 3){
            troopKinds.add(Utility.randomPick(usableTroopKinds));
        }
    }
    
    public Set<Integer> getTroopKinds() {
        return this.troopKinds;
    }

    public void setKing(Officer o) {
        king = o;
        name = o.getName();
        o.setKing(true);
    }

    public void setColor(int c) {
        color = c;
    }

    /**
     * Populate the faction with officers and buildings
     * @param allBuildings Buildings to choose from
     * @param cityCnt Number of cities the team has
     * @param connectedCity Whether to ensure the selected cities are connected
     * @param officers All the officers that belongs to the team
     * @param loyaltyLo
     * @param loyaltyHi
     * @return Whether this faction has been successfully assigned any city
     */
    public boolean populateOfficers(List<Building> allBuildings, int cityCnt, boolean connectedCity, List<Officer> officers, 
            int loyaltyLo, int loyaltyHi, boolean strongHiLoyalty) {
        Team t = new Team(id);
        buildings = t.populateOfficers(this, cityCnt, connectedCity, allBuildings, officers, king, loyaltyLo, loyaltyHi, strongHiLoyalty);
        if (buildings.size() > 0) {
            teams.add(t);
            capital = Utility.randomPick(t.buildings).id;
        }
        return buildings.size() > 0;
    }

    public void writeFaction(Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            //write faction data
            pstmt = conn.prepareStatement("insert into Faction (ID, LeaderID, ColorIndex, FName, CapitalID, Reputation, Sections, Architectures, BaseMilitaryKinds, AvailableTechniques)"
                    + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            pstmt.setInt(1, id);
            pstmt.setInt(2, king.id);
            pstmt.setInt(3, color);
            pstmt.setString(4, name);
            pstmt.setInt(5, capital);
            pstmt.setInt(6, reputation);
            pstmt.setString(7, Team.toListStr(teams));
            pstmt.setString(8, Team.toListBuildingStr(teams));
            pstmt.setString(9, Utility.join(troopKinds.toArray(), " "));
            pstmt.setString(10, Utility.join(techniques.toArray(), " "));
            pstmt.executeUpdate();
            for (Team t : teams) {
                t.writeTeam(conn);
            }
        } finally {
            pstmt.close();
        }
    }

    public static void writeFactionDiplomacy(Connection conn, List<Faction> f, int lo, int hi, boolean gaussian) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            int[][] diplomacy = new int[f.size()][f.size()];
            for (int i = 0; i < diplomacy.length; ++i) {
                for (int j = 0; j < diplomacy.length; ++j) {
                    diplomacy[i][j] = (int) (gaussian ? Utility.randGaussian((lo + hi) / 2, (hi - lo) / 2) : Utility.randBetween(lo, hi));
                    diplomacy[j][i] = diplomacy[i][j];
                }
            }
            //write faction data
            pstmt = conn.prepareStatement("insert into DiplomaticRelation (Faction1ID, Faction2ID, Relation) values (?, ?, ?)");
            for (int i = 0; i < diplomacy.length; ++i) {
                for (int j = 0; j < diplomacy.length; ++j) {
                    if (i == j) {
                        continue; //I mean identity comparsion!
                    }
                    pstmt.setInt(1, i);
                    pstmt.setInt(2, j);
                    pstmt.setInt(3, diplomacy[i][j]);
                    pstmt.executeUpdate();
                }
            }
        } finally {
            pstmt.close();
        }
    }
}
