package snu.poi;

import java.io.*;

public class EvaluateClassifier {

    public static void main (String[] ar) throws IOException {

        int TP = 0, TN=0, FP=0, FN = 0;

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("compare.tsv"), "UTF8"));
        while (true) {
            String line = br.readLine();
            if (line==null || line.trim().length() == 0) {
                break;
            }
            String[] tokens = line.split("\t");
            char answer = tokens[1].charAt(0);
            char predict = tokens[2].charAt(0);

            if (answer == 'O' && predict == 'O') {
                TN += 1;
            }
            else if (answer == 'O' && predict != 'O') {
                FP += 1;
            }
            else if (answer != 'O' && predict == 'O') {
                FN += 1;
            }
            else if (answer != 'O' && predict != 'O'){
                TP += 1;
            }
        }
        br.close();;

        double precision = ( TP + 0.0 ) / ( TP + FP );
        double recall = ( TP + 0.0 ) / ( TP + FN );
        double f1score = 2 * precision * recall / (precision + recall);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("evaluate.txt"), "UTF8"));
        bw.write("TP\t"+TP+"\n");
        bw.write("TN\t"+TN+"\n");
        bw.write("FP\t"+FP+"\n");
        bw.write("FN\t"+FN+"\n");
        bw.write("PRECISION\t" + precision+"\n");
        bw.write("RECALL\t"+ recall+"\n");
        bw.write("F1-SCORE\t" + f1score+"\n");

        bw.close();
    }


}
