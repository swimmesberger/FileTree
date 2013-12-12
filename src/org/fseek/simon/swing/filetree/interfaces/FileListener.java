package org.fseek.simon.swing.filetree.interfaces;

import java.io.File;
import java.util.EventObject;

/**
 *
 * @author Thedeath<www.fseek.org>
 */


public interface FileListener
{
    public void fileChanged(File f, EventObject ev);
}
