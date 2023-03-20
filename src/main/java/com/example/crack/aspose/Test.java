package com.example.crack.aspose;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.ImportFormatMode;
import com.aspose.words.License;

import java.nio.charset.StandardCharsets;

public class Test {

    static {
        try {
            new License().setLicense(ResourceUtil.getStream("aspose-license.xml"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {

        Document docA = new Document();

// Inisialize a DocumentBuilder
        DocumentBuilder builder = new DocumentBuilder(docA);

// Insert text to the document A start
        builder.moveToDocumentStart();
        builder.write("First Hello World paragraph");

        FileUtil.writeString("", "D:\\Temp\\documentB.docx", StandardCharsets.UTF_8);
// Open an existing document B
        Document docB = new Document("D:\\Temp\\documentB.docx");

// Add document B to the and of document A, preserving document B formatting
        docA.appendDocument(docB, ImportFormatMode.KEEP_SOURCE_FORMATTING);

//        new License()
// Save the output as PDF
        docA.save("D:\\Temp\\output_AB.pdf");
    }
}
