package org.fseek.thedeath.filetree.interfaces;

import java.io.Serializable;
import javax.swing.Icon;

/**
 *
 * @author Thedeath<www.fseek.org>
 */


public interface IconChangedListener extends Serializable
{
    public void iconChanged(LinkTreeNode node, Icon newIcon);
}
