package javasgsproject;

// Handles the message object that is sent between players
public class Message {
    Player sender;
    Player receiver;
    double positivityScale;
  
    // Constructor
    Message(Player sender, Player receiver, double positivityScale) {
      this.sender = sender;
      this.receiver = receiver;
      this.positivityScale = positivityScale;
    }
  }
