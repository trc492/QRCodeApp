/*
 * Copyright (c) 2016 Titan Robotics Club (http://www.titanrobotics.net)
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

/**
 * This class implements a refresh thread that runs every refresh interval to repaint the video pane.
 * So in effect, it is playing the video from the camera.
 */
public class RefreshThread extends Thread
{
    private ImagePanel panel;
    private long refreshInterval;
    private boolean threadRunning = true;
    private boolean threadResumed = false;

    /**
     * Constructor: Create an instance of the object.
     *
     * @param panel specifies the panel to draw the image on.
     * @param refreshInterval specifies the refresh interval in msec.
     */
    public RefreshThread(ImagePanel panel, long refreshInterval)
    {
        this.panel = panel;
        this.refreshInterval = refreshInterval;
    }   //RefreshThread

    /**
     * This method suspends the thread.
     */
    public synchronized void suspendThread()
    {
        threadResumed = false;
    }   //suspendThread

    /**
     * This method resumes the thread.
     */
    public synchronized void resumeThread()
    {
        threadResumed = true;
    }   //resumeThread

    /**
     * This method terminates the thread.
     */
    public synchronized void terminateThread()
    {
        threadRunning = false;
    }   //terminateThread

    /**
     * This method runs the thread. It refresh the panel with the camera image at refresh rate.
     */
    @Override
    public void run()
    {
        while (threadRunning)
        {
            //
            // Repaint the video pane every refreshInterval.
            //
            synchronized(this)
            {
                if (threadResumed)
                {
                    panel.captureImage();
                }
            }
            sleep(refreshInterval);
        }
    }   //run

    /**
     * This method puts the current thread to sleep for the given time in msec.
     * It handles InterruptException where it recalculates the remaining time
     * and calls sleep again repeatedly until the specified sleep time has past.
     *
     * @param sleepTime specifies sleep time in msec.
     */
    public static void sleep(long sleepTime)
    {
        long wakeupTime = System.currentTimeMillis() + sleepTime;

        while (sleepTime > 0)
        {
            try
            {
                Thread.sleep(sleepTime);
            }
            catch (InterruptedException e)
            {
            }
            sleepTime = wakeupTime - System.currentTimeMillis();
        }
    }   //sleep

}   //RefreshThread
