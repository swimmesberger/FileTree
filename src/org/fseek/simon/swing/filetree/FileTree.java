package org.fseek.simon.swing.filetree;

import java.awt.EventQueue;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import org.fseek.simon.swing.filetree.dnd.interfaces.IFileDragDropSupport;
import org.fseek.simon.swing.filetree.impl.AddChildsThread;
import org.fseek.simon.swing.filetree.impl.DefaultFavoritesHandler;
import org.fseek.simon.swing.filetree.impl.DefaultLinkTreeNode;
import org.fseek.simon.swing.filetree.impl.IconQueue;
import org.fseek.simon.swing.filetree.interfaces.FavoritesChangedListener;
import org.fseek.simon.swing.filetree.interfaces.FavoritesHandler;
import org.fseek.simon.swing.filetree.interfaces.FileListener;
import org.fseek.simon.swing.filetree.interfaces.IconChangedListener;
import org.fseek.simon.swing.filetree.interfaces.LinkTreeNode;
import org.fseek.simon.swing.ui.FileTreePopupMenu;
import org.fseek.simon.swing.ui.HideRowTreeUI;
import org.fseek.simon.swing.ui.IconTreeCellRenderer;
import org.fseek.simon.swing.util.UtilBox;
import org.fseek.thedeath.os.VirtuaDirectory;
import org.fseek.thedeath.os.interfaces.IFileSystem;
import org.fseek.thedeath.os.interfaces.IOSIcons;
import org.fseek.thedeath.os.util.Debug;
import org.fseek.thedeath.os.util.FileSystemUtil;
import org.fseek.thedeath.os.util.OSUtil;

public class FileTree extends JTree implements IFileDragDropSupport
{  
    private boolean finishedBuildTree = false;
    private boolean showFiles = true;
    private boolean showHidden = true;
    private boolean simpleIcons = false;
    
    private ArrayList<FileListener> selectionListener;
    private ArrayList<FileListener> clickListener;
    private FavoritesHandler favoriteHandler;
    
    //important nodes
    private DefaultLinkTreeNode biblioNode;
    private DefaultLinkTreeNode favoNode;
    private DefaultLinkTreeNode computerNode;
    
    private File selectedFile;
    
    private IconChangedListener iconChanged;

    public FileTree() {
        this(true, true);
    }

    public FileTree(boolean showFiles, boolean showHidden)
    {
        super();
        this.showFiles = showFiles;
        this.showHidden = showHidden;
        init();
        initDefaults();
        withoutModelCreation();
    }
    
    public FileTree(File folder){
        this(folder, true, true);
    }
    
