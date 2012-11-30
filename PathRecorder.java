import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.core.script.job.state.Tree;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.Menu;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.wrappers.Tile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Manifest(authors = {"djabby"}, name = "PathRecorder", description = "s=start/stop,w=write,r=read,r=run", version = 1.38)
public class PathRecorder extends ActiveScript implements KeyListener,
        PaintListener {

    @Override
    public void onStop() {
        stop();
    }

    Node lt = new Node() {

        @Override
        public boolean activate() {
            return true;
        }

        @Override
        public void execute() {
        }
    };

    public class Path {
        final List<Tile> nodes;
        int i;

        public Path(List<Tile> nodes_) {
            nodes = nodes_;
            i = 0;
        }

        public boolean isFinished() {
            return i >= nodes.size();
        }

        public void run() {
            Tile current = nodes.get(i);
            if (!tilesEqual(current, Walking.getDestination()))
                Walking.walk(current);
            int inRange = 6;
            if (current.distance(Players.getLocal().getLocation()) <= inRange)
                i++;
        }
    }

    private Path path = null;
    private List<Tile> currentPath;
    private Tile getLatest = null;

    enum State {
        NIL, COLLECT, STOPPED, RUNNING
    }

    private final int minimalDistance = 7;
    private State state = State.NIL;

    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }


    @Override
    public void keyTyped(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'm':
                Mouse.click(false);
                Task.sleep(1000);
                System.out.println("#actions=" + Menu.getActions().length);
                System.out.println("#options=" + Menu.getOptions().length);
                for (String action : Menu.getActions()) {
                    System.out.println(action);
                }
                for (String action : Menu.getOptions()) {
                    System.out.println(action);
                }
                break;
            case 's':
                switch (state) {
                    case NIL:
                    case STOPPED:
                        currentPath = new ArrayList<>();
                        state = State.COLLECT;
                        break;
                    case COLLECT:
                    case RUNNING:
                        state = State.STOPPED;
                        getLatest = null;
                        path = null;
                        break;
                }
                break;
            case 'w':
                if (state != State.NIL) {
                    System.out.println("new Tile[] {");
                    boolean b = false;
                    Tile lastTile = null;
                    for (Tile tile : currentPath) {
                        if (lastTile != null
                                && tile.distance(lastTile) < minimalDistance * 1.5) {
                            System.out.print(b ? "," : "");
                            System.out.println("new Tile(" + tile.getX() + ","
                                    + tile.getY() + "," + tile.getPlane() + ")");
                            b = true;
                        }
                        lastTile = tile;
                    }
                    System.out.println("};");
                }
                break;
            case 'r':
                if (state == State.STOPPED) {
                    state = State.RUNNING;
                    path = new Path(currentPath);
                }
        }

    }

    private BufferedImage proggy;

    @Override
    public void onStart() {
        BufferedImage img;
        log.info("Converted too new api by Wyn");
        try {
            File imgPath = new File("src/scripts/images/proggy.png");
            if (imgPath.exists()) {
                System.out.println("File found!");
                img = ImageIO.read(imgPath);
                int w = img.getWidth(null);
                int h = img.getHeight(null);
                System.out.println("Width: " + h);
                proggy = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                Graphics g = proggy.getGraphics();
                g.drawImage(img, 0, 0, null);
            }
        } catch (IOException e) {
            proggy = new BufferedImage(0, 0, BufferedImage.TYPE_INT_RGB);
            e.printStackTrace();
        }
    }

    private final Tree scriptTree = new Tree(new Node[]{new Collect(), new Running()});


    @Override
    public int loop() {
        final Node stateNode = scriptTree.state();
        if (stateNode != null) {
            scriptTree.set(stateNode);
            final Node setNode = scriptTree.get();
            if (setNode != null) {
                getContainer().submit(setNode);
                setNode.join();
            }
        }
        return Random.nextInt(100, 200);
    }


    private class Collect extends Node {
        public void execute() {
            if (currentPath.size() == 0) {
                getLatest = Players.getLocal().getLocation();
                currentPath.add(getLatest);
            } else if (!tilesEqual(Players.getLocal().getLocation(), getLatest)
                    && getLatest.distance(Players.getLocal().getLocation()) > minimalDistance) {
                getLatest = Players.getLocal().getLocation();
                currentPath.add(getLatest);
            }
        }

        public boolean activate() {
            return state == State.COLLECT;
        }
    }

    private class Running extends Node {
        public void execute() {
            path.run();
        }

        public boolean activate() {
            return state == State.RUNNING && !path.isFinished();
        }
    }

    private static boolean tilesEqual(Tile tile1, Tile tile2) {
        return tile1.getX() == tile2.getX() && tile1.getY() == tile2.getY()
                && tile1.getPlane() == tile2.getPlane();
    }

    public void onRepaint(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 510, 200, 30);
        Point p = Mouse.getLocation();
        g.drawLine(0, (int) p.getY(), 640, (int) p.getY());
        g.drawLine((int) p.getX(), 0, (int) p.getX(), 480);
        try {
            float[] scales = {1f, 1f, 1f, 0.95f};
            float[] offsets = new float[4];
            RescaleOp rescale = new RescaleOp(scales, offsets, null);
            Graphics2D g2d = (Graphics2D) g;
            g2d.drawImage(proggy, rescale, 0, 390);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (state == State.NIL)
            return;
        for (Tile t : currentPath) {

            Polygon[] bounds = t.getBounds();
            if (bounds.length == 1) {
                g.setColor(Color.RED);
                g.fillPolygon(bounds[0]);

            }
        }

    }
}