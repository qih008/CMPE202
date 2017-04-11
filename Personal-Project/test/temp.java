/**
 * @opt attributes
 * @opt operations
 * @opt visibility
 * @opt types
 * @hidden
 */
class UMLOptions {}
class ConcreteObserver implements Observer{
public void update() {};
public void showState() {};
}
class ConcreteSubject implements Subject{
public String subjectState;
public void attach(Observer obj) {};
public void detach(Observer obj) {};
public void notifyObservers() {};
public void showState() {};
}
interface Observer{
public void update() {};
}
class Optimist extends ConcreteObserver{
public void update() {};
}
class Pessimist extends ConcreteObserver{
public void update() {};
}
interface Subject{
public void attach(Observer obj) {};
public void detach(Observer obj) {};
public void notifyObservers() {};
}
class TheEconomy extends ConcreteSubject{
}
