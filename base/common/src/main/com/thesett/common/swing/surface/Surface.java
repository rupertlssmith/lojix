/*
 * Copyright The Sett Ltd, 2005 to 2014.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thesett.common.swing.surface;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import javax.swing.JPanel;
import javax.swing.RepaintManager;

/**
 * Surface is a Panel that generates a Graphics2D context onto which sub classes may render an image to be displayed in
 * the panel. Surface provides different properties and image types to select the exact nature of the image to render.
 * Subclasses need only provide a render method which takes a Graphics2D context as an argument and should draw into
 * this context without being aware of the colour depth and other parameters which are determined by the Surface
 * proprties.
 *
 * <p/>The different image types are:
 *
 * <pre><table>
 * <tr><td>0 <td>automatic, (on screen for non animating, double buffered for animating)
 * <tr><td>1 <td>on screen, no buffered image is used, rendering is direct to the screen.
 * <tr><td>2 to 15 <td>buffered image types
 * <tr><td>16 <td>custom binary
 * <tr><td>17 <td>custom binary
 * <tr><td>18 <td>custom sgi
 * <tr><td>19 <td>custom sgi
 * </table></pre>
 *
 * <p/>Rendering qaulity may be set or cleared. Higher quality means slower rendering, lower qaulity means faster
 * rendering.
 *
 * <pre><p/><table id="crc"><caption>CRC Card</caption>
 * <tr><th>Responsibilities<th>Collaborations
 * </table></pre>
 *
 * @author Rupert Smith
 */
public abstract class Surface extends JPanel
{
    /** Used for loging. */
    /* private static final Logger log = Logger.getLogger(Surface.class.getName()); */

    // Image type constants

    /** Automatic image type. */
    public static final int AUTO_SCREEN = 0;

    /** On screen image type, no buffered image is used, rendering is direct to the screen. */
    public static final int ON_SCREEN = 1;

    /** Off screen buffered image type. */
    public static final int OFF_SCREEN = 2;

    /** 24-bit RGB packed into last three bytes of a word. Direct color model. Off screen buffered. */
    public static final int INT_XRGB = 3;

    /** 32-bit Alpha and RGB. Direct color model. Off screen buffered. */
    public static final int INT_ARGB = 4;

    /** 32-bit Alpha and RGB. Direct color model. The RGB is pre-multiplied by the alpha. Off screen buffered. */
    public static final int INT_ARGB_PRE = 5;

    /** 24-bit RGB with direct color model. Off screen buffered. */
    public static final int INT_BGR = 6;

    /** 24-bit RGB with component color mode. Off screen buffered. */
    public static final int THREE_BYTE_BGR = 7;

    /** 32-bit Alpha and RGB. Component color model. Off screen buffered. */
    public static final int FOUR_BYTE_ABGR = 8;

    /** 32-bit Alpha and RGB. Component color model. The RGB is pre-multiplied by the alpha. Off screen buffered. */
    public static final int FOUR_BYTE_ABGR_PRE = 9;

    /** 5-bit red, 6-bit green and 5-bits blue. Direct color model. Off screen buffered. */
    public static final int USHORT_565_RGB = 10;

    /** 5-bit red, 5-bit green and 5-bits blue. Direct color model. Off screen buffered. */
    public static final int USHORT_X555_RGB = 11;

    /** 8-bit grey scale. Component color model. Off screen buffered. */
    public static final int BYTE_GRAY = 12;

    /** 16-bit grey scale. Component color model. Off screen buffered. */
    public static final int USHORT_GRAY = 13;

    /** 1-bit grey scale. Index color model. Off screen buffered. */
    public static final int BYTE_BINARY = 14;

    /** 8-bit grey scale. Index color model. Off screen buffered. */
    public static final int BYTE_INDEXED = 15;

    /** 2-bit grey scale. Index color model. Off screen buffered. */
    public static final int BYTE_BINARY_2_BIT = 16;

    /** 4-bit grey scale. Index color model. Off screen buffered. */
    public static final int BYTE_BINARY_4_BIT = 17;

    /** 32-bit color. Direct color model. Off screen buffered. */
    public static final int INT_RGBX = 18;

    /** 16-bit color. Direct color model. Off screen buffered. */
    public static final int USHORT_555X_RGB = 19;

    // Lookup tables for BYTE_BINARY 1, 2 and 4 bits.

    /** 1-bit intensity look up table. */
    static byte[] lut1Arr = new byte[] { 0, (byte) 255 };

