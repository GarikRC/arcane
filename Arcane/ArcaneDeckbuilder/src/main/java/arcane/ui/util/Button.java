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

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
public class Button extends JButton {
    private Boolean nextAlternate;

    /**
     * <p>Constructor for Button.</p>
     */
    public Button() {
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getButton() != 3) return;
                setNextAlternate(true);
                doClick();
            }
        });
    }

    /**
     * <p>Setter for the field <code>nextAlternate</code>.</p>
     *
     * @param nextAlternate a {@link java.lang.Boolean} object.
     */
    public void setNextAlternate(Boolean nextAlternate) {
        this.nextAlternate = nextAlternate;
    }

    /**
     * <p>isAlternate</p>
     *
     * @param evt a {@link java.awt.event.ActionEvent} object.
     * @return a boolean.
     */
    public boolean isAlternate(ActionEvent evt) {
        if (nextAlternate != null) {
            try {
                return nextAlternate;
            } finally {
                nextAlternate = null;
            }
        }
        return (evt.getModifiers() & InputEvent.ALT_MASK) != 0;
    }
}
