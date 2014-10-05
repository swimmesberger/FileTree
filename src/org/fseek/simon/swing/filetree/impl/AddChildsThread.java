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