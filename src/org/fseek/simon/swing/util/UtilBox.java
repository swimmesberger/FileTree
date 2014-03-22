
package org.fseek.simon.swing.util;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.swing.ImageIcon;
import org.imgscalr.Scalr;

/**
 *
 * @author Simon Wimmesberger
 */
public class UtilBox extends org.fseek.thedeath.os.util.UtilBox{
    public static ImageIcon rescaleIconIfNeeded(ImageIcon systemIcon, int target)
    {
        if(scaleCache == null){
            scaleCache = new HashMap<>();
        }
        if(scaleCache.containsKey(systemIcon)){
            ImageIcon get = scaleCache.get(systemIcon);
            if(get.getIconHeight() == target || get.getIconWidth() == target){
                return get;
            }
        }
        BufferedImage scaledInstance = null;
        if(systemIcon.getIconHeight() != target && systemIcon.getIconWidth() != target)
        {
            BufferedImage img = org.fseek.thedeath.os.util.UtilBox.imageToBufferedImage(systemIcon.getImage());
            scaledInstance = Scalr.resize(img, target);
        }
        if(scaledInstance == null)
        {
            return systemIcon;
        }
        ImageIcon scaledIcon = new ImageIcon(scaledInstance);
        scaleCache.put(systemIcon, scaledIcon);
        return scaledIcon;
    }
}
