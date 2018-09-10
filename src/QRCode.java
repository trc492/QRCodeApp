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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.HybridBinarizer;

/**
 * This class implements a simple QR Code encoder/decoder providing easy to use methods to encode messages into
 * QR code or decode QR code back to messages.
 *
 */
public class QRCode
{
    /**
     * This method encodes a message into QR code and returns the resulting image.
     * Note: if the message size is greater than the QR code capacity, it will throw a RuntimeException.
     *
     * @param msg specifies the message to be encoded.
     * @param width specifies the resulting image width in pixels.
     * @param height specifies the resulting image height in pixels.
     * @return QR code image.
     */
    public static BufferedImage encodeMessage(String msg, int width, int height)
    {
        BufferedImage image = null;

        try
        {
            image = MatrixToImageWriter.toBufferedImage(
                new MultiFormatWriter().encode(msg, BarcodeFormat.QR_CODE, width, height));
        }
        catch (WriterException e)
        {
            throw new RuntimeException(String.format("Failed to encode message: %s", e.getMessage()));
        }

        return image;
    }   //encodeMessage

    /**
     * This method decodes a QR code image and returns the resulting message.
     * Note: if there is no QR code in the image, it will throw a RuntimeException.
     *
     * @param image specifies the QR code image to be decoded.
     * @return decoded message.
     */
    public static String decodeMessage(BufferedImage image)
    {
        String msg = null;

        try
        {
            msg = new MultiFormatReader().decode(
                    new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)))).getText();
        }
        catch (NotFoundException e)
        {
            throw new RuntimeException(String.format("QR code not found in image: %s", e.getMessage()));
        }

        return msg;
    }   //decodeMessage

    /**
     * This method encodes a message into QR code and writes the resulting image to the specified file.
     *
     * @param msg specifies the message to be encoded.
     * @param width specifies the resulting image width in pixels.
     * @param height specifies the resulting image height in pixels.
     * @param filePath specifies the file path for writing the QR code image.
     * @throws IOException if the operation failed (e.g. invalid file path).
     */
    public static void writeMessage(String msg, int width, int height, String filePath) throws IOException
    {
        ImageIO.write(
            encodeMessage(msg, width, height),
            filePath.substring(filePath.lastIndexOf('.') + 1),
            new File(filePath));
    }   //writeMessage

    /**
     * This method decodes a QR code image file and returns the resulting message.
     * 
     * @param filePath specifies the QR code image file.
     * @return decoded message.
     * @throws IOException if the operation failed (e.g. invalid file path).
     */
    public static String readMessage(String filePath) throws IOException
    {
        return decodeMessage(ImageIO.read(new File(filePath)));
    }   //readMessage

}   //class QRCode
