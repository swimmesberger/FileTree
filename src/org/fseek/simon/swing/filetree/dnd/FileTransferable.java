/*
 * Copyright (C) 2011 Simon Wimmesberger<www.fseek.org>
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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileTransferable implements Transferable
{
    private final File[] temp;
    private int action;

    public FileTransferable(File[] temp) throws IOException
    {
        this.temp = temp;
    }

    @Override
    public Object getTransferData(DataFlavor flavor)
    {
        List list = new ArrayList();
        list.addAll(Arrays.asList(temp));
        return list;
    }
    
    @Override
    public DataFlavor[] getTransferDataFlavors()
    {
        return new DataFlavor[]{DataFlavor.javaFileListFlavor};
    }
    
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor)
    {
        return flavor == DataFlavor.javaFileListFlavor;
    }

    /**
     * @return the action
     */
    public int getAction()
    {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(int action)
    {
        this.action = action;
    }
    
    
}