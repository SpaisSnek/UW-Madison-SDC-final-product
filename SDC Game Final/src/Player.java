import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import java.util.ArrayList;

/**
 * @author EdmundYuYi Tan
 *
 */
public class Player extends GameObject {

  Random r = new Random();
  private final PlayerNumber number;
  private final int gravityAccel = 5;
  private String direction;
  private boolean isHit;
  private Handler processing;
  private long cooldownTime;
  private long lastAttack;
  private boolean recovery;
  private int hp;
  private Sprite sprite;
  private String state;
  private boolean movementLocked;
  private EnvironmentObject healthbar;
  
  //arraylists to store keycodes, in order of first pressed with the first at the front of the list
  //when conflicting direction keys are pressed, the confilcting key is added behind the first priority key
  //until either the first priority key or it is released
  private ArrayList<Integer> xAxisMovementKeysHeld;
  private ArrayList<Integer> yAxisMovementKeysHeld;

  public Player(int x, int y, ID id, PlayerNumber number, int width, int height,
      Handler processing) {
    super(x, y, id, width, height);
    speedX = 0;
    speedY = gravityAccel;
    this.number = number;
    this.processing = processing;
    xAxisMovementKeysHeld = new ArrayList<>();
    yAxisMovementKeysHeld = new ArrayList<>();
    // set the direction to be up before the first key press to avoid null pointers
    // player will still not move unless isMoving == true
    this.direction = "up";
    this.hp = 200;  
    sprite = new Sprite();
    state = "idle";
    if(this.number == PlayerNumber.ONE) {
      this.healthbar = new EnvironmentObject(110, 40, ID.Environment, 200, 30, Color.blue);
      processing.addObject(healthbar);
    } else if(this.number == PlayerNumber.TWO) { 
      this.healthbar = new EnvironmentObject(110, 80, ID.Environment, 200, 30, Color.red);
      processing.addObject(healthbar);
    }

  }

  /**
   * @return the number
   */
  public PlayerNumber getNumber() {
    return number;
  }

  public void tick() {
    if(this.hp <= 0) {
      processing.endGame(this.number);
    }
    updatePositions();
    x += speedX;
    y += speedY;
    this.isHit();
    this.cooldown();
  }
  
  public void updatePositions() {
    //logic for what happens when multiple keys are being pressed
    //start with vertical axis
    if(!this.yAxisMovementKeysHeld.isEmpty()) { //if it's empty no keys are being held - so we just skip this step
         //always move according to the first key in the list (highest priority)
         switch(this.yAxisMovementKeysHeld.get(0)) {
              case KeyEvent.VK_UP:
              case KeyEvent.VK_W:
                  if(this.speedY >= -5){this.speedY -= 2;}
                   break;
              case KeyEvent.VK_DOWN:
              case KeyEvent.VK_S:
                   if(this.speedY <= 5){this.speedY += 2;}
                   break;
         }
    }
    //now check the horizontal movement keys
    if(!this.xAxisMovementKeysHeld.isEmpty()) {
         switch(this.xAxisMovementKeysHeld.get(0)) {
              case KeyEvent.VK_LEFT:
              case KeyEvent.VK_A:
                   if(this.speedX >= -5){this.speedX -= 2;}
                   break;
              case KeyEvent.VK_RIGHT:
              case KeyEvent.VK_D:
                   if(this.speedX <= 5){this.speedX += 2;}
                   break;
       }
    }
 }

  public void render(Graphics g) {
    if (this.number == PlayerNumber.ONE) {
      g.setColor(Color.red);
    } else if (this.number == PlayerNumber.TWO) {
      g.setColor(Color.blue);
    }

    if (this.isHit) {
      g.setColor(Color.yellow);
    }

    // g.fillRect(x, y, width, height);
    if (this.state.equals("idle")) {
      sprite.idle(g, x, y, direction);
    } else if (this.state.equals("run")) {
      sprite.run(g, x, y, direction);
    }else if (this.state.equals("jab")) {
      sprite.jab(g, x, y, direction);
    }
    g.setFont(new Font("Monospaced", Font.BOLD, 20));
    g.setColor(Color.white);
    if (this.number == PlayerNumber.ONE) {
      g.drawString("Player 1:  " + this.hp + "/200", 0, 60);
    }
    if (this.number == PlayerNumber.TWO) {
      g.drawString("Player 2:  " + this.hp + "/200", 0, 100);
    }
  }

