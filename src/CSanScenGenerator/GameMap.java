package CSanScenGenerator;

import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 *
 * @author Peter
 */
public class GameMap {

    public enum Terrain {

        FLATLAND, GRASSLAND, FOREST, MARSHLAND, HILL, WATER, MOUNTAIN, WASTELAND, DESERT, SNOWLAND
    };
    private Terrain[][] mapData;
    private int dimensionX;
    private int dimensionY;

    private GameMap() {
    }
    
    public static class MapSetting{
        public Terrain terrain;
        public int pieceLo, pieceHi, sizeLo, sizeHi, thinnessLo, thinnessHi;
    }
    
    public static Terrain strToTerrain(String s){
        if (s.equals("平地")){
            return Terrain.FLATLAND;
        } else if (s.equals("草原")){
            return Terrain.GRASSLAND;
        } else if (s.equals("森林")){
            return Terrain.FOREST;
        } else if (s.equals("山地")){
            return Terrain.HILL;
        } else if (s.equals("荒地")){
            return Terrain.WASTELAND;
        } else if (s.equals("濕地")){
            return Terrain.MARSHLAND;
        } else if (s.equals("水域")){
            return Terrain.WATER;
        } else if (s.equals("峻嶺")){
            return Terrain.MOUNTAIN;
        } else if (s.equals("沙漠")){
            return Terrain.DESERT;
        } else if (s.equals("雪地")){
            return Terrain.SNOWLAND;
        } else {
            return Terrain.FLATLAND;
        }
    }
    
    private static int terrainIteration = 0;
    public static GameMap createMap(int sizeX, int sizeY, Terrain start, List<MapSetting> setting) {
        GameMap m = new GameMap();
        m.dimensionX = sizeX;
        m.dimensionY = sizeY;
        m.mapData = new Terrain[m.dimensionX][m.dimensionY];

        //fill the map with flatland
        for (int i = 0; i < m.mapData.length; ++i) {
            for (int j = 0; j < m.mapData[i].length; ++j) {
                m.mapData[i][j] = start;
            }
        }
        
        for (MapSetting i : setting){
            m.placeMass(i.terrain, start, Utility.randBetween(i.pieceLo, i.pieceHi),
                    Utility.randBetween(i.sizeLo, i.sizeHi),
                    i.thinnessLo, i.thinnessHi);
            terrainIteration++;
        }

        return m;
    }

    private void placeMass(Terrain terrain, Terrain baseTerrain, int massCnt, int massSize, double directionFactorLo, double directionFactionHi) {
        for (int i = 0; i < massCnt; ++i) {
            int leftWeight = 100, rightWeight = 100, upWeight = 100, downWeight = 100;
            double directionFactor = Utility.randBetween(directionFactorLo, directionFactionHi);
            if (directionFactor > 0) {
                int angle = Utility.randBetween(0, 360);
                leftWeight = (int) ((Math.sin(angle - Math.PI / 4) + 1) * directionFactor + 100);
                rightWeight = (int) ((Math.sin(angle + Math.PI / 4) + 1) * directionFactor + 100);
                upWeight = (int) ((Math.sin(angle) + 1) * directionFactor + 100);
                downWeight = (int) ((Math.sin(angle + Math.PI / 2) + 1) * directionFactor + 100);
            }

            Queue<Point> points = new LinkedList<Point>();
            Point newPoint = new Point(Utility.randBetween(0, dimensionX - 1), Utility.randBetween(0, dimensionY - 1));
            points.add(newPoint);
            mapData[newPoint.x][newPoint.y] = terrain;
            for (int j = 0; j < massSize; ++j){
                Point reference = points.remove();
                switch (Utility.randomCategorize_i(rightWeight, leftWeight, downWeight, upWeight)) {
                    case 0:
                        newPoint = new Point(reference.x + 1, reference.y);
                        break;
                    case 1:
                        newPoint = new Point(reference.x - 1, reference.y);
                        break;
                    case 2:
                        newPoint = new Point(reference.x, reference.y + 1);
                        break;
                    case 3:
                        newPoint = new Point(reference.x, reference.y - 1);
                        break;
                }
                if (newPoint.x >= 0 && newPoint.x < dimensionX && newPoint.y >= 0 && newPoint.y < dimensionY) {
                    if (mapData[newPoint.x][newPoint.y] != terrain && (Utility.probTestPercentage(terrainIteration * 12.5) || mapData[newPoint.x][newPoint.y] == baseTerrain 
                                || terrain == Terrain.WATER || terrain == Terrain.MOUNTAIN))
                        mapData[newPoint.x][newPoint.y] = terrain;
                }
                points.add(newPoint);
            }
        }
    }

