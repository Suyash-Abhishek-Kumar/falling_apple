import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.InputStream;

public class Main extends Frame {
    public static Frame menuGui;
    public static Main mainWindow;
    public static Image img;
    Main(){
        try {
            InputStream imageStream = getClass().getResourceAsStream("\\background_2.png");
            img = ImageIO.read(imageStream);
        } catch (IOException e) {
            System.out.println("Image could not be loaded.");
            e.printStackTrace();
        }
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    public void paint(Graphics g) {
        if (img != null) {
            g.drawImage(img, 0, 50, 400, 500, null);
        } else {
            g.drawString("Image not loaded.", 150, 200);
        }
    }
    public static void main(String[] args) {
        mainWindow = new Main();
        mainWindow.setSize(400, 550);
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setResizable(false);

        Panel panel = new Panel();
        Panel panel2 = new Panel();
        Button startGameButton = new Button("Start Game");
        Button exitButton = new Button("Exit");
        Label title = new Label("Apple Catching Game");
        Font myFont = new Font("Arial", Font.BOLD, 16);
        title.setFont(myFont);

        startGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainWindow.setVisible(false);
                new GameWindow();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        panel.add(startGameButton);
        panel.add(exitButton);
        panel2.add(title);
        mainWindow.add(panel2, BorderLayout.NORTH);
        mainWindow.add(panel, BorderLayout.SOUTH);
        mainWindow.setVisible(true);
    }
}