
package org.onebeartoe.web.enabled.pixel.controllers;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.UUID;
import javax.swing.*;


import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.onebeartoe.web.enabled.pixel.WebEnabledPixel;


public class WindowsLCD {

    GraphicsDevice[] screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
    static String NOT_FOUND = "";
    private static ImageIcon ii;
    private boolean addedScroller = false;
    private String basePath = "D:\\Arcade\\Pixelcade\\lcdmarquees";
    private static String pixelHome = WebEnabledPixel.getHome();
    protected JFrame myFrame = new JFrame();
    protected JFrame videoFrame = new JFrame();
    public JFrame marqueeFrame = new JFrame();
    final JFXPanel VFXPanel = new JFXPanel();
    StackPane vroot = null;
    protected BufferedImage bi = null;
    Font font = null;
    protected MarqueePanel marqueePanel;
    MediaView viewer = null;
    Scene scene = null;

    {
        this.myFrame.setSize(1280, 390);
        this.myFrame.setType(Window.Type.UTILITY);
        this.myFrame.setBackground(Color.black);
        this.myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.myFrame.setUndecorated(true);

        this.marqueeFrame.setSize(1280, 390);
        this.marqueeFrame.setType(Window.Type.UTILITY);
        this.marqueeFrame.setBackground(Color.black);
        this.marqueeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.marqueeFrame.setUndecorated(true);
        marqueePanel = new MarqueePanel();
        marqueeFrame.add(marqueePanel);

        this.videoFrame.setBackground(Color.BLACK);
        this.videoFrame.setSize(1280, 390);
        this.videoFrame.setType(Window.Type.UTILITY);
        this.videoFrame.setBackground(Color.black);
        this.videoFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.videoFrame.setUndecorated(true);
        this.videoFrame.setLayout(new BorderLayout());
        this.videoFrame.add(VFXPanel,BorderLayout.CENTER);


        this.font = new Font("Helvetica", Font.PLAIN, 268);
        viewer = new MediaView();
        vroot = new StackPane();
        scene = new Scene(vroot);

        // center video position
        javafx.geometry.Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        viewer.setX((screen.getWidth() - videoFrame.getWidth()) / 2);
        viewer.setY((screen.getHeight() - videoFrame.getHeight()) / 2);

    }

    public GraphicsDevice[] connectedDevices() {

        for (GraphicsDevice display: screens
        ) {
            System.out.print(String.format("Resolution:%dx%d\n",display.getDisplayMode().getWidth(),display.getDisplayMode().getHeight()));
        }
        return screens;

    }

    void scrollText(String message, Font font, Color color, int speed) {
        
        marqueePanel.setColor(color);
        marqueePanel.setSpeed(speed);
        System.out.print(String.format("scrollText about to setFont: %s\n",font.getFontName()));
        marqueePanel.setFont(font);
        marqueePanel.setMessage(message);
        showOnScreen(1,marqueeFrame);
    }