    public FileTree(File folder, boolean showFiles, boolean showHidden){
        super();
        this.showFiles = showFiles;
        this.showHidden = showHidden;
        try
        {
            init();
            LinkTreeNode node = new DefaultLinkTreeNode(folder, OSUtil.getFileSystemView().getSystemDisplayName(folder.getCanonicalFile()), iconChanged);
            MutableTreeNode root = new DefaultMutableTreeNode();
            DefaultTreeModel model = new DefaultTreeModel(root);
            model.insertNodeInto(node, root, 0);
            withModelCreation(model);
            loadNode(node);
        } catch (IOException ex)
        {
            Logger.getLogger(FileTree.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void init(){
        setSimpleIcons(false);
        iconChanged = new FileTreeIconChangedListener((DefaultTreeModel)getModel());
        UtilBox.initFileTransfer(this);
    }
    
    private void initDefaults(){
        setFavoriteHandler(new DefaultFavoritesHandler());
    }

    private void withoutModelCreation()
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        DefaultTreeModel defaultTreeModel = new DefaultTreeModel(root);
        setModel(defaultTreeModel);
        intTree(root,defaultTreeModel, true);
    }
    
    private void withModelCreation(DefaultTreeModel model)
    {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
        intTree(root,model, false);
    }
    

    
    public void updateComputerNode()
    {
        DefaultTreeModel model = (DefaultTreeModel)this.getModel();
        model.nodeChanged(getComputerNode());
        this.expandPath(new TreePath(getComputerNode().getPath()));
    }
    
    private void initComputerNode(){
         for(File f : File.listRoots())
        {
            addFileToComputer(f);
        }
    }
    
    public void addFileToComputer(File f)
    {
        DefaultLinkTreeNode linkTreeNode = new DefaultLinkTreeNode(f, OSUtil.getFileSystemView().getSystemDisplayName(f), iconChanged);
        loadNewNode(linkTreeNode);
    }
    
    private void loadNewNode(LinkTreeNode node){
        DefaultTreeModel model = (DefaultTreeModel)getModel();
        model.insertNodeInto(node, computerNode, computerNode.getChildCount());
        loadNode(node);
    }
    
    private void loadNode(LinkTreeNode node){
        DefaultMutableTreeNode trNode = (DefaultMutableTreeNode)node;
        boolean expanded = !isExpanded(new TreePath(trNode.getPath()));
        loadNode(node, false, expanded);
    }
    
    private void loadNode(LinkTreeNode node, boolean childToChild, boolean fake){
       AddChildsThread t = AddChildsThread.create((DefaultTreeModel)getModel(),node, childToChild, showFiles, showHidden, fake);
       if(t.isAlive() == false){
           t.start();
       }else if(t.isFake() != fake){
           try
           {
               t.interrupt();
               t.join();
               loadNode(node, childToChild, fake);
           } catch (InterruptedException ex)
           {
               Logger.getLogger(FileTree.class.getName()).log(Level.SEVERE, null, ex);
           }
           
       }
    }
    
    private void folderTreeValueChanged(TreeSelectionEvent evt)
    {
        if(this.getLastSelectedPathComponent() instanceof LinkTreeNode){
            LinkTreeNode node = (LinkTreeNode)this.getLastSelectedPathComponent();
            if(node != null)
            {
                File linkDir = node.getLinkDir();
                fileSelectionChanged(linkDir, evt);
            }
        }
    }
    
    private void fileSelectionChanged(File f, TreeSelectionEvent evt){
        if(selectionListener == null)return;
        this.selectedFile = f;
        for(FileListener lis : selectionListener){
            lis.fileChanged(f, evt);
        }
    }
    
    private void nodeClicked(File f, MouseEvent ev){
        if(clickListener == null)return;
        for(FileListener lis : clickListener){
            lis.fileChanged(f, ev);
        }
    }
    
    private void folderTreeExpanded(TreeExpansionEvent event)
    {
        TreePath path = event.getPath();
        if(path.getLastPathComponent() instanceof LinkTreeNode && path.getLastPathComponent() != getBiblioNode()){
            LinkTreeNode node = (LinkTreeNode)path.getLastPathComponent();
            if(node.getParent() == null){
                DefaultTreeModel model = (DefaultTreeModel)this.getModel();
                Debug.println("Node hovering nowhere, try to find the right one..");
                LinkTreeNode find = find((DefaultMutableTreeNode) model.getRoot(), node.getLinkDir());
                if(find == null){
                        Debug.println("No node found...");
                    return;
                }
                node = find;
            }
            node.setHighPriority();
            loadNode(node, false, false);
        }
    }
    
    private LinkTreeNode find(DefaultMutableTreeNode root, File f) {
        Enumeration<DefaultMutableTreeNode> e = root.depthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode nextElement = e.nextElement();
            if(nextElement instanceof LinkTreeNode){
                LinkTreeNode linkNode = (LinkTreeNode)nextElement;
                if (linkNode.getLinkDir().getAbsolutePath().equals(f.getAbsolutePath())) {
                    return linkNode;
                }
            }
        }
        return null;
    }
    
    
    private void folderTreeCollapsed(TreeExpansionEvent event)
    {
        TreePath path = event.getPath();
        if(path.getLastPathComponent() instanceof LinkTreeNode && path.getLastPathComponent() != getBiblioNode()){
            DefaultLinkTreeNode node = (DefaultLinkTreeNode)path.getLastPathComponent();
            cleanNode(node);
        }
    }
    
    /*
     * Maybe remove all child nodes so memory is saved
     */
    private void cleanNode(LinkTreeNode node){
        DefaultTreeModel model = (DefaultTreeModel)getModel();
        AddChildsThread create = AddChildsThread.create(model, node, false, showFiles, showHidden, true);
        if(create.isAlive() == false){
            create.start();
        }else if(create.isFake() == false){
            try
            {
                create.interrupt();
                create.join();
                cleanNode(node);
            } catch (InterruptedException ex)
            {
                Logger.getLogger(FileTree.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void maybeShowPopup(MouseEvent e)
    {
        if (e.isPopupTrigger())
        {
            TreePath selectionPath = this.getPathForLocation(e.getX(), e.getY());
            if(selectionPath == null)return;
            LinkTreeNode lastPathComponent = (LinkTreeNode)selectionPath.getLastPathComponent();
            this.setSelectionPath(selectionPath);
            showPopup(lastPathComponent, e);
        }
    }
    
    /*
     * Can be used to show a popup for the rightclicked node
     */
    protected void showPopup(LinkTreeNode node, MouseEvent e){
        FileTreePopupMenu menu = new FileTreePopupMenu(getFavoriteHandler(), node);
        menu.show(e.getComponent(), e.getX(), e.getY());
    }
    
    
    private void intTree(DefaultMutableTreeNode root, DefaultTreeModel model, boolean intModel)
    {
        intTreeActions();
        if(intModel == true)
        {
            intTreeModel(root, model);
        }
        else
        {
            this.setModel(model);
        }
        finishedBuildTree = true;
        setRootVisible(false);
        intTreeUI();
        finishTree();
    }
    
    private void finishTree()
    {
        if(getBiblioNode() != null)
            expandPath(new TreePath(getBiblioNode().getPath()));
        if(getFavoNode() != null)
            expandPath(new TreePath(getFavoNode().getPath()));
    }
    
    private void intTreeActions()
    {
        addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() 
        {
            @Override
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) 
            {
                folderTreeValueChanged(evt);
            }
        });
        addTreeExpansionListener(new TreeExpansionListener()
        {
            @Override
            public void treeExpanded(TreeExpansionEvent event)
            {
                folderTreeExpanded(event);
            }

            @Override
            public void treeCollapsed(TreeExpansionEvent event)
            {
                folderTreeCollapsed(event);
            }
        });
        addMouseListener(new MouseAdapter() 
        {

            @Override
            public void mousePressed(MouseEvent e)
            {
                maybeShowPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                maybeShowPopup(e);
            }
            
            @Override
            public void mouseClicked(MouseEvent e)
            {
                TreePath tp = FileTree.this.getPathForLocation(e.getX(), e.getY());
                if(tp == null)return;
                Object lastPathComponent = tp.getLastPathComponent();
                if(lastPathComponent instanceof LinkTreeNode){
                    LinkTreeNode node = (LinkTreeNode)lastPathComponent;
                    if(node.getLinkDir() != null && node.getLinkDir() instanceof VirtuaDirectory == false)
                        nodeClicked(node.getLinkDir(), e);
                }
            }
        });
    }
    
    private void intTreeModel(DefaultMutableTreeNode root, DefaultTreeModel model)
    {
        IOSIcons osIcons = OSUtil.getOsIcons();
        initFavoNode(root);
        // creating Bibliothek
        IFileSystem fileSystem = FileSystemUtil.getFileSystem();
        biblioNode = new DefaultLinkTreeNode(fileSystem.getHomeFolder(),osIcons.getLibraryIcon(), "Home", OSUtil.getOsColors().getTreeFontColor(), OSUtil.getOsColors().isTreeFontToUpperCase());
       
        // picture node
        addToLibraryIfExist(model, fileSystem.getImageFolder(), osIcons.getPictureIcon());
        
        // document node
        addToLibraryIfExist(model, fileSystem.getDocumentsFolder(), osIcons.getDocumentIcon());
        
        // music node
        addToLibraryIfExist(model, fileSystem.getMusicFolder(), osIcons.getMusicIcon());
        
        // videos node
        addToLibraryIfExist(model, fileSystem.getVideosFolder(), osIcons.getVideoIcon());
        
        if(biblioNode.getChildCount() > 0){
            model.insertNodeInto(getBiblioNode(), root, root.getChildCount());
        }
        // end creating Bibliothek
       
        // creating Computer node
        setComputerNode(new DefaultLinkTreeNode(OSUtil.getOsIcons().getComputerIcon(), "Computer", OSUtil.getOsColors().getTreeFontColor(), OSUtil.getOsColors().isTreeFontToUpperCase()));
        model.insertNodeInto(getComputerNode(), root, root.getChildCount());
        initComputerNode();
        //end creating computer node
        loadNode(getComputerNode());
        loadNode(getBiblioNode(), true, true);
        setModel(model);
    }
    
    protected void addToLibraryIfExist(DefaultTreeModel model, File f){
        addToLibraryIfExist(model, f, OSUtil.getFileSystemView().getSystemIcon(f, false));
    }
    
    protected void addToLibraryIfExist(DefaultTreeModel model, File f, ImageIcon icon){
        if(f.exists() == false)return;
        DefaultLinkTreeNode node = new DefaultLinkTreeNode(f, icon);
        if(node.getLinkDir().exists())
        {
            model.insertNodeInto(node, getBiblioNode(), getBiblioNode().getChildCount());
        }
    }

    /**
     * @return the computerNode
     */
    public DefaultLinkTreeNode getComputerNode()
    {
        return computerNode;
    }

    /**
     * @param computerNode the computerNode to set
     */
    public void setComputerNode(DefaultLinkTreeNode computerNode)
    {
        this.computerNode = computerNode;
    }
    
    private void intTreeUI()
    {
        setBackground(OSUtil.getOsColors().getTreePanelColor());
        HideRowTreeUI ownUi = new HideRowTreeUI();
        setUI(ownUi);
        ownUi.setCollapsedIcon(OSUtil.getOsIcons().getUncollapsedIcon());
        ownUi.setExpandedIcon(OSUtil.getOsIcons().getCollapsedIcon());
        DefaultTreeCellRenderer renderer = new IconTreeCellRenderer();
        setCellRenderer(renderer);
        setShowsRootHandles(true);
        putClientProperty("JTree.lineStyle", "None");
        setRowHeight(20);
        repaint();
    }
    
    public static void initUIManager(){
        UIManager.put("Tree.hash", OSUtil.getOsColors().getTreePanelColor());
    }
    
    /**
     * @return the finishedBuildTree
     */
    public boolean isFinishedBuildTree()
    {
        return finishedBuildTree;
    }

    /**
     * @return the biblioNode
     */
    public DefaultLinkTreeNode getBiblioNode()
    {
        return biblioNode;
    }

    /**
     * @return the favoNode
     */
    public DefaultLinkTreeNode getFavoNode()
    {
        return favoNode;
    }
    
    public void addFavorite(File link)
    {
        if(favoriteHandler != null){
            favoriteHandler.addFavorite(link, OSUtil.getFileSystemView().getSystemDisplayName(link));
        }
    }
    
    public void removeFavorite(File f)
    {
        if(favoriteHandler != null){
            favoriteHandler.removeFavorite(f);
        }
    }
    
    public void initFavorites(){
        if(favoriteHandler != null){
            favoriteHandler.initFavorites();
            HashMap<File, String> favorites = getFavorites();
            for (Iterator<Map.Entry<File, String>> it = favorites.entrySet().iterator(); it.hasNext();)
            {
                Map.Entry<File, String> entr = it.next();
                addFavoriteImpl(entr.getKey(), entr.getValue());
            }
            favoriteHandler.addFavoritesChangedListener(new FavoritesChangedListener() {
                @Override
                public void favoriteAdded(File f, String name)
                {
                    addFavoriteImpl(f, name);
                }

                @Override
                public void favoriteRemoved(File f)
                {
                    removeFavoriteImpl(f);
                }
            });
        }
    }

    private void initFavoNode(DefaultMutableTreeNode root){
        if(favoriteHandler != null){
            DefaultTreeModel model = (DefaultTreeModel)getModel();
            // creating Favorite node
            favoNode = new DefaultLinkTreeNode(OSUtil.getOsIcons().getStarIcon(), "Favoriten", OSUtil.getOsColors().getTreeFontColor(), OSUtil.getOsColors().isTreeFontToUpperCase());
            model.insertNodeInto(favoNode, root, 0);
            initFavorites();
            // end creating favorite node
        }
    }
    
    private void addFavoriteImpl(File f, String name){
        DefaultTreeModel model = (DefaultTreeModel)getModel();
        LinkTreeNode node = new DefaultLinkTreeNode(f, name, iconChanged);
        model.insertNodeInto(node, getFavoNode(), getFavoNode().getChildCount());
        loadNode(node);
    }
    
    private void removeFavoriteImpl(File f){
        DefaultTreeModel model = (DefaultTreeModel)getModel();
        for(int i = 0; i<getFavoNode().getChildCount(); i++){
            LinkTreeNode node = (LinkTreeNode)getFavoNode().getChildAt(i);
            if(node.getLinkDir().getAbsolutePath().equals(f.getAbsolutePath())){
                model.removeNodeFromParent(node);
            }
        }
    }
    
    public HashMap<File, String> getFavorites(){
        if(favoriteHandler == null){
            return null;
        }
        return favoriteHandler.getFavorites();
    }
    
    public void cleanup(){
        ArrayList<AddChildsThread> threads = AddChildsThread.getThreads();
        for(int i = 0; i<threads.size(); i++){
            AddChildsThread t = threads.get(i);
            t.interrupt();
            threads.remove(t);
        }
        if(favoriteHandler != null){
            favoriteHandler.dispose();
        }
    }
    
    
    public void addFileSelectionChangedListener(FileListener lis){
        if(selectionListener == null){
            selectionListener = new ArrayList<>();
        }
        selectionListener.add(lis);
    }
    
    public void removeFileSelectionChangedListener(FileListener lis){
        if(selectionListener == null){
            return;
        }
        selectionListener.remove(lis);
    }
    
    public void addFileClickListener(FileListener lis){
        if(clickListener == null){
            clickListener = new ArrayList<>();
        }
        clickListener.add(lis);
    }
    
    public void removeFileClickListener(FileListener lis){
        if(clickListener == null){
            return;
        }
        clickListener.remove(lis);
    }  

    public void setFavoriteHandler(FavoritesHandler favoriteHandler)
    {
        if(finishedBuildTree && this.favoriteHandler != favoriteHandler){
            DefaultTreeModel model = (DefaultTreeModel)getModel();
            if(favoriteHandler == null){
                model.removeNodeFromParent(getFavoNode());
            }else{
                initFavoNode((DefaultMutableTreeNode)model.getRoot());
            }
        }else{
            this.favoriteHandler = favoriteHandler;
        }
    }

    public FavoritesHandler getFavoriteHandler()
    {
        return favoriteHandler;
    }

    public File getSelectedFile()
    {
        return selectedFile;
    }

    public void setSimpleIcons(boolean simpleIcons)
    {
        this.simpleIcons = simpleIcons;
        IconQueue.setSimpleIcons(simpleIcons);
    }

    @Override
    public boolean importFile(File[] file, int action) {
        for(File f : file){
            Debug.println("Import file: " + f.getAbsolutePath());
        }
        return true;
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        support.setShowDropLocation(true);
        return true;
    }

    @Override
    public File[] getSelectedFiles() {
        TreePath[] selectionPaths = this.getSelectionPaths();
        File[] selFiles = new File[selectionPaths.length];
        for(int i = 0; i<selFiles.length; i++){
            Object lastPath  = selectionPaths[i].getLastPathComponent();
            if(lastPath instanceof LinkTreeNode){
                LinkTreeNode node = (LinkTreeNode)lastPath;
                selFiles[i] = node.getLinkDir();
            }
        }
        return selFiles;
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        //clipboard.setContents(null, this);
    }
    
    private static class FileTreeIconChangedListener implements IconChangedListener {
        private static final long serialVersionUID = 42L;
        private DefaultTreeModel model;
        public FileTreeIconChangedListener(DefaultTreeModel model)
        {
            this.model = model;
        }
        
        @Override
        public void iconChanged(final LinkTreeNode node, Icon newIcon)
        {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run()
                {
                    model.nodeChanged(node);
                }
            });
        }
    };
}