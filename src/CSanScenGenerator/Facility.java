package CSanScenGenerator;

import java.sql.*;
import java.util.*;

/**
 *
 * @author Peter
 */
public class Facility {
    
    private int id;
    private FacilityKind facilityKind;
    private int endurance;
    
    private static int autoId = 0;
    
    public Facility(FacilityKind fk, int e){
        this.facilityKind = fk;
        this.endurance = e;
        this.id = autoId;
        autoId++;
    }
    
    public Facility(FacilityKind fk){
        this(fk, fk.getMaxEndurance());
    }
    
    public int getId(){
        return id;
    }
    
    public void writeFacility(Connection conn) throws SQLException{
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement("insert into Facility (ID, KindID, Endurance) values (?, ?, ?)");
            pstmt.setInt(1, this.id);
            pstmt.setInt(2, this.facilityKind.getId());
            pstmt.setInt(3, this.endurance);
            pstmt.executeUpdate();
        } finally {
            pstmt.close();
        }
    }
    
    public static String toListStr(Collection<Facility> t) {
        int[] r = new int[t.size()];
        Iterator<Facility> it = t.iterator();
        int i = 0;
        while (it.hasNext()) {
            r[i++] = it.next().id;
        }
        return Utility.join(r, " ");
    }
    
}
