package org.fseek.thedeath.filetree.impl;

import java.io.File;
import org.fseek.thedeath.filetree.interfaces.LinkTreeNode;

/**
 *
 * @author Thedeath<www.fseek.org>
 */
 
public class IconEntry{
    private File file;
    private LinkTreeNode node;

    public IconEntry(File file, LinkTreeNode node)
    {
        this.file = file;
        this.node = node;
    }

    public LinkTreeNode getNode()
    {
        return node;
    }

    public File getFile()
    {
        return file;
    }
}