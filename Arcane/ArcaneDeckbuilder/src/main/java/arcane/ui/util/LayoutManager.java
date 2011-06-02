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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
public abstract class LayoutManager implements java.awt.LayoutManager {
    /** {@inheritDoc} */
    public void addLayoutComponent(String name, Component comp) {
    }

    /** {@inheritDoc} */
    public void layoutContainer(Container parent) {
        layout();
    }

    /** {@inheritDoc} */
    public Dimension minimumLayoutSize(Container parent) {
        return null;
    }

    /** {@inheritDoc} */
    public Dimension preferredLayoutSize(Container parent) {
        return null;
    }

    /** {@inheritDoc} */
    public void removeLayoutComponent(Component comp) {
    }

    /**
     * <p>layout</p>
     */
    abstract public void layout();
}
