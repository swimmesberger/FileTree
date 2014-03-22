package org.fseek.simon.swing.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import org.fseek.simon.swing.filetree.interfaces.FavoritesHandler;
import org.fseek.simon.swing.filetree.interfaces.LinkTreeNode;
import org.fseek.thedeath.os.util.OSUtil;

public class FileTreePopupMenu extends JPopupMenu
{
    private FavoritesHandler handler;
    private LinkTreeNode clickedNode;
    public FileTreePopupMenu()
    {
        super();
    }
    
    public FileTreePopupMenu(FavoritesHandler handler, LinkTreeNode clickedNode)
    {
        super();
        this.handler = handler;
        this.clickedNode = clickedNode;
        createFavoriteMenu();
        createOpenMenu();
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
    
    private void createOpenMenu(){
        File f = clickedNode.getLinkDir();
        if(f != null && f.isFile()){
            this.add(createOpen());
        }
    }
    
    private JMenuItem createOpen(){
        JMenuItem openFile = new JMenuItem("Open File");
        openFile.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                File f = clickedNode.getLinkDir();
                try{
                    OSUtil.openFile(f);
                }catch(UnsupportedOperationException ex){
                    JOptionPane.showMessageDialog(FileTreePopupMenu.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        return openFile;
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
                handler.addFavorite(f, OSUtil.getFileSystemView().getSystemDisplayName(f));
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
