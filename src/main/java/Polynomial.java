import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Polynomial {
    ArrayList<Term> terms = new ArrayList<>();
    Function<Double, Double> function;
    public static Function<Character, Boolean> isNumber = c -> c >= 48 && c <= 57;
    public static Function<String, Boolean> isNumberString = s -> {
        try{
            Integer.parseInt(s);
            return true;
        }
        catch(Exception e){
            return false;
        }
    };

    public Polynomial(){}
    public Polynomial(Term... terms){
        this.terms = new ArrayList<>(Arrays.asList(terms));
        function = createPolynomial(this.terms);
    }
    public void add(Term term){
        terms.add(term);
        terms.sort((o1, o2) -> {
            if(o1.power > o2.power)
                return -1;
            else if(o2.power > o1.power)
                return 1;
            return 0;
        });
        function = createPolynomial(this.terms);
    }
    public boolean hasTermWithPower(double power){
        for(Term term : terms)
            if(term.power == power)
                return true;
        return false;
    }
    public HashMap<Double, Double> getPowerToCoefficientMap() {
        HashMap<Double, Double> map = new HashMap<>();
        for(Term term : terms) {
            if (map.containsKey(term.power)) {
                map.replace(term.power, map.get(term.power) + term.coefficient);
            } else {
                map.put(term.power, term.coefficient);
            }
        }
        return map;
    }
    public double getCoOfPower(double power){
        for(Term term : terms)
            if(term.power == power)
                return term.coefficient;
        return 0.0;
    }
    private static Function<Double, Double> createPolynomial(ArrayList<Term> terms){
        return a ->{
            double output = 0.0;
            for(Term term : terms)
                output += term.function.apply(a);
            return output;
        };
    }
    public static Polynomial findDerivative(Polynomial poly){
        Polynomial output = new Polynomial();
        for(Term term : poly.terms)
            output.add(Term.findDerivative(term));
        return output;
    }

    public void clean(){
        for(Term orig : terms){
            for(Term other : terms){
                if(orig != other){
                    if(orig.power == other.power){
                        orig.coefficient += other.coefficient;
                        other.coefficient = 0.0;
                    }
                }
            }
        }
        terms = new ArrayList<>(terms.stream().filter(term -> term.coefficient != 0).collect(Collectors.toList()));
        boolean hasConstant = false;
        for(int i = 0; i < terms.size(); i++)
            if(terms.get(i).power == 0.0)
                hasConstant = true;
        if(!hasConstant)
            terms.add(new Term(0, 0));
        function = createPolynomial(this.terms);
    }
    public String toString() {
        String output = "";
        for(Term term : terms)
            output += term.toString() + " + ";
        output = output.substring(0, output.length() - 3);
        return output;
    }
    public static Polynomial parsePoly(String str){ return parsePoly(str, 'x'); }
    public static Polynomial parsePoly(String str, char termsOf){
        String filtered = str.chars().mapToObj(c -> (char)c).filter(c -> c != ' ').filter(c -> {
            if(isNumber.apply(c) || c == termsOf) {
                return true;
            }
            else {
                return c == '+' || c == '-' || c == '*' || c == '^' || c == '(' || c == ')' || c == '/' || c == '.' || c == termsOf;
            }
        }).collect(Collector.of(StringBuilder::new, StringBuilder::append, StringBuilder::append, StringBuilder::toString));

        //removes spaces and non numerical characters that aren't the variable that the equation is in terms of
        ArrayList<String> chunks = new ArrayList<>();
        filtered.chars().mapToObj(c -> (char)c).forEach(c ->{
            char before = chunks.size() == 0 ? ' ' : chunks.get(chunks.size() - 1).charAt(0);
            if( ((isNumber.apply(before) || before == termsOf)== isNumber.apply((c)) && chunks.size() > 0) || (isNumber.apply(before) && c == termsOf) || (c.equals('^')) || (c.equals('.')) )
            {
                chunks.set(chunks.size() - 1, chunks.get(chunks.size() - 1) + c);
            }
            else{
                chunks.add("" + c);
            }
        });

        Polynomial output = new Polynomial();
        int multiplier = 1;
        for(int i = 0; i < chunks.size(); i++) {
            String curr = chunks.get(i);
            if(curr.charAt(0) == '+')
                multiplier = 1;
            else if(curr.charAt(0) == '-')
                multiplier = -1;
            else {
                String[] segs = curr.split("\\^");
                if(segs[0].charAt(0) == 'x')
                    segs[0] = "1" + segs[0];
                output.add(new Term(multiplier * Double.parseDouble(segs[0].chars().
                        mapToObj(c -> (char)c).filter(c -> c != termsOf).collect(Collector.of(StringBuilder::new, StringBuilder::append, StringBuilder::append, StringBuilder::toString))),
                        segs.length > 1 ? Double.parseDouble(segs[1]) : (segs[0].contains("" + termsOf) ? 1.0 : 0.0)));
            }
        }
        return output;
    }
}
