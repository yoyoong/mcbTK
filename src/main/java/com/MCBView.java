package com;

import com.File.ChipFile;
import com.args.MCBViewArgs;
import com.bean.ChipInfo;
import com.bean.RInfo;
import com.bean.Region;
import com.common.Util;
import com.itextpdf.awt.DefaultFontMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.rewrite.CustomXYBlockRenderer;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.Range;
import org.jfree.data.xy.DefaultXYZDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MCBView {
    public static final Logger log = LoggerFactory.getLogger(MCBView.class);

    MCBViewArgs args = new MCBViewArgs();
    Util util = new Util();

    public void mcbView(MCBViewArgs mcbViewArgs) throws Exception {
        log.info("command.mcbView start!");
        args = mcbViewArgs;

        // check the command
        boolean checkResult = checkArgs();
        if (!checkResult) {
            log.error("Checkargs fail, please check the command.");
            return;
        }

        // init the object
        ChipFile chipFile = new ChipFile(args.getInput());
        Region region = new Region(args.getRegion());

        // get the chip methalation data from inputfile
        List<ChipInfo> chipInfoList = chipFile.parseByRegionAndSampleID(region, args.getSampleID());
        if (chipInfoList.size() < 1) {
            log.info("The data list in region: " + region.toHeadString() +  " is null, return...");
            return;
        }

        // get the cpg postion in region
        List<Integer> cpgPosListInRegion = util.getCpgPosListInRegion(chipInfoList, region);

        // generate the heat plot
        Integer rowNum = chipInfoList.get(0).getDataArray().length;
        Integer colNum = chipInfoList.size();
        Double[][] dataMatrix = new Double[rowNum][colNum];
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < colNum; j++) {
                dataMatrix[i][j] = chipInfoList.get(j).getDataArray()[i];
            }
        }
        XYPlot heatPlot = generateHeatPlot(dataMatrix);

        // generate the rvalue plot
        Double[] rvalueMatrix = new Double[colNum * (colNum - 1) / 2];
        Integer index = 0;
        for (int i = 0; i < chipInfoList.size(); i++) {
            for (int j = i + 1; j < chipInfoList.size(); j++) {
                Double[] dataArray1 = chipInfoList.get(i).getDataArray();
                Double[] dataArray2 = chipInfoList.get(j).getDataArray();
                // calculate the r value
                RInfo rInfo = util.calculateRvalue(dataArray1, dataArray2, args.getNSample());
                rvalueMatrix[index] = rInfo.getRvalue();
                index++;
            }
        }
        XYPlot RPlot = generateRPlot(rvalueMatrix, rowNum, colNum);

        // paint the plot and output to file
        Integer width = colNum * 100;
        List<Plot> plotList = new ArrayList<>();
        plotList.add(heatPlot);
        plotList.add(RPlot);
        List<Integer> heightList = new ArrayList<>();
        heightList.add(width * 2 / 3);
        heightList.add(width / 3);
        String outputPath = region.toFileString() + ".mcbView." + args.getOutFormat();
        if (args.getOutFormat().equals("pdf")) {
            saveAsPdf(cpgPosListInRegion, region, plotList, outputPath, width, heightList);
        } else if (args.getOutFormat().equals("png")) {
            saveAsPng(cpgPosListInRegion, region, plotList, outputPath, width, heightList);
        }


        log.info("command.mcbView end!");
    }

    private boolean checkArgs() {
        if (args.getInput().equals("")) {
            log.error("input can not be null.");
            return false;
        }
        if (args.getRegion().equals("")) {
            log.error("region can not be null.");
            return false;
        }
        return true;
    }

    private XYPlot generateHeatPlot(Double[][] dataMatrix) {

        // 创建数据集
        DefaultXYZDataset dataset = new DefaultXYZDataset();
        double x[] = new double[dataMatrix.length * dataMatrix[0].length];
        double y[] = new double[dataMatrix.length * dataMatrix[0].length];
        double z[] = new double[dataMatrix.length * dataMatrix[0].length];
        for (int i = 0; i < dataMatrix.length; i++) {
            for (int j = 0; j < dataMatrix[0].length; j++) {
                x[dataMatrix[0].length * i + j] = j;
                y[dataMatrix[0].length * i + j] = i;
                if (dataMatrix[i][j].isNaN()) {
                    dataMatrix[i][j] = 0.0;
                }
                z[dataMatrix[0].length * i + j] = dataMatrix[i][j];
            }
        }
        double pos[][] = {x, y, z};
        dataset.addSeries( "Series" , pos);

        // xy轴
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLowerMargin(0.025);
        xAxis.setUpperMargin(0.025);
        xAxis.setVisible(false);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickUnit(new NumberTickUnit(dataMatrix.length * 2));
        yAxis.setRange(new Range(1, dataMatrix.length));
        yAxis.setVisible(false); // 不显示y轴
        yAxis.setLabel("sample");
        yAxis.setLabelFont(new Font("", Font.PLAIN, 20));

        // 颜色定义
        LookupPaintScale paintScale = new LookupPaintScale(0, 1, Color.black);
        for (Double j = 0.0; j < 255.0; j++) {
            paintScale.add((255 - j) / 255, new Color((int) (255.0 - j / 3 * 2), (int) (255.0 - j / 3 * 2), j.intValue()));
        }

        // 绘制色块图
        XYPlot xyPlot = new XYPlot(dataset, xAxis, yAxis, new XYBlockRenderer());
        XYBlockRenderer xyBlockRenderer = new XYBlockRenderer();
        xyBlockRenderer.setPaintScale(paintScale);
        xyBlockRenderer.setBlockHeight(1.0f);
        xyBlockRenderer.setBlockWidth(1.0f);
        xyPlot.setRenderer(xyBlockRenderer);
        xyPlot.setDomainGridlinesVisible(false); // 不显示X轴网格线
        xyPlot.setRangeGridlinesVisible(false); // 不显示Y轴网格线
        xyPlot.setOutlineVisible(false);

        return xyPlot;
    }

    private XYPlot generateRPlot(Double[] rvalueMatrix, Integer rowNum, Integer colNum) throws IOException {
        // 创建数据集
        DefaultXYZDataset dataset = new DefaultXYZDataset();
        double x[] = new double[rvalueMatrix.length];
        double y[] = new double[rvalueMatrix.length];
        double z[] = new double[rvalueMatrix.length];
        int next = 0;
        for (int i = 0; i < colNum; i++) {
            for (int j = i + 1; j < colNum; j++) {
                x[next + j - i - 1] = i;
                y[next + j - i - 1] = j - i - 1;
                if (rvalueMatrix[next + j - i- 1].isNaN()) {
                    rvalueMatrix[next + j - i- 1] = 0.0;
                }
                z[next + j - i - 1] = rvalueMatrix[next + j - i- 1];
            }
            next += colNum - i - 1;
        }
        double pos[][] = {x , y , z};
        dataset.addSeries( "Series" , pos);

        // xy轴
        NumberAxis xAxis = new NumberAxis();
        xAxis.setUpperMargin(0.05);
        xAxis.setLowerMargin(0.05);
        xAxis.setVisible(false);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickUnit(new NumberTickUnit(rowNum * 2));
        yAxis.setRange(new Range(1, rowNum));
        yAxis.setVisible(false); // 不显示y轴
        yAxis.setLabel("rvalue");
        yAxis.setLabelFont(new Font("", Font.PLAIN, 20));

        // 颜色定义
        LookupPaintScale paintScale = new LookupPaintScale(-1, 1, Color.black);
        for (Double j = 0.0; j < 255.0; j++) {
            paintScale.add((j - 255) / 255, new Color(j.intValue(), j.intValue(), 255));
        }
        for (Double j = 255.0; j < 510.0; j++) {
            paintScale.add((j - 255) / 255, new Color(255, 510 - j.intValue(), 510 - j.intValue()));
        }

        XYPlot xyPlot = new XYPlot(dataset, xAxis, yAxis, new CustomXYBlockRenderer());
        CustomXYBlockRenderer xyBlockRenderer = new CustomXYBlockRenderer();
        xyBlockRenderer.setPaintScale(paintScale);
        xyBlockRenderer.setBlockHeight(1.0f);
        xyBlockRenderer.setBlockWidth(1.0f);
        xyBlockRenderer.setBlockNum(colNum);
        xyPlot.setRenderer(xyBlockRenderer);
        xyPlot.setDomainGridlinesVisible(false); // 不显示X轴网格线
        xyPlot.setRangeGridlinesVisible(false); // 不显示Y轴网格线
        xyPlot.setOutlineVisible(false);

        return  xyPlot;
    }

    // 保存为文件
    public void saveAsPdf(List<Integer> cpgPosListInRegion, Region region, List<Plot> plotList, String outputPath, 
                          Integer width, List<Integer> heightList) throws FileNotFoundException, DocumentException {
        width = width > 14400 ? 14400 : width;
        Integer sumHeight = 0;
        for (int i = 0; i < heightList.size(); i++) {
            sumHeight += heightList.get(i);
        }
        if (sumHeight > 14400) {
            width = width / sumHeight * 14400;
            sumHeight = 14400;
        }

        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputPath));
        // 设置文档大小
        com.itextpdf.text.Rectangle pagesize = new com.itextpdf.text.Rectangle(width, sumHeight);
        // 创建一个文档
        Document document = new Document(pagesize, 50, 50, 50, 50);
        // 创建writer，通过writer将文档写入磁盘
        PdfWriter pdfWriter = PdfWriter.getInstance(document, outputStream);
        // 打开文档，只有打开后才能往里面加东西
        document.open();
        // 加入统计图
        PdfContentByte pdfContentByte = pdfWriter.getDirectContent();
        PdfTemplate pdfTemplate = pdfContentByte.createTemplate(width, sumHeight);
        Graphics2D graphics2D = pdfTemplate.createGraphics(width, sumHeight, new DefaultFontMapper());

        Integer nextHeight = 0;
        for (int i = 0; i < plotList.size(); i++) {
            JFreeChart jFreeChart = new JFreeChart("", null, plotList.get(i), false);
            if (i == 0) {
                // 图标头
                jFreeChart = new JFreeChart(region.toHeadString(), new Font("", Font.PLAIN, sumHeight / 50), plotList.get(i), false);

                // 颜色定义
                LookupPaintScale paintScale = new LookupPaintScale(0, 1, Color.black);
                for (Double j = 0.0; j < 255.0; j++) {
                    paintScale.add((255 - j) / 255, new Color((int) (255.0 - j / 3 * 2), (int) (255.0 - j / 3 * 2), j.intValue()));
                }

                // 颜色示意图
                PaintScaleLegend paintScaleLegend = new PaintScaleLegend(paintScale, new NumberAxis());
                paintScaleLegend.setStripWidth(width * 0.01);
                paintScaleLegend.setPosition(RectangleEdge.RIGHT);
                paintScaleLegend.setAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
                paintScaleLegend.setMargin(heightList.get(i) * 1 / 2, 0, heightList.get(i) * 1 / 4, 0);
                jFreeChart.addSubtitle(paintScaleLegend);
            } else if (i == plotList.size() - 1) {
                // 颜色定义
                LookupPaintScale paintScale = new LookupPaintScale(-1, 1, Color.black);
                for (Double j = 0.0; j < 255.0; j++) {
                    paintScale.add((j - 255) / 255, new Color(j.intValue(), j.intValue(), 255));
                }
                for (Double j = 255.0; j < 510.0; j++) {
                    paintScale.add((j - 255) / 255, new Color(255, 510 - j.intValue(), 510 - j.intValue()));
                }

                // 颜色示意图
                PaintScaleLegend paintScaleLegend = new PaintScaleLegend(paintScale, new NumberAxis());
                paintScaleLegend.setStripWidth(width * 0.01);
                paintScaleLegend.setPosition(RectangleEdge.RIGHT);
                paintScaleLegend.setAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
                paintScaleLegend.setMargin(heightList.get(i) * 1 / 3, 0, heightList.get(i) * 1 / 3, 0);
                jFreeChart.addSubtitle(paintScaleLegend);
            }
            jFreeChart.setBackgroundPaint(Color.WHITE);

            Rectangle2D rectangle2D0 = new Rectangle2D.Double(0, nextHeight, width, heightList.get(i));
            jFreeChart.draw(graphics2D, rectangle2D0);
            pdfContentByte.addTemplate(pdfTemplate, 0, 0);
            nextHeight += heightList.get(i);
        }
        graphics2D.dispose();

        // 关闭文档，才能输出
        document.close();
        pdfWriter.close();
    }

    public void saveAsPng(List<Integer> cpgPosListInRegion, Region region, List<Plot> plotList, String outputPath,
                          Integer width, List<Integer> heightList) throws IOException {
        File outFile = new File(outputPath);
        Integer sumHeight = 0;
        for (int i = 0; i < heightList.size(); i++) {
            sumHeight += heightList.get(i);
        }
        BufferedImage bufferedImage = new BufferedImage(width, sumHeight, BufferedImage.TYPE_INT_RGB);

        Integer nextHeight = 0;
        for (int i = 0; i < plotList.size(); i++) {
            Graphics2D graphics2D = bufferedImage.createGraphics();

            JFreeChart jFreeChart = new JFreeChart("", null, plotList.get(i), false);
            if (i == 0) {
                // 图标头
                jFreeChart = new JFreeChart(region.toHeadString(), new Font("", Font.PLAIN, sumHeight / 50), plotList.get(i), false);

                // 颜色定义
                LookupPaintScale paintScale = new LookupPaintScale(0, 1, Color.black);
                for (Double j = 0.0; j < 255.0; j++) {
                    paintScale.add((255 - j) / 255, new Color((int) (255.0 - j / 3 * 2), (int) (255.0 - j / 3 * 2), j.intValue()));
                }

                // 颜色示意图
                PaintScaleLegend paintScaleLegend = new PaintScaleLegend(paintScale, new NumberAxis());
                paintScaleLegend.setStripWidth(width * 0.01);
                paintScaleLegend.setPosition(RectangleEdge.RIGHT);
                paintScaleLegend.setAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
                paintScaleLegend.setMargin(heightList.get(i) * 1 / 2, 0, heightList.get(i) * 1 / 4, 0);
                jFreeChart.addSubtitle(paintScaleLegend);
            } else if (i == plotList.size() - 1) {
                // 颜色定义
                LookupPaintScale paintScale = new LookupPaintScale(-1, 1, Color.black);
                for (Double j = 0.0; j < 255.0; j++) {
                    paintScale.add((j - 255) / 255, new Color(j.intValue(), j.intValue(), 255));
                }
                for (Double j = 255.0; j < 510.0; j++) {
                    paintScale.add((j - 255) / 255, new Color(255, 510 - j.intValue(), 510 - j.intValue()));
                }

                // 颜色示意图
                PaintScaleLegend paintScaleLegend = new PaintScaleLegend(paintScale, new NumberAxis());
                paintScaleLegend.setStripWidth(width * 0.01);
                paintScaleLegend.setPosition(RectangleEdge.RIGHT);
                paintScaleLegend.setAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
                paintScaleLegend.setMargin(heightList.get(i) * 1 / 3, 0, heightList.get(i) * 1 / 3, 0);
                jFreeChart.addSubtitle(paintScaleLegend);
            }
            jFreeChart.setBackgroundPaint(Color.WHITE);
            Rectangle2D rectangle2D0 = new Rectangle2D.Double(0, nextHeight, width, heightList.get(i));
            jFreeChart.draw(graphics2D, rectangle2D0);
            nextHeight += heightList.get(i);
            graphics2D.dispose();
        }

        RenderedImage rendImage = bufferedImage;
        ImageIO.write(rendImage, "png", outFile);
    }

}
