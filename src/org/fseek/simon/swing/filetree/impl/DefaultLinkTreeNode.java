package org.fseek.simon.swing.filetree.impl;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import org.fseek.simon.swing.filetree.interfaces.IconChangedListener;
import org.fseek.simon.swing.filetree.interfaces.LinkTreeNode;
import org.fseek.simon.swing.util.UtilBox;
import org.fseek.thedeath.os.util.OSUtil;

/**
 *
 * @author Thedeath<www.fseek.org>
 */
public class DefaultLinkTreeNode extends DefaultMutableTreeNode implements LinkTreeNode
{
    
    private static final ImageIcon blankimg = new ImageIcon(new BufferedImage(17, 17, BufferedImage.TYPE_INT_ARGB));
    
    private File linkDir;
    private Icon icon;
    private boolean mouseOver = false;
    private boolean selected = false;
    private boolean upperCase;
    private Color fontHeaderColor;
    
    private IconChangedListener lis;

    public DefaultLinkTreeNode(File file, ImageIcon icon, Object userObject, Color fontHeaderColor, boolean upperCase)
    {
        super(userObject);
        setIcon(icon);
        this.linkDir = file;
        this.upperCase = upperCase;
        this.fontHeaderColor = fontHeaderColor;
    }
    
    public DefaultLinkTreeNode(File file, Object userObject, IconChangedListener lis)
    {
        super(userObject);
        this.linkDir = file;
        this.upperCase = false;
        this.lis = lis;
        this.fontHeaderColor = Color.BLACK;
        this.icon = blankimg;
        IconQueue.addEntry(new IconEntry(file, this));
    }
    
    public DefaultLinkTreeNode(ImageIcon icon, Object userObject)
    {
        super(userObject);
        setIcon(icon);
        this.upperCase = false;
        this.fontHeaderColor = Color.BLACK;
    }
    
    public DefaultLinkTreeNode(ImageIcon icon, Object userObject, Color fontHeaderColor, boolean upperCase)
    {
        this(null, icon, userObject, fontHeaderColor, upperCase);
    }
    
    public DefaultLinkTreeNode(File file, ImageIcon icon, Object userObject)
    {
        this(file, icon, userObject, Color.BLACK, false);
    }
    
    public DefaultLinkTreeNode(File file, IconChangedListener lis)
    {
        this(file, OSUtil.getFileSystemView().getSystemDisplayName(file), lis);
    }
    
    public DefaultLinkTreeNode(File file, ImageIcon icon){
        this(file, icon, OSUtil.getFileSystemView().getSystemDisplayName(file));
    }
    
    
    @Override
    public Icon getIcon()
    {
        return icon;
    }

    @Override
    public File getLinkDir()
    {
        return linkDir;
    }

    @Override
    public void setLinkDir(File linkDir)
    {
        this.linkDir = linkDir;
    }

    @Override
    public void setIcon(ImageIcon icon)
    {
        if(icon.getIconWidth() > 17 || icon.getIconHeight() > 17){
            icon = UtilBox.rescaleIconIfNeeded(icon, 17);
        }
        this.icon = icon;
        if(lis != null)
            lis.iconChanged(this, icon);

    }

    @Override
    public boolean isUpperCase()
    {
        return upperCase;
    }

    @Override
    public Color getFontHeaderColor()
    {
        return fontHeaderColor;
    }
    
    @Override
    public void setHighPriority(){
        for(int i = 0; i<getChildCount(); i++)
        {
            if(this.getChildAt(i) instanceof LinkTreeNode){
                LinkTreeNode node = (LinkTreeNode)this.getChildAt(i);
                IconQueue.addPriorityNode(node);
            }
        }
    }
}
