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


package arcane;

import java.util.ArrayList;
import java.util.List;
public final class DecklistCard {
    static private Arcane arcane = Arcane.getInstance();

    private final int id;
    private String name;
    private String set;
    private int pictureNumber;
    private boolean isSideboard;

    /**
     * <p>Constructor for DecklistCard.</p>
     *
     * @param card a {@link arcane.Card} object.
     * @param isSideboard a boolean.
     */
    public DecklistCard(Card card, boolean isSideboard) {
        id = card.id;
        setName(card.name);
        setSet(card.set);
        setPictureNumber(card.pictureNumber);
        setSideboard(isSideboard);
    }

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a int.
     */
    public int getId() {
        return id;
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Setter for the field <code>name</code>.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>set</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSet() {
        return set;
    }

    /**
     * <p>Setter for the field <code>set</code>.</p>
     *
     * @param set a {@link java.lang.String} object.
     */
    public void setSet(String set) {
        this.set = set;
    }

    /**
     * <p>Getter for the field <code>pictureNumber</code>.</p>
     *
     * @return a int.
     */
    public int getPictureNumber() {
        return pictureNumber;
    }

    /**
     * <p>Setter for the field <code>pictureNumber</code>.</p>
     *
     * @param pictureNumber a int.
     */
    public void setPictureNumber(int pictureNumber) {
        this.pictureNumber = pictureNumber;
    }

    /**
     * <p>isSideboard</p>
     *
     * @return a boolean.
     */
    public boolean isSideboard() {
        return isSideboard;
    }

    /**
     * <p>setSideboard</p>
     *
     * @param isSideboard a boolean.
     */
    public void setSideboard(boolean isSideboard) {
        this.isSideboard = isSideboard;
    }

    /**
     * <p>getCard</p>
     *
     * @return a {@link arcane.Card} object.
     */
    public Card getCard() {
        return arcane.getCard(name, set, pictureNumber);
    }

    /**
     * <p>getCards</p>
     *
     * @param decklistCards a {@link java.util.List} object.
     * @return an array of {@link java.util.List} objects.
     */
    static public List<Card>[] getCards(List<DecklistCard> decklistCards) {
        List<Card> main = new ArrayList(70);
        List<Card> side = new ArrayList(15);

        for (DecklistCard decklistCard : decklistCards) {
            Card card = decklistCard.getCard();
            if (decklistCard.isSideboard())
                side.add(card);
            else
                main.add(card);
        }

        List<Card>[] cards = new List[2];
        cards[0] = main;
        cards[1] = side;
        return cards;
    }
}
