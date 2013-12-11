package org.fseek.thedeath.filetree.interfaces;

import java.io.File;

/**
 *
 * @author Thedeath<www.fseek.org>
 */


public interface FavoritesChangedListener
{
    public void favoriteAdded(File f, String name);
    public void favoriteRemoved(File f);
}
