package org.campagnelab.dl.genotype.performance;

import org.campagnelab.dl.genotype.predictions.GenotypePrediction;

/**
 * Estimate genotype statistics.
 * Created by rct66 on 12/19/16.
 */
public class StatsAccumulator {


    int numCorrect;
    int numProcessed;
    int numTruePositive;
    int numTrueNegative;
    int numFalsePositive;
    int numFalseNegative;
    int numIndelsCorrect;
    int numSnpsCorrect;
    int numIndelsProcessed;
    int numSnpsProcessed;
    int numIndelsTruePositive;
    int numIndelsFalsePositive;
    int numIndelsFalseNegative;
    int numIndelsTrueNegative;
    int numSnpsTruePositive;
    int numSnpsFalsePositive;
    int numSnpsFalseNegative;
    int numVariants;
    int numIndels;
    int concordantVariants;
    int numVariantsExpected;
    int numTrueOrPredictedVariants;

    int numSnpsTrueNegative;
    int hetCount = 0;
    int homCount = 0;
    int numTrueIndels = 0;

    public void initializeStats() {
        numCorrect = 0;
        numProcessed = 0;
        numTruePositive = 0;
        numTrueNegative = 0;
        numFalsePositive = 0;
        numFalseNegative = 0;
        numVariants = 0;
        numIndels = 0;
        concordantVariants = 0;
        numTrueOrPredictedVariants = 0;
        numIndelsCorrect = 0;
        numSnpsCorrect = 0;
        numIndelsProcessed = 0;
        numSnpsProcessed = 0;
        numSnpsTrueNegative = 0;
        numIndelsTruePositive = 0;
        numIndelsFalsePositive = 0;
        numIndelsFalseNegative = 0;
        numIndelsTrueNegative = 0;
        numSnpsTruePositive = 0;
        numSnpsFalsePositive = 0;
        numSnpsFalseNegative = 0;
        hetCount = 0;
        homCount = 0;
    }

    public void observe(GenotypePrediction fullPred) {
        observe(fullPred, fullPred.isVariant(), fullPred.isVariant());
    }

    public void observe(GenotypePrediction fullPred, boolean isTrueVariant, boolean isPredictedVariant) {
        fullPred.rebuild();
        numProcessed++;
        if (isPredictedVariant || isTrueVariant) {
            numTrueOrPredictedVariants += 1;
            concordantVariants += fullPred.isCorrect() ? 1 : 0;
        }
        if (isPredictedVariant) {
            final int size = fullPred.predictedAlleles().size();
            hetCount += (size == 2 ? 1 : 0); //AB
            homCount += (size == 1 ? 1 : 0); //BB
        }
        // estimate FP,TP,FN,TN for SNPs:
        if (fullPred.isPredictedSnp() || fullPred.isSnp()) {
            if (fullPred.isCorrect()) {
                numCorrect++;
                if (isTrueVariant) {
                    numSnpsTruePositive++;
                    numTruePositive++;
                } else {
                    numSnpsTrueNegative++;
                    numTrueNegative++;
                }
            } else {
                if (isTrueVariant) {
                    numSnpsFalseNegative++;
                    numFalseNegative++;
                } else {
                    numSnpsFalsePositive++;
                    numFalsePositive++;
                }
            }
        }
        // estimate FP,TP,FN,TN for indels:
        final int foundIndel = isTrueVariant && (fullPred.isIndel()) ? 1 : 0;
        numTrueIndels += foundIndel;

        if (fullPred.isPredictedIndel() || fullPred.isIndel()) {
            if (fullPred.isCorrect()) {
                numCorrect++;
                if (isTrueVariant) {
                    numIndelsTruePositive++;
                    numTruePositive++;
                } else {
                    numIndelsTrueNegative++;
                    numTrueNegative++;
                }
            } else {
                if (isTrueVariant) {
                    numIndelsFalseNegative++;
                    numFalseNegative++;
                } else {
                    numIndelsFalsePositive++;
                    numFalsePositive++;
                }
            }
        }

        if (fullPred.isVariant()) {
            if (fullPred.isIndel()) {
                numIndelsProcessed++;
                if (fullPred.isCorrect()) {
                    numIndelsCorrect++;
                }
            } else {
                numSnpsProcessed++;
                if (fullPred.isCorrect()) {
                    numSnpsCorrect++;
                }
            }
        }

        numVariants += isTrueVariant ? 1 : 0;
        //  assert numVariants == numSnpsTruePositive + numSnpsFalseNegative + numIndelsTruePositive + numIndelsFalseNegative;
        assert numTruePositive == numSnpsTruePositive + numIndelsTruePositive;
        assert numFalsePositive == numSnpsFalsePositive + numIndelsFalsePositive;
        assert numFalseNegative == numSnpsFalseNegative + numIndelsFalseNegative;
        assert numTrueNegative == numSnpsTrueNegative + numIndelsTrueNegative;

        numIndels += fullPred.isIndel() ? 1 : 0;
    }

