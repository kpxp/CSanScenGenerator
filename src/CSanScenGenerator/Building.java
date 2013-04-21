package CSanScenGenerator;

import java.sql.*;
import java.util.*;
import java.io.*;

/**
 *
 * @author Peter
 */
public class Building {

    public static final int TYPE_CITY = 1;
    public static final int TYPE_GATE = 2;
    public static final int TYPE_PORT = 3;
    public static final int TYPE_BASE = 4;
    protected int id, type;
    protected Set<Point> location;
    protected Set<Officer> officers, unemployedOfficers;
    protected Set<Army> armies;
    protected String name;
    protected int cash, crop, population;
    protected Team team;
    protected boolean emperor;
    protected int agriculture, commerce, technology, domination, morale, endurance;
    
    protected Set<Facility> facilities;
    protected Set<Integer> specialties = new HashSet<Integer>();
    
    protected List<Integer> connectedLandCity;
    protected List<Integer> connectedWaterCity;

    private Building() {
        officers = new HashSet<Officer>();
        unemployedOfficers = new HashSet<Officer>();
        location = new HashSet<Point>();
        armies = new HashSet<Army>();
        facilities = new HashSet<Facility>();
    }
    
    private static int autoId = 1;
    private static List<String> nameList;
    public static Building createBuilding(int type) throws IOException{
        Building b = new Building();
        b.id = autoId;
        autoId++;
        
        if (nameList == null) {
            nameList = new ArrayList<String>();
            String s;
            BufferedReader f = new BufferedReader(new InputStreamReader(new FileInputStream("DATA/" + (CSanScenGenerator.frontend.Frontend.isTc() ? "" : "GBK/") + "cityName.txt"), "UTF8"));

            while (true) {
                s = f.readLine();
                if (s == null) {
                    break;
                }
                nameList.add(s);
            }
        }
        b.name = "";
        int nameLen = 2;
        for (int i = 0; i < nameLen; ++i) {
            b.name += Utility.randomPick(nameList);
        }
        if (type == Building.TYPE_GATE){
            b.name += "關";
        } else if (type == Building.TYPE_PORT){
            b.name += "港";
        }
        
        b.type = type;
        
        return b;
    }
    
    public int getSize(){
        return location.size();
    }

    public void setLocation(Collection<Point> p){
        location = new HashSet<Point>(p);
    }

    public static Building getBuildingFromId(List<Building> b, int id){
        for (Building i : b){
            if (i.id == id){
                return i;
            }
        }
        return null;
    }

