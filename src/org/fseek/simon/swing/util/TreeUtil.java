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
package org.fseek.simon.swing.util;

import org.fseek.thedeath.os.util.Debug;
import java.awt.EventQueue;
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import org.apache.commons.io.comparator.DirectoryFileComparator;
import org.fseek.simon.swing.filetree.impl.AddChildsThread;
import org.fseek.simon.swing.filetree.impl.DefaultLinkTreeNode;
import org.fseek.simon.swing.filetree.impl.TreeFileFilter;
import org.fseek.simon.swing.filetree.interfaces.IconChangedListener;
import org.fseek.simon.swing.filetree.interfaces.LinkTreeNode;
/*
 * This class definetly needs rework
 * #dirty
 */
public class TreeUtil
{
    public static void addChilds(final LinkTreeNode child, final DefaultTreeModel model, boolean showFiles,  boolean showHidden, boolean fake)
    {
        
        if(child != null)
        {
            File linkDir = child.getLinkDir();
            if(linkDir == null)return;
            if(linkDir.canRead())
            {
                File[] listFiles = linkDir.listFiles((FileFilter)new TreeFileFilter(showHidden, showFiles));
                if(listFiles != null)
                {
                    final int length = listFiles.length;
                    if(fake){
                        EventQueue.invokeLater(new Runnable() { 
                           @Override
                           public void run() {
                               if(length > 0){
                                   fakeNode(child, model);
                               }
                           }
                         });
                        return;
                    }
                    final DefaultMutableTreeNode clear = clear(child);
                    if(clear == null && child.getChildCount() > 0){
                        return;
                    }
                    deleteAllChilds(child, model, clear);
                    if(Thread.interrupted()){
                        return;
                    }
                    Arrays.sort(listFiles, DirectoryFileComparator.DIRECTORY_COMPARATOR);
                    Debug.println("Filling node with real data: " + child.getUserObject().toString());
                    addFiles(listFiles, child, model, showFiles, showHidden);
                    if(clear != null){
                        EventQueue.invokeLater(new Runnable() { 
                          @Override
                          public void run() {
                             model.removeNodeFromParent(clear);
                          }
                        });
                    }
                }
            }
        }
    }
    
    //clear fake nodes
    public static DefaultMutableTreeNode clear(LinkTreeNode node){
        
        for(int i = 0; i<node.getChildCount(); i++)
        {
            TreeNode childAt = node.getChildAt(i);
            if(childAt instanceof DefaultMutableTreeNode){
                final DefaultMutableTreeNode child = (DefaultMutableTreeNode)childAt;
                if(child.getUserObject() == null || child.getUserObject().toString().length() <= 0){
                    return child;
                }
            }
        }
        return null;
    }
    
    public static void deleteAllChilds(final LinkTreeNode node, final DefaultTreeModel model, DefaultMutableTreeNode clearNode){
        for(int i = 0; i<node.getChildCount(); i++){
            if(Thread.interrupted()){
                break;
            }
            final MutableTreeNode childAt = (MutableTreeNode) node.getChildAt(i);
            if(childAt != null && clearNode != null && clearNode != childAt){
                EventQueue.invokeLater(new Runnable() { 
                    @Override
                    public void run(){
                        model.removeNodeFromParent(childAt);
                    }
                });
            }
        }
    }
    
    private static void fakeNode(LinkTreeNode node, DefaultTreeModel model){
        Debug.println("Faking node: " + node.getUserObject().toString());
        deleteAllChilds(node, model, null);
        if(Thread.interrupted()){
            return;
        }
        model.insertNodeInto(new DefaultMutableTreeNode(), node, 0);
    }
    
    private static void addFiles(File[] files, final LinkTreeNode root, final DefaultTreeModel model, boolean showFiles, boolean showHidden){
        for(int i = 0; i<files.length; i++)
        {
            if(Thread.interrupted()){
                break;
            }
            File f = files[i];
            if(f.isDirectory() || showFiles == true)
            {
                final LinkTreeNode childNode = new DefaultLinkTreeNode(f, new IconChangedListener() {
                    @Override
                    public void iconChanged(final LinkTreeNode node, Icon newIcon)
                    {
                        EventQueue.invokeLater(new Runnable() { 
                            @Override
                            public void run() {
                               model.nodeChanged(node);
                            }
                        });
                    }
                });
                final int index = i;
                EventQueue.invokeLater(new Runnable() { 
                  @Override
                  public void run() {
                     model.insertNodeInto(childNode, root, index);
                  }
                });
                AddChildsThread create = AddChildsThread.create(model, childNode, false, showFiles, showHidden, true);
                if(create.isAlive() && create.isFake() == false){
                    create.interrupt();
                    try
                    {
                        create.join();
                        create = AddChildsThread.create(model, childNode, false, showFiles, showHidden, true);
                        create.start();
                    } catch (InterruptedException ex)
                    {
                        return;
                    }
                }else if(create.isAlive() == false){
                    create.start();
                }
            }
        }
    }
    
    public static void addChildsToChilds(LinkTreeNode node, DefaultTreeModel model, boolean showFiles, boolean showHidden, boolean fake)
    {
        for(int i = 0; i<node.getChildCount(); i++)
        {
            LinkTreeNode child = (LinkTreeNode)node.getChildAt(i);
            if(child.getChildCount() <= 0)
            {
                addChilds(child, model, showFiles, showHidden, fake);
            }
        }
    }
}
