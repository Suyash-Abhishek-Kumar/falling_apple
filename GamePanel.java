import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

class Apple {
    Point position;
    boolean isRotten;
    boolean isGolden;

    Apple(Point position, boolean isRotten, boolean isGolden) {
        this.position = position;
        this.isRotten = isRotten;
        this.isGolden = isGolden;
    }
}

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private ArrayList<Apple> apples;
    private int basketX;
    private  int basketWidth = 120;
    private final int appleSize = 30;
    private int score = 0;
    private int highScore = 0;
    private boolean isGameOver = false;
    private JButton tryAgainButton;
    private int level = 1;
    private long lastAppleTime;
    private long shrinkTime;
    private String playerName;
    private BufferedImage appleImage;
    private BufferedImage rottenappleImage;
    private BufferedImage goldenappleImage;
    private BufferedImage backgroundImage;
    private ArrayList<PlayerScore> leaderboard = new ArrayList<>();

    public GamePanel() {
        apples = new ArrayList<>();
        basketX = getWidth() / 2 - basketWidth / 2;
        try {
            appleImage = ImageIO.read(new File("lol/apple.png"));
            appleImage = resizeImage(appleImage, appleSize, appleSize);
            rottenappleImage = ImageIO.read(new File("lol/apple_rotten.png"));
            rottenappleImage = resizeImage(rottenappleImage, appleSize, appleSize);
            goldenappleImage = ImageIO.read(new File("lol/golden_apple.png"));
            goldenappleImage = resizeImage(goldenappleImage, appleSize*2, appleSize*2);
            backgroundImage = ImageIO.read(new File("lol/background_1.png"));
        } catch (IOException e) {
            System.out.println("Error loading image: " + e.getMessage());
        }
        setFocusable(true);
        addKeyListener(this);
        
        // Ask the user for their leaderboard name before starting
        playerName = JOptionPane.showInputDialog(null, "Enter your name for the leaderboard:", "Leaderboard Name", JOptionPane.PLAIN_MESSAGE);
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "User"; // Default name if user cancels or enters nothing
        }
        timer = new Timer(30, this);
        timer.start();
        tryAgainButton = new JButton("Try Again");
        tryAgainButton.setVisible(false);
        tryAgainButton.addActionListener(e -> resetGame());
        add(tryAgainButton);
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g.dispose();
        return resizedImage;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Level: " + level, getWidth() - 100, 30);
        g.drawString("Player: " + playerName, 10, 30);

        if (isGameOver) {
            g.setColor(Color.RED);
            g.drawString("Game Over!", getWidth() / 2 - 50, getHeight() / 2 - 30);
            g.drawString("Score: " + score, getWidth() / 2 - 30, getHeight() / 2);
            g.drawString("High Score: " + highScore, getWidth() / 2 - 50, getHeight() / 2 + 30);
            tryAgainButton.setVisible(true);
            tryAgainButton.setBounds(getWidth() / 2 - 50, getHeight() / 2 + 40, 100, 30); // Closer to the message
        } else {
            g.setColor(Color.RED);
            for (Apple apple : apples) {
                Point pos = apple.position;
                if (apple.isRotten) {
                    g.drawImage(rottenappleImage, pos.x, pos.y, this);
                } else if (apple.isGolden) {
                    g.drawImage(goldenappleImage, pos.x, pos.y, this);
                } else {
                    g.drawImage(appleImage, pos.x, pos.y, this);
                }
            }            
            g.setColor(new Color(139, 69, 19));
            g.fillRect(basketX, getHeight() - 50, basketWidth, 20);
            g.setColor(Color.BLACK);
            g.drawString("Score: " + score, 10, 60);
            g.drawString("High Score: " + highScore, 10, 90);
        }
    }

    int randint(int min, int max){
        return (int)(Math.random() * (max-min+1)) + min;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isGameOver) {
            long currentTime = System.currentTimeMillis();
            // Spawn apples based on level
            if (currentTime - lastAppleTime > 1800/level) {
                spawnApple(1);
                lastAppleTime = currentTime;
            }
            if (basketWidth == 90 && currentTime - shrinkTime > 2000) {
                spawnApple(1);
                shrinkTime = currentTime;
            }

            for (int i = apples.size() - 1; i >= 0; i--) {
                boolean golden = apples.get(i).isGolden;
                boolean rotten = apples.get(i).isRotten;
                Point apple = apples.get(i).position;
                apple.y += 5;
                if (apple.y > getHeight() - 50 && apple.x + appleSize >= basketX && apple.x <= basketX + basketWidth) {
                    if (rotten){
                        basketWidth = 90;
                    } else if (golden){
                        score+=5;
                        apples.remove(i);
                        checkLevelUp();
                    }
                    else{
                        score++;
                        apples.remove(i);
                        checkLevelUp();
                    }
                } else if (apple.y > getHeight() && !rotten) {
                    isGameOver = true;
                    timer.stop();
                    updateLeaderboard();
                }
            }

            if (score > highScore) {
                highScore = score;
            }

            repaint();
        }
    }

    private void spawnApple(int count) {
        int lastX = -1;
        for (int i = 0; i < count; i++) {
            int x_coord = (int) (Math.random() * (getWidth() - appleSize));
            boolean isGolden = false;
            boolean isRotten = Math.random() < 0.3;
            if (!isRotten){ isGolden = Math.random() < 0.3; }
            if (lastX == -1 || Math.abs(x_coord - lastX) >= 30) {
                apples.add(new Apple(new Point(x_coord, -1 * randint(0, 100)), isRotten, isGolden));
            } else {
                int offset = randint(-30, 30);
                x_coord = Math.max(0, Math.min(x_coord + offset, getWidth() - appleSize));
                apples.add(new Apple(new Point(x_coord, -1 * randint(0, 100)), isRotten, isGolden));
            }
            lastX = x_coord;
        }
    }
    
    private void checkLevelUp() { level = (int)(score/10)+1; }

    private void updateLeaderboard() {
        leaderboard.add(new PlayerScore(playerName, score));
        leaderboard.sort(null);
        displayScoreBoard();
    }

    private void displayScoreBoard() {
        leaderboard.sort(null);
        StringBuilder leaderboardText = new StringBuilder("Leaderboard:\n");
        for (int i = 0; i < Math.min(3, leaderboard.size()); i++) {
            PlayerScore ps = leaderboard.get(i);
            leaderboardText.append(ps.getName()).append(": ").append(ps.getScore()).append("\n");
        }
        JOptionPane.showMessageDialog(this, "Game Over\n" + playerName + " scored: " + score + "\n" + leaderboardText);
    }
    

    private void resetGame() {
        isGameOver = false;
        score = 0;
        level = 1;
        apples.clear();
        basketX = getWidth() / 2 - basketWidth / 2;
        lastAppleTime = System.currentTimeMillis();
        tryAgainButton.setVisible(false);
        playerName = JOptionPane.showInputDialog(null, "Enter your name for the leaderboard:", "Leaderboard Name", JOptionPane.PLAIN_MESSAGE);
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "User"; // Default name if user cancels or enters nothing
        }
        timer.start();
        repaint();
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!isGameOver) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT && basketX > 0) {
                basketX -= 20;
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && basketX < getWidth() - basketWidth) {
                basketX += 20;
            }
            repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
    public void windowClose() {
        // Stop the animation when the window is closed
        timer.stop();
    }
}
