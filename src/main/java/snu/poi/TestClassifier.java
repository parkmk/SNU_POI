package snu.poi;

import edu.stanford.nlp.ie.crf.CRFClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class TestClassifier {

    public static void main (String[] ar) throws Exception {

        //redirect standard output
        File file = new File("compare.tsv");
        FileOutputStream fos = new FileOutputStream(file);
        PrintStream ps = new PrintStream(fos, true, "UTF-8");
        System.setOut(ps);

        //run cl
        CRFClassifier.main(new String[] {"-loadClassifier", "kor-only-model.ser.gz", "-testFile", "test.tsv"});
    }

}
