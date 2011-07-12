/*
 * Copyright 2008-2011 Sam Adams <sea36 at users.sourceforge.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.jnati.deploy.artefact;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Sam Adams
 */
public class ManifestReader {

	private static final Logger LOG = Logger.getLogger(ManifestReader.class);
	
	public void read(InputStream is, Artefact artefact) throws IOException {

		LOG.info("Reading manifest");
		
		Document doc;
        
        // Read manifest file to XML document
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = builder.parse(is);
        } catch (ParserConfigurationException e) {
        	throw new IOException("Failed to read manifest: " + e.getMessage());
        } catch (SAXException e) {
        	throw new IOException("Failed to read manifest: " + e.getMessage());
        } finally {
            is.close();
        }

        // Read manifest properties
        Element manifest = doc.getDocumentElement();
        if (!"manifest".equals(manifest.getNodeName())) {
        	throw new IOException("Wrong root node: " + manifest.getNodeName());
        }
        
        List<ArtefactFile> fileList = new ArrayList<ArtefactFile>();
        NodeList nodelist = manifest.getChildNodes();
        for (int i = 0, n = nodelist.getLength(); i < n; i ++) {
            Node node = nodelist.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) node;
                String nn = el.getNodeName();
                if (!"file".equals(nn)) {
                    throw new IOException("Unexpected manifest element: " + nn);
                }

                String filename = el.getTextContent().trim();
                LOG.trace("File: " + filename);
                ArtefactFile record = new ArtefactFile(filename);
                
                if (el.hasAttribute("exe")) {
        			record.setExe(Boolean.valueOf(el.getAttribute("exe")));
        		}
        		if (el.hasAttribute("library")) {
        			record.setLibrary(Boolean.valueOf(el.getAttribute("library")));
        		}
                
                fileList.add(record);

            } else {
                if (!(node.getNodeType() == Node.TEXT_NODE || "".equals(node.getTextContent().trim()))) {
//                    LOG.warn("Ignored manifest node: " + node);
                }
            }
        }
        
        artefact.setFileList(fileList);
		
	}

}
