
package org.onebeartoe.web.enabled.pixel.controllers;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;


public class WindowsLCD {

GraphicsDevice[] screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
//static String NOT_FOUND = "/Users/kaicherry/pixelcade.png";
static String NOT_FOUND = "";
private static ImageIcon ii;
private String basePath = "D:\\Arcade\\Pixelcade\\lcdmarquees";
private String pixelHome = System.getProperty("user.dir") + "\\";  

public GraphicsDevice[] connectedDevices() {

    for (GraphicsDevice display: screens
         ) {
        System.out.print(String.format("Resolution:%dx%d\n",display.getDisplayMode().getWidth(),display.getDisplayMode().getHeight()));
    }
     return screens;

}


    void displayImage(String named, String system){

    //basePath = new File(WindowsLCD.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent() + "/lcdmarquees/";
    basePath = pixelHome + "lcdmarquees/";
    NOT_FOUND = basePath + "pixelcade.png";  //if not found, we'll show d:\arcade\pixelcade\lcdmarquees\pixelcade.png for example

    String marqueePath = NOT_FOUND;
    if(new File(String.format("%s%s.png",basePath,named)).exists()){
        marqueePath = String.format("%s%s.png",basePath,named);
    }else if(new File(String.format("%sconsole/default-%s.png",basePath,system)).exists()){
        marqueePath = String.format("%sconsole/default-%s.png",basePath,system);
    }

    ii = new ImageIcon(marqueePath);
        JImage imageLabel = new JImage(marqueePath);

        JFrame myFrame = new JFrame();

        myFrame.setSize(1280,390);
        myFrame.add(imageLabel.p);

        myFrame.setBackground(Color.black);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.setUndecorated(true);
        myFrame.setVisible(true);
        showOnScreen(1, myFrame);

    }

    public  void showOnScreen(int screen, JFrame frame) {//, JComponent jc)

        Container pane = frame.getContentPane();
        pane.setBackground(Color.black);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gd = ge.getScreenDevices();
        if( screen > -1 && screen < gd.length ) {
            frame.setLocation(gd[screen].getDefaultConfiguration().getBounds().x, frame.getY());
        } else if( gd.length > 0 ) {
            frame.setLocation(gd[0].getDefaultConfiguration().getBounds().x, gd[0].getDefaultConfiguration().getBounds().y + frame.getY());
        } else {
            throw new RuntimeException( "No Screens Found" );
        }

        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }
}

class JImage{
    BufferedImage bi = null;
    MyJPanel p;
    JImage(String file){
        try{
            bi = ImageIO.read(new File(file));
            JFrame f = new JFrame();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            p = new MyJPanel();
            p.setPreferredSize(new Dimension(bi.getWidth(), bi.getHeight()));
            f.add(p);
            f.pack();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private static BufferedImage resize(BufferedImage img, int height, int width) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }
    public BufferedImage getScaledInstance(BufferedImage img,
                                           int targetWidth,
                                           int targetHeight,
                                           Object hint,
                                           boolean higherQuality)
    {
        int type = (img.getTransparency() == Transparency.OPAQUE) ?
                BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage)img;
        int w, h;
        if (higherQuality) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }

        do {
            if (higherQuality && w > targetWidth) {
                w /= 1.25;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            if (higherQuality && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            //g2.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        } while (w != targetWidth || h != targetHeight);

        return ret;
    }

    public static void main(String[] args){
        new JImage(args[0]);
    }
    class MyJPanel extends JPanel{
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            g.drawImage(bi, 0, 0, this);
        }
    }
}