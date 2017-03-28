/**
 * Created by qhuang on 3/4/17.
 */
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.beans.ParameterDescriptor;
import java.io.*;
import java.util.List;


public class javaparser {

    public static void main(String[] args) throws Exception {
        // creates an input stream for the file to be parsed
        //FileInputStream in = new FileInputStream("/Users/qing/Desktop/CMPE202/Tests/uml-parser-test-1");
        File dir = new File("/Users/qing/Desktop/CMPE202/Tests/uml-parser-test-1");

        File[] files = dir.listFiles(new FilenameFilter() {
            //@Override
            public boolean accept(File directory, String fileName) {
                if (fileName.endsWith(".java")) {
                    return true;
                }
                return false;
            }
        });

        for (File f : files) {
            // parse the file
            CompilationUnit cu = JavaParser.parse(f);


            // prints the changed compilation unit
            //System.out.println(cu);

            new ClassVisitor().visit(cu, null);

            for (TypeDeclaration typeDec : cu.getTypes()) {
                List<BodyDeclaration> members = typeDec.getMembers();
                if (members != null) {
                    for (BodyDeclaration member : members) {
                        //Check just members that are FieldDeclarations
                        FieldDeclaration field = (FieldDeclaration) member;
                        //Print the field's class type
                        System.out.println(field.getModifiers());
                        //Print the field's name
                        System.out.println(field.getVariables().get(0).getType() + " " + field.getVariables().get(0).getName());
                    }
                }
            }
        }
    }
    /*
            * Simple visitor implementation for visiting MethodDeclaration nodes.
     */
    private static class ClassVisitor extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            System.out.println(n.getName());
        }
    }
}
