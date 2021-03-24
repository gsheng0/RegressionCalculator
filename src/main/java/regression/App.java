package regression;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.function.Function;

public class App extends JPanel {
    private JFrame frame;
    private JMenuBar bar;
    private JButton optimize, clear;
    private JTextField coordinate, pointSizeField, runs, alpha;
    private JLabel functionLabel;
    private Polynomial function;
    private ArrayList<Vector> coords = new ArrayList<>();
    private int pointSize = 5;
    private static Dimension size = new Dimension(1200, 600);
    public static final Function<Double, Double> SCREEN_TO_GRAPH_X = a -> a - 30; //converts true x coordinates (relative to screen) to x coordinates relative to drawn axis
    public static final Function<Double, Double> SCREEN_TO_GRAPH_Y = a -> a * -1 + (size.height + 30); //converts true y coordinates (java y coordinate system) to cartesian y coordinates relative to drawn axis
    public static final Function<Double, Double> GRAPH_TO_SCREEN_X = a -> a + 30; //converts x coordinates relative to axis to true x coordinates relative to window
    public static final Function<Double, Double> GRAPH_TO_SCREEN_Y = a -> -1 * a + (size.height - 25); //converts cartesian y coordinates relative to drawn axis to java y coordinates relative to window
    public static final Function<Vector, Vector> GRAPH_TO_SCREEN = a -> new Vector(GRAPH_TO_SCREEN_X.apply(a.x), GRAPH_TO_SCREEN_Y.apply(a.y)); //condensed function for converting graph to true coordinates
    public static final Function<Vector, Vector> SCREEN_TO_GRAPH = a -> new Vector(SCREEN_TO_GRAPH_X.apply(a.x), SCREEN_TO_GRAPH_Y.apply(a.y)); //condesned function for converting true to graph coordinates


    public App(){
        frame = new JFrame("Regression Calculator");

        setUpMenuBar();
        frame.addMouseListener(new CoordinateClick());
        frame.add(this);
        frame.setResizable(false);
        frame.setSize(size.width, size.height + 65);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void setUpMenuBar(){
        setUpMenuBarComponents();
        bar = new JMenuBar();
        //text field for manually entering coordinates
        bar.add(getSpacer(20));
        bar.add(coordinate);

        //button for finding line of best fit
        bar.add(getSpacer(20));
        bar.add(optimize);

        //button for clearing the screen
        bar.add(getSpacer(20));
        bar.add(clear);

        //displays equation for line of best fit
        bar.add(getSpacer(20));
        bar.add(functionLabel);


        //input for how many times to run
        //if no input is given then default value is used
        bar.add(getSpacer(20));
        bar.add(new JLabel("Runs: "));
        bar.add(runs);

        //input for learning rate
        //if not input is given then default value is used
        bar.add(getSpacer(20));
        bar.add(new JLabel("Alpha: "));
        bar.add(alpha);

        frame.setJMenuBar(bar);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, size.width, size.height);

        //drawing the x and y axes
        g.setColor(Color.BLACK);
        g.drawLine(30, size.height - 30, size.width - 30, size.height - 30);
        g.drawLine(30, size.height - 30, 30, 30);

        //drawing inputted coordinates
        for(Vector vec : coords) {
            Vector temp = GRAPH_TO_SCREEN.apply(vec);
            g.fillRect((int)temp.x - pointSize / 2, (int)temp.y - pointSize / 2, pointSize, pointSize);
        }

        //draw line of best fit if calculated
        if(function != null)
        {
            ArrayList<Vector> convert = new ArrayList<>();
            g.setColor(Color.BLACK);
            for(int i = 0; i < 1170; i++)
            {
                double y = function.function.apply((double) i);
                Vector converted = GRAPH_TO_SCREEN.apply(new Vector(i, y));
                convert.add(converted);
                g.drawRect((int)converted.x, (int)converted.y, 1, 1);
            }
            for(int i = 1; i < convert.size() - 1; i++)
            {
                Vector prev = convert.get(i - 1);
                Vector curr = convert.get(i);
                g.drawLine((int)curr.x, (int)curr.y, (int)prev.x, (int)prev.y);
            }
        }

        repaint();
    }

    public void setUpMenuBarComponents(){
        Dimension dim = new Dimension(40,30);
        coordinate = new JTextField();
        coordinate.setPreferredSize(dim);
        coordinate.setMinimumSize(dim);
        coordinate.setMaximumSize(dim);
        coordinate.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) //parsing text input for coordinates
                {
                    String updated = "";
                    for(char c : coordinate.getText().toCharArray()) {
                        if (c != ' ')
                            updated += c; //filters out white space
                    }

                    String[] coord = updated.split(",");
                    if(coord.length < 2) //if there was no comma, then nothing happens
                        return;
                    try {
                        coords.add(new Vector(Integer.parseInt(coord[0]), Integer.parseInt(coord[1]))); //makes sure both values are numbers
                    }
                    catch(Exception excp){
                        System.out.println("error");
                    }
                    coordinate.setText("");
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        runs = new JTextField();
        runs.setPreferredSize(dim);
        runs.setMaximumSize(dim);
        runs.setMaximumSize(dim);
        runs.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    try{
                        //begins finding the line of best fit
                        Optimizer o = Optimizer.getInstance();
                        o.setRuns(Integer.parseInt(runs.getText()));
                    }
                    catch(Exception ex){}
                    runs.setText("");
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        alpha = new JTextField();
        alpha.setPreferredSize(dim);
        alpha.setMaximumSize(dim);
        alpha.setMinimumSize(dim);
        alpha.addKeyListener(new KeyListener() { //input listener for allowing user to input learning rate manually
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    try{
                        Optimizer o = Optimizer.getInstance();
                        o.setAlpha(Double.parseDouble(alpha.getText()));
                    }
                    catch(Exception ex){}
                    alpha.setText("");
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        optimize = new JButton("Optimize");
        optimize.addActionListener(e -> {
            Optimizer o = Optimizer.getInstance();
            o.setCoords(coords);
            o.regress();               //begins linear regression
            function = o.getFunction();
            functionLabel.setText(function.toString());
        });

        clear = new JButton("Clear");
        clear.addActionListener(a -> {
            coords.clear();
            functionLabel.setText("");
            function = null;
        });

        functionLabel = new JLabel();

    }
    public static JMenu getSpacer(int x) { //returns empty menu that provides spacing between non empty options on menu
        JMenu output = new JMenu();
        output.setEnabled(false);
        Dimension dim = new Dimension(x, 1);
        output.setMinimumSize(dim);
        output.setPreferredSize(dim);
        output.setMaximumSize(dim);
        return output;
    }
    public static void main(String args[])
    {
        App app = new App();
    }
    public class CoordinateClick implements MouseListener { //allows users to input coordinates by clicking on screen
        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            coords.add(new Vector(SCREEN_TO_GRAPH_X.apply(e.getPoint().getX()), SCREEN_TO_GRAPH_Y.apply(e.getPoint().getY()) ) );
            System.out.println(coords);
        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }
}
