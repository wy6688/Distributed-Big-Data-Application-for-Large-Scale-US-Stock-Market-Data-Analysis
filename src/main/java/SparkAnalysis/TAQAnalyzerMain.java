package SparkAnalysis;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static Misc.Print.print;
import static Misc.SystemProperties.getMaxMemorySize;
import static Misc.Time.printElapsedTime;

//argument 1 : file or direcotory location of file to convert( /home/user/Downloads/file.zip)
//argument 2 : start time (please follow number of digits for each file spec for 9 digit 090000000)
//argument 3 : end time please follow number of digits for each file spec for 9 digit 093000000)
//argument 4 : file location of file containing stock symbols seperated by space, comma( /home/user/Downloads/stock).
//argument 5 : type file location containing column index seperated by space, comma(/home/user/Downloads/columns).
//argument 6 : cluster size(leave with -1 to as cluster size is set automatically based on available worker nodes)

public class TAQAnalyzerMain {
    private String fileOrDirectoryName;
    private File fileOrDirectory;
    public static JavaSparkContext sc;

    TAQAnalyzerMain(String[] args, JavaSparkContext sc) {
        fileOrDirectoryName = args[0];
        fileOrDirectory = new File(fileOrDirectoryName);
        this.sc = sc;
        int fileOrDirectoryNameLengh = fileOrDirectoryName.length();
        if (fileOrDirectoryName.substring(fileOrDirectoryNameLengh - 1, fileOrDirectoryNameLengh).equals("/"))
            convertDirectory(sc, args);
        else
            convertSingleFile(sc, args);

    }

    public void convertDirectory(JavaSparkContext sc, String[] args) {

        File directory = new File(fileOrDirectoryName);

        File[] listOfFiles = directory.listFiles();
        List<String> strTimeList = new ArrayList<>();
        int i;
        for (i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                long startTime = System.currentTimeMillis();
                System.out.println("Converting File : " + listOfFiles[i].getName());
                String inputFileName = fileOrDirectoryName + listOfFiles[i].getName();
                TAQAnalyzer TAQConverterObject = new TAQAnalyzer(sc, args, inputFileName);
                long endTime = System.currentTimeMillis();
                print("Conversion completed for file : " + inputFileName);
                String tempStr = printElapsedTime(startTime, endTime, "converting single file");
//                tempStr = "Total Size is : " + TAQConverterSingleObject.getSize() + "\nConvertion Time : " + tempStr;
//                strTimeList.add(tempStr);

            }
        }
        for (String p : strTimeList)
            System.out.println(p);
    }

    private void convertSingleFile(JavaSparkContext sc, String[] args) {
        String inputFileName = args[0];
        long startTime = System.currentTimeMillis();
        TAQAnalyzer TAQConverterObject = new TAQAnalyzer(sc, args, inputFileName);
        long endTime = System.currentTimeMillis();
        print("Conversion completed for file : " + inputFileName);
        String tempStr = printElapsedTime(startTime, endTime, "converting single file");
//        tempStr = "Total Size is : " + TAQConverterObject.getSize() + "\nConvertsion Time : " + tempStr;
//        print(tempStr);
    }

    public static void main(String[] args) throws IOException {
        int memMin = 1;
        int memMax = getMaxMemorySize();
        System.out.println("\nPlease specify Memory Size for spark in GB (-1 to ignore):\n(Please consider leaving enough memory space for operating system to function properly which is usually 4-6GB)");
        Scanner scan = new Scanner(System.in);
        int memSizeNum = scan.nextInt();
        String memorySize;
        if (memSizeNum != -1 && memSizeNum <= memMax)
            memorySize = String.valueOf(memSizeNum) + "g";
        else
            memorySize = "1g";
        SparkConf conf = new SparkConf().setAppName("Financial Data Processor").setMaster("local[2]").set("spark.executor.memory", memorySize);
        JavaSparkContext sc = new JavaSparkContext(conf);
        long startTime = System.currentTimeMillis();
        TAQAnalyzerMain TAQConverterMainObject = new TAQAnalyzerMain(args, sc);
        long endTime = System.currentTimeMillis();
        printElapsedTime(startTime, endTime, "complete conversion");
        System.gc();
    }

}
