package Nodes;

public interface Visitable{

    void accept(Visitor v) throws IllegalAccessException, InstantiationException, ClassNotFoundException;

}
