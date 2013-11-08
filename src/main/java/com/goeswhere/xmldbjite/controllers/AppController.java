package com.goeswhere.xmldbjite.controllers;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.concurrent.ConcurrentMap;

@Controller
public class AppController {
    final ConcurrentMap<String, String> docs = Maps.newConcurrentMap();

    @RequestMapping(value = "/xml", method = RequestMethod.POST)
    public ResponseEntity<String> post(InputStream body) throws Exception {
        final Document doc;
        try {
            doc = parse(body);
        } catch (SAXException ex) {
            return new ResponseEntity<String>(Throwables.getStackTraceAsString(ex), HttpStatus.BAD_REQUEST);
        }

        final Node idTag = doc.getDocumentElement().getAttributes().getNamedItem("id");
        final String id;
        if (null == idTag || (id = idTag.getTextContent()).isEmpty()) {
            return new ResponseEntity<String>("missing id", HttpStatus.BAD_REQUEST);
        }

        if (docs.containsKey(id) || null != docs.putIfAbsent(id, serialize(doc))) {
            return new ResponseEntity<String>("already have that id", HttpStatus.CONFLICT);
        }

        return new ResponseEntity<String>("saved", HttpStatus.CREATED);
    }

    private Document parse(InputStream body) throws SAXException, IOException, ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(body);
    }

    private String serialize(Document doc) throws TransformerException {
        final StringWriter sw = new StringWriter();
        TransformerFactory.newInstance().newTransformer().transform(new DOMSource(doc), new StreamResult(sw));
        return sw.toString();
    }

    @RequestMapping(value = "/xml/{id}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> get(@PathVariable("id") String id) {
        final String resp = docs.get(id);
        if (null == resp)
            return new ResponseEntity<byte[]>("don't have that".getBytes(Charsets.UTF_8), HttpStatus.NOT_FOUND);

        return new ResponseEntity<byte[]>(resp.getBytes(Charsets.UTF_8), HttpStatus.OK);
    }
}
