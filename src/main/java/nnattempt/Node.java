package nnattempt;

import java.util.function.Function;

public class Node{
    private Polynomial linear;
    private Function<Double, Double> activation;
    private Layer parent;
    public Node(Layer parent, Polynomial linear, Function<Double, Double> activation){
        this.parent = parent;
        this.linear = linear;
        this.activation = activation;
    }

    public Polynomial getFunction() { return linear; }
    public Function<Double, Double> getActivation() { return activation; }
    public Layer getParent() { return parent; }

}
