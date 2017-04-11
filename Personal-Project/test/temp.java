/**
 * @opt attributes
 * @opt operations
 * @opt visibility
 * @opt constructors
 * @opt types
 * @hidden
 */
class UMLOptions {}
interface Component{
public String operation() {};
}
class ConcreteComponent implements Component{
public String operation() {};
}
/**
 * @depend - - - Component
*/ 
class ConcreteDecoratorA extends Decorator{
private String addedState;
public ConcreteDecoratorA(Component c) {};
public String operation() {};
}
/**
 * @depend - - - Component
*/ 
class ConcreteDecoratorB extends Decorator{
private String addedState;
public ConcreteDecoratorB(Component c) {};
public String operation() {};
}
/**
 * @depend - - - Component
*/ 
class Decorator implements Component{
public Decorator(Component c) {};
public String operation() {};
}
class Tester{
public void main(String[] args) {};
}
