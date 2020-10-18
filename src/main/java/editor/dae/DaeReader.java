
package editor.dae;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Trifindo
 */
public class DaeReader {

    public static void loadDae(String path) throws ParserConfigurationException, SAXException, IOException {

        File stocks = new File(path);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(stocks);
        doc.getDocumentElement().normalize();

        System.out.println("root of xml file " + doc.getDocumentElement().getNodeName());
        NodeList nodes = doc.getElementsByTagName("library_images");

        System.out.println("==========================");
        System.out.println("length: " + nodes.getLength());

        for (int i = 0; i < nodes.getLength(); i++) {
            for (int j = 0; j < nodes.item(i).getChildNodes().getLength(); j++) {

                Node n = nodes.item(i).getChildNodes().item(j);
                //n.getChildNodes()
                System.out.println(i + " " + j + ": " + n.getNodeName() + " "
                        + n.getLocalName() + " " + n.getNamespaceURI() + " "
                        + n.getTextContent() + " " + n.getNodeValue());
            }

            //System.out.println(nodes.item(i).getNodeName());
        }
        System.out.println("==========================");

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                /*
                System.out.println("Stock Symbol: " + getValue("symbol", element));
                System.out.println("Stock Price: " + getValue("price", element));
                System.out.println("Stock Quantity: " + getValue("quantity", element));*/
            }
        }

        System.out.println("Loaded");

    }

}
