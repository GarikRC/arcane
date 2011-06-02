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

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
public abstract class DocumentModifiedListener implements DocumentListener {
    /** {@inheritDoc} */
    public void changedUpdate(DocumentEvent e) {
    }

    /** {@inheritDoc} */
    public void insertUpdate(DocumentEvent e) {
        changed();
    }

    /** {@inheritDoc} */
    public void removeUpdate(DocumentEvent e) {
        changed();
    }

    /**
     * <p>changed</p>
     */
    abstract public void changed();
}
