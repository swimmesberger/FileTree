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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import org.fseek.simon.swing.filetree.dnd.interfaces.IFileDragDropSupport;
import org.fseek.thedeath.os.util.OSUtil;

public class FileDragGestureListener extends DragSourceAdapter implements DragGestureListener
{
    private Cursor cursor;

    private final IFileDragDropSupport fileSelection;
    

    public FileDragGestureListener(IFileDragDropSupport fileSelection)
    {
        this.fileSelection = fileSelection;
    }

    @Override
    public void dragGestureRecognized(DragGestureEvent evt)
    {
        File[] files = this.fileSelection.getSelectedFiles();
        if(files == null)return;
        if(files.length <= 0)
        {
            return;
        }
        Icon icn = OSUtil.getFileSystemView().getSystemIcon(files[0], true);
        if(icn == null){
            icn = OSUtil.getFileSystemView().getSystemIcon(files[0], false);
        }
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dim = tk.getBestCursorSize(icn.getIconWidth(), icn.getIconHeight());
        

        // set up drag image
        if (DragSource.isDragImageSupported())
        {
            BufferedImage buff = new BufferedImage(dim.width+100, dim.height, BufferedImage.TYPE_INT_ARGB);
            Graphics graphics = buff.getGraphics();
            Color color = graphics.getColor();
            icn.paintIcon(null, graphics, 0, 0);
            graphics.setColor(color);
            graphics.setColor(Color.RED);
            String elm = files.length == 1 ? "Element" : "Elements";
            graphics.drawString(files.length + " " + elm, icn.getIconWidth(), dim.height/2);
            try
            {
                evt.startDrag(DragSource.DefaultCopyDrop, buff, new Point(0, 0), new FileTransferable(files), this);
            } catch (IOException ex)
            {
                Logger.getLogger(FileDragGestureListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            BufferedImage buff = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
            Graphics graphics = buff.getGraphics();
            icn.paintIcon(null, graphics, 0, 0);
            try
            {
                cursor = tk.createCustomCursor(buff, new Point(0, 0), "tempcursor");
                evt.startDrag(cursor, null, new Point(0, 0), new FileTransferable(files), this);
            } catch (IOException ex)
            {
                Logger.getLogger(FileDragGestureListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void dragEnter(DragSourceDragEvent evt)
    {
        DragSourceContext ctx = evt.getDragSourceContext();
        ctx.setCursor(cursor);
    }

    @Override
    public void dragExit(DragSourceEvent evt)
    {
        DragSourceContext ctx = evt.getDragSourceContext();
        ctx.setCursor(DragSource.DefaultCopyNoDrop);
    }

    // drag and drop between 2 different windows, the FileTransferDialog only know about the drop TableModel not the drag TableModel for that case I need to remove the file from here
    @Override
    public void dragDropEnd(DragSourceDropEvent dsde)
    {
//        boolean dropSuccess = dsde.getDropSuccess();
//        if(dropSuccess)
//        {
//            if(dsde.getDropAction() == FileTransferHandler.MOVE)
//            {
//                for(MyFile file : files)
//                {
//                    updateModel(file, FileTransferDialog.MODEL_ACTION_REMOVE);
//                }
//            }
//        }
        super.dragDropEnd(dsde);
    }

}