    /** 2-bit intensity look up table. */
    static byte[] lut2Arr = new byte[] { 0, (byte) 85, (byte) 170, (byte) 255 };

    /** 4-bit intensity look up table. */
    static byte[] lut4Arr =
        new byte[]
        {
            0, (byte) 17, (byte) 34, (byte) 51, (byte) 68, (byte) 85, (byte) 102, (byte) 119, (byte) 136, (byte) 153,
            (byte) 170, (byte) 187, (byte) 204, (byte) 221, (byte) 238, (byte) 255
        };

    /** Defines quick reference to the anti aliasing on constant defined in RenderingHints. */
    public Object antiAlias = RenderingHints.VALUE_ANTIALIAS_ON;

    /** Defines quick reference to the fast rendering constant defined in RenderingHints. */
    public Object rendering = RenderingHints.VALUE_RENDER_SPEED;

    /** Compositing algorithm. */
    public AlphaComposite composite;

    /** Background texture to initialize the image with. */
    public Paint texture;

    /** The image buffer that this Surface displays. */
    public BufferedImage bimg;

    /** The type of image to render to. See image type constants. */
    public int imageType;

    /** Sets whether or not the image is cleared before every call to the render method. */
    public boolean clearSurface = true;

    /** Demos using animated gif's that implement ImageObserver set dontThread. */
    public boolean dontThread;

    /** The amount to sleep between animation frames. */
    protected long sleepAmount = 50;

    /** Reference to the AWT implementing Toolkit. */
    private Toolkit toolkit;

    /** Holds the size of teh current buffered image. */
    private int biw, bih;

    /** Flag used by the paint method to signal that the image should initially be cleared. */
    private boolean clearOnce;

    /** Creates a new Surface object. */
    public Surface()
    {
        toolkit = getToolkit();
        setImageType(0);
    }

    /**
     * Subclasses that extend Surface must implement this routine to paint in the graphics context.
     *
     * @param w  The width of the Graphics2D context.
     * @param h  The height of the Graphics2D context.
     * @param g2 The Graphics2D context to draw into.
     */
    public abstract void render(int w, int h, Graphics2D g2);

    /**
     * Returns the image type.
     *
     * @return The image type in use.
     */
    public int getImageType()
    {
        return imageType;
    }

    /**
     * Sets the image type.
     *
     * @param imgType Sets the image type to use.
     */
    public void setImageType(int imgType)
    {
        // Check if auto image type selection is set
        if (imgType == 0)
        {
            // Use direct to screen rendering
            imageType = 1;
        }
        else
        {
            imageType = imgType;
        }

        // Clear any existing buffered image so that the new type will take effect
        bimg = null;
    }

    /**
     * Turn anti-aliasing on or off.
     *
     * @param aa Set to true to use ani-aliasing and false to not use it.
     */
    public void setAntiAlias(boolean aa)
    {
        antiAlias = aa ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF;
    }

    /**
     * Sets the rendering quality flag. Set to false for speed, tru for quality.
     *
     * @param rd Set to false for speed, tru for quality.
     */
    public void setRendering(boolean rd)
    {
        rendering = rd ? RenderingHints.VALUE_RENDER_QUALITY : RenderingHints.VALUE_RENDER_SPEED;
    }

    /**
     * Sets the kind of paint to use. For the image background?
     *
     * @param obj The paint to use.
     */
    public void setTexture(Paint obj)
    {
        if (obj instanceof GradientPaint)
        {
            texture = new GradientPaint(0, 0, Color.white, getSize().width * 2, 0, Color.green);
        }
        else
        {
            texture = obj;
        }
    }

