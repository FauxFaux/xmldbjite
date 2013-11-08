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

import java.io.InputStream;
import java.util.concurrent.ConcurrentMap;

import static com.goeswhere.xmldbjite.util.XmlHelper.parse;
import static com.goeswhere.xmldbjite.util.XmlHelper.serialize;

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

    @RequestMapping(value = "/xml/{id}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> get(@PathVariable("id") String id) {
        final String resp = docs.get(id);
        if (null == resp)
            return new ResponseEntity<byte[]>("don't have that".getBytes(Charsets.UTF_8), HttpStatus.NOT_FOUND);

        return new ResponseEntity<byte[]>(resp.getBytes(Charsets.UTF_8), HttpStatus.OK);
    }
}
