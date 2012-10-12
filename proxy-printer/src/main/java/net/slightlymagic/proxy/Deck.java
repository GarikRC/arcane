package com.googlecode.arcane.proxy;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: dhudson
 * Date: 10/6/12
 * Time: 12:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class Deck {

    /**
     *
     */
    private static Logger log =
            Logger.getLogger(Deck.class.getCanonicalName());

    /**
     * @return String deckName
     */
    public final String getDeckName() {
        return deckName;
    }

    /**
     * @param deckNameParam DeckName
     */
    public final void setDeckName(final String deckNameParam) {
        this.deckName = deckNameParam;
    }

    /**
     * @return cardList
     */
    public final TreeMap<String, Integer> getCardList() {
        return cardList;
    }

    /**
     * @param cardListParam CardList
     */
    public final void setCardList(
            final TreeMap<String, Integer> cardListParam) {
        this.cardList = cardListParam;
    }

    /**
     *
     */
    private String deckName;

    /**
     *
     */
    private TreeMap<String, Integer> cardList = new TreeMap<String, Integer>();

    /**
     *
     */
    private File file;

    /**
     * @param fileParam File
     */
    public Deck(final File fileParam) {
        this.file = fileParam;

    }

    /**
     *
     */
    public final void parse() {
        try {
            // Open the file
            FileInputStream fstream = new FileInputStream(this.file);

            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String strLine;

            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                // Print the content on the console
                if (!strLine.startsWith("///")) {
                    if (strLine.startsWith("//")) {
                        this.deckName = strLine.substring(2).trim();
                        //System.out.println("Deck Name: " + this.deckName);
                    } else {
                        //System.out.println(strLine);
                        if (!strLine.trim().isEmpty()) {
                            String[] split = strLine.split(" ", 2);
                            this.cardList.put(split[1], Integer.parseInt(split[0]));
                        }
                    }
                }
            }
            //Close the input stream
            in.close();

        } catch (FileNotFoundException e) {
            log.info("File not found");
        } catch (IOException e) {
            log.info("IO Error");
        }
    }
}
