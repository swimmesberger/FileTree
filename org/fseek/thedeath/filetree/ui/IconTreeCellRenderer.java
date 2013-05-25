package org.fseek.thedeath.filetree.ui;


import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Locale;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.fseek.thedeath.filetree.interfaces.LinkTreeNode;
import org.fseek.thedeath.os.util.OSUtil;

public class IconTreeCellRenderer extends DefaultTreeCellRenderer
{

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        Font font = getFont();
        if(font == null)
        {
            font = (Font) UIManager.get("Label.font");
        }
        if (value instanceof LinkTreeNode)
        {
            LinkTreeNode node = (LinkTreeNode)value;
            setIcon(node.getIcon());
            setForeground(node.getFontHeaderColor());
            if(node.isUpperCase())
            {
                String s = value.toString();
                s = s.toUpperCase(Locale.getDefault());
                value = s;

                Font newFont = new Font(font.getName(), Font.BOLD, font.getSize());
                setFont(newFont);
            }
            else
            {
                Font newFont = new Font(font.getName(), Font.PLAIN, font.getSize());
                setFont(newFont);
            }
        }
        setBackground(OSUtil.getOsColors().getTreePanelColor());
        //we can not call super.getTreeCellRendererComponent method, since it overrides our setIcon call and cause rendering of labels to '...' when node expansion is done
        //so, we copy (and modify logic little bit) from super class method:
        String stringValue = tree.convertValueToText(value, sel,
        expanded, leaf, row, hasFocus);

        this.hasFocus = hasFocus;
        setText(stringValue);

        if (!tree.isEnabled())
        {
            setEnabled(false);
        }
        else
        {
            setEnabled(true);
        }
        
        if(sel)
        {
            setBackground(new Color(140,191,242));
        }
        setComponentOrientation(tree.getComponentOrientation());
        selected = sel;
        setOpaque(true);
        return this;
    }
}
