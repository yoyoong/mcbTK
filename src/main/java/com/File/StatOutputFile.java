package com.File;

import java.io.IOException;

public class StatOutputFile extends OutputFile {
    public StatOutputFile(String directory, String fileName) throws IOException {
        super(directory, fileName);
    }

    @Override
    public void writeHead(String head) throws IOException, IllegalAccessException {
        String[] metrics = head.split(" ");
        String headString = metrics[0];
        for (int i = 1; i < metrics.length; i++) { // joint the fields generate line string
            headString += "\t" + metrics[i];
        }
        headString += "\n";
        bufferedWriter.write(headString);
    }
}
