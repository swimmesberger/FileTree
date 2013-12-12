package org.fseek.simon.swing.filetree.impl;

import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.fseek.simon.swing.filetree.interfaces.LinkTreeNode;
import org.fseek.thedeath.os.util.Debug;
import org.fseek.simon.swing.util.TreeUtil;

public class AddChildsThread extends Thread
{ 
    private static HashMap<String, AddChildsThread> threads = new HashMap<>();
    
    private LinkTreeNode node;
    private boolean childToChild;
    private boolean showFiles;
    private boolean showHidden;
    private boolean fake;
    private DefaultTreeModel model;
    
    private AddChildsThread(DefaultTreeModel model,LinkTreeNode node, boolean childToChild, boolean showFiles, boolean showHidden, boolean fake)
    {
        super("AddChildsThread - " + node.getUserObject().toString());
        this.node = node;
        this.fake = fake;
        this.showFiles = showFiles;
        this.childToChild = childToChild;
        this.model = model;
        this.showHidden = showHidden;
    }
    
    public static AddChildsThread create(DefaultTreeModel model,LinkTreeNode node, boolean childToChild, boolean showFiles, boolean showHidden, boolean fake)
    {
        String s = new TreePath(node).toString() + childToChild;
        if(!threads.containsKey(s))
        {
            AddChildsThread t = new AddChildsThread(model, node, childToChild, showFiles, showHidden, fake);
            threads.put(s, t);
            return t;
        }
        return threads.get(s);
    }

    @Override
    public void run()
    {
        if(this.childToChild)
        {
            TreeUtil.addChildsToChilds(node, model, showFiles, showHidden, fake);
        }
        else
        {
            TreeUtil.addChilds(node, model, showFiles, showHidden, fake);
        }
        removeThread();
    }
    
    public void removeThread(){
        String s = new TreePath(node).toString() + childToChild;
        threads.remove(s);
    }

    public boolean isFake()
    {
        return fake;
    }

    public static ArrayList<AddChildsThread> getThreads()
    {
        if(threads.size() <= 0){
            return new ArrayList<>();
        }
        return new ArrayList<>(threads.values());
    }
    
    @Override
    public synchronized void start()
    {
        try{
        super.start();
        }catch(Exception ex){
            Debug.printException(ex);
        }
    }
}