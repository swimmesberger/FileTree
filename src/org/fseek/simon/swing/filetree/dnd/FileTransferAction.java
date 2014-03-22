/*
 * Copyright (C) 2014 Simon Wimmesberger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
