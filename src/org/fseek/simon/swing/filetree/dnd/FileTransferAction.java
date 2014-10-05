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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.UIManager;
import org.fseek.simon.swing.filetree.dnd.interfaces.IFileDragDropSupport;
import sun.swing.UIAction;

public class FileTransferAction extends UIAction {

    private IFileDragDropSupport selection;

    public FileTransferAction(IFileDragDropSupport selection, String name) {
        super(name);
        this.selection = selection;
    }

    public FileTransferAction(String name) {
        super(name);
    }

    private File[] getFiles() {
        return selection.getSelectedFiles();
    }

    public void setClipboardContents(File[] temp) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            clipboard.setContents(new FileTransferable(temp), selection);
        } catch (IOException ex) {
            Logger.getLogger(FileTransferAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean isEnabled(Object sender) {
        if (sender instanceof JComponent
                && ((JComponent) sender).getTransferHandler() == null) {
            return false;
        }

        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src instanceof JComponent) {
            JComponent c = (JComponent) src;
            TransferHandler th = c.getTransferHandler();
            Clipboard clipboard = getClipboard(c);
            String name = (String) getValue(Action.NAME);
            Transferable trans = null;
            if (!"paste".equals(name)) {
                File[] files = getFiles();
                if (files == null) {
                    return;
                }
                try {
                    trans = new FileTransferable(files);
                } catch (IOException ex) {
                    Logger.getLogger(FileTransferAction.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            // any of these calls may throw IllegalStateException
            try {
                if ((clipboard != null) && (th != null) && (name != null)) {
                    if ("cut".equals(name)) {
                        FileTransferable fileTrans = (FileTransferable) trans;
                        fileTrans.setAction(TransferHandler.MOVE);
                        clipboard.setContents(trans, selection);
                        th.exportToClipboard(c, clipboard, TransferHandler.MOVE);
                        return;
                    } else if ("copy".equals(name)) {
                        FileTransferable fileTrans = (FileTransferable) trans;
                        fileTrans.setAction(TransferHandler.COPY);
                        clipboard.setContents(trans, selection);
                        th.exportToClipboard(c, clipboard, TransferHandler.COPY);
                        return;
                    } else if ("paste".equals(name)) {
                        trans = clipboard.getContents(null);
                    }
                }
            } catch (IllegalStateException ise) {
                // clipboard was unavailable
                UIManager.getLookAndFeel().provideErrorFeedback(c);
                return;
            }

            // this is a paste action, import data into the component
            if (trans != null) {
                if (trans instanceof FileTransferable && th instanceof FileTransferHandler) {
                    FileTransferable fileTrans = (FileTransferable) trans;
                    FileTransferHandler fileTh = (FileTransferHandler) th;
                    fileTh.importData(new TransferSupport(c, fileTrans), fileTrans.getAction());
                } else {
                    th.importData(new TransferSupport(c, trans));
                }
            }
        }
    }

    /**
     * Returns the clipboard to use for cut/copy/paste.
     */
    private Clipboard getClipboard(JComponent c) {
        return Toolkit.getDefaultToolkit().getSystemClipboard();
    }
}
