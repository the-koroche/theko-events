package examples;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.theko.events.Event;
import org.theko.events.EventDispatcher;
import org.theko.events.EventMap;
import org.theko.events.Listener;

class PaintCanvas extends JPanel {

    private BufferedImage canvas;
    private Graphics2D g2d;

    public PaintCanvas() {
        setPreferredSize(new Dimension(600, 400));

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Component c = e.getComponent();
                resizeCanvas(c.getWidth(), c.getHeight());
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (canvas != null) {
            g.drawImage(canvas, 0, 0, null);
        }
    }

    public Graphics2D getCanvasGraphics() {
        return g2d;
    }

    private void resizeCanvas(int width, int height) {
        width = Math.max(1, width);
        height = Math.max(1, height);

        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D newG2d = newImage.createGraphics();

        newG2d.setColor(Color.WHITE);
        newG2d.fillRect(0, 0, width, height);

        if (canvas != null) {
            newG2d.drawImage(canvas, 0, 0, null);
            canvas.flush();
        }

        if (g2d != null) {
            g2d.dispose();
        }

        g2d = newG2d;
        canvas = newImage;
    }
}

public class PaintApplication extends JFrame {

    public enum BrushShape {
        Circle,
        Rectangle
    }

    public static class Brush {
        public final BrushShape shape;
        public final int size;
        public final Color color;

        public Brush(BrushShape shape, int size, Color color) {
            this.shape = shape;
            this.size = size;
            this.color = color;
        }
    }

    public static class BrushPaintEvent extends Event {
        public final Brush brush;
        public final int x;
        public final int y;

        public BrushPaintEvent(Brush brush, int x, int y) {
            this.brush = brush;
            this.x = x;
            this.y = y;
        }
    }

    public static class FillPaintEvent extends Event {
        public final Color color;

        public FillPaintEvent(Color color) {
            this.color = color;
        }

        public FillPaintEvent(FillPaintEvent other) {
            this.color = other.color;
        }
    }

    public interface BrushPaintListener extends Listener<BrushPaintEvent> {
        default void onPaint(BrushPaintEvent event) {}
        default void onErase(BrushPaintEvent event) {}
    }

    public interface FillPaintListener extends Listener<FillPaintEvent> {
        default void onFill(FillPaintEvent event) {}
    }

    public class DefaultPaintListener implements BrushPaintListener {
        @Override
        public void onPaint(BrushPaintEvent event) {
            Graphics2D g2d = canvas.getCanvasGraphics();
            if (g2d == null) return;

            g2d.setColor(event.brush.color);
            drawShape(g2d, event);
        }

        @Override
        public void onErase(BrushPaintEvent event) {
            Graphics2D g2d = canvas.getCanvasGraphics();
            if (g2d == null) return;

            g2d.setColor(Color.WHITE);
            drawShape(g2d, event);
        }

        private void drawShape(Graphics2D g2d, BrushPaintEvent event) {
            int startX = event.x - event.brush.size / 2;
            int startY = event.y - event.brush.size / 2;
            switch (event.brush.shape) {
                case Circle -> g2d.fillOval(startX, startY, event.brush.size, event.brush.size);
                case Rectangle -> g2d.fillRect(startX, startY, event.brush.size, event.brush.size);
            }
        }
    }

    private PaintCanvas canvas;
    private EventDispatcher<BrushPaintEvent, BrushPaintListener, String> dispatcher;
    private Brush brush;

    public PaintApplication() {
        canvas = new PaintCanvas();
        add(canvas);

        brush = new Brush(BrushShape.Circle, 10, Color.BLACK);

        dispatcher = new EventDispatcher<>();
        EventMap<BrushPaintEvent, BrushPaintListener, String> eventMap = dispatcher.createEventMap();
        eventMap.put("paint", BrushPaintListener::onPaint);
        eventMap.put("erase", BrushPaintListener::onErase);
        dispatcher.setEventMap(eventMap);

        dispatcher.addListener(new DefaultPaintListener());

        MouseAdapter ma = new MouseAdapter() {
            private void handleMouseEvent(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    dispatcher.dispatch("paint", new BrushPaintEvent(brush, e.getX(), e.getY()));
                    canvas.repaint();
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    dispatcher.dispatch("erase", new BrushPaintEvent(brush, e.getX(), e.getY()));
                    canvas.repaint();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                handleMouseEvent(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseEvent(e);
            }
        };

        canvas.addMouseListener(ma);
        canvas.addMouseMotionListener(ma);

        setTitle("Paint Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PaintApplication::new);
    }
}