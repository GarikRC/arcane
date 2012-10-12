/*
 * Forge: Play Magic: the Gathering.
 * Copyright (C) 2011  Forge Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.arcane.proxy;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * Generates the HTML for the proxies.
 */
public class ProxyPrinter {

    /**
     *
     */
    private int height;

    /**
     *
     */
    private int width;

    /**
     *
     */
    private int cardListWidth;

    /**
     *
     */
    private int cardBorder;

    /**
     *
     */
    private String outputTemplate = "";

    /**
     *
     */
    private static Logger log =
            Logger.getLogger(ProxyPrinter.class.getCanonicalName());

    /**
     * @param cardBorderParam     int CardBorder width
     * @param heightParam         int height of proxy card
     * @param widthParam          int width of proxy card
     * @param outputTemplateParam template
     * @param cardListWidthParam  int border around cardlist
     */
    public ProxyPrinter(
            final int cardBorderParam,
            final int heightParam,
            final int widthParam,
            final int cardListWidthParam,
            final String outputTemplateParam) {
        this.cardBorder = cardBorderParam;
        this.height = heightParam;
        this.width = widthParam;
        this.outputTemplate = outputTemplateParam;
        this.cardListWidth = cardListWidthParam;
    }

    /**
     * <p>
     * writeDeck.
     * </p>
     *
     * @param deck a String
     * @param file a File
     */
    private void generateHtml(final Deck deck, final File file) {
        try {
            final BufferedWriter htmlOutput =
                    new BufferedWriter(new FileWriter(file));

            Template template;

            final Configuration cfg = new Configuration();
            cfg.setClassForTemplateLoading(deck.getClass(), "/");
            cfg.setObjectWrapper(new DefaultObjectWrapper());
            template = cfg.getTemplate(this.outputTemplate);

            final TreeMap<String, Object> root = new TreeMap<String, Object>();
            root.put("title", deck.getDeckName());
            final List<String> list = new ArrayList<String>();
            for (final String cardName : deck.getCardList().keySet()) {
                for (int i = 0; i <= (deck.getCardList().get(cardName) - 1);
                     i++) {
                    String imageURL = getURL(cardName);
                    list.add(imageURL);
                }
            }

            final TreeMap<String, Integer> map =
                    deck.getCardList();
            root.put("urls", list);
            root.put("cardBorder", this.cardBorder);
            root.put("height", this.height);
            root.put("width", this.width);
            root.put("cardlistWidth", this.width - this.cardListWidth);
            root.put("cardList", map);

            /* Merge data-model with template */
            template.process(root, htmlOutput);
            htmlOutput.flush();
            htmlOutput.close();
        } catch (final IOException e) {
            log.info(e.toString());
        } catch (final TemplateException e) {
            log.info(e.toString());
        } catch (final URISyntaxException e) {
            log.info(e.toString());
        } catch (final InterruptedException e) {
            log.info(e.toString());
        }
    }

    /**
     * @param cardName Name of Card
     * @return String Url
     * @throws URISyntaxException    Exception
     * @throws IOException           Exception
     * @throws InterruptedException  Exception
     */
    private String getURL(final String cardName) throws
            URISyntaxException,
            IOException,
            InterruptedException {
        URI uri = new URI(
                "http",
                "magiccards.info",
                "/query",
                "q=!" + cardName,
                null);
        String request = uri.toString();

        URL url = new URL(request);
        InputStream inputStream = url.openStream();
        BufferedReader bufferedReader =
                new BufferedReader(
                        new InputStreamReader(inputStream, "UTF-8"));
        String returnString = "";

        while (bufferedReader.ready()) {
            String lineHtml = bufferedReader.readLine();
            Thread.sleep(1);
            if (lineHtml.contains("http://magiccards.info/scans/en")
                    && lineHtml.contains("jpg")) {
                lineHtml = lineHtml.substring(lineHtml.indexOf("http"),
                        lineHtml.indexOf("jpg") + ".jpg".length());
                return lineHtml;
            }
        }
        return returnString;
    }

    /**
     * @param files files
     */
    public final void printProxies(final String[] files) {
        for (String fileName : files) {
            File file = new File(fileName);
            com.googlecode.arcane.proxy.Deck deck = new com.googlecode.arcane.proxy.Deck(file);
            deck.parse();
            File htmlFile = new File(fileName.replace(".dec", ".html"));
            generateHtml(deck, htmlFile);
        }
    }
}
