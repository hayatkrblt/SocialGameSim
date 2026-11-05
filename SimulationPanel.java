package javasgsproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

// Panel that displays the simulation
public class SimulationPanel extends JPanel {
    private ArrayList<Player> players;
    private Player selectedPlayer;

    // Constructor
    public SimulationPanel(ArrayList<Player> players) {
        this.players = players;
        setupMouseMotionListener();
    }

    // Update the players in the simulation panel
    public void updatePlayers(ArrayList<Player> updatedPlayers) {
        this.players = updatedPlayers;
    }

    // Set up the mouse motion listener which selects a player when the mouse is moved
    private void setupMouseMotionListener() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                selectPlayerAt(e.getX(), e.getY());
                repaint();
            }
        });
    }

    // Select a player at the given coordinates (x, y)
    private void selectPlayerAt(int x, int y) {
        selectedPlayer = null;
        for (Player player : players) {
            if (player != null && player.contains(x, y) && player.isAlive) {
                selectedPlayer = player;
                break;
            }
        }
    }

    // Paint the simulation panel
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        setupGraphics(g2);
        drawPlayers(g2);
        drawSelectedPlayerInfo(g2);
    }

    // Set up the graphics for the simulation panel
    private void setupGraphics(Graphics2D g2) {
        Font currentFont = g2.getFont();
        Font boldFont = currentFont.deriveFont(Font.BOLD);
        g2.setFont(boldFont);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    // Draw all the players in the simulation panel
    private void drawPlayers(Graphics2D g2) {
        ArrayList<Player> playersCopy = new ArrayList<>(players);
        for (Player player : playersCopy) {
            if (player != null && player.isAlive) {
                drawPlayer(g2, player);
            }
        }
    }

    // Draw a single player in the simulation panel along with its connections
    private void drawPlayer(Graphics2D g2, Player player) {
        g2.setColor(player.dotColor);
        g2.fillOval(player.x, player.y, 10, 10);
        drawPlayerConnections(g2, player);
    }

    // Draw the connections of a player in the simulation panel
    private void drawPlayerConnections(Graphics2D g2, Player player) {
        Set<Player> acquaintancesCopy = new HashSet<>(player.acquaintances.keySet());
        for (Player acquaintance : acquaintancesCopy) {
            if (acquaintance != null) {
                g2.setColor(Color.BLACK);
                g2.drawLine(player.x + 5, player.y + 5, acquaintance.x + 5, acquaintance.y + 5);
            }
        }
    }

    // Draw the information of the selected player in the simulation panel
    private void drawSelectedPlayerInfo(Graphics2D g2) {
        if (selectedPlayer != null) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int width = (int) screenSize.getWidth();
            int height = (int) screenSize.getHeight();
            int baseX = width - 1000;
            int baseY = height - 900;
            int lineSpacing = 20;
            g2.drawString(selectedPlayer.name, baseX, baseY);
            g2.drawString("Mood: " + String.format("%.2f", selectedPlayer.mood), baseX, baseY + lineSpacing);
            g2.drawString("Intelligence: " + selectedPlayer.intelligence, baseX, baseY + 2 * lineSpacing);
            g2.drawString("Charisma: " + selectedPlayer.charisma, baseX, baseY + 3 * lineSpacing);
            g2.drawString("Age: " + selectedPlayer.age, baseX, baseY + 4 * lineSpacing);
            g2.drawString("Acquaintances: " + selectedPlayer.acquaintances.size(), baseX, baseY + 5 * lineSpacing);
            g2.drawString("Relationship score: " + String.format("%.2f", selectedPlayer.getAverageRelationshipScore()),
                    baseX, baseY + 6 * lineSpacing);
            g2.drawString("Gender: " + selectedPlayer.gender, baseX, baseY + 7 * lineSpacing);
            if (selectedPlayer.parent1 != null && selectedPlayer.parent2 != null){
                g2.drawString("Parent1: " + selectedPlayer.parent1.name, baseX, baseY + 8 * lineSpacing);
                g2.drawString("Parent2: " + selectedPlayer.parent2.name, baseX, baseY + 9 * lineSpacing);
                g2.drawString("Parent1 Int: " + selectedPlayer.parent1.intelligence, baseX, baseY + 10 * lineSpacing);
                g2.drawString("Parent2 Int: " + selectedPlayer.parent2.intelligence, baseX, baseY + 11 * lineSpacing);
            }
        }
    }
}
