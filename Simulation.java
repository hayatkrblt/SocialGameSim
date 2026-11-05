package javasgsproject;

import java.util.*;

// import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.Color;
import java.awt.RenderingHints;
import java.lang.Thread;

// Simulation class that represents the simulation
public class Simulation {
  private ArrayList<Player> players;
  private Random random;
  private final SimulationGUI gui;
  private int current_iteration;
  public static int NUM_ITERATIONS;
  private volatile boolean running = true;
  private List<Double> averageMoods = new ArrayList<>();
  private XYSeries moodSeries;
  private XYSeries playerCountSeries;
  private XYSeries malePopulationSeries;
  private XYSeries femalePopulationSeries;
  public boolean paused = false;
  public static int intelligentCount;
  public static int charismaticCount;

  public void pause(){
    paused = !paused;
  }

  public Simulation(SimulationGUI gui) {
    this.gui = gui;
    intelligentCount = 0;
    charismaticCount = 0;
    moodSeries = new XYSeries("Average Mood");
    playerCountSeries = new XYSeries("Player Count");

    malePopulationSeries = new XYSeries("Male Population");
    femalePopulationSeries = new XYSeries("Female Population");

    XYSeriesCollection moodDataset = new XYSeriesCollection(moodSeries);
    XYSeriesCollection playerCountDataset = new XYSeriesCollection(playerCountSeries);

    XYSeriesCollection populationDataset = new XYSeriesCollection();
    populationDataset.addSeries(malePopulationSeries);
    populationDataset.addSeries(femalePopulationSeries);

    JFreeChart dataChart = ChartFactory.createXYLineChart(
        "Average Mood Over Time",
        "Iteration",
        "Mood",
        moodDataset);
    JFreeChart playerCountChart = ChartFactory.createXYLineChart(
        "Player Count Over Time",
        "Iteration",
        "Player Count",
        playerCountDataset);
    JFreeChart populationChart = ChartFactory.createXYLineChart(
        "Population Over Time",
        "Iteration",
        "Population",
        populationDataset);
    XYPlot plot = populationChart.getXYPlot();
    
    XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
    renderer.setSeriesPaint(0, Color.BLUE);
    renderer.setSeriesPaint(1, Color.RED);
    renderer.setSeriesShapesVisible(0, false);
    renderer.setSeriesShapesVisible(1, false);
    plot.setRenderer(renderer);
    
    setRenderingHints(playerCountChart);
    setRenderingHints(dataChart);
    setRenderingHints(populationChart);

    gui.addDataChartToControlPanel(dataChart);
    gui.addPlayerCountChartToControlPanel(playerCountChart);
    gui.addPopulationChartToControlPanel(populationChart);
  }

  private void setRenderingHints(JFreeChart chart) {
    chart.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
}

  // Starts the simulation
  public void start(int numIterations, int numPlayers, int percentIntelligent, int percentCharismatic) {
    SwingWorker<Void, ArrayList<Player>> worker = new SwingWorker<Void, ArrayList<Player>>() {
      @Override
      protected Void doInBackground() throws Exception {
        players = new ArrayList<>(numPlayers);
        random = new Random();

        NUM_ITERATIONS = numIterations;
        addPlayers(numPlayers, percentIntelligent, percentCharismatic);
        addMinimumAcquaintance(numPlayers);
        addAcquaintances(numPlayers);
        simulate(numPlayers, numIterations);

        return null;
      }

      @Override
      protected void process(List<ArrayList<Player>> chunks) {
        ArrayList<Player> latestPlayers = chunks.get(chunks.size() - 1);
        gui.updateData(latestPlayers);
      }

      @Override
      protected void done() {
        gui.updateData(players);
      }
    };

    worker.execute();
  }

  // Creates a new player and adds it to the list of players
  private void addPlayers(int numPlayers, int percentIntelligent, int percentCharismatic) {
    for (int i = 0; i < numPlayers; i++) {
      boolean isIntelligent = i < numPlayers * percentIntelligent / 100.0;
      boolean isCharismatic = i < numPlayers * percentCharismatic / 100.0;
      if (isIntelligent) intelligentCount++;
      if (isCharismatic) charismaticCount++;
      players.add(new Player(isIntelligent, isCharismatic));
      pauseAndUpdateGUI();
    }
  }

