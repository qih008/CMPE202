/**
 * Created by hq199_000 on 2017/2/23.
 */

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

import java.io.FileInputStream;

import static com.github.javaparser.ast.type.PrimitiveType.intType;

public class MethodChanger2 {

    public static void main(String[] args) throws Exception {
        // creates an input stream for the file to be parsed
        FileInputStream in = new FileInputStream("C:\\Users\\hq199_000\\Desktop\\CMPE202\\Personal-Project\\src\\main\\java\\Hello1.java");

        // parse the file
        CompilationUnit cu = JavaParser.parse(in);

        // change the methods names and parameters
        changeMethods(cu);

        // prints the changed compilation unit
        System.out.println(cu.toString());
    }

    private static void changeMethods(CompilationUnit cu) {
        // Go through all the types in the file
        NodeList<TypeDeclaration<?>> types = cu.getTypes();
        for (TypeDeclaration<?> type : types) {
            // Go through all fields, methods, etc. in this type
            NodeList<BodyDeclaration<?>> members = type.getMembers();
            for (BodyDeclaration<?> member : members) {
                if (member instanceof MethodDeclaration) {
                    MethodDeclaration method = (MethodDeclaration) member;
                    changeMethod(method);
                }
            }
        }
    }

    private static void changeMethod(MethodDeclaration n) {
        // change the name of the method to upper case
        n.setName(n.getNameAsString().toUpperCase());

        // create the new parameter
        n.addParameter(intType(), "value");
    }

   private void processNode(Node node) {
        if (node instanceof TypeDeclaration) {
            // do something with this type declaration
        } else if (node instanceof MethodDeclaration) {
            // do something with this method declaration
        } else if (node instanceof FieldDeclaration) {
            // do something with this field declaration
        }
        for (Node child : node.getChildNodes()){
            processNode(child);
        }
    }
}