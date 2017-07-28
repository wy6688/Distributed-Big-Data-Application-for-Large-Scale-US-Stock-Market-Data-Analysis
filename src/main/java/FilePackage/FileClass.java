package FilePackage;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static Misc.Print.print;

public class FileClass {

    public static File[] readDirectory(String directory) {

        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File " + listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }
        return listOfFiles;
    }

    public static void deleteFileorDir(String fileOrDirStr) {
        File fileOrDir = new File(fileOrDirStr);
        if (fileOrDir.exists()) {
            System.out.println("Do you want to delete file : " + fileOrDirStr);
            Scanner scan = new Scanner(System.in);
            String s = scan.next();
            if (s.equals("y")) {
                if (fileOrDirStr.substring(fileOrDirStr.length() - 1, fileOrDirStr.length()).equals("/")) {
                    File[] files = fileOrDir.listFiles();
                    if (files != null) {
                        for (int i = 0; i < files.length; i++) {
                            if (files[i].isDirectory()) {
                                deleteFileorDir(files[i].getAbsolutePath());
                                System.out.println("Deleted Directory : " + files[i].getAbsolutePath());

                            } else {
                                files[i].delete();
                                System.out.println("Deleted File : " + files[i].getAbsolutePath());

                            }
                        }
                    }
                    fileOrDir.delete();
                } else {
                    fileOrDir.delete();
                    System.out.println("Deleted Single File : " + fileOrDirStr);
                }
            }
        }
    }


    public static String getParentDir(String inputFileName) {
        File file = new File(inputFileName);
        File parentDir = file.getParentFile(); // to get the parent dir
        String parentDirName = file.getParent();
        return parentDirName;
    }
    public static String getParentDir(String inputFileName, int level) {
        File file = new File(inputFileName);
        File parentDir=null;
        for(int i = 0; i<level; i++){
            parentDir = file.getParentFile(); // to get the parent dir
            file = new File(String.valueOf(parentDir));
        }
        String parentDirName = file.getParent();
        return parentDirName;
    }
    public static String mkdir(String dirName) {
        File dir = new File(dirName);
        boolean successful = dir.mkdir();
        if (successful) {
            System.out.println("directory was created successfully");
        } else {
            System.out.println("failed trying to create the directory");
        }
        return dir.getAbsolutePath();
    }
    public static String mkdir(String inputFileName, String dirName) {
        String dirLoc="";
        if (isFile(inputFileName)) {
            dirLoc = getParentDir(inputFileName, 2);
        }
        if (isDirectory(inputFileName)) {
            dirLoc = getParentDir(inputFileName);
        }
        File dir = new File(dirLoc + "/"+dirName);
        boolean successful = dir.mkdir();
        if (successful) {
            System.out.println("directory was created successfully");
        } else {
            System.out.println("failed trying to create the directory");
        }
        return dir.getAbsolutePath();
    }
    public static String mkdir(String inputFileName, String dirName, int level) {
        String dirLoc = getParentDir(inputFileName, level);
        File dir = new File(dirLoc + "/"+dirName);
        boolean successful = dir.mkdir();
        if (successful) {
            System.out.println("directory was created successfully");
        } else {
            System.out.println("failed trying to create the directory");
        }
        return dir.getAbsolutePath();
    }


    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static boolean isFile(String str){
        File file = new File(str);
        boolean isFile =      file.isFile();
        if (isFile)
            return true;
        else return false;
    }
    public static boolean isDirectory(String str){
        File file = new File(str);
        boolean isDirectory = file.isDirectory();
        if (isDirectory)
            return true;
        else return false;
    }
    public static boolean FileorDirExists(String str){
        File file = new File(str);
        boolean exists =      file.exists();
        if (exists)
            return true;
        else return false;
    }

    public static List<String> wordCollect(JavaSparkContext sc, String inputFileName) {
        JavaRDD<String> textFile = sc.textFile(inputFileName);
        List<String[]> wordListArr = textFile.map(e -> e.split("[\\s,;.]+")).collect();
        List<String> wordList = new ArrayList<>();

        for (String[] word : wordListArr) {
            for (int i = 0; i < word.length; i++) {
                wordList.add(word[i]);
            }
        }
        int k = 0;
        for (String word : wordList) {
            k++;
        }
        print("Number of Tickers selected : " + k);
        print("Finding the following Tickers");
        for (String word : wordList) {
            System.out.print(word + " ");

        }
        print("\n");
        return wordList;
    }

    public static List<Integer> columnSelect(JavaSparkContext sc, String inputFileName) {
        JavaRDD<String> textFile = sc.textFile(inputFileName);
        List<String[]> columnListArr = textFile.map(e -> e.split("[\\s,;.]+")).collect();
        List<Integer> columnList = new ArrayList<>();

        for (String[] col : columnListArr) {
            for (int i = 0; i < col.length; i++) {
                try {
                    columnList.add(Integer.parseInt(col[i]));
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
        int k = 0;
        for (int column : columnList) {
            k++;
        }
        print("Number of columns selected : " + k);
        print("Finding the following columns");
        for (int column : columnList) {
            System.out.print(column + " ");

        }
        print("\n");
        return columnList;
    }
    public static List<String> wordCollect(String wordListStr) {
        List<String> wordList = new ArrayList<>(Arrays.asList(wordListStr.split("[\\s,;.]+")));
        print("Number of Tickers selected : " + wordList.size());
        print("Finding the following Tickers");
        for (String word : wordList) {
            System.out.print(word + " ");

        }
        print("\n");
        return wordList;
    }
    public static List<Integer> columnSelect(String columns) {
        List<String> columnListStr = new ArrayList<>(Arrays.asList(columns.split("[\\s,;.]+")));
        List<Integer> columnList = new ArrayList<>();

        for (String column : columnListStr) {
            columnList.add(Integer.valueOf(column));
        }
        print("Number of columns selected : " + columnList.size());
        print("Finding the following columns");
        for (int column : columnList) {
            System.out.print(column + " ");

        }
        print("\n");
        return columnList;
    }
    public static void main(String[] args){
        boolean a =isDirectory("/home/anjana/Downloads/DATA/bulk/");
        print(String.valueOf(a));
    }

}


