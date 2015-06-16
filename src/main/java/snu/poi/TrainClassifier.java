package snu.poi;

import edu.stanford.nlp.ie.crf.CRFClassifier;

public class TrainClassifier {

    public static void main (String[] ar) throws Exception {
        CRFClassifier.main(new String[] {"-prop", "classifier.prop"});
    }
}
