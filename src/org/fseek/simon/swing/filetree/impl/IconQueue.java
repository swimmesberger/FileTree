package org.fseek.simon.swing.filetree.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import javax.swing.ImageIcon;
import org.fseek.simon.swing.filetree.interfaces.LinkTreeNode;
import org.fseek.thedeath.os.util.Debug;
import org.fseek.thedeath.os.CachedFileSystemView;

/**
 *
 * @author Thedeath<www.fseek.org>
 * 
 * Fast (and logical) implementation of retrieving icons for a certain node.
 * It simply works through all added nodes but also supports preferring a node (because it got expanded for example)
 */

public class IconQueue  extends Thread
{
   private static boolean simpleIcons = false;
   private static IconQueue runningThread;
   private static Stack<LinkTreeNode> priorityNodes = new Stack<>();
   private static HashMap<LinkTreeNode, IconEntry> mapCache = new HashMap<>();
    
   public static synchronized void addEntry(IconEntry entry){
        if(entry == null)return;
        if(runningThread == null){
            runningThread = new IconQueue();
            runningThread.start();
        }
        synchronized(mapCache){
            mapCache.put(entry.getNode(), entry);
            mapCache.notifyAll();
        }
    }

    public static void setSimpleIcons(boolean simpleIcons)
    {
        IconQueue.simpleIcons = simpleIcons;
    }

    public static boolean isSimpleIcons()
    {
        return simpleIcons;
    }

   public static void addPriorityNode(LinkTreeNode node){
       priorityNodes.push(node);
   }

    private LinkTreeNode priorityNode;
    public IconQueue()
    {
        super("IconQueue");
    }

    @Override
    public void run()
    {
        while(true){

            synchronized(mapCache){
                try
                {
                    if(mapCache.size() <= 0){
                    mapCache.wait();
                    }
                } catch (InterruptedException ex)
                {
                    break;
                }
            }
            if(priorityNodes.size() > 0 && priorityNode == null){
                priorityNode = priorityNodes.pop();
            }
            if(priorityNode != null){
                IconEntry get = mapCache.get(priorityNode);
                if(get != null){
                    mapCache.remove(priorityNode);
                    //mapCache.remove(priorityNode);
                    priorityNode = null;
                    doIt(get);
                    continue;
                }else{
                    //if the node can't be found in the cache just ignore it (seems a logical bug then)
                    //priorityNodes.add(0, priorityNode);
                    priorityNode = null;
                    continue;
                }
            }
            IconEntry poll;
            synchronized(mapCache){
                Iterator<Map.Entry<LinkTreeNode, IconEntry>> iterator = mapCache.entrySet().iterator();
                poll = iterator.next().getValue();
                iterator.remove();
            }          
            if(doIt(poll) == false){
                continue;
            }
        }
    }

    private boolean doIt(IconEntry entry){
        if(entry == null || entry.getFile() == null || entry.getNode() == null){
            return false;
        }
        CachedFileSystemView fileSystemView = CachedFileSystemView.getFileSystemView();
        ImageIcon icon = simpleIcons? fileSystemView.getSimpleIcon(entry.getFile()):fileSystemView.getSystemIcon(entry.getFile(), false);
        if(icon == null){
            //should normally not happen
            Debug.println("Something strange happened");
            return false;
        }
        entry.getNode().setIcon(icon);
        return true;
    }
}
