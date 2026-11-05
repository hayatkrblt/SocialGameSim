package javasgsproject;

import java.util.Random;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

// Events that can happen in the simulation
public class Events {
    static Random random = new Random();

    // Check age and if the player should die
    static void ageAndCheckDeath(Player player) {
        if (player.age > 70 && random.nextInt(101) > 98 || player.acquaintances.size() == 1 || player.age > 116) {
                player.isAlive = false;
                if (player.charisma > 0){
                    Simulation.charismaticCount--;
                }
                if (player.intelligence > 0){
                    Simulation.intelligentCount--;
                }
                System.out.println(player.name + " died at age " + player.age);
            Set<Player> acquaintancesCopy = new HashSet<>(player.acquaintances.keySet());
            for (Player friend : acquaintancesCopy) {
                friend.acquaintances.remove(player);
                friend.relationships.remove(player);
            }
            player.acquaintances.clear();
            player.relationships.clear();
        }
        checkForDepression(player);
    }

    static void becomePartners(Player player1, Player player2) {
        if ((player1.partner == null && player2.partner == null) && (player1.relationships.get(player2) >= 0)
                && (random.nextInt(100) > 10)) {
            player1.partner = player2;
            player2.partner = player1;
            System.out.println(player1.name + " and " + player2.name + " have started a relationship with a score of "
                    + player1.relationships.get(player2) + " and " + player2.relationships.get(player1));
        }
    }

    // Break up between two player
    static void breakUp(Player player1, Player player2) {
        if ((player1.partner == player2 && player2.partner == player1) && (player1.relationships.get(player2) < -50)
        && (random.nextInt(100) > 10)){
            player1.partner = null;
            player2.partner = null;
            System.out.println(player1.name + " and " + player2.name + " have broken up");
        }
    }

    // Create a child between two players
    static Player createChild(Player parent1, Player parent2, int birthRate) {
        if (parent1.partner == parent2 && parent2.partner == parent1 && parent1.childrenCount < 6 && parent2.childrenCount < 6) {
            if ((parent1.gender != parent2.gender) && (parent1.age >= 25) && (parent2.age >= 25)
                    && (random.nextInt(100) > birthRate)) {
                boolean childIntelligence = (parent1.intelligence + parent2.intelligence) > 50;
                boolean childCharisma = (parent1.charisma + parent2.charisma) > 50;
                Player child = new Player(childIntelligence, childCharisma);
                if (childIntelligence) {
                    Simulation.intelligentCount++;
                }
                if (childCharisma) {
                    Simulation.charismaticCount++;
                } 
                child.age = 0;
                child.parent1 = parent1;
                child.parent2 = parent2;
                parent1.childrenCount++;
                parent2.childrenCount++;
                child.relationships.put(parent1, 50.0);
                child.relationships.put(parent2, 50.0);
                parent1.relationships.put(child, 50.0);
                parent2.relationships.put(child, 50.0);
                child.addAcquaintance(parent1);
                child.addAcquaintance(parent2);
                parent1.addAcquaintance(child);
                parent2.addAcquaintance(child);
                for (Player player : parent1.acquaintances.keySet()) {
                    if (player != parent1 && player != parent2) {
                        child.addAcquaintance(player);
                        player.addAcquaintance(child);
                    }
                }
                for (Player player : parent2.acquaintances.keySet()) {
                    if (player != parent1 && player != parent2) {
                        child.addAcquaintance(player);
                        player.addAcquaintance(child);
                    }
                }
                System.out.println(parent1.name + " and " + parent2.name + " had a child named " + child.name);
                return child;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    // Introduce acquaintances to each other
    static void introduceAcquaintances(Player introducer) {
        Set<Player> acquaintances = introducer.acquaintances.keySet();

        if (acquaintances.size() < 3) {
            return;
        }

        List<Player> acquaintancesList = new ArrayList<>(acquaintances);

        Player acquaintance1 = acquaintancesList.get(random.nextInt(acquaintancesList.size()));
        Player acquaintance2 = acquaintancesList.get(random.nextInt(acquaintancesList.size()));

        if (acquaintance1 == acquaintance2) {
            return;
        }

        if (acquaintance1 == null || acquaintance2 == null) {
            return;
        }

        if (acquaintance1.isAcquaintance(acquaintance2)) {
            return;
        }

        Double relationship1 = introducer.relationships.get(acquaintance1);
        Double relationship2 = introducer.relationships.get(acquaintance2);

        if (relationship1 != null && relationship2 != null && ((relationship1 > 100 && relationship2 > 100)
                || (relationship1 < 100 && relationship2 < 100)) && random.nextInt(100) > 95) {
            acquaintance1.addAcquaintance(acquaintance2);
            acquaintance2.addAcquaintance(acquaintance1);
            System.out.println(introducer.name + " introduced " + acquaintance1.name + " and " + acquaintance2.name);
        }
    }

    // Method to check for depression
    static void checkForDepression(Player player) {
        if (player.disappointments > 1000) {
            double chanceOfSuicide = random.nextDouble();
            if (chanceOfSuicide <= 0.001) {
                commitSuicide(player);
            }
        }
    }

    // Commit suicide (please don't, be safe)
    static void commitSuicide(Player player) {
        if (player.isAlive) {
            player.isAlive = false;
            System.out.println(player.name + " has committed suicide.");
            Set<Player> acquaintancesCopy = new HashSet<>(player.acquaintances.keySet());
            for (Player friend : acquaintancesCopy) {
                friend.acquaintances.remove(player);
                friend.relationships.remove(player);
            }
            player.acquaintances.clear();
            player.relationships.clear();
        }
    }
}
