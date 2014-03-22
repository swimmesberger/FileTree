package org.fseek.simon.swing.ui;


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
    private static final Color SELECTION = new Color(140,191,242);
    private static final Color NON_SELECTION = OSUtil.getOsColors().getTreePanelColor();
    
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
