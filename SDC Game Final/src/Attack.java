import java.awt.Color;
import java.awt.Graphics;

/**
 * @author EdmundYuYi Tan
 *
 */
public class Attack extends GameObject{  
  private final AttackID attackID;
  private final PlayerNumber attackingPlayer;
  private long duration;
  private long startTime;
  private Handler processing;
  private int prevPlayerX;
  private int prevPlayerY;
  private Player player;
  private boolean startup;
  private int startupTime;
  private int damage;
  
  
  public Attack(int x, int y, ID id, int width, int height, AttackID attackID, 
      PlayerNumber playerID, Handler processing, Player player) {
    super(x, y, id, width, height);
    this.attackID = attackID;
    this.attackingPlayer = playerID;
    
    if (attackID == AttackID.BULLET) {
      this.duration = 1000;
      this.startupTime = 250;
      
      this.player = player;
      this.prevPlayerX = player.getX();
      this.prevPlayerY = player.getY();
      
      this.damage = 5;
    } else if(attackID == AttackID.JAB) {
      this.duration = 100;
      this.startupTime = 250;
      
      this.player = player;
      this.prevPlayerX = player.getX();
      this.prevPlayerY = player.getY();
      this.damage = 20;
    }
    this.startup = true;
    
    this.startTime = System.currentTimeMillis();
    
    this.processing = processing;
  }

  /**
   * @return the damage
   */
  public int getDamage() {
    return damage;
  }

  @Override
  public void tick() {
    // TODO Auto-generated method stub
    if (this.attackID == AttackID.JAB || this.startup) {
      meleePositionUpdate();
    } else {
      this.x += this.speedX;
      this.y += this.speedY;
    }
    
    startup();
    duration();    
  }

  @Override
  public void render(Graphics g) {
    // TODO Auto-generated method stub
    if(!startup) {
      g.setColor(Color.yellow);
      g.fillRect(x, y, width, height);
    }
  }
  
  public PlayerNumber getAttackingPlayerNum() {
    return attackingPlayer;    
  }
  
  private void duration() {
    long time = System.currentTimeMillis();
    if (!startup && time > startTime + duration) {
      processing.removeObject(this);
    }
  } 
   
  
  
  private void meleePositionUpdate() {
    this.x += this.player.getX() - this.prevPlayerX;
    this.prevPlayerX = this.player.getX();
    this.y += this.player.getY() - this.prevPlayerY;
    this.prevPlayerY = this.player.getY();    
  }
  
  private void startup() {
    long time = System.currentTimeMillis();
    if (startup) {
      if (time > startTime + startupTime) {
        startup = false;
        startTime = time;
      }
    }
  }
  
  public boolean getStartup() {
    return this.startup;
  }
}
