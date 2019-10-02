package ru.vlad.crrencyparser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

public class Main {

    private static final String URL_STR = "http://www.cbr.ru/scripts/XML_daily.asp";
    private static final String VALUE_TAG = "Value";
    private static final String NOMINAL_TAG = "Nominal";
    private static final String NAME_TAG = "Name";

    public static void main(String[] args) {
        try (BufferedInputStream bis = new BufferedInputStream(new URL(URL_STR).openStream())) {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(bis);
            document.normalizeDocument();
            NodeList cursList = document.getDocumentElement().getChildNodes();

            String cheapValuteName = null;
            double cheapCost = 0;
            String expensiveValuteName = null;
            double expensiveCost = 0;

            for (int i = 0; i < cursList.getLength(); i++) {
                Element valuteElement = (Element) cursList.item(i);
                String valueStr = valuteElement.getElementsByTagName(VALUE_TAG)
                        .item(0).getChildNodes().item(0).getNodeValue();
                String nominalStr = valuteElement.getElementsByTagName(NOMINAL_TAG)
                        .item(0).getChildNodes().item(0).getNodeValue();
                double value = Double.parseDouble(valueStr.replace(',', '.'));
                int nominal = Integer.parseInt(nominalStr.replace(',', '.'));
                double cost = value/nominal;
                if (cost < cheapCost || cheapCost == 0) {
                    cheapCost = cost;
                    cheapValuteName = valuteElement.getElementsByTagName(NAME_TAG)
                            .item(0).getChildNodes().item(0).getNodeValue();
                }
                if (cost > expensiveCost) {
                    expensiveCost = cost;
                    expensiveValuteName = valuteElement.getElementsByTagName(NAME_TAG)
                            .item(0).getChildNodes().item(0).getNodeValue();
                }
            }
            System.out.println(String.format("Cheap: %s = %s", cheapValuteName, cheapCost));
            System.out.println(String.format("Expensive: %s = %s", expensiveValuteName, expensiveCost));

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

}