    void displayImage(String named, String system) throws IOException {
        System.out.println(String.format("iShow banner or text for %s\n",named));
        if(addedScroller) {
            myFrame.getContentPane().remove(marqueePanel);
            addedScroller = false;
        }
        basePath = pixelHome + "lcdmarquees\\";
        NOT_FOUND = basePath + "\\pixelcade.png";  //if not found, we'll show d:\arcade\pixelcade\lcdmarquees\pixelcade.png for example
        if(named.contains("nodata")){
            //getVideo(basePath + "video\\");
            marqueePanel.setMessage("Welcome to Pixelcade and Game On!");
            showOnScreen(1,marqueeFrame);
            //this.scrollText("Welcome to KnJ's Funhouse!!!",this.font,Color.magenta,60);
            return;
        }
        String marqueePath = NOT_FOUND;
    if(new File(String.format("%s%s/%s.gif",pixelHome,system,named)).exists()) {
        marqueePath = String.format("%s%s/%s.gif",pixelHome,system,named);
    }
        else if(new File(String.format("%s%s.png",basePath,named)).exists()){
            marqueePath = String.format("%s%s.png",basePath,named);
        }else if(new File(String.format("%sconsole/default-%s.png",basePath,system)).exists()){
            marqueePath = String.format("%sconsole/default-%s.png",basePath,system);
        }

        System.out.println(String.format("MARQPATH is:%s Requested: %s %s",marqueePath,named,system));
        if(marqueePath.equals(NOT_FOUND)){
            marqueePanel.setMessage(String.format("%s...",named.replace("_"," ")));
            showOnScreen(1,marqueeFrame);
            //scrollText(String.format("%s - %s...",named,system),this.font,Color.blue,60);
            return;
        }
        JLabel joe = new JLabel(new ImageIcon());

        try{
            URL url = new File(marqueePath).toURI().toURL();

            if(marqueePath.contains(".gif"))
                ii = new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(1280,390, Image.SCALE_DEFAULT));
            else
                ii = new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(1280,390, Image.SCALE_AREA_AVERAGING));

        }catch(MalformedURLException e){
            bi = ImageIO.read(new File(marqueePath));
            bi = resize(bi, 390,1280);
            ii = new ImageIcon(bi);
        }

        joe.setIcon(ii);
        marqueeFrame.setVisible(false);
        myFrame.getContentPane().removeAll();
        myFrame.add(joe);

        showOnScreen(1, myFrame);

        if(marqueePath.contains(".gif")){
            String finalMarqueePath = marqueePath.replace("gif","png").replace(system,"lcdmarquees");
            TimerTask task = new TimerTask() {
                public void run()  {
                    try{
                        URL url = new File(finalMarqueePath).toURI().toURL();
                        ii = new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(1280,390, Image.SCALE_AREA_AVERAGING));
                        joe.setIcon(ii);

                    }catch(MalformedURLException e){
                        try{
                            bi = ImageIO.read(new File(finalMarqueePath));
                            bi = resize(bi, 390,1280);
                            ii = new ImageIcon(bi);
                            joe.setIcon(ii);
                        }catch(IOException io){

                        }
                    }
                }
            };

            java.util.Timer timer = new java.util.Timer();

            long delay = 20000L;
            timer.schedule(task, delay);
        }

        //if this was an aniGIF, how do we switch to static image?

    }

    void getVideo(String  videoPath){

        File video_source = new File(videoPath);
        Media m = new Media(video_source.toURI().toString());
        MediaPlayer player = new MediaPlayer(m);
        viewer.setMediaPlayer(player);
        player.setCycleCount(MediaPlayer.INDEFINITE);

        // resize video based on screen size
        DoubleProperty width = viewer.fitWidthProperty();
        DoubleProperty height = viewer.fitHeightProperty();
        //width.bind(Bindings.selectDouble(viewer.sceneProperty(), "width"));
        // height.bind(Bindings.selectDouble(viewer.sceneProperty(), "height"));
        viewer.setPreserveRatio(false);

        // add video to stackpane
        if(!vroot.getChildren().contains(viewer))
            vroot.getChildren().add(viewer);

        VFXPanel.setScene(scene);
        player.play();

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
        frame.setFocusable(false);
        frame.setVisible(true);
    }

    public BufferedImage resize(BufferedImage img, int height, int width) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }
}

class MarqueePanel extends JFXPanel implements ActionListener {
    //private String pixelHome = System.getProperty("user.dir") + "\\";
    private String pixelHome = WebEnabledPixel.getHome();
    private static final int RATE = 12;
    private final Timer timer = new Timer(60 / RATE, this);
    final HBox scrollingArea = new HBox();
    private final JLabel label = new JLabel();
    private  String s = "";
    String fontFileName = "";
    private Color color = Color.magenta;
    private  int n = 0;
    Font font = null;
    int numLoops = 0;
    javafx.scene.text.Font jfont = null;
    TranslateTransition tt = new TranslateTransition();
    private int index;
    double WIDTH =1280, HEIGHT = 390;
    Text scrollingText = new Text();
    boolean didHi = false;

