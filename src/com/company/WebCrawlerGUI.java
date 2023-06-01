package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class WebCrawlerGUI extends JFrame {

    private JTextField urlTextField;
    private JButton startButton;
    private JTextArea resultTextArea;

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
            url = crawlPage(url);
        }
    }

    private String crawlPage(String url) {
        try {
            Document document = Jsoup.connect(url).get();

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
        } catch (IndexOutOfBoundsException ignored){

        }catch (IOException e) {
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



