package nl.xupwup.Util;


import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import nl.xupwup.WindowManager.WindowManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import nl.xupwup.WindowManager.Component;
import nl.xupwup.WindowManager.Components.CheckBox;
import nl.xupwup.WindowManager.Components.Option;
import nl.xupwup.WindowManager.Listener;
import nl.xupwup.WindowManager.TopControls;
import nl.xupwup.WindowManager.Window;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import static org.lwjgl.opengl.GL20.*;

/**
 *
 * @author Rick Hendricksen
 */
public abstract class  GLFramework extends Thread {
    public static String WINDOW_TITLE;
    double averageFrameTime = 0;
    public static Vec2d windowSize;
    public WindowManager wm;
    
    public boolean exit = false; // set to true for exit
    public TopControls topcontrols;
    public static boolean useBlur = true;
    public static boolean keepRepainting = true;
    public static boolean useFXAA = true;
    public static boolean inhibitFXAA = false;
    boolean usetopbar = false;
  
    public FrameBuffer backBuffer = null;
    public FrameBuffer frontBuffer = null;
    public FrameBuffer applicationBuffer = null;

}
