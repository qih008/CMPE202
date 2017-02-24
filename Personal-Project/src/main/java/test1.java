/**
 * Created by hq199_000 on 2017/2/23.
 */

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.FileInputStream;
import java.util.List;

public class test1 {

    public static void main(String[] args) throws Exception {
        // creates an input stream for the file to be parsed
        FileInputStream in = new FileInputStream("C:\\Users\\hq199_000\\Desktop\\CMPE202\\Personal-Project\\src\\main\\java\\A.java");

        // parse the file
        CompilationUnit cu = JavaParser.parse(in);


        // prints the changed compilation unit
        //System.out.println(cu.);

        for (TypeDeclaration typeDec : cu.getTypes()) {
            List<BodyDeclaration> members = typeDec.getMembers();
            if (members != null) {
                for (BodyDeclaration member : members) {
                    //Check just members that are FieldDeclarations
                    FieldDeclaration field = (FieldDeclaration) member;
                    //Print the field's class typr
                    System.out.println(field.getModifiers());
                    //Print the field's name
                    System.out.println(field.getVariables().get(0).getType()+" "+field.getVariables().get(0).getName());
                }
            }

        }
    }


}
