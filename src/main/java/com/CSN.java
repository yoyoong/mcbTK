package com;

import Jama.Matrix;
import com.File.BedFile;
import com.File.CSNOutputFile;
import com.File.ChipFile;
import com.File.OutputFile;
import com.args.CSNArgs;
import com.bean.ChipInfo;
import com.bean.Region;
import com.common.Util;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.util.*;
import java.util.stream.IntStream;

public class CSN {
    public static final Logger log = LoggerFactory.getLogger(CSN.class);

    Util util = new Util();
    CSNArgs args = new CSNArgs();
    List<Region> regionList = new ArrayList<>();
    List<String> sampleIDList = new ArrayList<>();
    ChipFile chipFile;
    BedFile bedFile;
    CSNOutputFile csnOutputFile;
    OutputFile ndmOutputFile;

    public void csn(CSNArgs csnArgs) throws Exception {
        log.info("CSN start!");
        args = csnArgs;
        chipFile = new ChipFile(args.getInput());
        bedFile = new BedFile(args.getBed());
        csnOutputFile = new CSNOutputFile(args.getOutputDir(), args.getTag() + ".CSN.txt");

        // check the command
        boolean checkResult = checkArgs();
        if (!checkResult) {
            log.error("Checkargs fail, please check the command.");
            return;
        }

        // get region list from bed file
        regionList = bedFile.parseWholeFile();
        if (regionList.size() < 1) {
            log.info("The bed file is empty.");
            return;
        }

        // get the average MM matrix, x axis: cell, y axis: region
        sampleIDList = chipFile.getSampleIdList();
        double[][] mmMatrix = new double[regionList.size()][sampleIDList.size()];
        for (int i = 0; i < regionList.size(); i++) {
            Region region = regionList.get(i);

            // get the chip methalation data from inputfile
            List<ChipInfo> chipInfoList = chipFile.parseByRegionAndSampleID(region, new ArrayList<>());
            if (chipInfoList.size() < 1) {
                log.info("The data list in region: " + region.toHeadString() +  " is null, continue to next region...");
                continue;
            }

            for (int j = 0; j < sampleIDList.size(); j++) {
                double mm = 0.0;
                for (int k = 0; k < chipInfoList.size(); k++) {
                    mm += chipInfoList.get(k).getDataArray()[j];
                }
                mm = mm / chipInfoList.size();
                mmMatrix[i][j] = mm;
            }
        }

        Map<String, double[][]> upperlower = getUpperlower(mmMatrix);
        double[][] upper = upperlower.get("upper");
        double[][] lower = upperlower.get("lower");
        int[][] ndm = new int[regionList.size()][sampleIDList.size()];

        if (args.isNdmFlag()) {
            ndmOutputFile = new OutputFile(args.getOutputDir(), args.getTag() + ".NDM.txt");
            String ndmHead = "";
            for (int i = 0; i < sampleIDList.size(); i++) {
                ndmHead += "\t" + sampleIDList.get(i);
            }
            ndmOutputFile.writeHead(ndmHead + "\n");
        }

        for (int i = 0; i < sampleIDList.size(); i++) {
            String barcode = sampleIDList.get(i);
            Integer index = sampleIDList.indexOf(barcode);
            int[][] csn = getCSN(mmMatrix, upper, lower, index, csnOutputFile);

            if (args.isNdmFlag()) {
                int[] ndmColumn = Arrays.stream(csn).mapToInt(item -> IntStream.of(item).sum()).toArray();
                for (int j = 0; j < ndmColumn.length; j++) {
                    ndm[j][i] = ndmColumn[j];
                }
            }

            log.info("Get csn of barcode:" + barcode + " end.");
        }

        if (args.isNdmFlag()) {
            for (int i = 0; i < regionList.size(); i++) {
                String region = regionList.get(i).toHeadString();
                String ndmRow = region;
                for (int j = 0; j < sampleIDList.size(); j++) {
                    ndmRow += "\t" + ndm[i][j];
                }
                ndmOutputFile.writeLine(ndmRow.trim() + "\n");
            }
            ndmOutputFile.close();
        }

        csnOutputFile.close();
        log.info("CSN end!");
    }

    private boolean checkArgs() {
        if (args.getInput().equals("")) {
            log.error("input can not be null.");
            return false;
        }
        if (args.getBed().equals("")) {
            log.error("bed can not be null.");
            return false;
        }

        return true;
    }

