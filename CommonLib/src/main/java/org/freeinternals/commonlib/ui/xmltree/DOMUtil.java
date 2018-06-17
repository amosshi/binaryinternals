package org.freeinternals.commonlib.ui.xmltree;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * <p>
 * A little change may be done on formatting, annotation, java doc, etc.
 * </p>
 *
 * @author Santhosh Kumar T - santhosh@in.fiorano.com
 * @see <a href="http://www.javalobby.org/java/forums/t19666.html">XML Viewer for Swing</a>
 */
public class DOMUtil {

    public static Document createDocument(InputSource is)
            throws ParserConfigurationException, SAXException, TransformerConfigurationException, TransformerException{
        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        saxFactory.setNamespaceAware(true);
        XMLReader reader = new XMLTrimFilter(saxFactory.newSAXParser().getXMLReader());

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        DOMResult result = new DOMResult();
        transformer.transform(new SAXSource(reader, is), result);
        return (Document) result.getNode();
    }
}
