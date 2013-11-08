package com.goeswhere.xmldbjite.util;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class XmlHelper {

    private static final ThreadLocal<Transformer> transformer = new ThreadLocal<Transformer>() {
        @Override
        protected Transformer initialValue() {
            try {
                return TransformerFactory.newInstance().newTransformer();
            } catch (TransformerConfigurationException e) {
                throw new IllegalStateException(e);
            }
        }
    };
    private static ThreadLocal<DocumentBuilder> docBuilder = new ThreadLocal<DocumentBuilder>() {
        @Override
        protected DocumentBuilder initialValue() {
            try {
                return DocumentBuilderFactory.newInstance().newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                throw new IllegalStateException(e);
            }
        }
    };

    public static Document parse(InputStream body) throws IOException, SAXException {
        return docBuilder.get().parse(body);
    }

    public static String serialize(Document doc) throws TransformerException {
        final StringWriter sw = new StringWriter();
        transformer.get().transform(new DOMSource(doc), new StreamResult(sw));
        return sw.toString();
    }
}
