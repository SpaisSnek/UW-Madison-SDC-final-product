import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * @author EdmundYuYi Tan
 *
 */
public class Sprite {
  private static BufferedImage image;
  private long baseTime;
  private int[] frames = new int[] {7, 6, 7, 7, 7, 7, 4, 6};
  private int count;
  private String state;

  
  
  public void idle(Graphics g, int x, int y, String direction) {
    if (!this.state.equals("idle")) {
      count = 0;
      this.state = "idle";
    }
    int numFrames = frames[6];
    long currTime = System.currentTimeMillis();
    BufferedImage sprite = image.getSubimage(count*48, 6*48, 48, 48);   
    if (direction.equals("left")){
      sprite = flip(sprite);
      g.drawImage(sprite, x - 32, y - 16, 64, 64, null);
    } else {
      g.drawImage(sprite, x, y - 16, 64, 64, null);
    }
    if (currTime > baseTime + 100) {
      if (count == numFrames - 1) {
        count = 0;
      } else {
        count++;
      }
      baseTime = System.currentTimeMillis();
    }    
  }
  

  public Sprite() {
    try {
      image = ImageIO.read(getClass().getResourceAsStream("biker_sprite_sheet2.png"));
    } catch (IOException e) {
      e.printStackTrace();    }
    
    baseTime = System.currentTimeMillis();
    count = 0;
    state = "idle";
  }
  
  public void run(Graphics g, int x, int y, String direction) {
    if (!this.state.equals("run")) {
      count = 0;
      this.state = "run";
    }
    int numFrames = frames[7];
    long currTime = System.currentTimeMillis();
    BufferedImage sprite = image.getSubimage(count*48, 7*48, 48, 48);   
    if (direction.equals("left")){
      sprite = flip(sprite);
      g.drawImage(sprite, x - 32, y - 16, 64, 64, null);
    } else {
      g.drawImage(sprite, x, y - 16, 64, 64, null);
    }
    if (currTime > baseTime + 50) {
      if (count == numFrames - 1) {
        count = 0;
      } else {
        count++;
      }
      baseTime = System.currentTimeMillis();
    }
  }
  
  public void jab(Graphics g, int x, int y, String direction) {
    if (!this.state.equals("jab")) {
      count = 0;
      this.state = "jab";
    }
    int numFrames = frames[1];
    long currTime = System.currentTimeMillis();
    BufferedImage sprite = image.getSubimage(count*48, 1*48, 48, 48);   
    if (direction.equals("left")){
      sprite = flip(sprite);
      g.drawImage(sprite, x - 32, y - 16, 64, 64, null);
    } else {
      g.drawImage(sprite, x, y - 16, 64, 64, null);
    }
    if (count < 4 && currTime > baseTime + 50) {
      count++;
      baseTime = System.currentTimeMillis();
      System.out.println(50);
    } else if (count == 4 && currTime > baseTime + 100) {
      count++;
      System.out.println(100);
      baseTime = System.currentTimeMillis();
    } else if (count == 5 && currTime > baseTime + 200) {
      count = 0;
      System.out.println(100);
      baseTime = System.currentTimeMillis();
    }
  }
  
  private BufferedImage flip(BufferedImage sprite) {
    AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
    tx.translate(-sprite.getWidth(null), 0);
    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    return op.filter(sprite, null);
  }
}