    public MarqueePanel() {
        this.setBackground(Color.BLACK);

        this.setSize(1280, 390);
        label.setForeground(color);
        label.setBackground(Color.BLACK);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
        label.setVisible(true);
        this.setVisible(true);
        this.setFocusable(false);
        label.setFocusable(false);
        VBox root = new VBox();
        root.setSpacing(20);
        root.setFillWidth(true);
        root.setAlignment(Pos.CENTER_LEFT);
        scrollingArea.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.rgb(0, 0, 0), CornerRadii.EMPTY, javafx.geometry.Insets.EMPTY )));
        root.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.rgb(0, 0, 0), CornerRadii.EMPTY, javafx.geometry.Insets.EMPTY )));
        scrollingArea.setMaxWidth(1920 * 1.5);
        scrollingArea.setMaxHeight(300);
        scrollingArea.setMinSize(scrollingArea.getMaxWidth(),288);
        scrollingArea.setAlignment(Pos.CENTER_LEFT);
        scrollingArea.getChildren().add(scrollingText);
        root.getChildren().add(scrollingArea);
        this.setScene(new Scene(root, 1280,390));

        try {
            if (this.font == null) {
                this.font = new Font("Helvetica", Font.PLAIN, 268);
                this.jfont = new javafx.scene.text.Font("Helvetica", 268);
                System.out.println(String.format("Font null,internal mPanel inited and set font: %s\n", font.getFontName()));
            }
        } catch (NullPointerException npe) {
            System.out.println(String.format("internal mPanel FAILED init and set font: %s\n", font.getFontName()));
        }
        this.add(label);
        setMessage("Welcome to Pixelcade - Game On!");
    }

    @Override
    public void setFont(Font font) {
        System.out.println(String.format("internal mPanel setFont called with %s\n",font.getFontName()));
        if(font.getFontName().contains("Dialog")) {
            System.out.println(String.format("DIALOG OVERRIDE ENGAGED!!\n"));
            return;
        }
        if (font != null)
            this.font = font.deriveFont(244);
        System.out.println(String.format("internal mPanel setFont called with %s\n",font.getFontName()));
        Platform.runLater(new Runnable() {

            @Override public void run() {
                try {

                    InputStream inputStream = new FileInputStream(pixelHome + "fonts/" + fontFileName);
                    jfont = javafx.scene.text.Font.loadFont(inputStream, 244);
                    System.out.println(String.format("internal mPanel jFont set with %s\n",font.getFontName()));
                    if(!didHi) {
                        setMessage("Welcome to Pixelcade");
                        didHi = true;
                    }
                } catch (FileNotFoundException e){
                    jfont = new javafx.scene.text.Font("Helvetica", 244);
                    System.out.println(String.format("Couldn't set jFont for %s,\n resetting to internal default",fontFileName));
                }
            }
        });
    }

    public void setSpeed(int speed) {
        this.n = speed;
    }

    public void setFontFileName(String fontFileName) {
        this.fontFileName = fontFileName;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setNumLoops(int numLoops) {
        this.numLoops = numLoops;
    }

    public void setMessage(String message) {

        Platform.runLater(new Runnable() {
            @Override public void run() {
                scrollingText(scrollingArea,message);
            }
        });
    }

    public MarqueePanel(String s, int n, Color color, Font font) {
        if (s == null || n < 1) {
            throw new IllegalArgumentException("Null string or n < 1");
        }

        setColor(color);
        setFont(font);
        setSpeed(n);
        setMessage(s);
    }
    
    public void scrollingText(HBox parent, String text, int loops)
{
    //System.out.println("loops before if " + loops);
    
    String thiscall = UUID.randomUUID().toString();
    Map callTrace = new HashMap();
    callTrace.put(thiscall,loops);
    
    //TO DO if this is uncommented out, scrolling text will scroll infinite even if loop = 2 for example, fix this later
    
   
//    if(loops == 0){
//        System.out.println(String.format("Setting Infinity for [%s,%d]",callTrace,callTrace.get(thiscall)));
//        loops = Timeline.INDEFINITE;
//   }
   
  
    this.scrollingText.setText(text);
    this.scrollingText.setFill(javafx.scene.paint.Color.rgb(color.getRed(), color.getGreen(), color.getBlue()));
    this.scrollingText.setLayoutX(0);
    this.scrollingText.setLayoutY(20);
    this.scrollingText.setTextAlignment(TextAlignment.LEFT);
    this.scrollingText.setFont(jfont);
    this.scrollingText.setWrappingWidth(this.scrollingText.getBoundsInLocal().getWidth());
    TranslateTransition tt = new TranslateTransition(Duration.millis(10000), this.scrollingText);
    tt.setToX(0 - this.scrollingText.getWrappingWidth() - 10); // setFromX sets the starting position, coming from the left and going to the right.
    int boundWidth = (int)parent.getBoundsInParent().getWidth();
    tt.setFromX(1281); // setToX sets to target position, go beyond the right side of the screen. //to do this should be configurable to other resolutions work
    tt.setCycleCount(loops);
    tt.setAutoReverse(false); //Always start over
    tt.play();
}
    
public void scrollingText(HBox parent, String text) {
    scrollingText(parent, text, numLoops);
}

    public void start() {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                tt.play();
            }
        });

        //timer.start();
    }

    public void stop() {
        Platform.runLater(new Runnable() {

            @Override public void run() {
                tt.stop();
            }
        });
        //timer.stop();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        index++;
        if (index > s.length() - n) {
            index = 0;
        }
        label.setText(s.substring(index, index + n));
    }

}


