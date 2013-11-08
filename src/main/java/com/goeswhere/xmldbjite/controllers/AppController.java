package com.goeswhere.xmldbjite.controllers;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.concurrent.ConcurrentMap;

@Controller
public class AppController {
    final ConcurrentMap<String, String> docs = Maps.newConcurrentMap();

    @RequestMapping(value = "/xml", method = RequestMethod.POST)
    public ResponseEntity<String> post(InputStream body) throws Exception {
        final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(body);
        final Element root = doc.getDocumentElement();
        final Node id = root.getAttributes().getNamedItem("id");
        if (null == id) {
            return new ResponseEntity<String>("missing id", HttpStatus.BAD_REQUEST);
        }
        final StringWriter sw = new StringWriter();
        TransformerFactory.newInstance().newTransformer().transform(new DOMSource(doc), new StreamResult(sw));
        if (null != docs.putIfAbsent(id.getTextContent(), sw.toString())) {
            return new ResponseEntity<String>("already have that id", HttpStatus.CONFLICT);
        }

        return new ResponseEntity<String>("saved", HttpStatus.CREATED);
    }

    @RequestMapping(value = "/xml/{id}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> get(@PathVariable("id") String id) {
        final String resp = docs.get(id);
        if (null == resp)
            return new ResponseEntity<byte[]>("don't have that".getBytes(Charsets.UTF_8), HttpStatus.NOT_FOUND);

        return new ResponseEntity<byte[]>(resp.getBytes(Charsets.UTF_8), HttpStatus.OK);
    }
}