    private Map<String, double[][]> getUpperlower(double[][] rawData) {
        Map<String, double[][]> upperlower = new HashMap<>();
        Integer n1 = rawData.length;
        Integer n2 = rawData[0].length;
        double[][] upper = new double[n1][n2];
        double[][] lower = new double[n1][n2];

        for (Integer i = 0; i < n1; i++) {
            double[] rawDataLine = rawData[i].clone();
            int[] s2 = util.sortArray(rawDataLine);
            double[] s1 = rawDataLine; // rawDataClone is sorted
            Integer h = Math.toIntExact(Math.round(args.getBoxSize() / 2 * n2));
            Integer k = 0;
            while (k < n2) {
                Integer s = 0;
                while (k + s + 1 < n2) {
                    if (s1[k + s + 1] == s1[k]) {
                        s++;
                    } else {
                        break;
                    }
                }

                for (Integer index = k; index < k + s + 1; index++) {
                    if (s >= h) {
                        upper[i][s2[index]] = rawData[i][s2[k]];
                        lower[i][s2[index]] = rawData[i][s2[k]];
                    } else {
                        upper[i][s2[index]] = rawData[i][s2[n2 - 1 > k + s + h ? k + s + h : n2 - 1]];
                        lower[i][s2[index]] = rawData[i][s2[k - h > 0 ? k - h : 0]];
                    }
                }
                k = k + s + 1;
            }
        }

        upperlower.put("upper", upper);
        upperlower.put("lower", lower);
        return upperlower;
    }

    private int[][] getCSN(double[][] rawData, double[][] upper, double[][] lower, Integer index,
                           CSNOutputFile csnOutputFile) throws Exception {
        Integer n1 = rawData.length;
        Integer n2 = rawData[0].length;

        double[][] B = new double[n1][n2];
        double[] a = new double[n1];
        for (Integer j = 0; j < n2; j++) {
            for (Integer k = 0; k < n1; k++) {
                if (rawData[k][j] <= upper[k][index] && rawData[k][j] >= lower[k][index] && rawData[k][j] > 0) {
                    B[k][j] = 1;
                    a[k] += 1;
                }
            }
        }

        double[][] A = new double[1][n1];
        double[][] n2_A = new double[1][n1];
        double[] n2_a = new double[n1];
        for (Integer j = 0; j < n1; j++) {
            n2_a[j] = n2 - a[j];
        }
        A[0] = a;
        n2_A[0] = n2_a;

        Matrix matrixB = new Matrix(B);
        Matrix matrixB_T = matrixB.transpose();
        Matrix matrixA = new Matrix(A);
        Matrix matrixA_T = matrixA.transpose();
        Matrix matrixN2_a = new Matrix(n2_A);
        Matrix matrixN2_a_T = matrixN2_a.transpose();

        Matrix tempMatrix1 = matrixB.times(matrixB_T).times(n2).minus(matrixA_T.times(matrixA));
        Matrix tempMatrix2 = matrixA_T.times(matrixA).arrayTimes(matrixN2_a_T.times(matrixN2_a)).times(Double.valueOf(1) / Double.valueOf(n2 - 1));
        double[][] temp2Array = tempMatrix2.getArray();
        for (Integer row = 0; row < tempMatrix2.getRowDimension(); row++) {
            for (Integer col = 0; col < tempMatrix2.getColumnDimension(); col++) {
                if (tempMatrix2.get(row, col) == 0) {
                    temp2Array[row][col] = Double.MIN_NORMAL;
                } else {
                    temp2Array[row][col] = Math.sqrt(tempMatrix2.get(row, col));
                }
            }
        }

        Matrix tempMatrix = tempMatrix1.arrayRightDivide(tempMatrix2);
        double[][] tempArray = tempMatrix.getArray();
        int[][] csn = new int[n1][n1];
        NormalDistribution normalDistribution = new NormalDistribution();
        double level = normalDistribution.inverseCumulativeProbability(1 - args.getAlpha());
        for (Integer row = 0; row < n1; row++) {
            tempArray[row][row] = 0;
            for (Integer col = row + 1; col < n1; col++) {
                if (tempArray[row][col] > level) {
                    csn[row][col] = 1;
                    csn[col][row] = 1;
                }
                csnOutputFile.setBarCode(sampleIDList.get(index));
                csnOutputFile.setRegion1(regionList.get(row).toHeadString());
                csnOutputFile.setRegion2(regionList.get(col).toHeadString());
                csnOutputFile.setStatistic(String.valueOf(tempArray[row][col]));
                csnOutputFile.writeLine("");
            }
        }

        return csn;
    }
}
