/* 
 * The MIT License
 *
 * Copyright 2014 Simon Wimmesberger.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.fseek.simon.swing.filetree.ui;


import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Locale;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.fseek.simon.swing.filetree.interfaces.LinkTreeNode;
import org.fseek.thedeath.os.util.OSUtil;

public class IconTreeCellRenderer extends DefaultTreeCellRenderer
{
    private static final Color SELECTION = OSUtil.getOSAppearance().getTreePanelSelectionColor();
    private static final Color NON_SELECTION = OSUtil.getOSAppearance().getTreePanelColor();
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        Font font = getFont();
        if(font == null)
        {
            font = (Font) UIManager.get("Label.font");
        }
        if (value instanceof LinkTreeNode)
        {
            LinkTreeNode node = (LinkTreeNode)value;
            setIcon(node.getIcon());
            setForeground(this.getForeground() == getTextSelectionColor() ? getTextSelectionColor(node) : getTextNonSelectionColor(node));
            if(node.isUpperCase())
            {
                String s = value.toString();
                s = s.toUpperCase(Locale.getDefault());
                value = s;

                Font newFont = font.deriveFont(Font.BOLD);
                setFont(newFont);
            }
            else
            {
                Font newFont = font.deriveFont(Font.PLAIN);
                setFont(newFont);
            }
        }
        return this;
    }

    @Override
    public Color getBackgroundSelectionColor() {
        return SELECTION;
    }

    @Override
    public Color getBackgroundNonSelectionColor() {
        return NON_SELECTION;
    }

    public Color getTextSelectionColor(LinkTreeNode node) {
        return node.getFontHeaderColor();
    }

    public Color getTextNonSelectionColor(LinkTreeNode node) {
        return node.getFontHeaderColor();
    }
}
