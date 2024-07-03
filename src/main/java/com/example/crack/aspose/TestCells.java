package com.example.crack.aspose;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import com.aspose.cells.LoadFormat;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.TableToRangeOptions;
import com.aspose.cells.Workbook;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.ImportFormatMode;
import com.aspose.words.License;

import java.nio.charset.StandardCharsets;

public class TestCells {

    static {
        try {
            new License().setLicense(ResourceUtil.getStream("aspose-license.xml"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {

// For complete examples and data files, please go to https://github.com/aspose-cells/Aspose.Cells-for-Java
        String dataDir = "C:\\Users\\chenwh3\\Desktop\\Temp\\";
// Open an existing file that contains a table/list object in it


        Workbook workbook = new Workbook(dataDir + "1234.xlsx");


        TableToRangeOptions options = new TableToRangeOptions();
        options.setLastRow(5);

// Convert the first table/list object (from the first worksheet) to normal range
        workbook.getWorksheets().get(0).getListObjects().get(0).convertToRange(options);

// Save the file
        workbook.save(dataDir + "ConvertTableToRangeWithOptions_out.xlsx");

    }
}
