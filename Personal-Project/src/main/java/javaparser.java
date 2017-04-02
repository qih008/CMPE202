/**
 * Created by qhuang on 3/4/17.
 */
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.io.*;
import java.util.List;


public class javaparser {

    private StringBuilder sb = new StringBuilder();      // store intermediate code


    public javaparser(String path, String filename){

        // init umlgraph format
        sb.append("/**\n" +
                " * @opt attributes\n" +
                " * @opt operations\n" +
                " * @opt visibility\n" +
                " * @opt types\n" +
                " * @hidden\n" +
                " */\n" +
                "class UMLOptions {}\n");


        // creates an input stream for the file to be parsed
        //FileInputStream in = new FileInputStream("/Users/qing/Desktop/CMPE202/Tests/uml-parser-test-1");
        File dir = new File(path);

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
            try {
                CompilationUnit cu = JavaParser.parse(f);
                List<Node> nodes = cu.getChildNodes();
                if(nodes != null){
                    for(Node node : nodes){
                        if (node instanceof ClassOrInterfaceDeclaration) {
                            System.out.println("class "+((ClassOrInterfaceDeclaration) node).getName());
                            List<Node> childNodes = node.getChildNodes();
                            int temp = 1;
                            for(Node child : childNodes){
                                System.out.println(""+ temp++ +" "+ child);

                            }


                            List<ClassOrInterfaceType> extendLists = ((ClassOrInterfaceDeclaration) node).getExtendedTypes();
                            if(extendLists != null) {
                                for (ClassOrInterfaceType classtype : extendLists) {
                                    System.out.println(((ClassOrInterfaceDeclaration) node).getName()+" extends " + classtype);
                                }
                            }
                        }
                    }
                }
                System.out.println("---------------------------");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        System.out.println(sb.toString());
    }

}
