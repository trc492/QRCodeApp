/*
 * Copyright (c) 2018 Titan Robotics Club (http://www.titanrobotics.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

/**
 * This class implements an image panel which is a JPanel for rendering the QR code image or the image from the
 * camera.
 */
public class ImagePanel extends JPanel
{
    private static final long serialVersionUID = 4L;

    private VideoCapture camera = null;
    private Mat mat;
    private RefreshThread cameraThread;
    private BufferedImage image;

    /**
     * Constructor: Create an instance of the object. It initializes the camera using OpenCV library and created
     * the camera thread.
     */
    public ImagePanel()
    {
        //
        // Load OpenCV library.
        //
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //
        // Preallocate some global variables.
        //
        mat = new Mat();
        //
        // Create the Refresh thread to refresh the video pane at 10fps (i.e. every 100 msec).
        //
        cameraThread = new RefreshThread(this, 100);
        cameraThread.start();
    }   //ImagePanel

    /**
     * This method is called to update the QR code image.
     *
     * @param image specifies the image.
     */
    public synchronized void setImage(BufferedImage image)
    {
        this.image = image;
        repaint();
    }   //setImage

    /**
     * This method returns the current image displayed.
     *
     * @return displayed image.
     */
    public synchronized BufferedImage getImage()
    {
        return image;
    }   //getImage

    /**
     * This method resumes the camera thread.
     */
    public synchronized void startCamera()
    {
        //
        // Open the default camera.
        //
        camera = new VideoCapture(0);
        if (!camera.isOpened())
        {
            camera = null;
            JOptionPane.showMessageDialog(
                this, "Failed to open the camera, perhap not having the permission or is in use by another app.",
                QRCodeApp.PROGRAM_TITLE,
                JOptionPane.ERROR_MESSAGE);
        }
        else
        {
            cameraThread.resumeThread();
        }
    }   //startCamera

    /**
     * This method suspends the camera thread.
     */
    public synchronized void stopCamera()
    {
        cameraThread.suspendThread();
        if (camera != null)
        {
            camera.release();
            camera = null;
        }
    }   //stopCamera

    /**
     * This method captures an image from the camera.
     */
    public synchronized void captureImage()
    {
        if (camera != null)
        {
            camera.read(mat);
            if (mat.height() > 0 && mat.width() > 0)
            {
                image = MatToBufferedImage(mat);
                repaint();
            }
        }
    }   //captureImage

    /**
     * This method terminates the camera thread and release the camera.
     */
    public synchronized void terminateCameraThread()
    {
        cameraThread.terminateThread();
        if (camera != null)
        {
            camera.release();
        }
    }   //terminateCameraThread

    /**
     * This method converts an OpenCV image (i.e. Mat) into a BufferedImage that can be drawn on
     * a Java graphics object.
     *
     * @param mat specifies an OpenCV image.
     * @return converted BufferedImage object.
     */
    private BufferedImage MatToBufferedImage(Mat mat)
    {
        BufferedImage image = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);
        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte)raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        mat.get(0, 0, data);

        return image;
    }   //MatToBufferedImage

    /**
     * This method is called when the image needed a repaint.
     *
     * @param g specifies the Graphics object for the paint.
     */
    @Override
    public synchronized void paint(Graphics g)
    {
        g.drawImage(image, 0, 0, null);
    }   //paint

}   //class ImagePanel
