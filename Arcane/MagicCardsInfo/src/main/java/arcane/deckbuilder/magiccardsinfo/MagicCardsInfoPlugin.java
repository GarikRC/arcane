/**
 *     Copyright (C) 2010  snacko
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 *     Copyright (C) 2010  snacko
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @author dhudson
 * @version $Id: $
 */

package arcane.deckbuilder.magiccardsinfo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;

import arcane.Arcane;
import arcane.Card;
//import arcane.CardDataStoreConnection;
import arcane.ArcaneException;
import arcane.DecklistFile;
import arcane.deckbuilder.DeckBuilderPlugin;
import arcane.deckbuilder.ui.DeckBuilder;
//import arcane.ui.util.ProgressDialog;
import arcane.util.Preferences;
import arcane.util.Util;
import arcane.ui.util.ProgressDialog;
public class MagicCardsInfoPlugin extends DeckBuilderPlugin {
	static private File directory;
	static private JFileChooser dirFileChooser;

	private DeckBuilder deckBuilder;
	private JCheckBoxMenuItem setPrintBasicLandsMenuItem;

	/**
	 * <p>install</p>
	 *
	 * @param deckBuilder a {@link arcane.deckbuilder.ui.DeckBuilder} object.
	 */
	public void install (final DeckBuilder deckBuilder) {
		JMenu menu = new JMenu(getName());
		{
			JMenuItem loadMenuItem = new JMenuItem("Generate proxies...");
			menu.add(loadMenuItem);
			loadMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed (ActionEvent evt) {
					generateProxies(deckBuilder);
				}
			});
			{
				JMenuItem setDirectoryMenuItem = new JMenuItem("Set card proxy directory...");
				menu.add(setDirectoryMenuItem);
				setDirectoryMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed (ActionEvent evt) {
						promptDirectory();
					}
				});
			}
			{
				setPrintBasicLandsMenuItem = new JCheckBoxMenuItem("Print proxies for basic lands");
				menu.add(setPrintBasicLandsMenuItem);
				setPrintBasicLandsMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed (ActionEvent evt) {
						setPrintBasicLands();
					}
				});
				//setPrintBasicLandsMenuItem.setSelected(Boolean.getBoolean(System.getProperties().getProperty("plugin.magiccardsinfo.printbasiclands", "true")));
			}
		}
		deckBuilder.addPluginMenu(menu);
		//deckBuilder2 = deckBuilder;
	}

	private void setPrintBasicLands(){
		//System.getProperties().put("plugin.magiccardsinfo.printbasiclands", String.valueOf(setPrintBasicLandsMenuItem.isSelected()));
		if (setPrintBasicLandsMenuItem.isSelected())
		{
			//System.getProperties().put("plugin.magiccardsinfo.printbasiclands", true);
		}
		else
		{
			//System.getProperties().remove("plugin.magiccardsinfo.printbasiclands");
		}
	}


	private void promptDirectory () {
		if (dirFileChooser == null) {
			dirFileChooser = new JFileChooser((directory == null || !directory.exists()) ? "/" : directory.getAbsolutePath());
			dirFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			dirFileChooser.setDialogTitle("Set card proxy directory");
		}
		int result = dirFileChooser.showOpenDialog(deckBuilder);
		if (result != JFileChooser.APPROVE_OPTION) return;
		File file = dirFileChooser.getSelectedFile();
		if (!file.exists()) return;
		directory = file;
	}

	private void generateProxies (final DeckBuilder deckBuilder) {
		if (directory == null || !directory.exists()) {
			promptDirectory();
			if (directory == null || !directory.exists()) return;
		}

		final ProgressDialog dialog = new ProgressDialog(deckBuilder, "Magic cards Info");
		dialog.setMessage("Generating proxies...");
		dialog.setAlwaysOnTop(true);
		dialog.setValue(-1);

		new Thread(new Runnable() {
			public void run () {
				try {
					String language = "en";
					Map<String, String> blockMapping = new HashMap<String, String>();	
					blockMapping.put("lrw", "lw");

					blockMapping.put("10th", "10e");

					blockMapping.put("fut", "fut");
					blockMapping.put("plc", "pc");
					blockMapping.put("tsp", "ts");
					blockMapping.put("tsb", "tsts");

					blockMapping.put("dis", "di");
					blockMapping.put("gpt", "gp");
					blockMapping.put("rav", "rav");

					blockMapping.put("sok", "sok");
					blockMapping.put("bok", "bok");
					blockMapping.put("chk", "chk");

					blockMapping.put("9ed", "9e");

					blockMapping.put("unh", "uh");

					blockMapping.put("5dn", "5dn");
					blockMapping.put("dst", "ds");
					blockMapping.put("mrd", "mi");

					blockMapping.put("8ed", "8e");

					blockMapping.put("scg", "sc");
					blockMapping.put("lgn", "le");
					blockMapping.put("ons", "on");

					blockMapping.put("jud", "ju");
					blockMapping.put("tor", "tr");
					blockMapping.put("ody", "od");

					blockMapping.put("7th", "7e");

					blockMapping.put("apc", "ap");
					blockMapping.put("pls", "ps");
					blockMapping.put("inv", "in");

					blockMapping.put("pcy", "pr");
					blockMapping.put("nms", "ne");
					blockMapping.put("mmq", "mm");

					blockMapping.put("uds", "ud");
					blockMapping.put("ulg", "ul");
					blockMapping.put("usg", "us");

					blockMapping.put("6th", "6e");

					blockMapping.put("ugl", "ug");

					blockMapping.put("exo", "ex");
					blockMapping.put("sth", "sh");
					blockMapping.put("tmp", "tp");

					blockMapping.put("wth", "wl");
					blockMapping.put("vis", "vi");
					blockMapping.put("mir", "mr");

					blockMapping.put("5th", "5e");

					blockMapping.put("ptk", "p3k");
					blockMapping.put("po2", "po2");
					blockMapping.put("por", "po");

					blockMapping.put("cst", "cstd");
					blockMapping.put("cld", "cs");
					blockMapping.put("all", "ai");
					blockMapping.put("ice", "ia");

					blockMapping.put("hml", "hl");
					blockMapping.put("chr", "ch");
					blockMapping.put("4th", "4e");
					blockMapping.put("fem", "fe");
					blockMapping.put("drk", "dk");
					blockMapping.put("leg", "lg");
					blockMapping.put("r", "rv");
					blockMapping.put("atq", "aq");
					blockMapping.put("arn", "an");
					blockMapping.put("u", "un");
					blockMapping.put("b", "be");
					blockMapping.put("a", "al");

					blockMapping.put("at", "at");
					blockMapping.put("bd", "bd");
					blockMapping.put("dm", "dm");
					blockMapping.put("ov", "");

					blockMapping.put("st", "st");
					blockMapping.put("tk", "tk");
					blockMapping.put("tkx", "");
					blockMapping.put("vg", "");
					blockMapping.put("are", "alp04");
					blockMapping.put("chp", "cp");
					blockMapping.put("fnm", "fnmp");
					blockMapping.put("gtw", "");
					blockMapping.put("jgc", "jr");
					blockMapping.put("lnd", "");
					blockMapping.put("pr", "mbp");
					blockMapping.put("pre", "ptc");
					blockMapping.put("rel", "");
					blockMapping.put("rew", "mprp");

					//CardDataStoreConnection conn = DeckBuilder.cardDataStore.getThreadConnection();

					FileWriter out = new FileWriter(directory + "/" + deckBuilder.getCurrentDecklist().getName() + ".html");
					try {
						for (Card card: deckBuilder.getDeckTable().model.viewCards)
						{
							if (card.collectorNumber == null)
							{
								//Map<Integer, String> cardMapping = Arcane.getInstance().getCardDataStoreConnection().getPictureToCollectorNumbers(/*card.name, card.set*/);
								//card.collectorNumber = cardMapping.get(card.pictureNumber);
							}
							if (card.collectorNumber != null)
							{
								System.out.println("-"+card.name+"-");
								if (!setPrintBasicLandsMenuItem.isSelected() && (card.name.equals("Swamp") || card.name.equals("Plains") || card.name.equals("Mountain") || card.name.equals("Island") || card.name.equals("Forest"))) {
									//NOP
								}
								else {
									for (int j = 1; j <= card.qty; j++)
									{
										out.write("<img src=\"http://magiccards.info/scans/" + language + "/" + blockMapping.get(card.set.toLowerCase()) + "/" + card.collectorNumber.substring(0, card.collectorNumber.indexOf("/")) + ".jpg\" height=\"330\" width=\"229\" />");
										out.write("\n");
									}
								}
							}
						}
						for (Card card: deckBuilder.getSideTable().model.viewCards)
						{
							if (!setPrintBasicLandsMenuItem.isSelected() && (card.name.equals("Swamp") || card.name.equals("Plains") || card.name.equals("Mountain") || card.name.equals("Island") || card.name.equals("Forest"))) {
								//NOP
							}
							else {
								if (card.collectorNumber == null)
								{
									//Map<Integer, String> cardMapping = Arcane.getInstance().getCardDataStoreConnection().getPictureToCollectorNumbers(/*card.name, card.set*/);
									//card.collectorNumber = cardMapping.get(card.pictureNumber);
								}
								if (card.collectorNumber != null)
								{
									for (int j = 1; j <= card.qty; j++)
									{
										out.write("<img src=\"http://magiccards.info/scans/" + language + "/" + blockMapping.get(card.set.toLowerCase()) + "/" + card.collectorNumber.substring(0, card.collectorNumber.indexOf("/")) + ".jpg\" height=\"330\" width=\"229\" />");
										out.write("\n");
									}
								}
							}
						}
					} finally {
						out.close();
					}
					dialog.setMessage("Generating proxies...");
				//} catch (SQLException ex) {
				//	throw new ArcaneException("Error generating proxies.", ex);		
				}catch (NullPointerException ex) {
					throw new ArcaneException("Error generating proxies.", ex);
				} catch (IOException ex) {
					throw new ArcaneException("Error generating proxies.", ex);
				} finally {
					dialog.setVisible(false);
				}
			}
		}, "GenerateProxies").start();

		dialog.setVisible(true);
		dialog.dispose();
	}

	/**
	 * <p>getName</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getName () {
		return "Magic Cards Info Proxies";
	}
}