    public static List<Building> readBuildings(Connection conn) throws SQLException {
        List<Building> r = new ArrayList<Building>();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select * from Architecture");
            while (rs.next()) {
                String landLink = rs.getString("AILandLinks");
                String waterLink = rs.getString("AIWaterLinks");
                String[] landLinkBase = landLink == null ? new String[0] : landLink.split("\\s");
                String[] waterLinkBase = waterLink == null ? new String[0] : waterLink.split("\\s");
                Building b = new Building();
                b.id = rs.getInt("ID");
                b.type = rs.getInt("Kind");
                b.name = rs.getString("Name");
                //get location
                String location = rs.getString("Area");
                String[] s = location.split("\\s");
                for (int i = 0; i < s.length; i += 2) {
                    Point p = new Point(Integer.parseInt(s[i]), Integer.parseInt(s[i + 1]));
                    b.location.add(p);
                }
                b.connectedLandCity = new ArrayList<Integer>();
                b.connectedWaterCity = new ArrayList<Integer>();
                for (int i = 0; i < landLinkBase.length; ++i){
                    try {
                        b.connectedLandCity.add(Integer.parseInt(landLinkBase[i]));
                    } catch (NumberFormatException ex){   
                    }
                }
                for (int i = 0; i < waterLinkBase.length; ++i){
                    try {
                        b.connectedWaterCity.add(Integer.parseInt(waterLinkBase[i]));
                    } catch (NumberFormatException ex){   
                    }
                }
                //add the builduing
                r.add(b);
            }
        } finally {
            rs.close();
            stmt.close();
        }
        return r;
    }

    /**
     * Populate the building with officers
     * @param team The team the building belongs to
     * @param officer Populate the team with this officer
     * @param king The king of the faction
     * @param loyalty Loyalty of added officer
     */
    public void populateOfficers(Team team, Officer officer, Officer king, int loyaltyLo, int loyaltyHi, boolean strongHiLoyalty) {
        officer.joinFaction(this, king, loyaltyLo, loyaltyHi, strongHiLoyalty);
        this.team = team;
    }
    
    public boolean isStrong(){
        return this.team != null && this.team.faction != null && this.team.faction.strong;
    }

    public void randomCash(int lo, int hi) {
        if (this.team != null) {
            cash = Utility.randBetween(lo, hi) * 100;
        } else {
            cash = 0;
        }
    }

    public void randomCrop(int lo, int hi) {
        if (this.team != null) {
            crop = Utility.randBetween(lo, hi) * 10000;
        } else {
            crop = 0;
        }
    }

    public void randomPopulation(int lo, int hi) {
        if (type == TYPE_CITY){
            population = Utility.randBetween(lo, hi) * getSize() * 1000;
        } else if (type == TYPE_PORT){
            population = Utility.randBetween(lo, hi) * getSize() * 500;
        } else {
            population = 0;
        }
    }

    public void randomArmy(int cntLo, int cntHi, int troopLo, int troopHi, int mlo, int mhi, int elo, int ehi) {
        if (this.team != null) {
            int cnt = Utility.randBetween(cntLo, cntHi);
            for (int i = 0; i < cnt; ++i) {
                Army a = new Army();
                a.randomSoldier(troopLo, troopHi);
                a.randomType(this.type == 3, this.team.getFaction().getTroopKinds());
                a.randomMorale(mlo, mhi);
                a.randomLeader(this.officers, elo, ehi);
                armies.add(a);
            }
        }
    }
    
    public void randomInternal(int alo, int ahi, int clo, int chi, int tlo, int thi, int dlo, int dhi, int mlo, int mhi, int elo, int ehi){
        if (type == TYPE_CITY || type == TYPE_PORT){
            agriculture = Utility.randBetween(alo, ahi);
            commerce = Utility.randBetween(clo, chi);
            technology = Utility.randBetween(tlo, thi);
            domination = Utility.randBetween(dlo, dhi);
            morale = Utility.randBetween(mlo, mhi);
            endurance = Utility.randBetween(elo, ehi);
        } else if (type == TYPE_GATE) {
            agriculture = commerce = morale = 0;
            technology = Utility.randBetween(tlo, thi);
            domination = Utility.randBetween(dlo, dhi);
            endurance = Utility.randBetween(elo, ehi);
        } else {
            agriculture = commerce = technology = morale = 0;
            domination = Utility.randBetween(dlo, dhi);
            endurance = Utility.randBetween(elo, ehi);
        }
    }
    
    private static Map<Integer, Double> facilityProb = null;
    private static void getFacilityProb(){
        facilityProb = new HashMap<Integer, Double>();
        BufferedReader file = null;
        try {
            file = new BufferedReader(new FileReader("DATA/facility.txt"));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return;
        }
        try {
            String s;
            while ((s = file.readLine()) != null) {
                String[] temps = s.split("\\s");
                int id = Integer.parseInt(temps[0]);
                double prob = Double.parseDouble(temps[1]);
                facilityProb.put(id, prob);
            }
        } catch (IOException ex) {
            //Do not touch anything if file reading fails.
            ex.printStackTrace();
            return;
        }
    }
    
    public void randomFacility(Connection commonData) throws SQLException{
        //TODO make this function aware of arch kind and faction unique
        if (this.type == TYPE_CITY){
            int spaceOccupied = 0;
            if (facilityProb == null){
                getFacilityProb();
            }
            Map<Integer, FacilityKind> fk = FacilityKind.getFacilities(commonData);
            for (Map.Entry<Integer, Double> i : facilityProb.entrySet()){
                if (Utility.probTestPercentage(i.getValue()) && spaceOccupied + fk.get(i.getKey()).getOccupySpace() <= 4 * this.getSize()){
                    Facility f = new Facility(fk.get(i.getKey()));
                    this.facilities.add(f);
                }
            }
        }
    }
    
    public void randomSpecialties(Connection cdData, double prob, int threshold) throws IOException, SQLException{
        double realProb = prob;
        int i = 0;
        if (this.type == TYPE_CITY){
            while (Utility.probTestPercentage(realProb)){
                Map<Integer, Integer> probMap = InfluenceRate.getBuildingRates(cdData);
                specialties.add(Utility.randomCategorize(probMap));
                i++;
                if (i >= threshold){
                    realProb /= 2;
                }
            }
        }
    }
    
    public static void prepareBuildingForCreatedMap(Connection conn) throws SQLException{
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement("delete from Region");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("insert into Region (ID, Name, States, RegionCore) values (?, ?, ?, ?)");
            pstmt.setInt(1, 1);
            pstmt.setString(2, "");
            pstmt.setString(3, "1");
            pstmt.setInt(4, -1);
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("delete from State");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("insert into State (ID, Name, ContactStates, StateAdmin) values (?, ?, ?, ?)");
            pstmt.setInt(1, 1);
            pstmt.setString(2, "");
            pstmt.setString(3, "");
            pstmt.setInt(4, -1);
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("delete from Architecture");
            pstmt.executeUpdate();
        } finally {
            if (pstmt != null){
                pstmt.close();
            }
        }    
    }

    public void writeBuilding(Connection conn, boolean createdMap) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            if (!createdMap){
                pstmt = conn.prepareStatement("update Architecture set Persons = ?, NoFactionPersons = ?, Fund = ?, Food = ?, Population = ?, Militaries = ?, "
                        + " Agriculture = ?, Commerce = ?, Technology = ?, Domination = ?, Morale = ?, Endurance = ?, Emperor = ?, Facilities = ?, Characteristics =?,"
                        + " Area = ?, AILandLinks = ?, AIWaterLinks = ?, Name = ?, Kind = ? where ID = ?");
            } else {
                pstmt = conn.prepareStatement("insert into Architecture (Persons, NoFactionPersons, Fund, Food, Population, Militaries, Agriculture,"
                        + "Commerce, Technology, Domination, Morale, Endurance, Emperor, Facilities, Characteristics, Area, AILandLinks, AIWaterLinks,"
                        + "Name, Kind, ID, IsStrategicCenter, StateID, CaptionID) values"
                        + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, false, 1, 9999)");
            }
            pstmt.setString(1, Officer.toListStr(officers));
            pstmt.setString(2, Officer.toListStr(unemployedOfficers));
            pstmt.setInt(3, cash);
            pstmt.setInt(4, crop);
            pstmt.setInt(5, population);
            pstmt.setString(6, Army.toListStr(armies));
            pstmt.setInt(7, agriculture);
            pstmt.setInt(8, commerce);
            pstmt.setInt(9, technology);
            pstmt.setInt(10, domination);
            pstmt.setInt(11, morale);
            pstmt.setInt(12, endurance);
            pstmt.setBoolean(13, emperor);
            pstmt.setString(14, Facility.toListStr(facilities));
            pstmt.setString(15, Utility.join(specialties.toArray(), " "));
            pstmt.setString(16, Utility.joinPoints(location, " "));
            pstmt.setString(17, connectedLandCity == null ? "" : Utility.join(connectedLandCity.toArray(), " "));
            pstmt.setString(18, connectedWaterCity == null ? "" : Utility.join(connectedWaterCity.toArray(), " "));
            pstmt.setString(19, name);
            pstmt.setInt(20, type);
            pstmt.setInt(21, id);
            pstmt.executeUpdate();
            for (Army a : armies) {
                a.writeArmy(conn);
            }
            for (Facility f : facilities){
                f.writeFacility(conn);
            }
        } finally {
            if (pstmt != null){
                pstmt.close();
            }
        }
    }

    public void setEmperorToHere(){
        emperor = true;
    }

    public static String toListStr(List<Building> t) {
        int[] r = new int[t.size()];
        for (int i = 0; i < t.size(); ++i) {
            r[i] = t.get(i).id;
        }
        return Utility.join(r, " ");
    }
}
