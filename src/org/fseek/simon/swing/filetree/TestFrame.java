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
package org.fseek.simon.swing.filetree;

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
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import org.fseek.simon.swing.filetree.interfaces.FileListener;
import org.fseek.thedeath.os.util.Debug;
import org.fseek.thedeath.os.util.OSUtil;

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
                if(me.getClickCount() >= 2 && f.isFile()){
                    //JOptionPane.showMessageDialog(TestFrame.this, "Double click: " + f.getAbsolutePath());
                    OSUtil.openFile(f);
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
            String path;
            if(f == null){
                path = null;
            }else{
                path = f.getAbsolutePath();
            }
            fileInfo.setText("Selected File: " + path);
            Debug.println("File changed: " + path);
        } 
    }
}
