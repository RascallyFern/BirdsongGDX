package io.github.birdsong.scraper;

import java.io.*;
import java.net.URL;

public class Scraper {

    private String key, endpoint, outputName, data, url;
    private String genus, species, subspecies, sampleRate;
    private String[] downloadUrls, extensions;
    private int files, limit, prevCount;

    public Scraper() {
        key = System.getenv("XENOCANTO");
        endpoint = "https://xeno-canto.org/api/3/recordings?query=";
        files = 0;
        limit = 30;
        outputName = "";
    }

    public void setQueries(String genus, String species, String subspecies, String sampleRate) {
        url = endpoint;

        this.genus = genus;
        this.species = species;
        this.subspecies = subspecies;
        this.sampleRate = sampleRate;

        if (!genus.isEmpty()) {
            url += "gen:" + genus + "+";
        }
        if (!species.isEmpty()) {
            url += "sp:" + species + "+";
        }
        if (!subspecies.isEmpty()) {
            url += "ssp:" + subspecies + "+";
        }
        if (!sampleRate.isEmpty()) {
            url += "smp:" + sampleRate + "+";
        }

        url = url.substring(0, url.length() - 1) + "&per_page=1000&key=" + key;
    }

    public void setSubspecies(String ssp) {
        if (!subspecies.isEmpty()) {
            url = url.replace(subspecies, ssp);
        } else {
            url = url.replace("&per_page=1000&key=" + key, "+" + ssp + "&per_page=1000&key=" + key);
        }
    }

    public void scrape(int minMins, int maxMins, int fileLimit) throws IOException {
        scrape(1, minMins, maxMins, fileLimit);
    }

    public void scrape(int start, int minMins, int maxMins, int fileLimit) throws IOException {

        InputStream is = new URL(url).openStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        data = readAll(br);

        int count = 0;

        for (String line : data.replaceAll(" ", "").split("\n")) {
            if (line.contains("\"numRecordings\"")) {
                files = Integer.parseInt(line.split(":", 2)[1].replace("\"", "").replace(",", ""));
                downloadUrls = new String[files];
                extensions = new String[files];
            }
            if (line.contains("\"file\"")) {
                downloadUrls[count] = line.split(":", 2)[1].replace("\"", "").replace(",", "");
                count++;
            }
            if (line.contains("\"file-name\"")) {
                extensions[count - 1] = line.substring(line.length() - 5, line.length() - 2).toLowerCase();
            }
            if (line.contains("\"length\"")) {
                String[] time = line.split("\":\"")[1].replace(" ", "").replaceAll("[\",]", "").split(":");

                int mins = Integer.parseInt(time[0]);
                int secs = Integer.parseInt(time[1]);

                if (((minMins > 0) && (mins < minMins)) || ((maxMins > 0) && (mins > maxMins)) || (mins == 0 && secs < 10)) {
                    count--;
                }
            }

            if (fileLimit > 0) {
                if (count > fileLimit) {
                    break;
                }
            }
        }

        prevCount = count;

        for (int i = 0; i < count - 1; i++) {
            download(downloadUrls[i], extensions[i], String.valueOf(i + start));
        }
    }

    public int getPrevCount() { return prevCount; }

    private String readAll(BufferedReader br) throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = br.read()) != -1) {
            sb.append((char) c);
        }
        return sb.toString();
    }

    private void download(String url, String extension, String label) {
        try {
            BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());

            File dir = new File("./audio/" + outputName);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            FileOutputStream fos = new FileOutputStream("./audio/" + outputName + "/" + outputName + "_" + label + "." + extension);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer, 0, 1024)) != -1) {
                fos.write(buffer, 0 , bytesRead);
            }

            fos.close();
            System.out.println("File " + outputName + "_" + label + "." + extension + " downloaded successfully.");

        } catch (IOException e) {
        System.out.println("Error downloading from: " + url);
        System.out.println("Reason: " + e.getMessage());
        e.printStackTrace();
    }
    }

    public void setOutputName(String name) {
        outputName = name;
    }
}
