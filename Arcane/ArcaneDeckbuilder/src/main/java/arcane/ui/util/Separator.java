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


package arcane.ui.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
public class Separator extends JComponent {
    private Dimension size = new Dimension(6, 20);

    /** {@inheritDoc} */
    protected void paintComponent(Graphics g) {
        int x = 2;
        g.setColor(Color.gray);
        g.drawLine(x, 2, x, getHeight() - 4);
        x++;
        g.setColor(Color.white);
        g.drawLine(x, 2, x, getHeight() - 4);
        super.paintComponent(g);
    }

    /**
     * <p>getWidth</p>
     *
     * @return a int.
     */
    public int getWidth() {
        return (int) size.getWidth();
    }

    /**
     * <p>getHeight</p>
     *
     * @return a int.
     */
    public int getHeight() {
        return (int) size.getHeight();
    }

    /**
     * <p>Getter for the field <code>size</code>.</p>
     *
     * @return a {@link java.awt.Dimension} object.
     */
    public Dimension getSize() {
        return size;
    }

    /**
     * <p>getPreferredSize</p>
     *
     * @return a {@link java.awt.Dimension} object.
     */
    public Dimension getPreferredSize() {
        return size;
    }

    /**
     * <p>getMinimumSize</p>
     *
     * @return a {@link java.awt.Dimension} object.
     */
    public Dimension getMinimumSize() {
        return size;
    }

    /**
     * <p>getMaximumSize</p>
     *
     * @return a {@link java.awt.Dimension} object.
     */
    public Dimension getMaximumSize() {
        return size;
    }
}
