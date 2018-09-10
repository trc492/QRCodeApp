public class QRCodeApp
{
    private static final String MSG_FILE = "msg.png";
    private static final int IMAGE_WIDTH = 1024;
    private static final int IMAGE_HEIGHT = 1024;
    private static final String MSG = "http://www.titanrobotics.com";

    public static void main(String[] args)
    {
        try
        {
            QRCode.writeMessage(MSG, IMAGE_WIDTH, IMAGE_HEIGHT, MSG_FILE);
            System.out.println("Successfully generated QR code file " +  MSG_FILE);
        }
        catch (Exception e)
        {
            System.out.println("Failed to write QR code: " + e.getMessage());
        }

        try
        {
            System.out.println("QR code message: " + QRCode.readMessage(MSG_FILE));
        }
        catch (Exception e)
        {
            System.out.printf("Failed to read QR code: %s\n", e.getMessage());
        }
    }
}
