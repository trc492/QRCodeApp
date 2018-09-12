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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * This program can take a text message and generate a QR code or it can take a QR code image and decode the
 * text message. The program can take one or no parameter during start up. With no parameter, it will start
 * up the user interface and allow the user to either do a "File->Open Image" of an image file or type a message
 * to generate the QR code image. With one parameter, it will either specify:
 *      java -jar qrcodeapp.jar image=<ImageFile>
 * or
 *      java -jar qrcodeapp.jar msg="<MessageText>"
 *
 * When the program starts up, it will display the message and the corresponding QR code image. It also allows the
 * user to save the QR code image to a file by doing "File->Save Image".
 */
public class QRCodeApp extends JApplet implements ActionListener, WindowListener
{
    private static final long serialVersionUID = 1L;
    public static final String PROGRAM_TITLE = "QR Code Application";
    public static final String COPYRIGHT_MSG = "Copyright (c) Titan Robotics Club";
    public static final String PROGRAM_VERSION = "[version 1.0.0]";

    private static final int ERROR_NONE = 0;
    private static final int ERROR_INVALID_NUM_ARGUMENTS = -1;
    private static final int ERROR_INVALID_ARGUMENT = -2;

    private static final int IMAGE_WIDTH = 640;
    private static final int IMAGE_HEIGHT = 480;
    private static final int BORDER_SIZE = 20;
    private static final int MENU_BAR_HEIGHT = 70;
    private static final int MSG_LABEL_WIDTH = 60;
    private static final int MSG_LABEL_HEIGHT = 20;
    private static final int MSG_WIDTH = IMAGE_WIDTH - MSG_LABEL_WIDTH;
    private static final int MSG_HEIGHT = MSG_LABEL_HEIGHT;
    private static final int FRAME_WIDTH = IMAGE_WIDTH + 2*BORDER_SIZE;
    private static final int FRAME_HEIGHT = MENU_BAR_HEIGHT + IMAGE_HEIGHT + MSG_HEIGHT + 2*BORDER_SIZE;
    private static final int IMAGE_X = BORDER_SIZE;
    private static final int IMAGE_Y = BORDER_SIZE;
    private static final int MSG_LABEL_X = BORDER_SIZE;
    private static final int MSG_LABEL_Y = FRAME_HEIGHT - MSG_HEIGHT - BORDER_SIZE - 50;
    private static final int MSG_X = BORDER_SIZE + MSG_LABEL_WIDTH;
    private static final int MSG_Y = MSG_LABEL_Y;

    private static String imageFile = null;
    private static String msg = null;

    private MenuBar menuBar = new MenuBar(this);
    private ImagePanel imagePanel = new ImagePanel();
    private JLabel msgLabel = new JLabel("Message:");
    private JTextField msgPanel = new JTextField();

