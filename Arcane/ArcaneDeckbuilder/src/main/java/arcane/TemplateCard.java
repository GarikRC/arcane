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


package arcane;

/**
 * Provides extra getters solely for StringTemplate output.
 *
 * @author dhudson
 * @version $Id: $
 */
public class TemplateCard extends Card {
    /**
     * <p>Constructor for TemplateCard.</p>
     *
     * @param card a {@link arcane.Card} object.
     */
    public TemplateCard(Card card) {
        super(card);
    }

    /**
     * <p>Constructor for TemplateCard.</p>
     *
     * @param card a {@link arcane.Card} object.
     * @param qty a int.
     */
    public TemplateCard(Card card, int qty) {
        super(card, qty);
    }

    /**
     * <p>getQty</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getQty() {
        return Integer.toString(qty);
    }

    /**
     * <p>getCsvName</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getCsvName() {
        String csvName = getNameWithPictureNumber();
        if (csvName.contains("\"") || csvName.contains(",")) return '"' + csvName.replace("\"", "\"\"") + '"';
        return csvName;
    }

    /**
     * <p>getSet</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSet() {
        return set.toUpperCase();
    }

    /**
     * <p>getShortSet</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getShortSet() {
        return Arcane.getInstance().getAlternateSets(set).iterator().next().toUpperCase();
    }

    /**
     * <p>getNameWithPictureNumber</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getNameWithPictureNumber() {
        if (pictureNumber > 0)
            return englishName + " (" + pictureNumber + ")";
        else
            return englishName;
    }

    /**
     * <p>getType</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getType() {
        return typeSpecialCharacters;
    }

    /**
     * <p>getLegal</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getLegal() {
        return legalSpecialCharacters;
    }

    /**
     * <p>getLegalIndented</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getLegalIndented() {
        return legalSpecialCharacters.replace("\r\n", "\r\n    ");
    }
}
