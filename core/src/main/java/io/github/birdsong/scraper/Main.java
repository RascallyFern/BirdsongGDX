package io.github.birdsong.scraper;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Scraper s = new Scraper();

        try {
            s.setOutputName("Mallard");
            s.setQueries("anas", "", "platyrhynchos", "");
            s.scrape(0, 10, 100);
            s.setOutputName("BlueTit");
            s.setQueries("cyanistes", "", "caeruleus", "");
            s.scrape(0, 10, 100);
            s.setOutputName("Robin");
            s.setQueries("erithacus", "", "rubecula", "");
            s.scrape(0, 10, 100);
            s.setOutputName("GreatTit");
            s.setQueries("parus", "", "major", "");
            s.scrape(0, 10, 100);
            s.setOutputName("GreatCrestedGrebe");
            s.setQueries("podiceps", "", "cristatus", "");
            s.scrape(0, 10, 100);
            s.setOutputName("WillowTit");
            s.setQueries("poecile", "", "kleinschmidti", "");
            s.scrape(0, 10, 100);
            s.setSubspecies("rhenanus");
            s.scrape(s.getPrevCount(),0, 10, 100);
            s.setOutputName("BlackHeadedGull");
            s.setQueries("chroicocephalus", "ridibundus", "", "");
            s.scrape(0, 10, 100);
            s.setOutputName("Kingfisher");
            s.setQueries("alcedo", "", "", "");
            s.scrape(0, 10, 100);
            s.setSubspecies("ispida");
            s.scrape(s.getPrevCount(), 0, 10, 100);
            s.setOutputName("Swift");
            s.setQueries("apus", "", "apus", "");
            s.scrape(0, 10, 100);
            s.setOutputName("Yellowhammer");
            s.setQueries("emberiza", "", "citrinella", "");
            s.scrape(0, 10, 100);
            s.setOutputName("Bullfinch");
            s.setQueries("pyrrhula", "", "pyrrhula", "");
            s.scrape(0, 10, 100);
            s.setOutputName("Cuckoo");
            s.setQueries("cuculus", "", "canorus", "");
            s.scrape(0, 10, 100);
            s.setOutputName("LittleTern");
            s.setQueries("sternula", "", "albifrons", "");
            s.scrape(0, 10, 100);
            s.setOutputName("BeardedReedling");
            s.setQueries("panurus", "", "biarmicus", "");
            s.scrape(0, 10, 100);
            s.setOutputName("Goldcrest");
            s.setQueries("regulus", "", "regulus", "");
            s.scrape(0, 10, 100);
            s.setOutputName("LongTailedTit");
            s.setQueries("aegithalos", "", "caudatus", "");
            s.scrape(0, 10, 100);
            s.setOutputName("CettisWarbler");
            s.setQueries("cettia", "", "cetti", "");
            s.scrape(0, 10, 100);
            s.setOutputName("Magpie");
            s.setQueries("pica", "", "pica", "");
            s.scrape(0, 10, 100);
            s.setOutputName("Jackdaw");
            s.setQueries("coloeus", "", "monedula", "");
            s.scrape(0, 10, 100);
            s.setSubspecies("spermologus");
            s.scrape(s.getPrevCount(),0, 10, 50);

        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