    /**
     * This is the entry point of the program. It parses the parameters, creates the main window of the program,
     * set the proper size and location and initialized all the UI elements.
     *
     * @param args specifies the command line parameters.
     */
    public static void main(String[] args)
    {
        int exitCode = ERROR_NONE;

        if (args.length > 1)
        {
            //
            // Must have zero or one parameter.
            //
            exitCode = ERROR_INVALID_NUM_ARGUMENTS;
        }
        else
        {
            if (args.length == 1)
            {
                int sepIndex = args[0].indexOf('=');
                //
                // Has one parameter, must be in the form of:
                //  <left>=<right>
                //
                if (sepIndex == -1)
                {
                    exitCode = ERROR_INVALID_ARGUMENT;
                }
                else
                {
                    String left = args[0].substring(0, sepIndex);
                    String right = args[0].substring(sepIndex + 1);
                    //
                    // Argument is either a message to be encoded into QR code or an image file name to be decoded
                    // into a message.
                    //
                    if (left.equalsIgnoreCase("image"))
                    {
                         imageFile = right;
                    }
                    else if (left.equalsIgnoreCase("msg"))
                    {
                        msg = right;
                    }
                    else
                    {
                        exitCode = ERROR_INVALID_ARGUMENT;
                    }
                }
            }

            if (exitCode == ERROR_NONE)
            {
                JFrame frame = new JFrame(PROGRAM_TITLE);
                JApplet app = new QRCodeApp(frame, imageFile, msg);
                frame.add(app);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(null);
                frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
                frame.setResizable(false);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        }

        if (exitCode != ERROR_NONE)
        {
            printUsage();
            System.exit(exitCode);
        }
    }   //main

    /**
     * This method prints the program title and usage syntax.
     */
    private static void printUsage()
    {
        System.out.printf("%s %s\n%s\n", PROGRAM_TITLE, PROGRAM_VERSION, COPYRIGHT_MSG);
        System.out.println("Usage: %s [file=<ImageFile> | msg=<Message>]");
    }   //printUsage

    /**
     * Constructor: Creates an instance of the application.
     *
     * @param frame specifies the main window frame.
     * @param imageFile specifies the optional image file parameter (can be null).
     * @param msg specifies the optional message text (can be null).
     */
    public QRCodeApp(JFrame frame, String imageFile, String msg)
    {
        frame.setJMenuBar(menuBar);
        frame.add(imagePanel);
        frame.add(msgLabel);
        frame.add(msgPanel);
        frame.addWindowListener(this);

        imagePanel.setBounds(IMAGE_X, IMAGE_Y, IMAGE_WIDTH, IMAGE_HEIGHT);

        msgLabel.setBounds(MSG_LABEL_X, MSG_LABEL_Y, MSG_LABEL_WIDTH, MSG_LABEL_HEIGHT);
        msgPanel.setBounds(MSG_X, MSG_Y, MSG_WIDTH, MSG_HEIGHT);
        msgPanel.addActionListener(this);

        if (imageFile != null)
        {
            //
            // There is an image file parameter, display the image and decode the message.
            //
            setImageFile(new File(imageFile));
        }
        else if (msg != null)
        {
            //
            // There is a message parameter, display the text and generate the image.
            //
            setMessageText(msg);
        }
    }   //QRCodeApp

    /**
     * This method displays the image file and decodes the message.
     *
     * @param imageFile specifies the image file.
     * @return true if the operation is successful, false otherwise.
     */
    public boolean setImageFile(File imageFile)
    {
        boolean success = false;

        try
        {
            BufferedImage image = ImageIO.read(imageFile);
            imagePanel.setImage(image);
            msgPanel.setText(QRCode.decodeMessage(image));
            success = true;
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(
                this,
                String.format("Failed to read image file %s.", imageFile),
                QRCodeApp.PROGRAM_TITLE,
                JOptionPane.ERROR_MESSAGE);
        }
        catch (RuntimeException e)
        {
            JOptionPane.showMessageDialog(
                this, "QR code not found in image.", QRCodeApp.PROGRAM_TITLE, JOptionPane.ERROR_MESSAGE);
        }

        return success;
    }   //setImageFile

    /**
     * This method saves the image to the specified file.
     *
     * @param imageFile specifies the file path to save the image file to.
     */
    public void saveImageFile(File imageFile)
    {
        try
        {
            String formatName = imageFile.getName();
            formatName = formatName.substring(formatName.lastIndexOf('.') + 1);
            ImageIO.write(imagePanel.getImage(), formatName, imageFile);
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(
                this,
                String.format("Failed to write image file %s.", imageFile),
                QRCodeApp.PROGRAM_TITLE,
                JOptionPane.ERROR_MESSAGE);
        }
    }   //saveImageFile

    /**
     * This method displays the text message and generates the corresponding QR code image.
     *
     * @param msg specifies the message.
     */
    public void setMessageText(String msg)
    {
        msgPanel.setText(msg);
        imagePanel.setImage(QRCode.encodeMessage(msg, IMAGE_WIDTH, IMAGE_HEIGHT));
    }   //setMessageText

    /**
     * This method starts the camera by resuming the camera thread.
     */
    public void startCamera()
    {
        imagePanel.startCamera();
    }   //startCamera

    /**
     * This method stops the camera by suspending the camera thread.
     */
    public void stopCamera()
    {
        imagePanel.stopCamera();
    }   //stopCamera

    /**
     * This method captures an image from the camera, decodes the QR code in the image and update the text message.
     *
     * @return true if operation is successful, false otherwise.
     */
    public boolean captureImage()
    {
        boolean success = false;

        imagePanel.captureImage();
        BufferedImage image = imagePanel.getImage();
        try
        {
            msgPanel.setText(QRCode.decodeMessage(image));
            success = true;
        }
        catch (RuntimeException e)
        {
            JOptionPane.showMessageDialog(
                this, "QR code not found in image.", QRCodeApp.PROGRAM_TITLE, JOptionPane.ERROR_MESSAGE);
        }

        return success;
    }   //captureImage

    /**
     * This method terminates the program by terminating the camera thread and exiting the program.
     */
    public void terminateProgram()
    {
        imagePanel.terminateCameraThread();
        System.exit(0);
    }   //terminateProgram

    //
    // Implements ActionListener interface.
    //

    /**
     * This method is called when the user enters a message in the msgPanel.
     *
     * @param event specifies the event that caused this callback.
     */
    @Override
    public void actionPerformed(ActionEvent event)
    {
        //
        // Message has changed, update the image.
        //
        imagePanel.setImage(QRCode.encodeMessage(msgPanel.getText(), IMAGE_WIDTH, IMAGE_HEIGHT));
    }   //actionPerformed

    //
    // Implements WindowListener interface.
    //

    @Override
    public void windowActivated(WindowEvent e)
    {
    }   //windowActivated

    @Override
    public void windowClosed(WindowEvent e)
    {
    }   //windowClosed

    /**
     * This method is called when the "X" Window Close button is clicked. This will exit the program. It makes sure
     * the program shuts down properly by terminating the camera thread releasing the camera.
     */
    @Override
    public void windowClosing(WindowEvent e)
    {
        terminateProgram();
    }   //windowClosing

    @Override
    public void windowDeactivated(WindowEvent e)
    {
    }   //windowDeactivated

    @Override
    public void windowDeiconified(WindowEvent e)
    {
    }   //windowDeiconified

    @Override
    public void windowIconified(WindowEvent e)
    {
    }   //windowIconified

    @Override
    public void windowOpened(WindowEvent e)
    {
    }   //windowOpened

}   //class QRCodeApp
