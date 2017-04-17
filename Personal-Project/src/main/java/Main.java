import java.io.IOException;

/**
 * Created by qhuang on 3/28/17.
 */
public class Main {

    // main class to call javaparser
    public static void main(String[] args) throws IOException {
    	  //System.out.println(""+args.length+" "+args[0] + " " + args[1]);
        //new javaparser("/Users/qing/Desktop/CMPE202/Tests/uml-parser-test-5");
        new javaparser(args[0], args[1]);
    }
}
