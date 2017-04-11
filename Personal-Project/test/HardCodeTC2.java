/**
 * @opt attributes
 * @opt operations
 * @opt visibility
 * @opt types
 * @hidden
 */

class UMLOptions {}

interface A1{
}
interface A2{
}

class B1 extends P implements A1{

}
class B2 extends P implements A1, A2{

}
/**
 * @depend - <uses> - A1
 */
class C1{
    public void test(A1 a1) {};
}

/**
 * @depend - <uses> - A2
 */
class C2{
    public void test(A2 a2) {};
}

class P{
}