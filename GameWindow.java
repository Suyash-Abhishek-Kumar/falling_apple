import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;

public class GameWindow extends JFrame implements WindowListener {
    private static final long serialVersionUID = 1L;
    private GamePanel gamePanel;

    public GameWindow() {
        setFrame();
        addComponents();
        setVisible(true);
    }

    private void setFrame() {
        setSize(400, 550); // Adjusted size to fit game panel
        setTitle("Apple Catching Game");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(this);
    }

    private void addComponents() {
        gamePanel = new GamePanel();
        gamePanel.setBackground(Color.CYAN); // Background color for the game
        add(gamePanel);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        System.out.println("Window was closed");
        if (gamePanel != null) {
            gamePanel.windowClose(); // Ensures the game stops on close
        }
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }
}
