package javasgsproject;

import java.util.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

// Player class that represents a player in the simulation
public class Player {
    int age;
    boolean isAlive = true;
    String name;
    double mood;
    int intelligence;
    int charisma;
    char gender;
    double disappointments;
    Map<Player, Double> relationships = new HashMap<>();
    Map<Player, Integer> acquaintances = new HashMap<>();
    ArrayList<Message> receivedMessages = new ArrayList<>();
    ArrayList<Message> sentMessages = new ArrayList<>();
    int receivedMessagesCounter = 0;
    int sentMessagesCounter = 0;
    int maxAcquaintances = 10;
    Player partner;
    static Set<String> usedNames = new HashSet<>();
    static Random random = new Random();
    public int x;
    public int y;
    static List<Player> allPlayers = new ArrayList<>();
    Player parent1 = null;
    Player parent2 = null;
    int childrenCount = 0;
    Color dotColor;
    final Color BLUE = new Color(33, 44, 255);
    final Color GREEN = new Color(118, 252, 61);
    final Color PURPLE = new Color(225, 156, 255);
    static int SCREEN_WIDTH;
    static int SCREEN_HEIGHT;

    // Static block for getting screen size
    static {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        SCREEN_WIDTH = (int) screenSize.getWidth();
        SCREEN_HEIGHT = (int) screenSize.getHeight();
    }

    // Constructor
    // Player(Player parent1, Player parent2) {
    //     this.age = 0;
    //     this.name = generateUniqueName();
    //     this.acquaintances.put(this, 0);
    //     this.mood = 0;
    //     this.parent1 = parent1;
    //     this.parent2 = parent2;
    //     // this.intelligence = new Random().nextInt(201) - 100;
    //     // this.charisma = new Random().nextInt(201) - 100;
    //     this.intelligence = (parent1.intelligence+parent2.intelligence)/2;
    //     this.charisma = parent1.charisma/2+parent2.charisma/2;
    //     do {
    //         this.x = 50 + random.nextInt(600);
    //         this.y = 50 + random.nextInt(700);
    //     } while (isPositionTaken(this.x, this.y));
    //     allPlayers.add(this);
    //     this.gender = random.nextBoolean() ? 'M' : 'F';
    //     this.partner = null;
    // }

    Player(boolean isIntelligent, boolean isCharismatic) {
        this.age = random.nextInt(100);
        this.name = generateUniqueName();
        this.acquaintances.put(this, 0);
        this.mood = 0;
        this.intelligence = random.nextInt(101);
        if (!isIntelligent) {
            this.intelligence -= 100;
        }
        this.charisma = random.nextInt(101);
        if (!isCharismatic) {
            this.charisma -= 100;
        } else {
            this.maxAcquaintances = 20;
        }
        if (isIntelligent && isCharismatic) {
            this.dotColor = GREEN;
        } else if (isCharismatic) {
            this.dotColor = PURPLE;
        } else if (isIntelligent) {
            this.dotColor = BLUE;
        } else {
            this.dotColor = Color.BLACK;
        }
        do {
            this.x = 50 + random.nextInt(SCREEN_WIDTH-950);
            this.y = 50 + random.nextInt(SCREEN_HEIGHT-200);
        } while (isPositionTaken(this.x, this.y));
        allPlayers.add(this);
        this.gender = random.nextBoolean() ? 'M' : 'F';
        this.partner = null;
    }    
    // Generate a unique name for each player (Player + some random number)
    private static String generateUniqueName() {
        String name;
        int i = 0;
        do {
            name = "Player" + i++;
        } while (usedNames.contains(name));
        usedNames.add(name);
        return name;
    }

    // Check if the position (In the GUI) is already taken by another player
    private boolean isPositionTaken(int x, int y) {
        for (Player player : allPlayers) {
            if (player.x == x && player.y == y) {
                return true;
            }
        }
        return false;
    }

    // Add an acquaintance to the player
    void addAcquaintance(Player acquaintance) {
        if (this.acquaintances.size() < this.maxAcquaintances &&
                acquaintance.acquaintances.size() < acquaintance.maxAcquaintances) {
            this.acquaintances.put(acquaintance, this.acquaintances.size());
            acquaintance.acquaintances.put(this, acquaintance.acquaintances.size());
        }
    }

    // Add a random acquaintance to the player with a 20% chance
    public void addRandomAcquaintance(Player acquaintance) {
        if ((this.acquaintances.size() < this.maxAcquaintances) && Math.random() < 0.2 &&
                acquaintance.acquaintances.size() < acquaintance.maxAcquaintances && Math.random() < 0.2) {
            this.acquaintances.put(acquaintance, this.acquaintances.size());
            acquaintance.acquaintances.put(this, acquaintance.acquaintances.size());
        }
    }

