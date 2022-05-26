import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * @author EdmundYuYi Tan
 *
 */
public abstract class GameObject {
  protected int x;
  protected int y;
  protected ID id;
  protected int speedX;
  protected int speedY;
  protected int width;
  protected int height;

  public GameObject(int x, int y, ID id, int width, int height) {
    this.x = x;
    this.y = y;
    this.id = id;
    this.width = width;
    this.height = height;
  }
  
  public abstract void tick();  
  public abstract void render(Graphics g);
  
  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public ID getId() {
    return id;
  }
  
  public void setId(ID id) {
    this.id = id;
  }

  public int getSpeedX() {
    return speedX;
  }

  public void setSpeedX(int speedX) {
    this.speedX = speedX;
  }

  public int getSpeedY() {
    return speedY;
  }

  public void setSpeedY(int speedY) {
    this.speedY = speedY;
  }
  
  public int getWidth() {
    return this.width;
  }
  
  public int getHeight() {
    return this.height;
  }
  
  public void setWidth(int width) {
    this.width = width;
  }
  
  public Rectangle getBounds() {    
    return new Rectangle(this.x, this.y, this.width, this.height);
  }

}
