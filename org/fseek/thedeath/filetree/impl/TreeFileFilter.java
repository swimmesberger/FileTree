 package org.fseek.thedeath.filetree.impl;

import java.io.File;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;

/**
 *
 * @author root
 */


public class TreeFileFilter extends AbstractFileFilter{
    public static final IOFileFilter VISIBLE = HiddenFileFilter.VISIBLE;
    public static final IOFileFilter DIRECTORY = DirectoryFileFilter.DIRECTORY;

    private boolean showHidden = true;
    private boolean showFiles = true;
    public TreeFileFilter(boolean showHidden, boolean showFiles) {
        this.showHidden = showHidden;
        this.showFiles = showFiles;
    }
    
    
    
    @Override
    public boolean accept(File file) {
        if(showFiles == false && showHidden == false){
            return VISIBLE.accept(file) && DIRECTORY.accept(file);
        }else if(showFiles == false){
            return DIRECTORY.accept(file);
        }else if(showHidden == false){
            return VISIBLE.accept(file);
        }else{
            return true;
        }
    }
    
}
