package regression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class Optimizer {
    private int degree = 1; //degree of polynomial being regressed
    private static boolean created = false;
    private static Optimizer instance; //global instance of this class
    private List<Vector> coords = new ArrayList<>();
    private int runs = 500000; //number of times the gradient descrnet algorithm is run
    private double alpha = 0.000001;//"learning rate" the size of the changes to the paraemeters of the function
    private static Function3<Vector, Function<Double, Double>, Double, Double> GRADIENT = (vec, function, power) -> 2 * (function.apply(vec.x) - vec.y) * Math.pow(vec.x, power); //the computes the general direction and magnitude of next step
    private static Function2<Function<Double, Double>, List<Vector>, Double> COST = (func, coords) -> coords.stream().mapToDouble(coord -> Math.pow(func.apply(coord.x) - coord.y, 2)).sum()/coords.size(); //computes how far off the current function is
    private Polynomial function; //the test function
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
    public void oldOptimize(){ //previous version of the optimize function
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
                System.out.println("\tdA for regression.Vector + " + vec + ": " + deriv);
                return deriv.function.apply(a);
            }).sum();
            double dB = 1.0/coords.size() * coords.stream().mapToDouble(vec -> {
                Polynomial deriv = Polynomial.parsePoly(2 * a * vec.x + " - " + 2 * vec.y + " + " + 2 + "b", 'b');
                System.out.println("\tdB for regression.Vector + " + vec + ": " + deriv);
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
    public void regress(){ //current version of the optimize function
        coords.sort( (o1, o2) -> (o1.x > o2.x ? 1 : -1));
        double minCost = Double.MAX_VALUE;
        Polynomial best = new Polynomial(); //holds the function with the lowest cost
        Polynomial testFunction = generateStarterWithDegreeOf(degree); //creates generic function as starting point

        for(int i = 0; i < runs; i++){
            double cost = COST.apply(testFunction.function, coords); //find the cost of current function

            if(cost < minCost) //saves the function if the cost is lesser than the current best
            {
                best = testFunction;
                minCost = cost;
            }

            HashMap<Double, Double> map = testFunction.getPowerToCoefficientMap(); //map of coefficients (power -> coefficient)
            HashMap<Double, Double> dMap = new HashMap<>(); //map of derivatives (power -> derivative)

            for(double power : map.keySet()){
                Function<Double, Double> function = testFunction.function;
                dMap.put(power,
                        1.0/coords.size() * coords.stream().mapToDouble(vec -> GRADIENT.apply(vec, function, power)).sum()); //averages the gradient of all existing points
            }

            testFunction = new Polynomial();
            for(double power : map.keySet()) {
                map.replace(power, map.get(power) - dMap.get(power) * alpha); //updates new values for coefficients
                testFunction.add(new Term(map.get(power), power));
            }

            testFunction.clean(); //removes terms that have a coefficient of 0

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
