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


package arcane.ui.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import arcane.Arcane;
import arcane.Card;
import arcane.CardProperty;
public class CardTableModel extends AbstractTableModel {
    public List<Card> viewCards = new ArrayList(512);
    public List<Card> unsortedCards = new ArrayList(512);
    public CardProperty[] properties = new CardProperty[0];
    public Map<Card, Integer> cardToQty;
    public boolean isUniqueOnly;

    private boolean editable;

    /**
     * <p>Constructor for CardTableModel.</p>
     *
     * @param editable a boolean.
     */
    public CardTableModel(boolean editable) {
        this.editable = editable;
    }

    /** {@inheritDoc} */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (!isUniqueOnly && properties[columnIndex] == CardProperty.ownedQty) return true;
        if (!editable) return false;
        return properties[columnIndex] == CardProperty.set || properties[columnIndex] == CardProperty.setName
                || properties[columnIndex] == CardProperty.pictureNumber;
    }

    /**
     * <p>Setter for the field <code>properties</code>.</p>
     *
     * @param properties a {@link arcane.CardProperty} object.
     */
    public void setProperties(CardProperty... properties) {
        this.properties = properties;
        fireTableStructureChanged();
    }

    /** {@inheritDoc} */
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex < 0 || columnIndex > properties.length)
            throw new IllegalArgumentException("Invalid column index: " + columnIndex);
        Card card = viewCards.get(rowIndex);
        if (properties[columnIndex] == CardProperty.qty) return cardToQty.get(card);
        if (properties[columnIndex] == CardProperty.ownedQty && isUniqueOnly)
            return Arcane.getInstance().getTotalOwnedQty(card.name);
        return card.getValue(properties[columnIndex]);
    }

    /**
     * <p>getRowCount</p>
     *
     * @return a int.
     */
    public int getRowCount() {
        return viewCards.size();
    }

    /**
     * <p>getColumnCount</p>
     *
     * @return a int.
     */
    public int getColumnCount() {
        return properties.length;
    }

    /** {@inheritDoc} */
    public String getColumnName(int columnIndex) {
        if (columnIndex < 0 || columnIndex > properties.length)
            throw new IllegalArgumentException("Invalid column index: " + columnIndex);
        return " " + properties[columnIndex].text;
    }

    /**
     * <p>getColumnIndex</p>
     *
     * @param property a {@link arcane.CardProperty} object.
     * @return a int.
     */
    public int getColumnIndex(CardProperty property) {
        for (int i = 0; i < properties.length; i++)
            if (properties[i] == property) return i;
        return -1;
    }

    /**
     * <p>getValueAt</p>
     *
     * @param rowIndex a int.
     * @param property a {@link arcane.CardProperty} object.
     * @return a {@link java.lang.String} object.
     */
    public String getValueAt(int rowIndex, CardProperty property) {
        return (String) getValueAt(rowIndex, getColumnIndex(property));
    }

    /**
     * <p>getRowIndex</p>
     *
     * @param card a {@link arcane.Card} object.
     * @return a int.
     */
    public int getRowIndex(Card card) {
        for (int i = 0, n = viewCards.size(); i < n; i++)
            if (viewCards.get(i).equals(card)) return i;
        return -1;
    }

    /**
     * <p>getRowIndices</p>
     *
     * @param cardName a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    public List<Integer> getRowIndices(String cardName) {
        List<Integer> indices = new ArrayList();
        for (int i = 0, n = viewCards.size(); i < n; i++)
            if (viewCards.get(i).name.equals(cardName)) indices.add(i);
        return indices;
    }

    /**
     * <p>getCards</p>
     *
     * @param cardName a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    public List<Card> getCards(String cardName) {
        List<Card> cards = new ArrayList();
        for (int index : getRowIndices(cardName))
            cards.add(viewCards.get(index));
        return cards;
    }
}
