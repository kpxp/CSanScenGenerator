package CSanScenGenerator;

import java.io.*;
import java.util.*;

/**
 *
 * @author Peter
 */
public class Database {

    private List<String> surname = new ArrayList<String>();
    private List<String> maleGivenName = new ArrayList<String>();
    private List<String> femaleGivenName = new ArrayList<String>();
    private List<Integer> maleFace = new ArrayList<Integer>();
    private List<Integer> femaleFace = new ArrayList<Integer>();
    private List<Integer> maleFaceA = new ArrayList<Integer>();
    private List<Integer> maleFaceM = new ArrayList<Integer>();
    private List<Integer> maleFaceU = new ArrayList<Integer>();
    private List<Integer> femaleFaceA = new ArrayList<Integer>();
    private List<Integer> femaleFaceM = new ArrayList<Integer>();

    public Database(boolean shuffleLists) throws IOException {
        //Read in data file
        String s;
        BufferedReader surnameFile = new BufferedReader(new InputStreamReader(new FileInputStream("DATA/" + (CSanScenGenerator.frontend.Frontend.isTc() ? "" : "GBK/") + "surname.txt"), "UTF8"));

        while (true) {
            s = surnameFile.readLine();
            if (s == null) break;
            surname.add(s);
        }

        BufferedReader maleGivenNameFile = new BufferedReader(new InputStreamReader(new FileInputStream("DATA/" + (CSanScenGenerator.frontend.Frontend.isTc() ? "" : "GBK/") + "malegivenname.txt"), "UTF8"));

        while (true) {
            s = maleGivenNameFile.readLine();
            if (s == null) break;
            maleGivenName.add(s);
        }
        
        BufferedReader femaleGivenNameFile = new BufferedReader(new InputStreamReader(new FileInputStream("DATA/" + (CSanScenGenerator.frontend.Frontend.isTc() ? "" : "GBK/") + "femalegivenname.txt"), "UTF8"));

        while (true) {
            s = femaleGivenNameFile.readLine();
            if (s == null) break;
            femaleGivenName.add(s);
        }

        BufferedReader maleFaceAFile = new BufferedReader(new FileReader("DATA/maleFaceA.txt"));

        while (true) {
            s = maleFaceAFile.readLine();
            if (s == null) {
                break;
            }
            maleFace.add(Integer.parseInt(s));
            maleFaceA.add(Integer.parseInt(s));
        }

        BufferedReader maleFaceUFile = new BufferedReader(new FileReader("DATA/maleFaceU.txt"));

        while (true) {
            s = maleFaceUFile.readLine();
            if (s == null) {
                break;
            }
            maleFace.add(Integer.parseInt(s));
            maleFaceU.add(Integer.parseInt(s));
        }

        BufferedReader maleFaceMFile = new BufferedReader(new FileReader("DATA/maleFaceM.txt"));

        while (true) {
            s = maleFaceMFile.readLine();
            if (s == null) {
                break;
            }
            maleFace.add(Integer.parseInt(s));
            maleFaceM.add(Integer.parseInt(s));
        }

        BufferedReader femaleFaceAFile = new BufferedReader(new FileReader("DATA/femaleFaceA.txt"));
        
        while (true) {
            s = femaleFaceAFile.readLine();
            if (s == null) {
                break;
            }
            femaleFace.add(Integer.parseInt(s));
            femaleFaceA.add(Integer.parseInt(s));
        }

        BufferedReader femaleFaceMFile = new BufferedReader(new FileReader("DATA/femaleFaceM.txt"));

        while (true) {
            s = femaleFaceMFile.readLine();
            if (s == null) {
                break;
            }
            femaleFace.add(Integer.parseInt(s));
            femaleFaceM.add(Integer.parseInt(s));
        }

        if (shuffleLists){
            Collections.shuffle(surname);
            Collections.shuffle(maleGivenName);
            Collections.shuffle(femaleGivenName);
            Collections.shuffle(maleFace);
            Collections.shuffle(femaleFace);
            Collections.shuffle(maleFaceA);
            Collections.shuffle(maleFaceM);
            Collections.shuffle(maleFaceU);
            Collections.shuffle(femaleFaceA);
            Collections.shuffle(femaleFaceM);
        }
    }

    /**
     * @return the surname
     */
    public List<String> getSurname() {
        return surname;
    }

    /**
     * @return the malegivenname
     */
    public List<String> getMalegivenname() {
        return maleGivenName;
    }

    /**
     * @return the femalegivenname
     */
    public List<String> getFemalegivenname() {
        return femaleGivenName;
    }

    /**
     * @return the maleface
     */
    public List<Integer> getMaleface() {
        return maleFace;
    }

    /**
     * @return the femaleface
     */
    public List<Integer> getFemaleface() {
        return femaleFace;
    }

    /**
     * @return the maleFaceA
     */
    public List<Integer> getMaleFaceA() {
        return maleFaceA;
    }

    /**
     * @return the maleFaceM
     */
    public List<Integer> getMaleFaceM() {
        return maleFaceM;
    }

    /**
     * @return the maleFaceU
     */
    public List<Integer> getMaleFaceU() {
        return maleFaceU;
    }

    /**
     * @return the femaleFaceA
     */
    public List<Integer> getFemaleFaceA() {
        return femaleFaceA;
    }

    /**
     * @return the femaleFaceM
     */
    public List<Integer> getFemaleFaceM() {
        return femaleFaceM;
    }

}
