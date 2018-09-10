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

import java.awt.event.*;
import java.io.File;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This class constructs the menu bar of the program.
 */
public class MenuBar extends JMenuBar implements ActionListener
{
    private static final long serialVersionUID = 2L;
    private QRCodeApp app;
    private JFileChooser fileChooser = new JFileChooser();

    private JMenu menuFile = new JMenu("File");
    private JMenuItem menuItemFileOpen = new JMenuItem("Open Image");
    private JMenuItem menuItemFileSave = new JMenuItem("Save Image");
    private JMenuItem menuItemFileExit = new JMenuItem("Exit");

    private JMenu menuHelp = new JMenu("Help");
    private JMenuItem menuItemHelpAbout = new JMenuItem("About");

    /**
     * Constructor: Create an instance of the object. It initializes all the menu items in the menu bar.
     *
     * @param app specifies the parent application object.
     */
    public MenuBar(QRCodeApp app)
    {
        this.app = app;
        //
        // Create and initialize a global FileChooser object for File->Open.
        //
        String[] formats = ImageIO.getWriterFormatNames();
        String desc = String.format("Image file %s", Arrays.toString(formats));
        fileChooser.setFileFilter(new FileNameExtensionFilter(desc, formats));
//        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

        menuFile.setMnemonic(KeyEvent.VK_F);
        menuItemFileOpen.setMnemonic(KeyEvent.VK_O);
        menuItemFileOpen.addActionListener(this);
        menuItemFileSave.setMnemonic(KeyEvent.VK_S);
        menuItemFileSave.addActionListener(this);
        menuItemFileExit.setMnemonic(KeyEvent.VK_X);
        menuItemFileExit.addActionListener(this);

        menuHelp.setMnemonic(KeyEvent.VK_H);
        menuItemHelpAbout.setMnemonic(KeyEvent.VK_A);
        menuItemHelpAbout.addActionListener(this);

        menuFile.add(menuItemFileOpen);
        menuFile.add(menuItemFileSave);
        menuFile.addSeparator();
        menuFile.add(menuItemFileExit);

        menuHelp.add(menuItemHelpAbout);

        add(menuFile);
        add(menuHelp);
    }   //MenuBar

    //
    // Implements ActionListener interface.
    //

    /**
     * This method is called when a File menu item is clicked.
     *
     * @param event specifies the event that caused this callback.
     */
    @Override
    public void actionPerformed(ActionEvent event)
    {
        Object source = event.getSource();

        if (source == menuItemFileOpen)
        {
            //
            // File->Open is clicked.
            //
            fileChooser.setSelectedFile(new File(""));
            int returnVal = fileChooser.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                //
                // The user clicked "open" approving the file choice.
                //
                app.setImageFile(fileChooser.getSelectedFile());
            }
        }
        else if (source == menuItemFileSave)
        {
            //
            // File->Save is clicked.
            //
            fileChooser.setSelectedFile(new File(""));
            int returnVal = fileChooser.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                //
                // The user clicked "open" approving the file choice.
                //
                app.saveImageFile(fileChooser.getSelectedFile());
            }
        }
        else if (source == menuItemFileExit)
        {
            //
            // File->Exit is clicked.
            //
            System.exit(0);
        }
        else if (source == menuItemHelpAbout)
        {
            //
            // Help->About is clicked.
            //
            JOptionPane.showMessageDialog(
                    this,
                    QRCodeApp.PROGRAM_TITLE + " " + QRCodeApp.PROGRAM_VERSION + "\n" + QRCodeApp.COPYRIGHT_MSG + "\n",
                    QRCodeApp.PROGRAM_TITLE,
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }   //actionPerformed

}   //class MenuBar
