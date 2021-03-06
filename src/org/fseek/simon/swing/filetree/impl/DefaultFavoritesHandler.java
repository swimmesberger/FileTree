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
package org.fseek.simon.swing.filetree.impl;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.fseek.simon.swing.filetree.interfaces.FavoritesChangedListener;
import org.fseek.simon.swing.filetree.interfaces.FavoritesHandler;
import org.fseek.thedeath.os.VirtuaDirectory;
import org.fseek.thedeath.os.util.FileSystemUtil;
import org.fseek.thedeath.os.util.OSUtil;

/**
 *
 * @author Thedeath<www.fseek.org>
 */


public class DefaultFavoritesHandler implements FavoritesHandler
{
    private File favoFile;
    
    private HashMap<File, String> favorites = new HashMap<>();
    
    private ArrayList<FavoritesChangedListener> listener;
    
    @Override
    public void initFavorites()
    {
        favoFile = new File(FileSystemUtil.getFileSystem().getJarDirectory(), "favorites.xml");
        if (!favoFile.exists())
        {
            initDefaults();
        }else{
            readFavos();
        }
    }
    
    private void initDefaults(){
        File desktop = FileSystemUtil.getFileSystem().getDesktop();
        createFavo(desktop);
        File downloadsFolder = FileSystemUtil.getFileSystem().getDownloadsFolder();
        createFavo(downloadsFolder);
        File recentFolder = FileSystemUtil.getFileSystem().getRecentFolder();
        createFavo(recentFolder);
    }
    
    public boolean createFavo(File f){
        if(f == null)return false;
        return createFavo(f, OSUtil.getFileSystemView().getSystemDisplayName(f));
    }
    
    public boolean createFavo(File f, String name)
    {
        if(f == null)return false;
        favorites.put(f, name);
        return refreshFavos();
    }
    
    /*
     * Completely refreshes the file
     */
    public boolean refreshFavos(){
        try
        {
            try (XMLEncoder enc = new XMLEncoder(new FileOutputStream(favoFile, false)))
            {
                for (Iterator<Map.Entry<File, String>> it = favorites.entrySet().iterator(); it.hasNext();)
                {
                    Entry<File, String> entr = it.next();
                    enc.writeObject(entr.getValue());
                    enc.writeObject(entr.getKey().getAbsolutePath());
                    if(entr.getKey() instanceof VirtuaDirectory){
                        VirtuaDirectory vd = (VirtuaDirectory)entr.getKey();
                        enc.writeObject(vd.getType());
                    }else{
                        enc.writeObject(VirtuaDirectory.TYPE_NONE);
                    }
                }
                enc.flush();
            }
            return true;
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(DefaultFavoritesHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private void readFavos(){
        try
        {
            try (XMLDecoder dec = new XMLDecoder(new FileInputStream(favoFile)))
            {
                while(true){
                    Object name = dec.readObject();
                    Object file = dec.readObject();
                    Object virType = dec.readObject();
                    File f = VirtuaDirectory.getFileByType((Integer)virType);
                    if(f == null){
                        f = new File(file.toString());
                    }
                    if(name == null)break;
                    this.favorites.put(f, name.toString());
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException ex){
            //no more objects
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(DefaultFavoritesHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void addFavorite(File f, String name)
    {
        createFavo(f, name);
        for(FavoritesChangedListener lis : listener){
            lis.favoriteAdded(f, name);
        }
    }

    @Override
    public void removeFavorite(File f)
    {
        if(favorites.remove(f) != null){
            for(FavoritesChangedListener lis : listener){
                lis.favoriteRemoved(f);
            }
            refreshFavos();
        }
    }

    @Override
    public HashMap<File, String> getFavorites()
    {
        return favorites;
    }

    @Override
    public void dispose()
    {
        refreshFavos();
    }

    @Override
    public void addFavoritesChangedListener(FavoritesChangedListener lis)
    {
        if(listener == null){
            listener = new ArrayList<>();
        }
        listener.add(lis);
    }

    @Override
    public void removeFavoritesChangedListener(FavoritesChangedListener lis)
    {
        if(listener == null){
            return;
        }
        listener.remove(lis);
    }
}
