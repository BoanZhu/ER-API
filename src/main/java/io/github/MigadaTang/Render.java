package io.github.MigadaTang;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import io.github.MigadaTang.exception.ParseException;

import java.io.*;
import java.util.Base64;
import java.util.regex.Pattern;

public class Render {

    private final static Pattern pattern = Pattern.compile("^(data:(.*?);base64,)");
    private final static String htmlPath = "src/main/resources/show.html";
    private final static String FILENAME = "test.png";

    public static void render(String jsonString) throws ParseException {

        // load page using HTML Unit and fire scripts

        try {
            writeFile(jsonString);
        } catch (IOException e) {
            throw new ParseException("Fail to write the json string to file: " + e.getMessage());
        }

        WebClient webClient = new WebClient();
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setDownloadImages(true);
        HtmlPage myPage = null;
        try {
            myPage = webClient.getPage(new File(htmlPath).toURI().toURL());
        } catch (IOException e) {
            throw new ParseException("Fail to read the file: show.html");
        }
        // get the base64 code
        String baseImageCode = myPage.getElementById("image").asNormalizedText();
        // close connection
        webClient.close();

        //decode the code into image
        decode(baseImageCode, FILENAME);
    }

    public static void writeFile(String jsonString) throws IOException {

        File f = new File("src/main/resources/template.html");
        InputStreamReader isr1 = new InputStreamReader(new FileInputStream(f), "UTF-8");
        BufferedReader br = new BufferedReader(isr1);
        String s;
        StringBuilder allContent = new StringBuilder();
        while ((s = br.readLine()) != null) {
            allContent.append(s);
        }
        StringBuilder jsonStr = new StringBuilder("");
        jsonStr.append("{\"schema\":").append(jsonString).append("}");
        allContent.replace(allContent.indexOf("##"), allContent.indexOf("##") + 2, jsonStr.toString());
        File writeFile = new File("src/main/resources/show.html");
        FileOutputStream fileOutputStream = new FileOutputStream(writeFile);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "gb2312");
        outputStreamWriter.write(String.valueOf(allContent));

        outputStreamWriter.close();
        fileOutputStream.close();
        br.close();
        isr1.close();

    }

    private static void decode(String baseImageCode, String filename) {

        String base64Data = baseImageCode.split(",")[1];

        try (FileOutputStream imageOutFile = new FileOutputStream(FILENAME)) {
            byte[] imageByteArray = Base64.getDecoder().decode(base64Data);
            imageOutFile.write(imageByteArray);
        } catch (FileNotFoundException e) {
            System.out.println("Image not found" + e);
        } catch (IOException ioe) {
            System.out.println("Exception while reading the Image " + ioe);
        }
    }
}



