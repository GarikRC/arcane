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

import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
public class ButtonMenuItem extends MenuItem {
    private final Button button;
    private boolean isAlternate;

    /**
     * <p>Constructor for ButtonMenuItem.</p>
     *
     * @param text a {@link java.lang.String} object.
     * @param button a {@link arcane.ui.util.Button} object.
     * @param isAlternate a boolean.
     */
    public ButtonMenuItem(String text, Button button, boolean isAlternate) {
        this(text, button);
        this.isAlternate = isAlternate;
    }

    /**
     * <p>Constructor for ButtonMenuItem.</p>
     *
     * @param text a {@link java.lang.String} object.
     * @param button a {@link arcane.ui.util.Button} object.
     */
    public ButtonMenuItem(String text, Button button) {
        super(text);

        this.button = button;

        setIcon(button.getIcon());
    }

    /** {@inheritDoc} */
    public void doClick(int pressTime) {
        if (isAlternate) button.setNextAlternate(true);
        button.doClick(0);
    }

    /** {@inheritDoc} */
    public void setModel(ButtonModel newModel) {
        super.setModel(new DefaultButtonModel() {
            public boolean isEnabled() {
                return ButtonMenuItem.this.isEnabled();
            }
        });
    }

    /**
     * <p>isEnabled</p>
     *
     * @return a boolean.
     */
    public boolean isEnabled() {
        if (button == null) return true;
        return button.isEnabled();
    }
}
