import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;


public class Main {
    public static void main(String[] args) throws BadLocationException {
        JFrame jFrame=new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(600,600);
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new java.io.File(".")); // start at application current directory
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        File yourFolder=null;
        int returnVal = fc.showSaveDialog(jFrame);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            yourFolder = fc.getSelectedFile();
        }

        Container cp = jFrame.getContentPane();
        JTextPane pane = new JTextPane();
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        SimpleAttributeSet attributeSetBold = new SimpleAttributeSet();
        StyleConstants.setBold(attributeSet, true);
        StyleConstants.setBold(attributeSetBold, true);
        StyleConstants.setFontSize(attributeSetBold,20);
        pane.setCharacterAttributes(attributeSet, true);
        JScrollPane scrollPane = new JScrollPane(pane);
        Document doc = pane.getStyledDocument();
        cp.add(scrollPane, BorderLayout.CENTER);
        System.out.println("Hello world!");
        ArrayList<String>blackList=new ArrayList<>();
        blackList.add("you");
        blackList.add("was");
        blackList.add("happier");
        blackList.add("the");
        blackList.add("up");
        ArrayList<String>blackListImageFiles=new ArrayList<>();
        blackListImageFiles.add("auto");
        blackListImageFiles.add("side");
        //String path="D:\\downloads\\renpy-8.1.1-sdk\\FriendsinNeed.0.58-pc\\game";
        String path=yourFolder.getPath();
        System.out.println(path);
        ArrayList<String>renpyFiles=getRenpyFiles(path);
        ArrayList<String>images=new ArrayList<>();
        for(String file:renpyFiles){
            try {
                images.addAll(whenReadLargeFileJava7_thenCorrect(path+"\\"+file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        final File folder = new File(path+"\\images");
        ArrayList<String>imageFiles=listFilesForFolder(folder);
        for(String image:images){
           //System.out.println(image);
        }
        images.removeAll(animationTags);
        images.removeAll(blackList);
        images.removeAll(imageFiles);
        doc.insertString(doc.getLength(), "<--Images missing-->\n",attributeSetBold);
        for(String image:images){
            System.out.println(image);
            doc.insertString(doc.getLength(), image+"\n", attributeSet);
            if(!imageFiles.contains(image)){
                if(!blackList.contains(image)){
                    //System.out.println(image);
                }
            }
        }
        for(String file:renpyFiles){
            try {
                images.addAll(whenReadLargeFileJava7_thenCorrect(path+"\\"+file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        animationTags.removeAll(images);
        animationTags.removeAll(blackListImageFiles);
        imageFiles.removeAll(images);
        imageFiles.removeAll(blackListImageFiles);
        doc.insertString(doc.getLength(), "<--Excess Images-->\n",attributeSetBold);
        for(String image:animationTags){
            System.out.println(image);
            doc.insertString(doc.getLength(), image+"\n", attributeSet);
            if(!imageFiles.contains(image)){
                if(!blackListImageFiles.contains(image)){
                    //System.out.println(image);
                }
            }
        }
        for(String image:imageFiles){
            System.out.println(image);
            doc.insertString(doc.getLength(), image+"\n", attributeSet);
            if(!imageFiles.contains(image)){
                if(!blackListImageFiles.contains(image)){
                    //System.out.println(image);
                }
            }
        }
        /*images.removeAll(imageFiles);
        for(String s:images){
            System.out.println(s);
        }*/
        jFrame.setVisible(true);
    }

    public static void readImageNamesFromScripts(){

    }
    static ArrayList<String> animationTags=new ArrayList<>();

    public static ArrayList<String> whenReadLargeFileJava7_thenCorrect(String filePath)
            throws IOException {
        ArrayList<String> images=new ArrayList<>();

        Path path = Paths.get(filePath);
        boolean readingAnimation=false;
        BufferedReader reader = Files.newBufferedReader(path);
        String line;
        while ((line = reader.readLine())!=null){
            line=line.strip();
            if(line.startsWith("scene")||line.startsWith("show")||line.startsWith("hide")){
                String[] split=line.split(" ");
                if(split.length>1&&!split[1].equals("black")){
                    images.add(split[1].toLowerCase().replace(":",""));
                }
            }
            if (line.startsWith("image")){
                readingAnimation=true;
                String[] split=line.split(" ");
                if(split.length>1&&!split[1].equals("black")){
                    //System.out.println(split[1].toLowerCase().replace(":",""));
                    animationTags.add(split[1].toLowerCase().replace(":",""));
                }
            }
            if (line.startsWith("image side")){
                readingAnimation=true;
                String[] split=line.split("/");
                if(split.length>1&&!split[1].equals("black")){
                    images.add(split[1].split("\\.")[0].toLowerCase());
                    //System.out.println(split[1].toLowerCase().replace(":",""));
                    //animationTags.add(split[1].toLowerCase().replace(":",""));
                }
            }
            if (readingAnimation&&line.startsWith("'images")){
                String[]split=line.split(" ");
                split=split[0].split("/");
                split=split[split.length-1].split("\\.");
                //System.out.println(split[0]);
                images.add(split[0]);
            }
        }
        return images;
    }

    public static ArrayList<String> listFilesForFolder(final File folder) {
        ArrayList<String>list=new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                list.addAll(listFilesForFolder(fileEntry));
            } else {
                //System.out.println(fileEntry.getName());
                String[]strings=fileEntry.getName().strip().split("\\.");
                if(strings.length>1){
                    //System.out.println(strings[0].toLowerCase());
                    list.add(strings[0].toLowerCase());
                }

            }
        }
        return list;
    }

    public static ArrayList<String> getRenpyFiles(String path){
        ArrayList<String>fileNames=new ArrayList<>();
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()&&file.getName().endsWith(".rpy")) {
                //System.out.println(file.getName());
                fileNames.add(file.getName());
            }
        }
        return fileNames;
    }

}