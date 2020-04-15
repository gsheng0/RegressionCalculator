import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

public class Optimizer {
    private int degree = 1;
    private static boolean created = false;
    private static Optimizer instance;
    private List<Vector> coords = new ArrayList<>();
    private int runs = 500000;
    private double alpha = 0.000001;//0.000000000001;
    private static Function3<Vector, Function<Double, Double>, Double, Double> GRADIENT = (vec, function, power) -> 2 * (function.apply(vec.x) - vec.y) * Math.pow(vec.x, power);
    private static Function2<Function<Double, Double>, List<Vector>, Double> COST = (func, coords) -> coords.stream().mapToDouble(coord -> Math.pow(func.apply(coord.x) - coord.y, 2)).sum()/coords.size();
    private Polynomial function;
    private Optimizer(){
        created = true;
    }
    public static Optimizer getInstance(){
        if(!created)
            instance = new Optimizer();
        return instance;
    }
    public void setAlpha(double alpha) { this.alpha = alpha; }
    public void add(Vector coord) { coords.add(coord); }
    public void setCoords(List<Vector> coords){
        this.coords = coords;
    }
    public void setRuns(int runs) { this.runs = runs;}
    public Polynomial getFunction() { return function; }
    public void setDegree(int degree) { this.degree = degree; }
    public void oldOptimize(){
        coords.sort( (o1, o2) -> (o1.x > o2.x ? 1 : -1));
        String initial = "0x + " + coords.get(0).y;
        System.out.println(initial);
        double minCost = Double.MAX_VALUE;
        double prevCost = Double.MAX_VALUE;
        Polynomial best = new Polynomial();
        Polynomial testFunction = Polynomial.parsePoly("1x + 0");

        while(true){
            testFunction.clean();
            HashMap<Double, Double> map = testFunction.getPowerToCoefficientMap();
            //System.out.println("Function: " + testFunction);
            double a = map.get(1.0);
            double b = map.get(0.0);
            double cost = COST.apply(testFunction.function, coords);
            //System.out.println("\tCost: " + cost);
            if(cost < minCost)
            {
                best = testFunction;
                minCost = cost;
            }
            if(cost > prevCost)
                break;

            prevCost = cost;

            double dA = 1.0/coords.size() * coords.stream().mapToDouble(vec -> {//IN TERMS OF A
                //Write polynomial in terms of one of the variables
                Polynomial deriv = Polynomial.parsePoly(2 * Math.pow(vec.x, 2.0) + "a + " + 2 * b * vec.x + " - " + 2 * vec.x * vec.y, 'a');
                System.out.println("\tdA for Vector + " + vec + ": " + deriv);
                return deriv.function.apply(a);
            }).sum();
            double dB = 1.0/coords.size() * coords.stream().mapToDouble(vec -> {
                Polynomial deriv = Polynomial.parsePoly(2 * a * vec.x + " - " + 2 * vec.y + " + " + 2 + "b", 'b');
                System.out.println("\tdB for Vector + " + vec + ": " + deriv);
                return deriv.function.apply(b);
            }).sum();
            System.out.println("\tdA: " + dA);
            System.out.println("\tdB: " + dB);
            double newA = a - dA * alpha;
            double newB = b - dB * alpha;

            testFunction = Polynomial.parsePoly(newA + "x + " + newB);
        }
        System.out.println("Final output: " + best);
        System.out.println("\tCost: " + minCost);
        function = best;

    }
    public void regress(){
        coords.sort( (o1, o2) -> (o1.x > o2.x ? 1 : -1));
        double minCost = Double.MAX_VALUE;
        Polynomial best = new Polynomial();
        Polynomial testFunction = generateStarterWithDegreeOf(degree);

        for(int i = 0; i < runs; i++){
            //System.out.println("Function: " + testFunction);
            double cost = COST.apply(testFunction.function, coords);
            //System.out.println("\tCost: " + cost);
            if(cost < minCost)
            {
                best = testFunction;
                minCost = cost;
            }

            HashMap<Double, Double> map = testFunction.getPowerToCoefficientMap();
            HashMap<Double, Double> dMap = new HashMap<>();
            for(double power : map.keySet()){
                Function<Double, Double> function = testFunction.function;
                dMap.put(power, 1.0/coords.size() * coords.stream().mapToDouble(vec -> GRADIENT.apply(vec, function, power)).sum());
            }

            testFunction = new Polynomial();
            for(double power : map.keySet()) {
                map.replace(power, map.get(power) - dMap.get(power) * alpha);
                testFunction.add(new Term(map.get(power), power));
            }

            testFunction.clean();

        }
        System.out.println("Final output: " + best);
        System.out.println("\tCost: " + minCost);
        function = best;
    }
    public Polynomial generateStarterWithDegreeOf(int degree){
        Polynomial output = new Polynomial();
        for(int i = 0; i <= degree; i++)
            output.add(new Term(1, i));

        return output;
    }




}
