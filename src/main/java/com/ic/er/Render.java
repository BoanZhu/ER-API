package com.ic.er;

import com.microsoft.playwright.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class Render {
    public static void main(String[] args) throws IOException, InterruptedException {
        try (Playwright playwright = Playwright.create()) {
            List<BrowserType> browserTypes = Arrays.asList(
                    playwright.chromium()
            );
            for (BrowserType browserType : browserTypes) {
                try (Browser browser = browserType.launch()) {
                    BrowserContext context = browser.newContext();
                    Page page = context.newPage();
                    String filePath = new File("src/main/resources/Render.html").toURI().toURL().toString();
                    page.navigate(filePath);
                    System.out.println("image: " + page.locator("#image").textContent());
                }
            }
        }
        // load page using HTML Unit and fire scripts
//        WebClient webClient = new WebClient(BrowserVersion.CHROME);
//        webClient.getOptions().setJavaScriptEnabled(true);
//        HtmlPage myPage = webClient.getPage(new File("src/main/resources/Render.html").toURI().toURL());
//        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
//        webClient.waitForBackgroundJavaScript(10000);
//
//        int tries = 5;  // Amount of tries to avoid infinite loop
//        while (tries > 0 && myPage.getElementById("image").asNormalizedText().equals("")) {
//            tries--;
//            synchronized (myPage) {
//                myPage.wait(2000);  // How often to check
//            }
//        }
//        System.out.println("image: " + myPage.getElementById("image").asNormalizedText());
//        webClient.close();
    }

}