  /**
   * called every time a key is pressed
   * 
   * @param k
   */
  public void keyPressed(KeyEvent k) {
    int key = k.getKeyCode();
    if (this.number == PlayerNumber.ONE) {
      // wasd keys for player one
      switch (key) {
        case KeyEvent.VK_W: 
        //add to the x axis keys pressed
        if(!this.yAxisMovementKeysHeld.contains(KeyEvent.VK_W)) {
             this.yAxisMovementKeysHeld.add(KeyEvent.VK_W);
        }
        //change the attack direction
        this.direction = "up";
        break;
        case KeyEvent.VK_S:
          if(!this.yAxisMovementKeysHeld.contains(KeyEvent.VK_S)) {
            this.yAxisMovementKeysHeld.add(KeyEvent.VK_S);
          }
          this.state = "run";
          this.direction = "down";
          break;
        case KeyEvent.VK_A:
          if(!this.xAxisMovementKeysHeld.contains(KeyEvent.VK_A)) {
            this.xAxisMovementKeysHeld.add(KeyEvent.VK_A);
          }
          this.state = "run";
          this.direction = "left";
          break;
        case KeyEvent.VK_D:
          if(!this.xAxisMovementKeysHeld.contains(KeyEvent.VK_D)) {
            this.xAxisMovementKeysHeld.add(KeyEvent.VK_D);
          }
          this.state = "run";
          this.direction = "right";
          break;
        default:
          break;
      }
    } else if (this.number == PlayerNumber.TWO) {
      // arrow keys for player two
      switch (key) {
        case KeyEvent.VK_UP:
          if(!this.yAxisMovementKeysHeld.contains(KeyEvent.VK_UP)) {
            this.yAxisMovementKeysHeld.add(KeyEvent.VK_UP);
          }
          this.direction = "up";
          break;
        case KeyEvent.VK_DOWN:
          if(!this.yAxisMovementKeysHeld.contains(KeyEvent.VK_DOWN)) {
            this.yAxisMovementKeysHeld.add(KeyEvent.VK_DOWN);
          }
          this.direction = "down";
          break;
        case KeyEvent.VK_LEFT:
          if(!this.xAxisMovementKeysHeld.contains(KeyEvent.VK_LEFT)) {
            this.xAxisMovementKeysHeld.add(KeyEvent.VK_LEFT);
          }
          this.state = "run";
          this.direction = "left";
          break;
        case KeyEvent.VK_RIGHT:
          if(!this.xAxisMovementKeysHeld.contains(KeyEvent.VK_RIGHT)) {
            this.xAxisMovementKeysHeld.add(KeyEvent.VK_RIGHT);
          }
          this.state = "run";
          this.direction = "right";
          break;
        default:
          break;
      }
    }
  }

  /**
   * called every time a key is released
   * 
   * @param k
   */
  public void keyReleased(KeyEvent k) {
    int key = k.getKeyCode();
    if (this.number == PlayerNumber.ONE) {
      // wasd keys for player one
      switch (key) {
        case KeyEvent.VK_W:
          this.yAxisMovementKeysHeld.remove(yAxisMovementKeysHeld.indexOf(KeyEvent.VK_W));
          speedY = gravityAccel;
          break;
        case KeyEvent.VK_S:
          this.yAxisMovementKeysHeld.remove(yAxisMovementKeysHeld.indexOf(KeyEvent.VK_S));
          speedY = gravityAccel;
          break;
        case KeyEvent.VK_A:
          this.xAxisMovementKeysHeld.remove(xAxisMovementKeysHeld.indexOf(KeyEvent.VK_A));
          speedX = 0;
          this.state = "idle";
          break;
        case KeyEvent.VK_D:
          this.xAxisMovementKeysHeld.remove(xAxisMovementKeysHeld.indexOf(KeyEvent.VK_D));
          speedX = 0;
          this.state = "idle";
          break;
        case KeyEvent.VK_E:
          if (!this.recovery) {
            attack(AttackID.BULLET, this.direction);
            this.recovery = true;
          }
          break;
        case KeyEvent.VK_R:
          if (!this.recovery) {
            attack(AttackID.JAB, this.direction);
            this.state = "jab";
            this.recovery = true;
          }
          break;
        default:
          break;
      }
    } else if (this.number == PlayerNumber.TWO) {
      // arrow keys for player two
      switch (key) {
        case KeyEvent.VK_UP:
          this.yAxisMovementKeysHeld.remove(yAxisMovementKeysHeld.indexOf(KeyEvent.VK_UP));
          speedY = gravityAccel;
          break;
        case KeyEvent.VK_DOWN:
          this.yAxisMovementKeysHeld.remove(yAxisMovementKeysHeld.indexOf(KeyEvent.VK_DOWN));
          speedY = gravityAccel;
          break;
        case KeyEvent.VK_LEFT:
          this.xAxisMovementKeysHeld.remove(xAxisMovementKeysHeld.indexOf(KeyEvent.VK_LEFT));
          this.state = "idle";
          speedX = 0;
          break;
        case KeyEvent.VK_RIGHT:
          this.xAxisMovementKeysHeld.remove(xAxisMovementKeysHeld.indexOf(KeyEvent.VK_RIGHT));
          this.state = "idle";
          speedX = 0;
          break;
        case KeyEvent.VK_PERIOD:
          if (!this.recovery) {
            attack(AttackID.BULLET, this.direction);
            this.recovery = true;
          }
        case KeyEvent.VK_COMMA:
          if (!this.recovery) {
            attack(AttackID.JAB, this.direction);
            this.state = "jab";
            this.recovery = true;
          }
          break;
        default:
          break;
      }
    }
  }

