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
package org.fseek.simon.swing.filetree.ui;

import java.awt.Graphics;
import java.awt.Insets;
import java.util.HashSet;
import java.util.Set;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;

/**
 *
 * @author Thedeath<www.fseek.org>
 */
public class HideRowTreeUI extends BasicTreeUI
{

    private Set<Integer> hiddenRows;

    public void hideRow(int row)
    {
        if(hiddenRows == null){
            hiddenRows = new HashSet<>();
        }
        hiddenRows.add(row);
    }

    @Override
    protected void paintHorizontalPartOfLeg(Graphics g,java.awt.Rectangle clipBounds, Insets insets, java.awt.Rectangle bounds,TreePath path, int row, boolean isExpanded,boolean hasBeenExpanded, boolean isLeaf)
    {
        if (hiddenRows == null || !hiddenRows.contains(row))
        {
            super.paintHorizontalPartOfLeg(g, clipBounds, insets, bounds,
            path, row, isExpanded, hasBeenExpanded, isLeaf);
        }
    }

    @Override
    protected void paintRow(Graphics g, java.awt.Rectangle clipBounds,Insets insets, java.awt.Rectangle bounds, TreePath path, int row,boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf)
    {
        if (hiddenRows == null || !hiddenRows.contains(row))
        {
            super.paintRow(g, clipBounds, insets, bounds, path, row,
            isExpanded, hasBeenExpanded, isLeaf);
        }
    }

    @Override
    protected void paintExpandControl(Graphics g, java.awt.Rectangle clipBounds,Insets insets, java.awt.Rectangle bounds, TreePath path, int row,boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf)
    {
        if (hiddenRows == null || !hiddenRows.contains(row))
        {
            super.paintExpandControl(g, clipBounds, insets, bounds,
            path, row, isExpanded, hasBeenExpanded, isLeaf);
        }
    }
}
