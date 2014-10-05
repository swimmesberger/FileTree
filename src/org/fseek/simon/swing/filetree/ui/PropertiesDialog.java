
package org.fseek.simon.swing.filetree.ui;

import java.awt.GridLayout;
import java.io.File;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.apache.commons.io.FilenameUtils;
import org.fseek.thedeath.os.filetypes.Association;
import org.fseek.thedeath.os.filetypes.AssociationService;

/**
 *
 * @author Simon Wimmesberger
 */
public class PropertiesDialog extends JDialog{
    private File f;
    public PropertiesDialog(File f) {
        super();
        this.f = f;
        setLocationRelativeTo((JFrame)null);
        initUI();
        this.pack();
    }
    
    private void initUI(){
        this.setLayout(new GridLayout(5, 2));
        AssociationService as = new AssociationService();
        Association fileExtensionAssociation = as.getFileExtensionAssociation(FilenameUtils.getExtension(f.toString()));
        this.add(new JLabel("Default Application:"));
        this.add(new JLabel(fileExtensionAssociation.getActionByVerb("open").getCommand()));
    }
}
