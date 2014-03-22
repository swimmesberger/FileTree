
package org.fseek.simon.swing.filetree.dnd.interfaces;

import java.awt.datatransfer.ClipboardOwner;
import java.io.File;
import javax.swing.ActionMap;
import javax.swing.DropMode;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;

/**
 *
 * @author Simon Wimmesberger
 */
public interface IFileDragDropSupport extends ClipboardOwner{
    public boolean importFile(File[] file, int action);
    public boolean canImport(TransferSupport support);
    public File[] getSelectedFiles();
    public TransferHandler getTransferHandler();
    public void setTransferHandler(TransferHandler newHandler);
    public void setDragEnabled(boolean b);
    public void setDropMode(DropMode dropMode);
    public ActionMap getActionMap();
}
