import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.util.Random;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Dimension;

/**
 * @author Edmund YuYi Tan
 *
 */
public class Game extends Canvas implements Runnable {

  private static final int WIDTH = 1280;
  private static final int HEIGHT = 720;
  private double height;
  private double width;
  private Thread thread;
  private boolean running = false;
  public boolean jumping = false;
  public long jumpingTime = 200;
  private Handler handler;
  private Random r;
  protected static GameState gs = GameState.MENU; //menu by default

  public Game() {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    width = screenSize.getWidth();
    height = HEIGHT * (width / WIDTH);
    new Window((int) width, (int) height, "Game", this);
    handler = new Handler(this);
    r = new Random();
    addKeyListener(new KeyMapper());
    addMouseListener(new MouseMapper());

    // add a floor and a platform (for now)
    handler.addObject(
        new EnvironmentObject(0, (int) height - 30, ID.Environment, (int) width, 30, Color.white)); // floor
    
    
    handler.addObject(new EnvironmentObject((int) width / 3, (int) height / 2, ID.Environment, 500,
        40, Color.white));
    


    addPlayers();
  }

  public synchronized void start() {
    thread = new Thread(this);
    thread.start();
    running = true;
  }

  public synchronized void stop() {
    try {
      thread.join();
      running = false;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void run() {
    long lastTime = System.nanoTime();
    double amountOfTicks = 60.0;
    double ns = 1000000000 / amountOfTicks;
    double delta = 0;
    long timer = System.currentTimeMillis();
    int frames = 0;

    while (running) {
      long now = System.nanoTime();
      delta += (now - lastTime) / ns;
      lastTime = now;
      while (delta >= 1) {
        tick();
        delta--;
      }
      if (running) {
        render();
      }
      frames++;

      if (System.currentTimeMillis() - timer > 1000) {
        timer += 1000;
        //System.out.println("FPS: " + frames);
        frames = 0;
      }
    }
    stop();
  }

  public class thread implements Runnable {
    @Override
    public void run() {
      try {
        Thread.sleep(jumpingTime);
        jumping = false;
      } catch (Exception e) {
        e.printStackTrace();
        new Thread(this).start();
        System.exit(0);
      }
    }
  }

  private void tick() {
    handler.tick((int) width, (int) height);
  }

  public void render() {
    BufferStrategy bs = this.getBufferStrategy();
    if (bs == null) {
      this.createBufferStrategy(3);
      return;
    }

    Graphics2D g = (Graphics2D)bs.getDrawGraphics();

    g.setColor(Color.black);
    g.fillRect(0, 0, (int) width, (int) height);

    if(gs == GameState.IN_LEVEL) {
      handler.render(g);
    } else if(gs == GameState.GAME_OVER) {
      handler.drawGameOver(g, this.width, this.height, this.getLocationOnScreen());
    } else {
      handler.drawMenu(g, this.width, this.height, this.getLocationOnScreen());
    }

    g.dispose();
    bs.show();
  }
  
  public void addPlayers() {
    handler.addObject(new Player((int) width / 3, (int) height / 2 - 48, ID.Player,
        PlayerNumber.ONE, 48, 48, handler));
    handler.addObject(new Player((int) width / 3 + 500 - 48, (int) height / 2 - 48, ID.Player,
        PlayerNumber.TWO, 48, 48, handler));
  }
  
  public void reset() {
    this.gs = GameState.IN_LEVEL;
    for(int i = 0; i < handler.object.size(); i++) {
      if(handler.object.get(i) instanceof Player) {
        handler.removeObject(handler.object.get(i));
      }
    }
    addPlayers();
  }

  public static void main(String[] args) {
    new Game();
  }

  /**
   * a key listener to send key press information to the handler
   * 
   * @author ben staehle
   * 
   */
  private class KeyMapper extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent k) {
      if(k.getKeyCode() == KeyEvent.VK_ESCAPE) {
        gs = GameState.MENU;
      }
      handler.handleKeyPress(k);
    }

    @Override
    public void keyReleased(KeyEvent k) {
      handler.handleKeyRelease(k);
    }
  }
  
  private class MouseMapper extends MouseAdapter {
    @Override
    public void mousePressed(MouseEvent e) {
      int[] bound = {(int)width/2 - 150, (int)width/2 + 150, (int)height/2, (int)height/2 + 50}; //x1, x2, y1, y2
      if(gs == GameState.MENU) {
        if(e.getX() > bound[0] && e.getX() < bound[1] && e.getY() > bound[2] && e.getY() < bound[3]) {
          //start button
          gs = GameState.IN_LEVEL;
        } else if(e.getX() > bound[0] && e.getX() < bound[1] && e.getY() > bound[2] + 150 && e.getY() < bound[3] + 150) {
          //quit button
          System.exit(0);
        }
      } else if(gs == GameState.GAME_OVER) {
        if(e.getX() > bound[0] && e.getX() < bound[1] && e.getY() > bound[2] && e.getY() < bound[3]) {
          //restart button
          reset();
        } else if(e.getX() > bound[0] && e.getX() < bound[1] && e.getY() > bound[2] + 150 && e.getY() < bound[3] + 150) {
          //quit button
          System.exit(0);
        }
      }
    }
  }
}
