package leaguemon;

import leaguemon.graphics.RosterImage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Inconvenius
 */
public class LeagueMonitor {

    public static final String IMG_EXTENSION = ".png";
    public static final String LEAGUES_FILE = "leagues.txt";

    public static String badgeImagesDir;
    public static String raceImagesDir;

    private List<Player> players = new ArrayList<Player>();

    private String rosterImageDir;
    private String workingDir;

    public LeagueMonitor() throws IOException {
        loadConfig();
        this.workingDir = System.getProperty("user.dir");

        System.out.print("Reading player info from file...");
        players = readPlayerInfo();
        System.out.println(" Done");
    }

    private void loadConfig() {
        Properties p = new Properties();

        try {
            p.load(new FileInputStream("config.properties"));

            rosterImageDir = p.getProperty("rosterImageDir");
            badgeImagesDir = p.getProperty("leagueBadgeImagesDir");
            raceImagesDir = p.getProperty("raceImagesDir");

        } catch (IOException e) {
            System.out.println("Error loading configurations from config.properties file.");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static ArrayList<Player> readPlayerInfo() throws IOException {
        ArrayList<Player> players = new ArrayList<Player>();

        BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/" + LEAGUES_FILE));
        String line;

        while ((line = br.readLine()) != null) {
            players.add(new Player(line));
        }

        return players;
    }

    public void start(int interval) throws IOException, InterruptedException {
        while (true) {
            boolean changed = false;

            for (Player player : players) {
                System.out.print("Checking " + player.getName() + " ...");
                Player.League currentLeague = getCurrentLeague(player.getProfile());

                if(!currentLeague.equals(player.getLeague())) {
                    System.out.print("Updating ...");
                    player.setLeague(currentLeague);
                    changed = true;
                }

                System.out.println(" Done");
            }

            if(changed) {
                sortRoster();
                updateRoster();
                writeFile();
            }

            if(interval <= 0)
                break;

            Thread.sleep(1000 * 60 * interval);
        }
    }

    private Player.League getCurrentLeague(String address) throws IOException {
        Document doc = Jsoup.connect(address).get();

        String portraitFrameClass = doc.getElementById("portrait-frame").className();

        if(portraitFrameClass.isEmpty()) {
            return Player.League.NONE;
        }

        int lastDash = portraitFrameClass.lastIndexOf('-');
        String league = portraitFrameClass.substring(lastDash + 1);

        return Player.League.valueOf(league.toUpperCase());
    }

    /**
     * Updates the leagues file with the most recent info.
     */
    private void writeFile() {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(workingDir + "/" + LEAGUES_FILE));

            for (Player player : players) {
                out.write(player.getProfile() + " " + player.getLeague() + " "
                        + player.getRace() + " " + player.getPosition() + "\n");
            }

            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateRoster() {
        try {
            System.out.println("Writing roster to:" + rosterImageDir + "carrier.png");
            new RosterImage(players).writeImage(rosterImageDir + "carrier.png");
            System.out.println(" Done");
        } catch (IOException e) {
            System.out.println("Error writing roster image.");
            e.printStackTrace();
        }
    }

    private void sortRoster() {
        Collections.sort(players);
    }


    // main ///////////////////////////////////////////////////////

    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Usage: monitor <interval>");
            return;
        }

        try {
            new leaguemon.LeagueMonitor().start(Integer.parseInt(args[0]));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
