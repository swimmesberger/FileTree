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

import java.awt.Component;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.TransferHandler;
import org.fseek.simon.swing.filetree.dnd.FileDragGestureListener;
import org.fseek.simon.swing.filetree.dnd.FileTransferHandler;
import org.fseek.simon.swing.filetree.dnd.interfaces.IFileDragDropSupport;
import org.imgscalr.Scalr;

/**
 *
 * @author Simon Wimmesberger
 */
public class UtilBox extends org.fseek.thedeath.os.util.UtilBox{
    public static ImageIcon rescaleIconIfNeeded(ImageIcon systemIcon, int target)
    {
        if(scaleCache == null){
            scaleCache = new HashMap<>();
        }
        if(scaleCache.containsKey(systemIcon)){
            ImageIcon get = scaleCache.get(systemIcon);
            if(get.getIconHeight() == target || get.getIconWidth() == target){
                return get;
            }
        }
        BufferedImage scaledInstance = null;
        if(systemIcon.getIconHeight() != target && systemIcon.getIconWidth() != target)
        {
            BufferedImage img = org.fseek.thedeath.os.util.UtilBox.imageToBufferedImage(systemIcon.getImage());
            scaledInstance = Scalr.resize(img, target);
        }
        if(scaledInstance == null)
        {
            return systemIcon;
        }
        ImageIcon scaledIcon = new ImageIcon(scaledInstance);
        scaleCache.put(systemIcon, scaledIcon);
        return scaledIcon;
    }
    
    public static void initFileTransferActions(IFileDragDropSupport c){
        TransferHandler transferHandler = c.getTransferHandler();
        if(transferHandler instanceof FileTransferHandler == false){
            throw new IllegalArgumentException("Component TransferHandler needs to be a FileTransferHandler");
        }
        FileTransferHandler fileTransferhandler = (FileTransferHandler)transferHandler;
        ActionMap actionMap = c.getActionMap();
        actionMap.put(fileTransferhandler.getCutFileAction().getValue(Action.NAME), fileTransferhandler.getCutFileAction());
        actionMap.put(fileTransferhandler.getCopyFileAction().getValue(Action.NAME), fileTransferhandler.getCopyFileAction());
        actionMap.put(fileTransferhandler.getPasteFileAction().getValue(Action.NAME), fileTransferhandler.getPasteFileAction());
    }
    
    public static void initFileDnD(IFileDragDropSupport c){
        if(c instanceof Component == false){
            throw new IllegalArgumentException("IFileDragSupport must be a awt or swing component.");
        }
        c.setDragEnabled(true);
        c.setDropMode(DropMode.ON_OR_INSERT);
        FileTransferHandler fileTransferhandler = new FileTransferHandler(c);
        c.setTransferHandler(fileTransferhandler);
        DragSource ds = DragSource.getDefaultDragSource();
        ds.createDefaultDragGestureRecognizer((Component) c, DnDConstants.ACTION_COPY_OR_MOVE, new FileDragGestureListener(c));
    }
    
    public static void initFileTransfer(IFileDragDropSupport c){
        initFileDnD(c);
        initFileTransferActions(c);
    }
}