    public double[] createOutputStatistics() {
        double accuracy = numCorrect / (double) numProcessed;
        double indelAccuracy = numIndelsCorrect / (double) numIndelsProcessed;
        double snpAccuracy = numSnpsCorrect / (double) numSnpsProcessed;
        double genotypeConcordance = concordantVariants / (double) numTrueOrPredictedVariants;
        final int variantsExpected = Math.max(numTruePositive + numFalseNegative, this.numVariantsExpected);
        double recall = numTruePositive / ((double) numTruePositive + numFalseNegative);
        double precision = numTruePositive / ((double) (numTruePositive + numFalsePositive));
        // important fix. Remi, see https://en.wikipedia.org/wiki/F1_score
        double F1 = 2 * precision * recall / (precision + recall);
        double indelRecall = numIndelsTruePositive / ((double) numTrueIndels);
        double indelPrecision = numIndelsTruePositive / ((double) numIndelsTruePositive + numIndelsFalsePositive);
        double indelF1 = 2 * indelPrecision * indelRecall / (indelPrecision + indelRecall);
        double snpRecall = numSnpsTruePositive / ((double) numSnpsTruePositive + numSnpsFalseNegative);
        double snpPrecision = numSnpsTruePositive / ((double) numSnpsTruePositive + numSnpsFalsePositive);
        double snpF1 = 2 * snpPrecision * snpRecall / (snpPrecision + snpRecall);
        double het_hom_ratio = (hetCount)/*AB*/ / (homCount == 0 ? 1 : homCount) /*BB*/;
        return new double[]{accuracy, recall, precision, F1, numVariants, genotypeConcordance, indelAccuracy,
                indelRecall, indelPrecision, indelF1, snpAccuracy, snpRecall, snpPrecision, snpF1, numIndels, het_hom_ratio, numTruePositive, numTrueNegative};
    }

    public double[] createOutputStatistics(String... metrics) {
        double[] estimates = createOutputStatistics();
        double[] values = new double[metrics.length];
        int i = 0;
        for (String metricName : metrics) {
            int j = -1;
            switch (metricName) {
                case "Accuracy":
                    j = 0;
                    break;
                case "Recall":
                    j = 1;
                    break;
                case "Precision":
                    j = 2;
                    break;
                case "F1":
                    j = 3;
                    break;
                case "NumVariants":
                    j = 4;
                    break;
                case "Concordance":
                    j = 5;
                    break;
                case "Accuracy_Indels":
                    j = 6;
                    break;
                case "Recall_Indels":
                    j = 7;
                    break;
                case "Precision_Indels":
                    j = 8;
                    break;
                case "F1_Indels":
                    j = 9;
                    break;
                case "Accuracy_SNPs":
                    j = 10;
                    break;
                case "Recall_SNPs":
                    j = 11;
                    break;
                case "Precision_SNPs":
                    j = 12;
                    break;
                case "F1_SNPs":
                    j = 13;
                    break;
                case "numIndels":
                    j = 14;
                    break;
                case "Het_Hom_Ratio":
                    j = 15;
                    break;
                case "TP":
                    j = 16;
                    break;
                case "TN":
                    j = 17;
                    break;

                default:
                    throw new RuntimeException("performance metric not recognized: " + metricName);
            }
            values[i++] = estimates[j];
        }
        return values;
    }

    public String[] createOutputHeader() {
        return new String[]{"Accuracy", "Recall", "Precision", "F1", "NumVariants",
                "Accuracy_Indels", "Recall_Indels", "Precision_Indels", "F1_Indels",
                "Accuracy_SNPs", "Recall_SNPs", "Precision_SNPs", "F1_SNPs",
                "numIndels", "Het_Hom_Ratio", "TP", "TN"
        };
    }

    public static final int F1_INDEX = 3;

    public void reportStatistics(String prefix) {
        double[] statsArray = createOutputStatistics();
        System.out.printf("Number of variants expected=%d%n", numVariantsExpected);
        System.out.println("Statistics estimated for " + prefix);
        System.out.println("Accuracy =" + statsArray[0]);
        System.out.println("Recall =" + statsArray[1]);
        System.out.println("Precision =" + statsArray[2]);
        System.out.println("F1 =" + statsArray[3]);
        System.out.println("numVariants =" + statsArray[4]);
        System.out.println("genotype concordance =" + statsArray[5]);
        System.out.println("Indel Accuracy =" + statsArray[6]);
        System.out.println("Indel Recall =" + statsArray[7]);
        System.out.println("Indel Precision =" + statsArray[8]);
        System.out.println("Indel F1 =" + statsArray[9]);
        System.out.printf("Indel TP %d FN %d FP %d TN %d %n", numIndelsTruePositive, numIndelsFalseNegative, numIndelsFalsePositive, numIndelsTrueNegative);
        System.out.printf("numIndels=%d%n", numIndels);
        System.out.println("numIndels =" + statsArray[14]);
        System.out.println("SNP Accuracy =" + statsArray[10]);
        System.out.println("SNP Recall =" + statsArray[11]);
        System.out.println("SNP precision =" + statsArray[12]);
        System.out.println("SNP F1=" + statsArray[13]);
    }

    public void setNumVariantsExpected(int numVariantsExpected) {
        this.numVariantsExpected = numVariantsExpected;
    }
}
