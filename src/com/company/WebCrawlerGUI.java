package com.company;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Random;

public class WebCrawlerGUI extends JFrame {

    private JTextField urlTextField;
    private JButton startButton;
    private JTextArea resultTextArea;

    private String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36";

    public WebCrawlerGUI() {
        setTitle("Web Crawler");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        urlTextField = new JTextField();
        startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                crawlWebsite();
            }
        });

        resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(urlTextField, BorderLayout.CENTER);
        panel.add(startButton, BorderLayout.EAST);

        add(panel, BorderLayout.NORTH);
        add(new JScrollPane(resultTextArea), BorderLayout.CENTER);
    }

    private void crawlWebsite() {
        String url = urlTextField.getText().trim();
        while (url != null) {
            if (isAllowedToCrawl(url)) {
                url = crawlPage(url);
                try {
                    // Introduce a delay between requests
                    Thread.sleep(randomTime(500, 5000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                resultTextArea.append("URL not allowed: " + url + "\n");
                break;
            }
        }
    }

    private int randomTime(int min, int max){
        // Create an instance of Random class
        Random random = new Random();

        // Generate a random number between min and max (inclusive)
        int randomNumber = random.nextInt(max - min + 1) + min;

        return  randomNumber;
    }

    private boolean isAllowedToCrawl(String url) {
        try {
            String robotsTxtUrl = url + "/robots.txt";
            Connection connection = Jsoup.connect(robotsTxtUrl)
                    .userAgent(userAgent)
                    .timeout(5000);
            Document document = connection.get();

            Elements disallowElements = document.select("Disallow");
            for (Element disallowElement : disallowElements) {
                String disallowedPattern = disallowElement.text();
                if (url.matches(disallowedPattern)) {
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    private String crawlPage(String url) {
        try {
            Connection connection = Jsoup.connect(url)
                    .userAgent(userAgent)
                    .timeout(5000);
            Document document = connection.get();

            Elements businessNames = document.select(".business-name");
            Elements addresses = document.select(".street-address");
            Elements phoneNumbers = document.select(".phones");

            for (int i = 0; i < addresses.size(); i++) {
                String output = "Business Name: " + businessNames.get(i).text() +
                        "\nAddress: " + addresses.get(i).text() +
                        "\nPhone: " + phoneNumbers.get(i).text() +
                        "\n------------------------------\n";
                resultTextArea.append(output);
            }

            // Check for next page
            Element nextButton = document.selectFirst(".next.ajax-page");
            if (nextButton != null) {
                String nextPage = nextButton.absUrl("href");
                if (!nextPage.isEmpty()) {
                    return nextPage;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WebCrawlerGUI client = new WebCrawlerGUI();
            client.setVisible(true);
        });
    }
}