    public List<Building> populateWithNewBuildings(int cityCntLo, int cityCntHi, int harbourCntLo, int harbourCntHi, int sizeLo, int sizeHi, boolean sizeExp, int borderNoCity) throws IOException {
        int buildingCnt = Utility.randBetween(cityCntLo, cityCntHi);
        int harbourCnt = Utility.randBetween(harbourCntLo, harbourCntHi);

        List<Building> result = new ArrayList<Building>(buildingCnt);

        Set<Point> occupiedPoints = new HashSet<Point>();
        for (int i = 0; i < buildingCnt; ++i) {
            Building b = Building.createBuilding(Building.TYPE_CITY);

            int size;
            if (sizeLo >= sizeHi){
                size = sizeLo;
            } else if (!sizeExp){
                size = (int) Utility.randGaussian((sizeLo + sizeHi) / 2, (sizeHi - sizeLo) / 2);
            } else {
                do {
                    size = (int) Utility.randGaussian(sizeLo, (sizeHi - sizeLo));
                } while (size < sizeLo);
            }
            if (size <= 0) size = 1;

            Set<Point> loc = new HashSet<Point>();
            Point candidate;
            while (true) {
                candidate = new Point(Utility.randBetween(borderNoCity, dimensionX - borderNoCity - 1), Utility.randBetween(borderNoCity, dimensionY - borderNoCity - 1));
                if (!occupiedPoints.contains(candidate)) {
                    break;
                }
            }
            occupiedPoints.add(candidate);
            loc.add(candidate);
            if (mapData[candidate.y][candidate.x] == Terrain.MOUNTAIN){
                mapData[candidate.y][candidate.x] = Terrain.HILL;
            }

            for (int j = 1; j < size; ++j) {
                while (true) {
                    Point start = Utility.randomPick(loc);
                    switch (Utility.randBetween(1, 4)) {
                        case 1:
                            candidate = new Point(start.x + 1, start.y);
                            break;
                        case 2:
                            candidate = new Point(start.x - 1, start.y);
                            break;
                        case 3:
                            candidate = new Point(start.x, start.y + 1);
                            break;
                        case 4:
                            candidate = new Point(start.x, start.y - 1);
                            break;
                    }
                    if (!occupiedPoints.contains(candidate)) {
                        break;
                    }
                }
                if (candidate.x >= borderNoCity && candidate.x < dimensionX - borderNoCity && candidate.y >= borderNoCity && candidate.y < dimensionY -  borderNoCity) {
                    occupiedPoints.add(candidate);
                    loc.add(candidate);
                    if (mapData[candidate.y][candidate.x] == Terrain.MOUNTAIN){
                        mapData[candidate.y][candidate.x] = Terrain.HILL;
                    }
                } else {
                    j--;
                }
            }

            b.setLocation(loc);

            result.add(b);
        }
        
        Set<Point> besideWaterLoc = new HashSet<Point>();
        for (int i = 0; i < mapData.length; ++i){
            for (int j = 0; j < mapData[i].length; ++j){
                if (occupiedPoints.contains(new Point(i, j))) continue;
                if (i < borderNoCity || j < borderNoCity || i >= dimensionX - borderNoCity || j >= dimensionY - borderNoCity) continue;
                if (mapData[i][j] == Terrain.WATER){
                    if (i > 0 && mapData[i-1][j] != Terrain.WATER){
                        besideWaterLoc.add(new Point(j, i));
                    }
                    if (i < mapData.length-1 && mapData[i+1][j] != Terrain.WATER){
                        besideWaterLoc.add(new Point(j, i));
                    }
                    if (j > 0 && mapData[i][j-1] != Terrain.WATER){
                        besideWaterLoc.add(new Point(j, i));
                    }
                    if (j < mapData[i].length-1 && mapData[i][j+1] != Terrain.WATER){
                        besideWaterLoc.add(new Point(j, i));
                    }
                }
            }
        }
        if (besideWaterLoc.size() > 0){
            for (int i = 0; i < harbourCnt; ++i){
                Building b = Building.createBuilding(Building.TYPE_PORT);

                Point loc = Utility.randomPick(besideWaterLoc);
                b.setLocation(Collections.singleton(loc));
                besideWaterLoc.remove(loc);

                result.add(b);
            }
        }

        return result;
    }

    public void writeMap(Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement("Update Map set DimensionX = ?, DimensionY = ?, MapData = ?, FileName = '', useSimpleArchImages = yes");
            pstmt.setInt(1, dimensionX);
            pstmt.setInt(2, dimensionY);
            StringBuilder mapDataBuilder = new StringBuilder();
            for (int i = 0; i < mapData.length; ++i) {
                for (int j = 0; j < mapData[i].length; ++j) {
                    mapDataBuilder.append(mapData[i][j].ordinal() + 1).append(" ");
                }
            }
            pstmt.setString(3, mapDataBuilder.toString());
            pstmt.executeUpdate();
        } finally {
            if (pstmt != null) {
                pstmt.close();
            }
        }
    }
}
