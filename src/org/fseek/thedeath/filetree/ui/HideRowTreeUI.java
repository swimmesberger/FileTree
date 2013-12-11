package org.fseek.thedeath.filetree.ui;

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
