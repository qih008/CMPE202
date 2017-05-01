/**
 * Created by qhuang on 3/4/17.
 */
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.*;

import java.io.*;
import java.util.*;


public class javaparser {

    private StringBuilder sb = new StringBuilder();      // store intermediate code
    private HashSet<String> hs = new HashSet<String>();
    private HashMap<String, HashMap<String, String>> assocMap = new HashMap<String, HashMap<String, String>>();
    private List<String> assocArray = new ArrayList<String>();


    public javaparser(String path, String outfile) throws IOException {

        // init umlgraph format
        sb.append("/**\n" +
                " * @opt attributes\n" +
                " * @opt operations\n" +
                " * @opt visibility\n" +
                " * @opt constructors\n" +
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

                            //int relation_flat = 0;
                            String class_or_interface = "";

                            // check class or interface
                            if(((ClassOrInterfaceDeclaration) node).isInterface())
                                class_or_interface = "interface ";
                            else
                                class_or_interface = "class ";

                            // check extension
                            List<ClassOrInterfaceType> extendeds = ((ClassOrInterfaceDeclaration) node).getExtendedTypes();
                            // check implementation
                            List<ClassOrInterfaceType> implementeds = ((ClassOrInterfaceDeclaration) node).getImplementedTypes();

                            //System.out.println("extendeds " + extendeds.size() + " implementeds " + implementeds.size());

                            if((extendeds.size() != 0) && (implementeds.size() == 0)){
                                for (ClassOrInterfaceType classtype : extendeds) {
                                    //System.out.println("class "+((ClassOrInterfaceDeclaration) node).getName()+" extends " + classtype);
                                    sb.append(class_or_interface + ((ClassOrInterfaceDeclaration) node).getName() + " extends " + classtype + "{\n");
                                    //relation_flat = 1;
                                }
                            }
                            else if((implementeds.size() != 0) && (extendeds.size() == 0)) {
//                                for (ClassOrInterfaceType classtype : implementeds) {
//                                    //System.out.println("class "+((ClassOrInterfaceDeclaration) node).getName()+" implements " + classtype);
//                                    sb.append(class_or_interface + ((ClassOrInterfaceDeclaration) node).getName() + " implements " + classtype + "{\n");
//                                    //relation_flat = 1;
//                                }
                                for(int i = 0; i < implementeds.size(); i++){
                                    if(i == 0){
                                        sb.append(class_or_interface + ((ClassOrInterfaceDeclaration) node).getName() + " implements " + implementeds.get(i));
                                    }
                                    else{
                                        sb.append(", " + implementeds.get(i));
                                    }
                                }
                                sb.append("{\n");
                            }
                            else if((extendeds.size() != 0) && (implementeds.size() != 0)){
                                for (ClassOrInterfaceType classtype : extendeds) {
                                    sb.append(class_or_interface + ((ClassOrInterfaceDeclaration) node).getName() + " extends " + classtype);

                                }
//                                for (ClassOrInterfaceType classtype : implementeds) {
//                                    sb.append(" implements " + classtype);
//                                }
                                for(int i = 0; i < implementeds.size(); i++){
                                    if(i == 0){
                                        sb.append(" implements " + implementeds.get(i));
                                    }
                                    else{
                                        sb.append(", " + implementeds.get(i));
                                    }
                                }
                                sb.append("{\n");
                                //relation_flat = 1;
                            }
                            else{
                                //System.out.println("class "+((ClassOrInterfaceDeclaration) node).getName());
                                sb.append(class_or_interface + ((ClassOrInterfaceDeclaration) node).getName() + "{\n");
                            }


                            // store temp assoc relation between class
                            HashMap<String, String> subMap = new HashMap<String, String>();

                            List<Node> childNodes = node.getChildNodes();
                            for (Node child : childNodes) {

                                //check Attributes, methods
                                if (child instanceof FieldDeclaration) {
                                    // check for Collection, Class, Arrays Attributes
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
                                                if (isPublic(child)) {
                                                    //System.out.println(((FieldDeclaration) child).getModifiers());
                                                    addPublicAttribute((FieldDeclaration) child);
                                                }
                                                else if (isPrivate(child))
                                                    addPrivateAttribute((FieldDeclaration) child);
                                            }
                                        }
                                        // for others type, e.g: class, collection
                                        else{

                                            // special check for type String
                                            if(String.valueOf(elementType).equals("String")){
                                                if (isPublic(child)){
                                                    //System.out.println(((FieldDeclaration) child).getModifiers());
                                                    addPublicAttribute((FieldDeclaration) child);
                                                }
                                                else if (isPrivate(child))
                                                    addPrivateAttribute((FieldDeclaration) child);

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
                                    // normal Attributes (which not include String...)
                                    else {
                                        if (isPublic(child)) {
                                            //System.out.println(((FieldDeclaration) child).getModifiers());
                                            addPublicAttribute((FieldDeclaration) child);
                                        }
                                        else if (isPrivate(child)) {
                                            addPrivateAttribute((FieldDeclaration) child);
                                            //sb.append("-------------------------------\n");
                                        }
                                    }
                                }
                                else if (child instanceof MethodDeclaration) {
                                    if (isPublic(child)) {
                                        addPublicMethod((MethodDeclaration) child);
                                    }
                                }
                                else if (child instanceof ConstructorDeclaration){
                                    if (isPublic(child)) {
                                        //System.out.println(((ConstructorDeclaration) child).getName());
                                        addPublicConstructor((ConstructorDeclaration) child);
                                    }
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
        out_intermedia_file(outfile);
    }

    private void out_intermedia_file(String outfile) throws IOException {

        // replace private Attribute to public if it have getter/setter
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
        //System.out.println(sb.toString());
        FileOutputStream out = new FileOutputStream("./" + outfile + ".java");
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

    private void addPublicAttribute(FieldDeclaration node){
        sb.append("public " + node.getCommonType() + " " + node.getVariables().get(0).getName() + ";\n");
    }

    private void addPrivateAttribute(FieldDeclaration node){
        sb.append("private " + node.getCommonType() + " " + node.getVariables().get(0).getName() + ";\n");
    }

    private void addPublicMethod(MethodDeclaration node){
        String name = String.valueOf(node.getName());

        if(name.startsWith("get"))                       // check Java Setters/Getters
            hs.add(name.split("get")[1].toLowerCase());
        else if(name.startsWith("set"))
            hs.add(name.split("set")[1].toLowerCase());
        else {

            sb.append("public " + node.getType() + " " + node.getName() + "(");

            // check function's params
            List<Parameter> params = node.getParameters();

            int first_depen = 0;
            for(int i = 0; i < params.size(); i++){
                Parameter param = params.get(i);

                // add params to stringbuilder
                if(i == 0){
                    sb.append(param.getType() + " " + param.getName() );
                }
                else{
                    sb.append(", " + param.getType() + " " + param.getName());
                }

                // check interface dependency
                if (param.getType() instanceof ClassOrInterfaceType) {
                    if(first_depen == 0) {
                        //System.out.println(node.getName() + " " + param.getType());
                        //int name_index = sb.lastIndexOf(String.valueOf(node.getName()));
                        //int temp_index = name_index - 30;
                        int class_index = sb.lastIndexOf("class");
                        int interface_index = sb.lastIndexOf("interface");
                        int target_index = Math.max(class_index, interface_index);
                        if(target_index == -1)
                            continue;

                        sb.insert(target_index, "/**\n" +
                                " * @depend "+"- - - " + param.getType() + "\n" +
                                "*/ \n");

                        first_depen = 1;
                    }
                    else{
                        // TODO handle multiple dependency for one class
                    }
                }
            }

            sb.append(") {};\n");
        }

        // check attributes inside method
//        List<Node> children = node.getChildNodes();
//        for(Node child : children){
//            if(child instanceof FieldDeclaration)
//                System.out.println(child);
//        }

    }

    private void addPublicConstructor(ConstructorDeclaration node){


        sb.append("public " + node.getName() + "(");

        // check function's params
        List<Parameter> params = node.getParameters();

        int first_depen = 0;
        for(int i = 0; i < params.size(); i++){
            Parameter param = params.get(i);
            // add params to stringbuilder
            if(i == 0){
                sb.append(param.getType() + " " + param.getName() );
            }
            else{
                sb.append(", " + param.getType() + " " + param.getName());
            }

            // check interface dependency
            if (param.getType() instanceof ClassOrInterfaceType) {
                if(first_depen == 0) {
                    //System.out.println(node.getName() + " " + param.getType());
                    //int name_index = sb.lastIndexOf(String.valueOf(node.getName()));
                    //int temp_index = name_index - 30;
                    int class_index = sb.lastIndexOf("class");
                    int interface_index = sb.lastIndexOf("interface");
                    int target_index = Math.max(class_index, interface_index);
                    if(target_index == -1)
                        continue;

                    sb.insert(target_index, "/**\n" +
                            " * @depend "+"- - - " + param.getType() + "\n" +
                            "*/ \n");

                    first_depen = 1;
                }
                else{
                    // TODO handle multiple dependency for one class
                }
            }
        }

        sb.append(") {};\n");
    }

    private void addAssoc(String in_string){

        String[] temp = in_string.split(" ");
        int class_index = sb.lastIndexOf(temp[0]);

        //System.out.println(temp[0] +" "+ temp[1] +" "+ temp[2] +" "+ temp[3]);
        if(temp[3].equals("null") || temp[1].equals("null"))
            return;

        // check if we have multiple assoc
        int temp_index = class_index - 30;
        int target_index = sb.indexOf("* @assoc", temp_index);
        //System.out.println(target_index);

        if((target_index == -1) || (target_index > class_index)){
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
