package CSanScenGenerator.frontend;

import CSanScenGenerator.InfluenceRate;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 *
 * @author Peter
 */
public class TitleLevelCalculator {
    
    public static void main(String[] args) throws Exception {
        String gamePath;
        
        BufferedReader br = new BufferedReader(new FileReader("CSSG.ini"));
        gamePath = br.readLine();
        br.close();
        
        Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        java.util.Properties prop = new java.util.Properties();
        prop.put("charSet", "Big5");
        Connection commonDataConn = DriverManager.getConnection("jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=" + gamePath + "/GameData/Common/CommonData.mdb", prop);
        
        Statement stmt = commonDataConn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Title");
        
        while (rs.next()){
            String s = rs.getString(8);
            String[] strids = s.split(" ");
            List<Integer> ids = new ArrayList<Integer>();
            for (String i : strids) {
                ids.add(Integer.parseInt(i));
            }
            int value = 0;
            double multiple = 1.5;
            for (int i : ids) {
                if (i == 281){
                    multiple = 1;
                } else {
                    value += InfluenceRate.getActualValue(commonDataConn, i) * multiple;
                }
            }
            
            int level = (int) (value < 30 ? 0 : Math.sqrt(value - 30) / 5) + 1;
            
            PreparedStatement pstmt = commonDataConn.prepareStatement("UPDATE Title SET Level = ? WHERE ID = ?");
            pstmt.setInt(1, level);
            pstmt.setInt(2, Integer.parseInt(rs.getString(1)));
            pstmt.executeUpdate();
            pstmt.close();
        }
        
        rs.close();
        stmt.close();
    }
}
