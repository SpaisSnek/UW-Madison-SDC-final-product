import java.awt.Graphics;
import java.awt.Color;

public class EnvironmentObject extends GameObject {

    private Color color;

    public EnvironmentObject(int x, int y, ID id, int width, int height, Color c) {
        super(x, y, id, width, height);
        this.color = c;
    }

    @Override
    public void tick() {
        //as of right now, environment objects do nothing on tick 
    }

    @Override
    public void render(Graphics g) {
        g.setColor(this.color);
        g.fillRect(x, y, width, height);
    }
    
}
