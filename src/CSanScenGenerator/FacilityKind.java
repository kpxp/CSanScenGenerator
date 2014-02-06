package CSanScenGenerator;

import java.util.*;

/**
 *
 * @author Peter
 */
public class FacilityKind {
    
    private static Map<Integer, FacilityKind> facilities;
    private int id;
    private int occupySpace, buildingUnique, factionUnique;
    private boolean populationRelated;
    private int maxEndurance;
    
    private FacilityKind(int id, int occupySpace, int buildingUnique, int factionUnique, boolean populationRelated, int maxEndurance){
        this.id = id;
        this.occupySpace = occupySpace;
        this.buildingUnique = buildingUnique;
        this.factionUnique = factionUnique;
        this.populationRelated = populationRelated;
        this.maxEndurance = maxEndurance;
    }
    
    public static Map<Integer, FacilityKind> getFacilities(java.sql.Connection commonData) throws java.sql.SQLException {
        if (facilities == null) {
            java.sql.Statement stmt = commonData.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery("select ID, PositionOccupied, ArchitectureLimit, FactionLimit, PopulationRelated, Endurance from FacilityKind");
            facilities = new HashMap<Integer, FacilityKind>();
            while (rs.next()) {
                int id = rs.getInt("ID");
                int occupies = rs.getInt("PositionOccupied");
                int uia = rs.getInt("ArchitectureLimit");
                int uif = rs.getInt("FactionLimit");
                boolean pr = rs.getBoolean("PopulationRelated");
                int me = rs.getInt("Endurance");
                facilities.put(id, new FacilityKind(id, occupies, uia, uif, pr, me));
            }
            rs.close();
            stmt.close();
        }
        return facilities;
    }
    
    public int getId(){
        return id;
    }

    /**
     * @return the occupySpace
     */
    public int getOccupySpace() {
        return occupySpace;
    }

    /**
     * @return the buildingUnique
     */
    public int isBuildingUnique() {
        return buildingUnique;
    }

    /**
     * @return the factionUnique
     */
    public int isFactionUnique() {
        return factionUnique;
    }

    /**
     * @return the populationRelated
     */
    public boolean isPopulationRelated() {
        return populationRelated;
    }

    /**
     * @return the maxEndurance
     */
    public int getMaxEndurance() {
        return maxEndurance;
    }
   
    
}
