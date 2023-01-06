package com;

import com.args.MCBDiscoveryArgs;
import com.args.MCBViewArgs;
import com.args.RArgs;
import com.args.StatArgs;
import com.common.Annotation;
import org.apache.commons.cli.*;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;

public class Main {
    static Stat stat = new Stat();
    static R r = new R();
    static MCBDiscovery mcbDiscovery = new MCBDiscovery();
    static MCBView mcbView = new MCBView();

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");

        if (args != null && args[0] != null && !"".equals(args[0])) {
            if (args[0].equals("stat")) {
                StatArgs statyArgs = parseStat(args);
                if (statyArgs != null) {
                    stat.stat(statyArgs);
                }
            } else if (args[0].equals("R")) {
                RArgs rArgs = parseR(args);
                if (rArgs != null) {
                    r.r(rArgs);
                }
            } else if (args[0].equals("MCBDiscovery")) {
                MCBDiscoveryArgs mcbDiscoveryArgs = parseMCBDiscovery(args);
                if (mcbDiscoveryArgs != null) {
                    mcbDiscovery.mcbDiscovery(mcbDiscoveryArgs);
                }
            } else if (args[0].equals("mcbView")) {
                MCBViewArgs mcbViewArgs = parseMCBView(args);
                if (mcbViewArgs != null) {
                    mcbView.mcbView(mcbViewArgs);
                }
            } else {
                System.out.println("unrecognized command:" + args[0]);
            }
        } else { // show the help message

        }
    }

    private static Options getOptions(Field[] declaredFields) {
        Options options = new Options();
        Option helpOption = OptionBuilder.withLongOpt("help").withDescription("help").create("h");
        options.addOption(helpOption);
        Field[] fields = declaredFields;
        for(Field field : fields) {
            String annotation = field.getAnnotation(Annotation.class).value();
            Option option = null;
            if (field.getType().equals(boolean.class)) {
                option = OptionBuilder.withLongOpt(field.getName()).withDescription(annotation).create(field.getName());
            } else {
                option = OptionBuilder.withLongOpt(field.getName()).hasArg().withDescription(annotation).create(field.getName());
            }
            options.addOption(option);
        }
        return options;
    }

    public static String getStringFromMultiValueParameter(CommandLine commandLine, String args) {
        String value = commandLine.getOptionValue(args);
        if (commandLine.getArgs().length > 1) {
            for (int i = 1; i < commandLine.getArgs().length; i++) {
                value += " " + commandLine.getArgs()[i];
            }
        }
        // 去除重复的值
        String[] valueList = value.split(" ");
        Set<Object> haoma = new LinkedHashSet<Object>();
        for (int i = 0; i < valueList.length; i++) {
            haoma.add(valueList[i]);
        }

        String realValue = "";
        for (int i = 0; i < haoma.size(); i++) {
            realValue += " " + haoma.toArray()[i];
        }

        return realValue;
    }

    private static StatArgs parseStat(String[] args) throws ParseException {
        Options options = getOptions(StatArgs.class.getDeclaredFields());

        BasicParser parser = new BasicParser();
        StatArgs statArgs = new StatArgs();
        CommandLine commandLine = parser.parse(options, args);
        if (commandLine.getOptions().length > 0) {
            if (commandLine.hasOption('h')) {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp("Options", options);
                return null;
            } else {
                statArgs.setInput(commandLine.getOptionValue("input"));
                statArgs.setBed(commandLine.getOptionValue("bed"));
                if (commandLine.hasOption("sampleID")) {
                    statArgs.setSampleID(getStringFromMultiValueParameter(commandLine, "sampleID"));
                }
                statArgs.setMetrics(getStringFromMultiValueParameter(commandLine, "metrics"));
            }
        } else {
            System.out.println("The parameter is null");
        }

        return statArgs;
    }

    private static RArgs parseR(String[] args) throws ParseException {
        Options options = getOptions(RArgs.class.getDeclaredFields());

        BasicParser parser = new BasicParser();
        RArgs rArgs = new RArgs();
        CommandLine commandLine = parser.parse(options, args);
        if (commandLine.getOptions().length > 0) {
            if (commandLine.hasOption('h')) {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp("Options", options);
                return null;
            } else {
                rArgs.setInput(commandLine.getOptionValue("input"));
                if (commandLine.hasOption("bed")) {
                    rArgs.setBed(commandLine.getOptionValue("bed"));
                }
                if (commandLine.hasOption("region")) {
                    rArgs.setRegion(commandLine.getOptionValue("region"));
                }
                if (commandLine.hasOption("sampleID")) {
                    rArgs.setSampleID(getStringFromMultiValueParameter(commandLine, "sampleID"));
                }
                rArgs.setNSample(Integer.valueOf(String.valueOf(commandLine.getOptionValue("nSample"))));
            }
        } else {
            System.out.println("The parameter is null");
        }

        return rArgs;
    }

    private static MCBDiscoveryArgs parseMCBDiscovery(String[] args) throws ParseException {
        Options options = getOptions(MCBDiscoveryArgs.class.getDeclaredFields());

        BasicParser parser = new BasicParser();
        MCBDiscoveryArgs mcbDiscoveryArgs = new MCBDiscoveryArgs();
        CommandLine commandLine = parser.parse(options, args);
        if (commandLine.getOptions().length > 0) {
            if (commandLine.hasOption('h')) {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp("Options", options);
                return null;
            } else {
                mcbDiscoveryArgs.setInput(commandLine.getOptionValue("input"));
                mcbDiscoveryArgs.setBed(commandLine.getOptionValue("bed"));
                if (commandLine.hasOption("sampleID")) {
                    mcbDiscoveryArgs.setSampleID(getStringFromMultiValueParameter(commandLine, "sampleID"));
                }
                if (commandLine.hasOption("R")) {
                    mcbDiscoveryArgs.setR(Double.valueOf(String.valueOf(commandLine.getOptionValue("R"))));
                }
                if (commandLine.hasOption("pvalue")) {
                    mcbDiscoveryArgs.setPvalue(Double.valueOf(String.valueOf(commandLine.getOptionValue("pvalue"))));
                }
                if (commandLine.hasOption("window")) {
                    mcbDiscoveryArgs.setWindow(Integer.valueOf(String.valueOf(commandLine.getOptionValue("window"))));
                }
                if (commandLine.hasOption("distance")) {
                    mcbDiscoveryArgs.setDistance(Integer.valueOf(String.valueOf(commandLine.getOptionValue("distance"))));
                }
                mcbDiscoveryArgs.setNSample(Integer.valueOf(String.valueOf(commandLine.getOptionValue("nSample"))));
            }
        } else {
            System.out.println("The parameter is null");
        }

        return mcbDiscoveryArgs;
    }

    private static MCBViewArgs parseMCBView(String[] args) throws ParseException {
        Options options = getOptions(MCBViewArgs.class.getDeclaredFields());

        BasicParser parser = new BasicParser();
        MCBViewArgs mcbViewArgs = new MCBViewArgs();
        CommandLine commandLine = parser.parse(options, args);
        if (commandLine.getOptions().length > 0) {
            if (commandLine.hasOption('h')) {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp("Options", options);
                return null;
            } else {
                mcbViewArgs.setInput(commandLine.getOptionValue("input"));
                mcbViewArgs.setRegion(commandLine.getOptionValue("region"));
                if (commandLine.hasOption("sampleID")) {
                    mcbViewArgs.setSampleID(getStringFromMultiValueParameter(commandLine, "sampleID"));
                }
                mcbViewArgs.setNSample(Integer.valueOf(String.valueOf(commandLine.getOptionValue("nSample"))));
            }
        } else {
            System.out.println("The parameter is null");
        }

        return mcbViewArgs;
    }
}
