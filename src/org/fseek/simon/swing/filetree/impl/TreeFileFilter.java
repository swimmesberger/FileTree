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
