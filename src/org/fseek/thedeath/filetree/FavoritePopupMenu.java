package org.fseek.thedeath.filetree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.fseek.thedeath.filetree.interfaces.FavoritesHandler;
import org.fseek.thedeath.filetree.interfaces.LinkTreeNode;
import org.fseek.thedeath.os.CachedFileSystemView;

public class FavoritePopupMenu extends JPopupMenu
{
    private FavoritesHandler handler;
    private LinkTreeNode clickedNode;
    public FavoritePopupMenu()
    {
        super();
    }
    
    public FavoritePopupMenu(FavoritesHandler handler, LinkTreeNode clickedNode)
    {
        super();
        this.handler = handler;
        this.clickedNode = clickedNode;
        createFavoriteMenu();
    }
    
    private void createFavoriteMenu()
    {
        if(handler == null)return;
        if(handler.getFavorites().containsKey(clickedNode.getLinkDir())){
            this.add(createRemoveFavorite());
        }else{
            this.add(createAddFavorite());
        }
    }
    
    private JMenuItem createAddFavorite()
    {
        JMenuItem addFavItem = new JMenuItem("Add to favorites");
        addFavItem.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                File f = clickedNode.getLinkDir();
                handler.addFavorite(f, CachedFileSystemView.getFileSystemView().getSystemDisplayName(f));
            }
        });
        return addFavItem;
    }
    
    private JMenuItem createRemoveFavorite()
    {
        JMenuItem remFavItem = new JMenuItem("Remove from favorites");
        remFavItem.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                handler.removeFavorite(clickedNode.getLinkDir());
            }
        });
        return remFavItem;
    }
    
    
}
