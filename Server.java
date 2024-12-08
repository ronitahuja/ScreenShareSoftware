
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Server {

    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(9090);
        System.out.println("Server started, waiting for client...");

        // Create a window to display the live screen feed
        JFrame frame = new JFrame("Client Live Screen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        JLabel label = new JLabel();
        frame.getContentPane().add(label, BorderLayout.CENTER);
        frame.setVisible(true);

        while (true) {
            Socket client = server.accept();
            System.out.println("Client connected");

            // Receive image data from the client
            InputStream in = client.getInputStream();
            DataInputStream dataIn = new DataInputStream(in);

            // Continuously receive images and display them
            while (true) {
                try {
                    int length = dataIn.readInt(); // Read the length of the image data
                    byte[] imageData = new byte[length];
                    dataIn.readFully(imageData); // Read the image data

                    // Convert the byte array to a BufferedImage
                    ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
                    BufferedImage image = ImageIO.read(bais);

                    // Display the image on the JFrame (overwrite the previous image)
                    label.setIcon(new ImageIcon(image));
                    frame.repaint(); // Immediately refresh the JFrame with the new image
                } catch (Exception e) {
                    System.out.println("Error receiving image: " + e);
                    break;
                }
            }

            client.close();
        }
    }
}
