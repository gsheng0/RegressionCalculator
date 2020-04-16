package regression;

import java.util.function.Function;
import java.util.stream.Collector;

public class Term {

    double coefficient;
    double power;
    Function<Double, Double> function;
    Function<Double, Double> derivative;
    public Term(double coefficient, double power){
        this.coefficient = coefficient;
        this.power = power;
        function = a -> coefficient * Math.pow(a, power);
    }
    public static Term findDerivative(Term term){
        double co = term.coefficient;
        double power = term.power;
        if(power == 0)
            return new Term(0, 0);
        return new Term(power * co, power - 1);
    }
    public String toString() { return coefficient + "x ^ (" + power +")";}

    public Term parseTerm(String str, char termsOf){
        int multiplier = 1;
        if(str.charAt(0) == '-')
            multiplier = -1;
        String[] segs = str.split("\\^");
        if(segs[0].charAt(0) == 'x')
            segs[0] = "1" + segs[0];
        return new Term(multiplier * Double.parseDouble(segs[0].chars().
                mapToObj(c -> (char)c).filter(c -> c != termsOf).collect(Collector.of(StringBuilder::new, StringBuilder::append, StringBuilder::append, StringBuilder::toString))),
                segs.length > 1 ? Double.parseDouble(segs[1]) : 0);
    }

}
