package CSanScenGenerator;

import java.sql.*;
import java.util.*;

/**
 *
 * @author Peter
 */
public class Team {
    
    protected int id;
    protected String name;
    protected List<Building> buildings;
    
    protected Faction faction;
    
    public Team(int id){
        this.id = id;
        buildings = new ArrayList<Building>();
    }
    
    /**
     * Populate the team with officers
     * @param belongFaction Which faction this team belong to
     * @param cityCnt Number of cities the team has
     * @param connectedCity Whether to ensure the selected cities are connected
     * @param allBuildings Buildings to choose from
     * @param officers All the officers that belongs to the team
     * @param king The king of the faction
     * @param loyalty The loyalty of the team officers
     * @return Buildings that the team has selected
     */
    public List<Building> populateOfficers(Faction belongFaction, int cityCnt, boolean connectedCity, List<Building> allBuildings, List<Officer> officers, 
            Officer king, int loyaltyLo, int loyaltyHi, boolean strongHiLoyalty){
        List<Building> candidate = new ArrayList<Building>();
        for (Building i : allBuildings){
            if (i.type == Building.TYPE_CITY && i.team == null){
                candidate.add(i);
            }
        }
        if (cityCnt > candidate.size()){
            return new ArrayList<Building>();
        }
        List<Building> pick;
        if (!connectedCity){
            pick = Utility.subset(candidate, cityCnt);
        } else {
            pick = new ArrayList<Building>();
            pick.add(Utility.randomPick(candidate));
            for (int i = 1; i < cityCnt; ++i){
                Building extendFrom = Utility.randomPick(pick);
                List<Building> connectedCandidate = new ArrayList<Building>();
                if (extendFrom.connectedLandCity != null && extendFrom.connectedWaterCity != null){
                    for (int j : extendFrom.connectedLandCity){
                        Building b = Building.getBuildingFromId(allBuildings, j);
                        if (b.team == null && !pick.contains(b)){
                            connectedCandidate.add(b);
                        }
                    }
                    for (int j : extendFrom.connectedWaterCity){
                        Building b = Building.getBuildingFromId(allBuildings, j);
                        if (b.team == null && !pick.contains(b)){
                            connectedCandidate.add(b);
                        }
                    }
                } else {
                    for (Building b : allBuildings){
                        if (Point.distance((Point) (b.location.toArray()[0]), (Point) (extendFrom.location.toArray()[0])) < 50){
                            if (b.team == null && !pick.contains(b)){
                                connectedCandidate.add(b);
                            }
                        }
                    }
                }
                if (connectedCandidate.size() > 0){
                    pick.add(Utility.randomPick(connectedCandidate));
                }
            }
        }
        buildings.addAll(pick);
        name = Utility.randomPick(pick).name + (CSanScenGenerator.frontend.Frontend.isTc() ? "軍區" : "军区");
        //split all input officers into cities
        for (int i = 0; i < officers.size(); ++i){
            Utility.randomPick(buildings).populateOfficers(this, officers.get(i), king, loyaltyLo, loyaltyHi, strongHiLoyalty);
        }
        this.faction = belongFaction;
        return pick;
    }
    
    public void writeTeam(Connection conn) throws SQLException{
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement("insert into Sections (ID, Name, Architectures, AIDetail) values (?, ?, ?, 1)");
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, Building.toListStr(buildings));
            pstmt.executeUpdate();
        } finally {
            pstmt.close();
        }
    }
    
    public static String toListStr(List<Team> t){
        int[] r = new int[t.size()];
        for (int i = 0; i < t.size(); ++i){
            r[i] = t.get(i).id;
        }
        return Utility.join(r, " ");
    }
    
    public static String toListBuildingStr(List<Team> t){
        List<Building> b = new ArrayList<Building>();
        for (Team i : t){
            b.addAll(i.buildings);
        }
        return Building.toListStr(b);
    }
    
}
