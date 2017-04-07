/**
 * Created by qhuang on 3/4/17.
 */
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.io.*;
import java.util.List;


public class javaparser {

    private StringBuilder sb = new StringBuilder();      // store intermediate code


    public javaparser(String path) throws IOException {

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

        // parse the file
        for (File f : files) {
            try {
                CompilationUnit cu = JavaParser.parse(f);
                List<Node> nodes = cu.getChildNodes();
                if(nodes != null){
                    for(Node node : nodes){
                        if (node instanceof ClassOrInterfaceDeclaration) {

                            int relation_flat = 0;

                            // check extension
                            List<ClassOrInterfaceType> extendeds = ((ClassOrInterfaceDeclaration) node).getExtendedTypes();
                            if(extendeds != null) {
                                for (ClassOrInterfaceType classtype : extendeds) {
                                    //System.out.println("class "+((ClassOrInterfaceDeclaration) node).getName()+" extends " + classtype);
                                    sb.append("class "+((ClassOrInterfaceDeclaration) node).getName() + " extends " + classtype + "{\n");
                                    relation_flat = 1;
                                }
                            }

                            // check implementation
                            List<ClassOrInterfaceType> implementeds = ((ClassOrInterfaceDeclaration) node).getImplementedTypes();
                            if(implementeds != null) {
                                for (ClassOrInterfaceType classtype : implementeds) {
                                    //System.out.println("class "+((ClassOrInterfaceDeclaration) node).getName()+" implements " + classtype);
                                    sb.append("class "+((ClassOrInterfaceDeclaration) node).getName()+" implements " + classtype + "{\n");
                                    relation_flat = 1;
                                }
                            }

                            if(relation_flat == 0)
                                //System.out.println("class "+((ClassOrInterfaceDeclaration) node).getName());
                                sb.append("class "+((ClassOrInterfaceDeclaration) node).getName() + "{\n");

                            List<Node> childNodes = node.getChildNodes();
                            //int temp = 1;
                            for(Node child : childNodes){
                                //System.out.println(""+ temp++ +" "+ child);
                                if(child instanceof FieldDeclaration){                    //check params, methods, constructor
                                    if(isPublic(child))
                                        addPublicParam((FieldDeclaration) child);
                                    else if(isPrivate(child))
                                        addPrivateParam((FieldDeclaration) child);
                                }
                                else if(child instanceof MethodDeclaration){
                                    if(isPublic(child))
                                        addPublicMethod((MethodDeclaration) child);
                                }
                                else if(child instanceof ConstructorDeclaration){

                                }

                            }

                            sb.append("}\n");

                        }
                    }
                }
                System.out.println("---------------------------");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        System.out.println(sb.toString());

       FileOutputStream out = new FileOutputStream("./temp.java");
       out.write(sb.toString().getBytes());
       out.close();

    }

    private boolean isPublic(Node node){
        if(node instanceof FieldDeclaration) {
            return (((FieldDeclaration) node).getModifiers().contains(Modifier.PUBLIC));
        }
        else if(node instanceof MethodDeclaration){
            return (((MethodDeclaration) node).getModifiers().contains(Modifier.PUBLIC));
        }
        else if(node instanceof ConstructorDeclaration){
            return (((ConstructorDeclaration) node).getModifiers().contains(Modifier.PUBLIC));
        }
        else
            return false;
    }

    private boolean isPrivate(Node node){
        if(node instanceof FieldDeclaration) {
            return (((FieldDeclaration) node).getModifiers().contains(Modifier.PRIVATE));
        }
        else
            return false;
    }

    private void addPublicParam(FieldDeclaration node){
        sb.append("public " + node.getCommonType() + " " + node.getVariables().get(0).getName() + ";\n");
    }

    private void addPrivateParam(FieldDeclaration node){
        sb.append("private " + node.getCommonType() + " " + node.getVariables().get(0).getName() + ";\n");
    }

    private void addPublicMethod(MethodDeclaration node){
        sb.append("public " + node.getType() + " " + node.getName() + "() {};\n");
        //System.out.println(node.getType());
    }
}
