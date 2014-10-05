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
package org.fseek.simon.swing.filetree.ui;

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
        createPropertiesMenu();
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
    
    private void createPropertiesMenu(){
        this.add(createProperty());
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
    
    private JMenuItem createProperty(){
        JMenuItem openProperty = new JMenuItem("Properties");
        openProperty.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                File f = clickedNode.getLinkDir();
                PropertiesDialog dia = new PropertiesDialog(f);
                dia.setVisible(true);
            }
        });
        return openProperty;
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
