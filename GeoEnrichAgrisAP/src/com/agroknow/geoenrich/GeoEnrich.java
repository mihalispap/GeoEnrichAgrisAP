/**
 * 
 */
package com.agroknow.geoenrich;

import java.io.File;
import java.io.FilenameFilter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Mihalis
 *
 */
public class GeoEnrich {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		if (args.length != 2) {
            System.err.println("Usage: param1(inputdir) param2(outputdir)");                
            System.exit(1);
        } 
		
		String output=args[1];
        File file = new File(output);
		file.mkdirs();
        
        String folder_path=args[0];
        
        File folder = new File(folder_path);
        //File[] listOfFiles = folder.listFiles();

        File[] listOfFiles = folder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".xml");
            }
        });
        
        //folder.list
        
        for (int i = 0; i < listOfFiles.length; i++) 
        {
        	if (listOfFiles[i].isFile()) 
        	{
        		System.out.println("File " + listOfFiles[i].getName());
        	} 
        	else if (listOfFiles[i].isDirectory()) 
        	{
        		System.out.println("Directory " + listOfFiles[i].getName());
            }
        }
        
        try {

        	File fXmlFile = new File(listOfFiles[0].getAbsolutePath());
        	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        	Document doc = dBuilder.parse(fXmlFile);
        			
        	//optional, but recommended
        	//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        	doc.getDocumentElement().normalize();

        	System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
        			
        	NodeList nList = doc.getElementsByTagName("ags:resources");
        			
        	System.out.println("----------------------------");

        	for (int temp = 0; temp < nList.getLength(); temp++) {

        		Node nNode = nList.item(temp);
        				
        		System.out.println("\nCurrent Element :" + nNode.getNodeName());
        				
        		/*if (nNode.getNodeType() == Node.ELEMENT_NODE) 
        		{

        			Element eElement = (Element) nNode;

        			System.out.println("Staff id : " + eElement.getAttribute("id"));
        			System.out.println("First Name : " + eElement.getElementsByTagName("firstname").item(0).getTextContent());
        			System.out.println("Last Name : " + eElement.getElementsByTagName("lastname").item(0).getTextContent());
        			System.out.println("Nick Name : " + eElement.getElementsByTagName("nickname").item(0).getTextContent());
        			System.out.println("Salary : " + eElement.getElementsByTagName("salary").item(0).getTextContent());

        		}*/
        	}
        } 
        catch (Exception e) 
        {
        	e.printStackTrace();
        }
          
	}
        

		

}
