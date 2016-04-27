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
        AgrisAPHandler handler=new AgrisAPHandler();
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
        	handler.generate(listOfFiles[i].getAbsolutePath(), output, listOfFiles[i].getName());
        	
        }
        
        
        
          
	}
        

		

}
