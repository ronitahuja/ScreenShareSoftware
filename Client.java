/*
 * to create a jar file: jar cmf manifest.txt Client.jar Client.class
 * to run the jar file: java -jar Client.jar
 * c: Create a new JAR file.
 * m: Include a manifest file in the JAR. This allows you to specify the entry point (Main-Class) and other metadata for your JAR file.
 * f: Specify the name of the JAR file
 */

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Client {

    private static boolean running = true; // Controls the while loop

    public static void main(String[] args) throws Exception {
        String serverIp = "192.168.0.106"; // Update with your server IP
        int port = 9090;

        // Create a JFrame for the UI
        JFrame frame = new JFrame("Client Control");
        JButton stopButton = new JButton("Stop Client");
        stopButton.setFont(new Font("Arial", Font.BOLD, 14));
        stopButton.addActionListener(e -> {
            running = false; // Stop the loop
            System.out.println("Stopping client...");
            frame.dispose(); // Close the UI
        });
        frame.add(stopButton);
        frame.setSize(200, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setVisible(true);

        try {
            Socket client = new Socket(serverIp, port);
            OutputStream out = client.getOutputStream();
            DataOutputStream dataOut = new DataOutputStream(out);
            System.out.println("Connected to server");

            // Create a Robot object to capture the screen
            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

            // Set frame rate (30 FPS)
            int fps = 30;
            long frameTime = 1000 / fps;

            while (running) {
                long startTime = System.currentTimeMillis();

                BufferedImage screenCapture = robot.createScreenCapture(screenRect);

                // Write the image to a ByteArrayOutputStream
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(screenCapture, "png", baos);
                byte[] imageData = baos.toByteArray();

                // Send the length of the image data and the image itself
                dataOut.writeInt(imageData.length);
                dataOut.write(imageData);

                // Flush the stream to ensure data is sent
                dataOut.flush();

                // Calculate time taken for this frame and adjust sleep time to maintain constant frame rate
                long timeTaken = System.currentTimeMillis() - startTime;
                long sleepTime = frameTime - timeTaken;
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
            }

            // Close resources when stopping
            dataOut.close();
            client.close();
            System.out.println("Client stopped.");
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        finally {
            running = false; // Stop the loop
            frame.dispose(); // Close the UI
            System.exit(0); // Exit the program
        }
    }
}