  // Adds a minimum number of acquaintances(1) to each player apart from
  // themselves
  private void addMinimumAcquaintance(int numPlayers) {
    for (int i = 0; i < numPlayers; i++) {
      int randomIndex;
      do {
        randomIndex = random.nextInt(numPlayers);
      } while (randomIndex == i);
      players.get(i).addRandomAcquaintance(players.get(randomIndex));
    }
  }

  // Adds random acquaintances to each player
  private void addAcquaintances(int numPlayers) {
    for (int i = 0; i < numPlayers; i++) {
      int numAcquaintances = random.nextInt(numPlayers - 1);
      for (int j = 0; j < numAcquaintances; j++) {
        int randomIndex;
        do {
          randomIndex = random.nextInt(numPlayers);
        } while (randomIndex == i);
        players.get(i).addRandomAcquaintance(players.get(randomIndex));
      }
    }
  }

  // Simulates the interactions between players
  private void simulate(int numPlayers, int NUM_ITERATIONS) {

    while (running) {
      List<Player> shuffledPlayers = new ArrayList<>(players);
      List<Player> tempPlayers = new ArrayList<>();

      for (int i = 0; i < NUM_ITERATIONS; i++) {
        System.out.println("Iteration: " + i);
        System.out.println("Intelligent Count: " + intelligentCount);
        System.out.println("Charismatic Count: " + charismaticCount);
        while (paused){
          try{
            Thread.sleep(1);
          }
          catch(InterruptedException e){
            Thread.currentThread().interrupt();
          }
        }
        if (!running) {
          break;
        }
        Collections.shuffle(shuffledPlayers);
        for (Player player : shuffledPlayers) {
          simulatePlayer(player, tempPlayers);
        }
        players.addAll(tempPlayers);
        shuffledPlayers.addAll(tempPlayers);
        tempPlayers.clear();
        pauseAndUpdateGUI();
        current_iteration = i;
        double averageMood = calculateAverageMood();
        averageMoods.add(averageMood);
        if (i > 5 && i % 10 == 0) {
          moodSeries.add(i, averageMood);
          playerCountSeries.add(i, players.size());
          updatePopulationSeries();
        }
        if (players.size() == 0){
          gui.stopButton.setEnabled(false);
          gui.startButton.setEnabled(true);
          gui.pauseButton.setEnabled(false);
          running = false;
        }
      }
      gui.stopButton.setEnabled(false);
      gui.startButton.setEnabled(true);
      running = false;
    }
  }

  // Simulates the interactions of a single player
  private void simulatePlayer(Player player, List<Player> tempPlayers) {
    player.age++;
    int birthRate;
    if (player.isAlive) {
      player.sendMessage();
    }
    else{
      players.remove(player);
    }
    for (Player otherPlayer : players) {
      if (players.size() < 100)
        birthRate = 0;
      else if (players.size() < 200)
        birthRate = 20;
      else if (players.size() < 400)
        birthRate = 40;
      else if (players.size() < 600)
        birthRate = 60;
      else {
        birthRate = 80;
      }
      if (player != otherPlayer && player.relationships.get(otherPlayer) != null) {
        Events.becomePartners(player, otherPlayer);
        Events.breakUp(player, otherPlayer);
        Player child = Events.createChild(player, otherPlayer, birthRate);
        if (child != null) {
          tempPlayers.add(child);
        }
        player.breakAcquaintance(otherPlayer);
      }
    }
    Events.ageAndCheckDeath(player);
    Events.checkForDepression(player);
    Events.introduceAcquaintances(player);
    player.changeMood();
  }

  // Adds delay to the simulation and updates the GUI
  private void pauseAndUpdateGUI() {
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    gui.updateData(players);
  }

  // Returns the current iteration of the simulation
  public int getCurrentIteration() {
    return current_iteration;
  }

  // Stops the simulation
  public void stop() {
    running = false;
  }

  private double calculateAverageMood() {
    if (players.isEmpty()) {
      return 0.0;
    }

    double totalMood = 0;
    for (Player player : players) {
      totalMood += player.getMood();
    }

    return totalMood / players.size();
  }

  public void updatePopulationSeries() {
    int maleCount = 0;
    int femaleCount = 0;

    for (Player player : players) {
      if (player.gender == 'M') {
        maleCount++;
      } else {
        femaleCount++;
      }
    }

    malePopulationSeries.add(current_iteration, maleCount);
    femalePopulationSeries.add(current_iteration, femaleCount);
  }
}
