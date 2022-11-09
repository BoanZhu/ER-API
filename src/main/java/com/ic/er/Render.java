package com.ic.er;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Base64;



public class Render {
    public static void main(String[] args) throws
            IOException, InterruptedException {

        // load page using HTML Unit and fire scripts
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setDownloadImages(true);
        HtmlPage myPage = webClient.getPage(new File("src/main/resources/Render.html").toURI().toURL());

        String imageDate = myPage.getElementById("image").asNormalizedText();

        System.out.println("image: " + myPage.getElementById("image").asNormalizedText());
        webClient.close();
//        decoder(imageDate,"test.png");
//        try (Playwright playwright = Playwright.create()) {
//            List<BrowserType> browserTypes = Arrays.asList(
//                    playwright.chromium()
//            );
//            for (BrowserType browserType : browserTypes) {
//                try (Browser browser = browserType.launch()) {
//                    BrowserContext context = browser.newContext();
//                    Page page = context.newPage();
//                    String filePath = new File("src/main/resources/Render.html").toURI().toURL().toString();
//                    page.navigate(filePath);
//                    System.out.println("image: " + page.locator("#image").textContent());
//                    decoder(page.locator("#image").textContent(),"test2.jpeg");
//                }
//            }
//        }
    }

//    public static void decoder(String base64Image, String pathFile) {
//        try (FileOutputStream imageOutFile = new FileOutputStream(pathFile)) {
//            // Converting a Base64 String into Image byte array
//            byte[] imageByteArray = Base64.getDecoder().decode(base64Image);
//            imageOutFile.write(imageByteArray);
//        } catch (FileNotFoundException e) {
//            System.out.println("Image not found" + e);
//        } catch (IOException ioe) {
//            System.out.println("Exception while reading the Image " + ioe);
//        }
//    }
}



