package com.File;

import com.bean.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public interface InputFile {
    List<?> parseByRegion(Region region) throws Exception;
    List<?> parseWholeFile() throws Exception;
    Map<?, ?> parseWholeFileGroupByChr() throws Exception;
    void close();
}
