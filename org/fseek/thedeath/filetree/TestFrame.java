/*
 * Copyright (C) 2013 Thedeath<www.fseek.org>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.fseek.thedeath.filetree;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.EventObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import org.fseek.thedeath.filetree.interfaces.FileListener;
import org.fseek.thedeath.os.util.Debug;

/**
 *
 * @author Thedeath<www.fseek.org>
 */
public class TestFrame extends JFrame implements WindowListener
{
    private FileTree tree;
    public TestFrame()
    {
        super();
        this.setTitle("Folder Tree - Test frame");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        initComps();
    }
    
    private void initComps(){
        this.addWindowListener(this);
        this.setLayout(new BorderLayout());
        final JLabel fileInfo = new JLabel("", JLabel.CENTER);
        tree = new FileTree(true, false);
        tree.setSimpleIcons(false);
        tree.addFileSelectionChangedListener(new DebugFileChangeListener(fileInfo));
        tree.addFileClickListener(new FileListener() {
            @Override
            public void fileChanged(File f, EventObject ev)
            {
                MouseEvent me = (MouseEvent)ev;
                if(me.getClickCount() >= 2){
                    JOptionPane.showMessageDialog(TestFrame.this, "Double click: " + f.getAbsolutePath());
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(tree);
        tree.setBorder(new EmptyBorder(10, 10, 0, 0));
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(fileInfo, BorderLayout.SOUTH);
        this.pack();
    }
    
    public static void main(final String[] args){
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Debug.setDebug(false);
                    Debug.setShowErrors(false);
                    if(args.length > 0){
                        if(args[0].toLowerCase().equals("-debug")){
                            Debug.setDebug(true);
                            Debug.setShowErrors(true);
                        }
                    }
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    FileTree.initUIManager();
                    new TestFrame().setVisible(true);
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex)
                {
                    Logger.getLogger(TestFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    @Override
    public void windowOpened(WindowEvent e)
    {
    }

    @Override
    public void windowClosing(WindowEvent e)
    {
        tree.cleanup();
    }

    @Override
    public void windowClosed(WindowEvent e)
    {
        
    }

    @Override
    public void windowIconified(WindowEvent e)
    {
    }

    @Override
    public void windowDeiconified(WindowEvent e)
    {
    }

    @Override
    public void windowActivated(WindowEvent e)
    {
    }

    @Override
    public void windowDeactivated(WindowEvent e)
    {
    }
    
    private static class DebugFileChangeListener implements FileListener{
        private JLabel fileInfo;

        public DebugFileChangeListener(JLabel fileInfo)
        {
            this.fileInfo = fileInfo;
        }
        
        @Override
        public void fileChanged(File f, EventObject ev)
        {
            fileInfo.setText("Selected File: " + f.getAbsolutePath());
            Debug.println("File changed: " + f.getAbsolutePath());
        } 
    }
}
