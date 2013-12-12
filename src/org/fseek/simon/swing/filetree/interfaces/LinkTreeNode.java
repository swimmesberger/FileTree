package org.fseek.simon.swing.filetree.interfaces;

import java.awt.Color;
import java.io.File;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.MutableTreeNode;

public interface LinkTreeNode extends MutableTreeNode
{
    public Icon getIcon();

    public File getLinkDir();

    public void setLinkDir(File linkDir);

    public void setIcon(ImageIcon icon);

    public boolean isUpperCase();

    public Color getFontHeaderColor();
    
    public Object getUserObject();
    
    public void setHighPriority();
}
