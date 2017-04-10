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
import com.github.javaparser.ast.type.*;

import java.io.*;
import java.util.*;


public class javaparser {

    private StringBuilder sb = new StringBuilder();      // store intermediate code
    private HashSet<String> hs = new HashSet<String>();
    private HashMap<String, HashMap<String, String>> assocMap = new HashMap<String, HashMap<String, String>>();
    private List<String> assocArray = new ArrayList<String>();


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
                if (nodes != null) {
                    for (Node node : nodes) {                         // each node is a class or interface
                        if (node instanceof ClassOrInterfaceDeclaration) {

                            int relation_flat = 0;

                            // check extension
                            List<ClassOrInterfaceType> extendeds = ((ClassOrInterfaceDeclaration) node).getExtendedTypes();
                            if (extendeds != null) {
                                for (ClassOrInterfaceType classtype : extendeds) {
                                    //System.out.println("class "+((ClassOrInterfaceDeclaration) node).getName()+" extends " + classtype);
                                    sb.append("class " + ((ClassOrInterfaceDeclaration) node).getName() + " extends " + classtype + "{\n");
                                    relation_flat = 1;
                                }
                            }

                            // check implementation
                            List<ClassOrInterfaceType> implementeds = ((ClassOrInterfaceDeclaration) node).getImplementedTypes();
                            if (implementeds != null) {
                                for (ClassOrInterfaceType classtype : implementeds) {
                                    //System.out.println("class "+((ClassOrInterfaceDeclaration) node).getName()+" implements " + classtype);
                                    sb.append("class " + ((ClassOrInterfaceDeclaration) node).getName() + " implements " + classtype + "{\n");
                                    relation_flat = 1;
                                }
                            }

                            if (relation_flat == 0)
                                //System.out.println("class "+((ClassOrInterfaceDeclaration) node).getName());
                                sb.append("class " + ((ClassOrInterfaceDeclaration) node).getName() + "{\n");


                            // store temp assoc relation between class
                            HashMap<String, String> subMap = new HashMap<String, String>();

                            List<Node> childNodes = node.getChildNodes();
                            for (Node child : childNodes) {

                                //check params, methods
                                if (child instanceof FieldDeclaration) {
                                    // check for Collection, Class, Arrays params
                                    if(((FieldDeclaration) child).getCommonType() instanceof ReferenceType){
                                        //System.out.println(((FieldDeclaration) child).getCommonType());
                                        //System.out.println(((FieldDeclaration) child).getElementType());

                                        Type elementType = ((FieldDeclaration) child).getElementType();

                                        // check for any array type, e.g: class[], int[], string[]
                                        if(((FieldDeclaration) child).getCommonType().getArrayLevel() > 0){
                                            //System.out.println(((FieldDeclaration) child).getCommonType());

                                            // class[]...
                                            if(elementType instanceof ClassOrInterfaceType){
                                                // TODO

                                            }
                                            // int[], boolean[], string[]...
                                            else{
                                                if (isPublic(child))
                                                    addPublicParam((FieldDeclaration) child);
                                                else if (isPrivate(child))
                                                    addPrivateParam((FieldDeclaration) child);
                                            }
                                        }
                                        // for others type, e.g: class, collection
                                        else{

                                            // special check for type String
                                            if(String.valueOf(elementType).equals("String")){
                                                if (isPublic(child))
                                                    addPublicParam((FieldDeclaration) child);
                                                else if (isPrivate(child))
                                                    addPrivateParam((FieldDeclaration) child);

                                                continue;
                                            }

                                            //System.out.println(elementType.getChildNodes());
                                            List<Node> type_child = elementType.getChildNodes();

                                            // collection: assoc with *
                                            if(type_child.size() > 0) {
//                                                System.out.print(((ClassOrInterfaceDeclaration) node).getName());
//                                                System.out.print(" 1 ");
//                                                System.out.print(elementType.getChildNodes().get(0));
//                                                System.out.println(" *");

                                                if(!subMap.containsKey(String.valueOf(elementType.getChildNodes().get(0))))
                                                    subMap.put(String.valueOf(elementType.getChildNodes().get(0)), "*");
                                            }

                                            // class: assoc with 1
                                            else{
//                                                System.out.print(((ClassOrInterfaceDeclaration) node).getName());
//                                                System.out.print(" 1 ");
//                                                System.out.print(elementType);
//                                                System.out.println(" 1");

                                                if(!subMap.containsKey(String.valueOf(elementType)))
                                                    subMap.put(String.valueOf(elementType), "1");

                                            }

                                        }

                                    }
                                    // normal params (which not include String...)
                                    else {
                                        if (isPublic(child))
                                            addPublicParam((FieldDeclaration) child);
                                        else if (isPrivate(child)) {
                                            addPrivateParam((FieldDeclaration) child);
                                            //sb.append("-------------------------------\n");
                                        }
                                    }
                                }
                                else if (child instanceof MethodDeclaration) {
                                    if (isPublic(child))
                                        addPublicMethod((MethodDeclaration) child);
                                }
                            }
                            sb.append("}\n");


                            // store the printable assoc relation between classes
                            for(Map.Entry<String, String> entry : subMap.entrySet()){
                                HashMap<String, String> tempMap = assocMap.get(entry.getKey());
                                if( tempMap != null){
                                    assocArray.add(entry.getKey()+" "+entry.getValue()+" "+((ClassOrInterfaceDeclaration) node).getName()
                                            + " "+tempMap.get(String.valueOf(((ClassOrInterfaceDeclaration) node).getName())));
                                }

                            }
                            assocMap.put(String.valueOf(((ClassOrInterfaceDeclaration) node).getName()), subMap);
                        }
                    }
                }
                //System.out.println("---------------------------");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        //System.out.println(assocArray);


