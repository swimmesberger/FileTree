package org.fseek.thedeath.filetree.interfaces;

import java.io.File;
import java.util.HashMap;

/**
 *
 * @author Thedeath<www.fseek.org>
 */


public interface FavoritesHandler
{
    public void initFavorites();
    public void addFavorite(File f, String name);
    public void removeFavorite(File f);
    public void dispose();
    public HashMap<File, String> getFavorites();
    public void addFavoritesChangedListener(FavoritesChangedListener lis);
    public void removeFavoritesChangedListener(FavoritesChangedListener lis);
}