  private void attack(AttackID attackID, String direction) {
    Attack tmp;
    this.lastAttack = System.currentTimeMillis();
    this.movementLocked = true;
    switch (direction) {
      case "up":
        switch (attackID) {
          case BULLET:
            this.cooldownTime = 750;
            tmp = new Attack(x + width / 2 - 4, y - 24, ID.Attack, 8, 24, AttackID.BULLET,
                this.number, processing, this);
            tmp.setSpeedY(-7);
            tmp.setSpeedX(0);
            processing.addObject(tmp);
            break;
          case JAB:
            this.cooldownTime = 550;
            tmp = new Attack(x + width / 2 - 12, y - 24, ID.Attack, 24, 24, AttackID.JAB,
                this.number, processing, this);
            processing.addObject(tmp);
            break;
        }
        break;

      case "down":
        switch (attackID) {
          case BULLET:
            this.cooldownTime = 750;
            tmp = new Attack(x + width / 2 - 4, y + height, ID.Attack, 8, 24, AttackID.BULLET,
                this.number, processing, this);
            tmp.setSpeedY(7);
            tmp.setSpeedX(0);
            processing.addObject(tmp);
            break;
          case JAB:
            this.cooldownTime = 550;
            tmp = new Attack(x + width / 2 - 12, y + height, ID.Attack, 24, 24, AttackID.JAB,
                this.number, processing, this);
            processing.addObject(tmp);
            break;
        }
        break;

      case "right":
        switch (attackID) {
          case BULLET:
            this.cooldownTime = 750;
            tmp = new Attack(x + width, y + height / 2 - 4, ID.Attack, 24, 8, AttackID.BULLET,
                this.number, processing, this);
            tmp.setSpeedY(0);
            tmp.setSpeedX(7);
            processing.addObject(tmp);
            break;
          case JAB:
            this.cooldownTime = 550;
            tmp = new Attack(x + width, y + height / 2 - 12, ID.Attack, 24, 24, AttackID.JAB,
                this.number, processing, this);
            processing.addObject(tmp);
            break;
        }
        break;

      case "left":
        switch (attackID) {
          case BULLET:
            this.cooldownTime = 750;
            tmp = new Attack(x - 24, y + height / 2 - 4, ID.Attack, 24, 8, AttackID.BULLET,
                this.number, processing, this);
            tmp.setSpeedY(0);
            tmp.setSpeedX(-7);
            processing.addObject(tmp);
            break;
          case JAB:
            this.cooldownTime = 550;
            tmp = new Attack(x - 24, y + height / 2 - 12, ID.Attack, 24, 24, AttackID.JAB,
                this.number, processing, this);
            processing.addObject(tmp);
            break;
        }
        break;
    }
  }

  public void isHit() {
    for (int i = 0; i < processing.object.size(); i++) {
      if (processing.object.get(i).getId() == ID.Attack) {
        Attack tmp = (Attack) processing.object.get(i);
        if (tmp.getAttackingPlayerNum() != this.number
            && tmp.getBounds().intersects(this.getBounds()) && !tmp.getStartup()) {
          this.healthbar.setWidth(this.healthbar.getWidth() - tmp.getDamage());
          this.isHit = true;
          processing.removeObject(tmp);
          this.hp -= tmp.getDamage();
          return;
        }
      }
    }
    this.isHit = false;
  }

  public void cooldown() {
    if (this.recovery) {
      long time = System.currentTimeMillis();
      if (time > this.cooldownTime + this.lastAttack) {
        this.recovery = false;
        this.cooldownTime = 0;
        this.lastAttack = 0;
        this.state = "idle";
        this.movementLocked = false;
      }
    }
  } 
}