        // add each assoc relation to sb
        for(String element : assocArray){
            addAssoc(element);
        }

        // last step of javaparser
        out_intermedia_file();
    }

    private void out_intermedia_file() throws IOException {

        // replace private param to public if it have getter/setter
        Iterator iterator = hs.iterator();
        while(iterator.hasNext()) {
            String temp_name = String.valueOf(iterator.next());
            int name_index = sb.indexOf(temp_name);
            int temp_index = name_index - 20;  // move forward index to check modifier
            int modifier_index = sb.indexOf("private", temp_index);
            if(modifier_index == 0)
                continue;

            sb.replace(modifier_index, modifier_index+7, "public");    // replace modifier to public
        }
        System.out.println(sb.toString());
        FileOutputStream out = new FileOutputStream("./test/temp.java");
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
        String name = String.valueOf(node.getName());
        if(name.startsWith("get"))                       // check Java Setters/Getters
            hs.add(name.split("get")[1].toLowerCase());
        else if(name.startsWith("set"))
            hs.add(name.split("set")[1].toLowerCase());
        else
            sb.append("public " + node.getType() + " " + node.getName() + "() {};\n");
            //System.out.println(node.getType());
    }

    private void addAssoc(String in_string){

        String[] temp = in_string.split(" ");
        int class_index = sb.indexOf(temp[0]);

        // check if we have multiple assoc
        int temp_index = class_index - 30;
        int target_index = sb.indexOf("* @assoc", temp_index);

        if(target_index == -1) {
            temp_index = class_index - 10;
            target_index = sb.indexOf("class", temp_index);

            // append umlgraph assoc format "* @assoc 1 - * B"
            sb.insert(target_index, "/**\n" +
                    " * @assoc "+temp[1]+" - "+temp[3]+" "+temp[2]+"\n" +
                    "*/ \n");
        }
        else {
            sb.insert(target_index, " * @assoc " + temp[1] + " - " + temp[3] + " " + temp[2] + "\n");
        }
    }
}
