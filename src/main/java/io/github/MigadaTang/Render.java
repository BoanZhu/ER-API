package io.github.MigadaTang;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.regex.Pattern;

public class Render {

    private final static Pattern pattern = Pattern.compile("^(data:(.*?);base64,)");
    private final static String FILENAME= "test.png";

    public static void main(String[] args) throws IOException, InterruptedException {

        // load page using HTML Unit and fire scripts
        WebClient webClient = new WebClient();
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setDownloadImages(true);
        HtmlPage myPage = webClient.getPage(new File("src/main/resources/Render.html").toURI().toURL());
        // get the base64 code
        String baseImageCode = myPage.getElementById("image").asNormalizedText();
        // close connection
        webClient.close();

        //decode the code into image
        decode(baseImageCode,FILENAME);

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