    /**
     * Sets whether or not to alpha composite the image.
     *
     * @param cp Set to true to use alpha compositing, false to not use it.
     */
    public void setComposite(boolean cp)
    {
        composite = cp ? AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f) : null;
    }

    /**
     * Sets amount to sleep between animation frames.
     *
     * @param amount The time in milliseconds to wait between animation frames.
     */
    public void setSleepAmount(long amount)
    {
        sleepAmount = amount;
    }

    /**
     * Gets the amount to sleep between animation frames.
     *
     * @return The amount of sleep time set between animation frames.
     */
    public long getSleepAmount()
    {
        return sleepAmount;
    }

    /**
     * Immediately repaints the surface.
     *
     * <p/>It's possible to turn off double-buffering for just the repaint calls invoked directly on the non double
     * buffered component. This can be done by overriding paintImmediately() (which is called as a result of repaint)
     * and getting the current RepaintManager and turning off double buffering in the RepaintManager before calling
     * super.paintImmediately(g).
     *
     * @param x The X coord to start painting at.
     * @param y The Y coord to start painting at.
     * @param w The width of the region to paint.
     * @param h The height of the region to paint.
     */
    public void paintImmediately(int x, int y, int w, int h)
    {
        RepaintManager repaintManager = null;
        boolean save = true;

        if (!isDoubleBuffered())
        {
            repaintManager = RepaintManager.currentManager(this);
            save = repaintManager.isDoubleBufferingEnabled();
            repaintManager.setDoubleBufferingEnabled(false);
        }

        super.paintImmediately(x, y, w, h);

        if (repaintManager != null)
        {
            repaintManager.setDoubleBufferingEnabled(save);
        }
    }

    /**
     * Renders the surface. This method will create a buffered image to render in if one does not already exist. If
     * rendering is to be done directly to the screen no buffered image will be generated. If the size of the buffered
     * image does not match the Graphics context size then a new buffered image will be generated of the correct size
     * before rendering. The {@link #render} method will be called to do the actual drawing.
     *
     * @param g The graphics context to paint.
     */
    public void paint(Graphics g)
    {
        // Get the size of the surface
        Dimension d = getSize();

        // Check if direct to screen rendering is to be used
        if (imageType == 1)
        {
            // Use no buffered image for direct rendering
            bimg = null;
        }

        // Check if no buffered image has been created yet or if the existing one has the wrong size
        else if ((bimg == null) || (biw != d.width) || (bih != d.height))
        {
            // Create a new bufferd image. Note that imageType has two subtracted for it as type 0 stands for auto
            // selection and type 1 for direct to screen. Subtracting two corresponds to the image types in the
            // BufferedImage class.
            bimg = createBufferedImage(d.width, d.height, imageType - 2);

            // Set clear once flag to clear the new image on the first pass
            clearOnce = true;
        }

        // Build a Graphics2D context for the image
        Graphics2D g2 = createGraphics2D(d.width, d.height, bimg, g);

        // Call the subclass to perform rendering
        render(d.width, d.height, g2);

        // Clear up the Graphics2D context
        g2.dispose();

        // Check if a buffered image was used (and not direct to screen rendering)
        if (bimg != null)
        {
            // Copy the buffered image onto the screen
            g.drawImage(bimg, 0, 0, null);

            // Ensure screen is up to date
            toolkit.sync();
        }
    }

    /**
     * Generates a fresh buffered image of the appropriate type.
     *
     * @param  w       The image width.
     * @param  h       The image height.
     * @param  imgType The image type. Note that this should be two less than the image types defined as constants in
     *                 this class to correspond with the image type constants defined in BufferedImage. This class uses
     *                 0 for auto image type selection and 1 for direct to screen.
     *
     * @return A buffered image of the specifed size and type.
     */
    protected BufferedImage createBufferedImage(int w, int h, int imgType)
    {
        BufferedImage bi = null;

        if (imgType == 0)
        {
            bi = (BufferedImage) createImage(w, h);
        }
        else if ((imgType > 0) && (imgType < 14))
        {
            bi = new BufferedImage(w, h, imgType);
        }
        else if (imgType == 14)
        {
            bi = createBinaryImage(w, h, 2);
        }
        else if (imgType == 15)
        {
            bi = createBinaryImage(w, h, 4);
        }
        else if (imgType == 16)
        {
            bi = createSGISurface(w, h, 32);
        }
        else if (imgType == 17)
        {
            bi = createSGISurface(w, h, 16);
        }

        // Store the buffered image size
        biw = w;
        bih = h;

        return bi;
    }

    /**
     * Creates a Graphics2D drawing context from a BufferedImage or Graphics context. The graphics context is built
     * using the properties defined for the surface. This method is used to generate the Graphics2D context that
     * subclasses will render in. IF the buffered image is null then the passed in Graphics context will be used to
     * generate the Graphics2D context. This is the case when no buffered image is used and the subclass renders
     * straight to the screen.
     *
     * @param  width  The width of the buffered image.
     * @param  height The height of the buffered image.
     * @param  bi     The buffered image to generate a Graphics2D context for, or null if the next param is to be used.
     * @param  g      The Graphics context to generate a Graphics2D context for, only used if bi is null.
     *
     * @return A Graphics2D drawing context of the specified size and type.
     */
    protected Graphics2D createGraphics2D(int width, int height, BufferedImage bi, Graphics g)
    {
        Graphics2D g2 = null;

        // Check if the buffered image is null
        if (bi != null)
        {
            // Create Graphics2D context for the buffered image
            g2 = bi.createGraphics();
        }
        else
        {
            // The buffered image is null so create Graphics2D context for the Graphics context
            g2 = (Graphics2D) g;
        }

        // @todo what is this for?
        g2.setBackground(getBackground());

        // Set the rendering properties of the graphics context
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, rendering);

        // Check the clear flags to see if the graphics context should be cleared
        if (clearSurface || clearOnce)
        {
            // Clear the image
            g2.clearRect(0, 0, width, height);

            // Reset the clear once flag to show that clearing has been done
            clearOnce = false;
        }

        // Check if a background fill texture is to be used
        if (texture != null)
        {
            // set composite to opaque for texture fills
            g2.setComposite(AlphaComposite.SrcOver);
            g2.setPaint(texture);
            g2.fillRect(0, 0, width, height);
        }

        // Check if alpha compositing is to be used
        if (composite != null)
        {
            // Set the alpha compositing algorithm
            g2.setComposite(composite);
        }

        return g2;
    }

    /**
     * Creates a custom grey-scale binary image format.
     *
     * @param  w         The image width.
     * @param  h         The image height.
     * @param  pixelBits The number of grey-scale bits to use. Must be 1, 2 or 4.
     *
     * @return A buffered image of the specifed size and type.
     */
    private BufferedImage createBinaryImage(int w, int h, int pixelBits)
    {
        int bytesPerRow = w * pixelBits / 8;

        if ((w * pixelBits % 8) != 0)
        {
            bytesPerRow++;
        }

        byte[] imageData = new byte[h * bytesPerRow];
        IndexColorModel cm = null;

        switch (pixelBits)
        {
        case 1:
        {
            cm = new IndexColorModel(pixelBits, lut1Arr.length, lut1Arr, lut1Arr, lut1Arr);
            break;
        }

        case 2:
        {
            cm = new IndexColorModel(pixelBits, lut2Arr.length, lut2Arr, lut2Arr, lut2Arr);
            break;
        }

        case 4:
        {
            cm = new IndexColorModel(pixelBits, lut4Arr.length, lut4Arr, lut4Arr, lut4Arr);
            break;
        }

        default:
        {
            new Exception("Invalid # of bit per pixel").printStackTrace();
        }
        }

        DataBuffer db = new DataBufferByte(imageData, imageData.length);
        WritableRaster r = Raster.createPackedRaster(db, w, h, pixelBits, null);

        return new BufferedImage(cm, r, false, null);
    }

    /**
     * Creates a custom colour image format.
     *
     * @param  w         The image width.
     * @param  h         The image height.
     * @param  pixelBits The number of bits-per-pixel to use. Must be 16 or 32.
     *
     * @return A buffered image of the specifed size and type.
     */
    private BufferedImage createSGISurface(int w, int h, int pixelBits)
    {
        int rMask32 = 0xFF000000;
        int rMask16 = 0xF800;
        int gMask32 = 0x00FF0000;
        int gMask16 = 0x07C0;
        int bMask32 = 0x0000FF00;
        int bMask16 = 0x003E;
        DirectColorModel dcm = null;
        DataBuffer db = null;
        WritableRaster wr = null;

        switch (pixelBits)
        {
        case 16:
        {
            short[] imageDataUShort = new short[w * h];
            dcm = new DirectColorModel(16, rMask16, gMask16, bMask16);
            db = new DataBufferUShort(imageDataUShort, imageDataUShort.length);
            wr = Raster.createPackedRaster(db, w, h, w, new int[] { rMask16, gMask16, bMask16 }, null);

            break;
        }

        case 32:
        {
            int[] imageDataInt = new int[w * h];
            dcm = new DirectColorModel(32, rMask32, gMask32, bMask32);
            db = new DataBufferInt(imageDataInt, imageDataInt.length);
            wr = Raster.createPackedRaster(db, w, h, w, new int[] { rMask32, gMask32, bMask32 }, null);

            break;
        }

        default:
        {
            new Exception("Invalid # of bit per pixel").printStackTrace();
        }

        }

        return new BufferedImage(dcm, wr, false, null);
    }
}
