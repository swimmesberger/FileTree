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
package org.fseek.simon.swing.filetree.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import org.apache.commons.io.FileUtils;
import org.fseek.simon.swing.filetree.dnd.interfaces.IFileDragDropSupport;
import sun.awt.datatransfer.TransferableProxy;

public class FileTransferHandler extends TransferHandler
{    
    private FileTransferAction cutAction;
    private FileTransferAction copyAction;
    private FileTransferAction pasteAction;

    private static DataFlavor urlFlavor;
    private static DataFlavor uriList;
    
    public static DataFlavor[] supportedFlavors = null;
    
    static
    {
        try
        {
            urlFlavor = new DataFlavor("application/x-java-url; class=java.net.URL");
            uriList = new DataFlavor("text/uri-list; class=java.lang.String; charset=Unicode");
            supportedFlavors = new DataFlavor[]
            {
                urlFlavor, 
                DataFlavor.javaFileListFlavor, 
                uriList
            };
        } catch (ClassNotFoundException ex)
        {
            Logger.getLogger(FileTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private IFileDragDropSupport hasFile;
    public FileTransferHandler(IFileDragDropSupport hasFile)
    {
        cutAction = new FileTransferAction(hasFile, "cut");
        copyAction = new FileTransferAction(hasFile, "copy");
        pasteAction = new FileTransferAction(hasFile, "paste");
        this.hasFile = hasFile;
    }
   
    @Override
    public boolean canImport(TransferSupport support)
    {
        DataFlavor[] dataFlavors = support.getDataFlavors();
        for (DataFlavor f : supportedFlavors)
        {
            String suppMime = f.getMimeType();
            for(DataFlavor dataFlavor : dataFlavors){
                if(dataFlavor == null)continue;;
                if(dataFlavor.getMimeType().equals(suppMime)){
                    return this.hasFile.canImport(support);
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean importData(TransferSupport support)
    {
        Transferable transferable = support.getTransferable();
        // didnt find other solution to get importData known about the action from copy/paste - clipboard
        if(transferable instanceof TransferableProxy)
        {
            try
            {
                TransferableProxy fileTrans = (TransferableProxy)transferable;
                Field privateStringField = TransferableProxy.class.getDeclaredField("transferable");
                privateStringField.setAccessible(true);
                FileTransferable fieldValue = (FileTransferable) privateStringField.get(fileTrans);
                return importData(support, fieldValue.getAction());
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ex)
            {
                Logger.getLogger(FileTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //TODO: Java doesn't support cut operations via Clipboard, so we have to assume COPY if the source is the Clipboard
        int dropAction = TransferHandler.COPY;
        if(support.isDrop()){
            dropAction = support.getDropAction();
        }
        return importData(support, dropAction);
    }

    public boolean importData(TransferSupport support, int action)
    {
        Transferable transferable = support.getTransferable();
        DataFlavor[] dataFlavors = support.getDataFlavors();
        File[] transferData = null;
        //check if we support one of the data flavors
        for(DataFlavor dataFlavor : dataFlavors){
            String mimeType = dataFlavor.getMimeType();
            if(mimeType.equals(urlFlavor.getMimeType())){
                transferData = getFromURL(transferable);
            }else if(mimeType.equals(DataFlavor.javaFileListFlavor.getMimeType())){
                transferData = getFile(transferable);
            }else if(mimeType.equals(uriList.getMimeType())){
                transferData = getFromFileString(transferable);
            }
            if(transferData != null){
                return hasFile.importFile(transferData, action, support.getDropLocation());
            }
        }
        if(transferData == null){
            Logger.getLogger(FileTransferHandler.class.getName()).log(Level.INFO, "Unsupported flavor: {0}", dataFlavors[0].getMimeType());
        }
        return false;
    }

    @Override
    public int getSourceActions(JComponent c)
    {
        return COPY_OR_MOVE;
    }
    
    public FileTransferAction getCutFileAction() {
        return cutAction;
    }

    public FileTransferAction getCopyFileAction() {
        return copyAction;
    }

    public FileTransferAction getPasteFileAction() {
        return pasteAction;
    }

    private File[] getFile(Transferable transferable)
    {
        try
        {
            Collection col = (Collection) transferable.getTransferData(DataFlavor.javaFileListFlavor);
            ArrayList arrList = new ArrayList(col);
            return (File[]) arrList.toArray(new File[arrList.size()]);
        } catch (UnsupportedFlavorException | IOException ex)
        {
            //Logger.getLogger(FileTransferhandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private File[] getFromURL(Transferable transferable)
    {
        try
        {
            java.net.URL url = (java.net.URL) transferable.getTransferData(urlFlavor);
            File tmpFile = new File(FileUtils.getTempDirectory(), url.getFile());
            FileUtils.copyURLToFile(url, tmpFile);
            return new File[]{tmpFile};
        } catch (UnsupportedFlavorException | IOException ex)
        {
            //Logger.getLogger(FileTransferhandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static java.util.List textURIListToFileList(String data)
    {
        java.util.List list = new java.util.ArrayList(1);
        for (java.util.StringTokenizer st = new java.util.StringTokenizer(data, "\r\n");
        st.hasMoreTokens();)
        {
            String s = st.nextToken();
            if (s.startsWith("#"))
            {
                // the line is a comment (as per the RFC 2483)
                continue;
            }
            try
            {
                java.net.URI uri = new java.net.URI(s);
                java.io.File file = new java.io.File(uri);
                list.add(file);
            } catch (java.net.URISyntaxException e)
            {
                // malformed URI
            } catch (IllegalArgumentException e)
            {
                // the URI is not a valid 'file:' URI
            }
        }
        return list;
    }

    private File[] getFromFileString(Transferable transferable)
    {
        try
        {
            String str = (String) transferable.getTransferData(uriList);
            List textURIListToFileList = textURIListToFileList(str);
            return (File[]) textURIListToFileList.toArray(new File[textURIListToFileList.size()]);
        } catch (UnsupportedFlavorException | IndexOutOfBoundsException | IOException ex)
        {
            //ignore not supported
        }
        return null;
    }
}
