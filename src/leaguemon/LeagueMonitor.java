package leaguemon;

import leaguemon.graphics.RosterImage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Inconvenius
 */
public class LeagueMonitor {

    public static final String IMG_EXTENSION = ".png";
    public static final String IMG_DIR = "img/";
    public static final String LEAGUES_FILE = "leagues.txt";
    public static final String RACE_IMG_DIR = "races/";
    public static final String LEAGUE_IMG_DIR = "leagues/";

    private List<Player> players = new ArrayList<Player>();

    private String imagesDir;
    private String workingDir;

    public LeagueMonitor(String imagesDir) throws IOException {
        this.imagesDir = imagesDir;
        this.workingDir = System.getProperty("user.dir");

        System.out.print("Reading player info from file...");
        players = readPlayerInfo();
        System.out.println(" Done");
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
                System.out.println(" Done");

                if(!currentLeague.equals(player.getLeague())) {
                    player.setLeague(currentLeague);
                    updateImage(player);
                    changed = true;
                }
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

    private void updateImage(Player player) {
        Path source = Paths.get(IMG_DIR + LEAGUE_IMG_DIR + player.getLeague().toString().toLowerCase() + IMG_EXTENSION);
        Path target = Paths.get(imagesDir + player.getName() + IMG_EXTENSION);

        System.out.println("Updating form: " + source);
        System.out.println("Updating to: " + target);

        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateRoster() {
        try {
            System.out.println("Writing roster to:" + imagesDir + "carrier.png");
            new RosterImage(players).writeImage(imagesDir + "carrier.png");
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
        if(args.length != 2) {
            System.out.println("Usage: monitor <img_dir> <interval>");
            return;
        }

        try {
            new leaguemon.LeagueMonitor(args[0]).start(Integer.parseInt(args[1]));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
