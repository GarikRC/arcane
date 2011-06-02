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

import java.io.IOException;
import java.util.List;
import java.util.Map;
public interface Decklist {
    /**
     * <p>getName</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName();

    /**
     * <p>exists</p>
     *
     * @return a boolean.
     */
    public boolean exists();

    /**
     * <p>isOpenable</p>
     *
     * @return a boolean.
     */
    public boolean isOpenable();

    /**
     * <p>open</p>
     *
     * @throws java.io.IOException if any.
     */
    public void open() throws IOException;

    /**
     * <p>getDecklistCards</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<DecklistCard> getDecklistCards();

    /**
     * <p>save</p>
     *
     * @param deckCards a {@link java.util.List} object.
     * @param deckCardToQty a {@link java.util.Map} object.
     * @param sideCards a {@link java.util.List} object.
     * @param sideCardToQty a {@link java.util.Map} object.
     * @throws java.io.IOException if any.
     */
    public void save(List<Card> deckCards, Map<Card, Integer> deckCardToQty, List<Card> sideCards, Map<Card, Integer> sideCardToQty) throws IOException;

    /**
     * <p>getData</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getData();
}