    // Break acquaintance with a player
    public void breakAcquaintance(Player friend) {
        if (this == friend) {
            return;
        }
        if (this.partner == friend) {
            return;
        }
        if ((this.relationships.get(friend) != null && this.relationships.get(friend) < 0) ||
                (friend.relationships.get(this) != null && friend.relationships.get(this) < 0)) {
            System.out.println(this.name + " broke acquaintance with " + friend.name);
            this.acquaintances.remove(friend);
            this.relationships.remove(friend);
            friend.acquaintances.remove(this);
            friend.relationships.remove(this);
        }
    }

    // Sigmoid function to calculate positivity scale
    private double sigmoid(double x) {
        return (1 / (1 + Math.pow(Math.E, (-1 * x))));
    }

    // Send a message to all acquaintances
    public void sendMessage() {
        double positivityScale;

        for (Player acquaintance : this.acquaintances.keySet()) {

            // if (this.mood > 0){
            // positivityScale = sigmoid(this.mood) * this.charisma / 10;
            // }
            // else if (this.mood < 0){
            // positivityScale = sigmoid(this.mood) * this.charisma / 10;
            // }
            // else{
            // positivityScale = 0 + this.charisma / 10;
            // }
            positivityScale = sigmoid(this.mood) + this.charisma / 10;

            if (!this.receivedMessages.isEmpty()) {
                Message lastMessage = this.receivedMessages.get(this.receivedMessages.size() - 1);
                if (lastMessage.positivityScale > 0) {
                    positivityScale += random.nextInt(2);
                } else if (lastMessage.positivityScale < 0) {
                    positivityScale -= random.nextInt(2);
                }
            }
            Message message = new Message(this, acquaintance, positivityScale);
            sentMessages.add(message);
            sentMessagesCounter++;
            acquaintance.receiveMessage(message);
        }
    }

    // Receive a message from another player
    public void receiveMessage(Message message) {
        receivedMessages.add(message);
        receivedMessagesCounter++;
        if (!this.relationships.containsKey(message.sender)) {
            this.relationships.put(message.sender, 0.0);
        }
        double newScore = this.relationships.get(message.sender) + message.positivityScale + this.intelligence / 10;

        this.relationships.put(message.sender, newScore);
        message.sender.relationships.put(this, newScore);
        if (message.sender.mood < 0) {
            addDisappointment(10);
        }
    }

    // Calculate the average positivity scale of received messages
    public double calculateAverageReceivedPositivityScale() {
        if (receivedMessages.isEmpty()) {
            return 0.0;
        }

        int totalReceivedPositivityScale = 0;
        for (Message message : receivedMessages) {
            totalReceivedPositivityScale += message.positivityScale;
        }

        return (double) totalReceivedPositivityScale / receivedMessages.size();
    }

    // Calculate the average positivity scale of sent messages
    public double calculateAverageSentPositivityScale() {
        if (sentMessages.isEmpty()) {
            return 0.0;
        }

        int totalSentPositivityScale = 0;
        for (Message message : sentMessages) {
            totalSentPositivityScale += message.positivityScale;
        }

        return (double) totalSentPositivityScale / receivedMessages.size();
    }

    // Change the mood of the player based on the average positivity scale of sent
    // and received messages
    public void changeMood() {
        double sentPositivityScale = calculateAverageSentPositivityScale();
        double receivedPositivityScale = calculateAverageReceivedPositivityScale();
        double moodChange;
        moodChange = sentPositivityScale - receivedPositivityScale;
        // System.out.println("Mood Change: " + moodChange);
        // System.out.println("Average Sent Positivity Scale: " +
        // calculateAverageSentPositivityScale());
        // System.out.println("Average Received Positivity Scale: " +
        // calculateAverageReceivedPositivityScale());
        this.mood = mood * 0.7 + moodChange * 0.3;
    }

    // Check if the player contains a point (x, y) for the GUI
    public boolean contains(int x, int y) {
        return Math.sqrt(Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2)) <= 10;
    }

    // Get the average relationship score of the player
    public double getAverageRelationshipScore() {
        if (relationships.isEmpty()) {
            return 0;
        }
        double totalScore = 0;
        for (double score : relationships.values()) {
            totalScore += score;
        }
        return (double) totalScore / relationships.size();
    }

    // Check if the player is an acquaintance of another player
    public boolean isAcquaintance(Player otherPlayer) {
        return this.acquaintances.containsKey(otherPlayer);
    }

    // Method to add a disappointment to the player
    public void addDisappointment(double disappointment) {
        this.disappointments += disappointment;
    }

    public double getMood() {
        return this.mood;
    }
}
