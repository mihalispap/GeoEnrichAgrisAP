package com.agroknow.geoenrich;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AgrisAPHandler 
{
	String to_write="";
	String rights="";
	String enrichments="";
	String prefix="";
	List<String> stack=new ArrayList<String>();
	List<String> rights_list=new ArrayList<String>();
	
	public void generate(String filename, String output, String only_filename)
	{
		try {

			try
			{
				System.out.println(only_filename);
				prefix=only_filename.substring(0,2)+only_filename.charAt(6);
				if(!prefix.isEmpty() && prefix!="")
					searchRights();
			}
			catch(java.lang.Exception e)
			{
				e.printStackTrace();
			}
			
        	File fXmlFile = new File(filename);
        	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        	Document doc = dBuilder.parse(fXmlFile);
        			
        	//optional, but recommended
        	//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        	doc.getDocumentElement().normalize();
        	
        	System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
        	//System.out.println("ToString method call:" + doc.getDocumentElement().getTextContent());
        			
        	NodeList nList = doc.getElementsByTagName("ags:resources");
        	
        	System.out.println("----------------------------");

        	//TODO:
        	//	<dc:rights><ags:rightsTermsOfUse>
        	
        	
        	writeInitialize(only_filename, output);
        	for (int temp = 0; temp < nList.getLength(); temp++) 
        	{
        		
        		Node nNode = nList.item(temp);
        		
        		//System.out.println("\nCurrent Element :" + nNode.getNodeName());
        		
        		if (nNode.getNodeType() == Node.ELEMENT_NODE) 
        		{

        			Element eElement = (Element) nNode;  			
        			
        			NodeList node_list = eElement.getChildNodes();
        			for(int i=0;i<node_list.getLength();i++)
        			{
        				enrichments="";
        				Node node=node_list.item(i);
        				
        				
        				if (node.getNodeType() == Node.ELEMENT_NODE) 
                		{
        					//write(node, only_filename, output);
	        				Element element = (Element) node;
	        				update(node);
	        				NodeList resource_list=element.getChildNodes();
	        				for(int j=0;j<resource_list.getLength();j++)
	        				{
	        					Node resource=resource_list.item(j);
	        					if (resource.getNodeType() == Node.ELEMENT_NODE) 
	                    		{
	        						update(resource);
		            				Element resource_element = (Element) resource;
		            				
		            				NodeList third_lvl=resource_element.getChildNodes();
		            				for(int k=0;k<third_lvl.getLength();k++)
		            				{
		            					Node thirdLvlElement=third_lvl.item(k);
		            					if (thirdLvlElement.getNodeType() == Node.ELEMENT_NODE) 
			                    		{
		            						update(thirdLvlElement);
			                    		}
		            				}
		            				pop();
	                    		}
	        				}
	        				pop();
	        				write(only_filename, output);
                		}
        				
        			}
        			
        			
        		}
        	}
        	
        	finalize(only_filename, output);
        } 
        catch (Exception e) 
        {
        	e.printStackTrace();
        }
          
	}
	
	void pop()
	{
		if(stack.size()==0)
			return;
		
		if(stack.get(stack.size()-1).equals("ags:resource"))
			return;
		
		to_write+="\n\t</"+stack.get(stack.size()-1)+">";
		stack.remove(stack.size()-1);
	}
	
	private void update(Node element)
	{
		to_write+="\n\t<";
		
		String node_name=element.getNodeName();
		to_write+=node_name;
		
		try
		{
			for(int i=0;i<element.getAttributes().getLength();i++)
			{
				Node node = element.getAttributes().item(i);
				
				to_write+=" "+node.getNodeName()+"=\""+node.getNodeValue()+"\" ";
				
				
			}
		}
		catch(java.lang.NullPointerException e)
		{
			System.out.println("No attributes? for name:"+element.getNodeName());
		}
		to_write+=">\n";
		
		
		/*while(true)
		{
			Node previous=element;
			NodeList nlist=element.getChildNodes();
			

			if(nlist.getLength()==0)
			{
				to_write+=previous.getTextContent();
				break;
			}
			
			
		}*/
		
		NodeList nlist=element.getChildNodes();
		
		System.out.println("Node named:"+element.getNodeName()+" has list length:"+nlist.getLength()+" and is named:"
				+nlist.item(0).getNodeName());
		
		if(!hasNodeChild(nlist))
		{
			to_write+="<![CDATA["+element.getTextContent()+"]]>";
			to_write+="</"+element.getNodeName()+">";
			

			if(node_name.equals("dc:title"))
			{
				try {
					
					System.out.println("Trying with:"+element.getNodeName()+" and value:"+element.getTextContent());
					
					searchGeo(element.getTextContent());
				} catch (DOMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if(node_name.equals("dcterms:abstract"))
			{
				try {
					
					System.out.println("Trying with:"+element.getNodeName()+" and value:"+element.getTextContent());
					
					searchGeo(element.getTextContent());
				} catch (DOMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else
		{
			stack.add(element.getNodeName());
		}
	}
	
	private boolean hasNodeChild(NodeList nlist)
	{
		for(int i=0;i<nlist.getLength();i++)
		{
			Node temp=nlist.item(i);
			if (temp.getNodeType() == Node.ELEMENT_NODE)
			{
				return true;
			}
		}
		
		return false;
	}
	
	private void write(Node element, String filename, String output)
	{
		/*PrintWriter writer = new PrintWriter(output+System.getProperty("file.separator")+
				filename, "UTF-8");
		writer.println(jsonPrettyPrintString);
		writer.close();*/
		
		try
		{
			//String data = " This content will append to the end of the file\n";
    		
			String data="<";
			
			String node_name=element.getNodeName();
			data+=node_name;
			
			try
			{
				for(int i=0;i<element.getAttributes().getLength();i++)
				{
					Node node = element.getAttributes().item(i);
					
					data+=" "+node.getNodeName()+"=\""+node.getNodeValue()+"\" ";
				}
			}
			catch(java.lang.NullPointerException e)
			{
				System.out.println("No attributes? for name:"+element.getNodeName());
			}
			data+=">\n";
			
    		File file =new File(output, filename);

			System.out.println(output+"|"+filename);
    		
			//if file doesnt exists, then create it
    		if(!file.exists()){
    			file.createNewFile();
    			
    			String header="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
    					"<!DOCTYPE ags:resources SYSTEM \"http://purl.org/agmes/agrisap/dtd/\">"+
    					"<ags:resources xmlns:ags=\"http://purl.org/agmes/1.1/\" "
    					+ "xmlns:dc=\"http://purl.org/dc/elements/1.1/\" "
    					+ "xmlns:agls=\"http://www.naa.gov.au/recordkeeping/gov_online/agls/1.2\" "
    					+ "xmlns:dcterms=\"http://purl.org/dc/terms/\">\n";
    			FileWriter fileWritter = new FileWriter(file.getAbsolutePath(),true);
    	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
    	        bufferWritter.write(header);
    	        bufferWritter.close();
    			
    		}
    		
    		//true = append file
    		FileWriter fileWritter = new FileWriter(file.getAbsolutePath(),true);
    	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
    	        bufferWritter.write(data);
    	        bufferWritter.close();
    	    
	        System.out.println("Done");
			    //more code
		} 
		catch (IOException e) {
				e.printStackTrace();
			    //exception handling left as an exercise for the reader
		}
		
	}

	private void write(String filename, String output)
	{
		File file =new File(output, filename);
		
		String data=to_write.replace("\n\n", "\n")+"\n"
				+ "\t"+enrichments+"\n"
				+ "\t"+rights+"\n"
				+ "</ags:resource>";
		
		FileWriter fileWritter = null;
		try {
			fileWritter = new FileWriter(file.getAbsolutePath(),true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
        try {
			bufferWritter.write(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			bufferWritter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        to_write="";
	}

	private void writeInitialize(String filename, String output)
	{
		File file =new File(output, filename);
		
		String header="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
				"<!DOCTYPE ags:resources SYSTEM \"http://purl.org/agmes/agrisap/dtd/\">"+
				"<ags:resources xmlns:ags=\"http://purl.org/agmes/1.1/\" "
				+ "xmlns:dc=\"http://purl.org/dc/elements/1.1/\" "
				+ "xmlns:agls=\"http://www.naa.gov.au/recordkeeping/gov_online/agls/1.2\" "
				+ "xmlns:dcterms=\"http://purl.org/dc/terms/\">\n";
		FileWriter fileWritter = null;
		try {
			fileWritter = new FileWriter(file.getAbsolutePath(),true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
        try {
			bufferWritter.write(header);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			bufferWritter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void finalize(String filename, String output)
	{
		
		
		
		File file =new File(output, filename);
		
		String data="</ags:resources>";
		
		FileWriter fileWritter = null;
		try {
			fileWritter = new FileWriter(file.getAbsolutePath(),true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
        try {
			bufferWritter.write(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			bufferWritter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	void searchGeo(String value_total) throws Exception
	{
		String[] value=value_total.split(" ");
		
		String absolute_path=System.getProperty("user.dir")+System.getProperty("file.separator")+""
				+ "assets"+System.getProperty("file.separator");
		
		List<String> coverages=new ArrayList<String>();
		
		/*
		 * 	TODO: 
		 * 		rethink about case sensitive/insensitive
		 * 
		 * */
		for(int j=0;j<value.length;j++)
		{
			value[j]=value[j].replace(",", "");
			value[j]=value[j].replace("(", "");
			value[j]=value[j].replace(")", "");
			
			FileInputStream fstream = new FileInputStream(absolute_path+"continents.db");
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

			String strLine;
			while ((strLine = br.readLine()) != null)   
			{

				String[] geonames=strLine.split("\t");
			  
				boolean found=false;
				String geonames_id="";

				if(value[j].equalsIgnoreCase(geonames[1]))
				{
						found=true;
						//geonames_id=geonames[2];
				}
				if(found)
				{
					coverages.add(geonames[1]);
					//break;
				}
				found=false;
				if(j!=value.length-1)
				{
					
					String to_check=value[j]+" "+value[j+1];
					if(to_check.equalsIgnoreCase(geonames[1]))
					{
							found=true;
							//geonames_id=geonames[2];
					}
					if(found)
					{
						coverages.add(geonames[1]);
						//break;
					}
				}
				found=false;
			}
			br.close();
			
			fstream = new FileInputStream(absolute_path+"countries.db");
			br = new BufferedReader(new InputStreamReader(fstream));
			while ((strLine = br.readLine()) != null)   
			{

				String[] geonames=strLine.split("\t");
			  
				boolean found=false;
				String geonames_id="";
				
				if(value[j].equalsIgnoreCase(geonames[4]))
				{
						found=true;
						geonames_id=geonames[16];
				}
				if(found)
				{
					coverages.add(geonames[4]);
					//break;
				}

				found=false;
				if(j!=value.length-1)
				{
					String to_check=value[j]+" "+value[j+1];
					if(to_check.equalsIgnoreCase(geonames[4]))
					{
							found=true;
							//geonames_id=geonames[16];
					}
					if(found)
					{
						coverages.add(geonames[4]);
						//break;
					}
				}

				found=false;
			}
			br.close();

			fstream = new FileInputStream(absolute_path+"null.txt");
			br = new BufferedReader(new InputStreamReader(fstream));
			while ((strLine = br.readLine()) != null)   
			{

				String[] geonames=strLine.split("\t");
			  
				boolean found=false;
				String geonames_id="";
				
				if(value[j].equalsIgnoreCase(geonames[1]))
				{
						found=true;
				}
				if(found)
				{
					coverages.add(geonames[1]);
					//break;
				}
				found=false;
								
				if(j!=value.length-1)
				{
					String to_check=value[j]+" "+value[j+1];
					
					//if(to_check.contains("Aegean") && geonames[1].contains("Aegean"))
					//	System.out.println("Checking...\n...:"+to_check+", wt:"+geonames[1]);
					if(to_check.equalsIgnoreCase(geonames[1]))
					{
							found=true;
							//geonames_id=geonames[16];
					}
					if(found)
					{
						coverages.add(geonames[1]);
						//break;
					}
					found=false;
					
				}
				found=false;
			}
			br.close();
			
			
		}
			/*fstream = new FileInputStream(absolute_path+"cities1000.txt");
			br = new BufferedReader(new InputStreamReader(fstream));
			while ((strLine = br.readLine()) != null)   
			{

				String[] geonames=strLine.split("\t");
			  
				boolean found=false;
				String geonames_id="";
				
				if(value[j].equalsIgnoreCase(geonames[1]))
				{
						found=true;
				}
				if(found)
				{
					coverages.add(geonames[1]);
					//break;
				}
				found=false;
				
				if(value[j].equalsIgnoreCase(geonames[2]))
				{
					found=true;
				}
				if(found)
				{
					coverages.add(geonames[2]);
					//break;
				}
				found=false;
				
				if(j!=value.length-1)
				{
					String to_check=value[j]+" "+value[j+1];
					if(to_check.equalsIgnoreCase(geonames[1]))
					{
							found=true;
							//geonames_id=geonames[16];
					}
					if(found)
					{
						coverages.add(geonames[1]);
						//break;
					}
					found=false;
					if(to_check.equalsIgnoreCase(geonames[2]))
					{
							found=true;
							//geonames_id=geonames[16];
					}
					if(found)
					{
						coverages.add(geonames[2]);
						//break;
					}
					
				}
				found=false;
			}
			br.close();
			

			fstream = new FileInputStream(absolute_path+"cities5000.txt");
			br = new BufferedReader(new InputStreamReader(fstream));
			while ((strLine = br.readLine()) != null)   
			{

				String[] geonames=strLine.split("\t");
			  
				boolean found=false;
				String geonames_id="";
				
				if(value[j].equalsIgnoreCase(geonames[1]))
				{
						found=true;
				}
				if(found)
				{
					coverages.add(geonames[1]);
					//break;
				}
				found=false;
				
				if(value[j].equalsIgnoreCase(geonames[2]))
				{
					found=true;
				}
				if(found)
				{
					coverages.add(geonames[2]);
					//break;
				}
				found=false;
				
				if(j!=value.length-1)
				{
					String to_check=value[j]+" "+value[j+1];
					if(to_check.equalsIgnoreCase(geonames[1]))
					{
							found=true;
							//geonames_id=geonames[16];
					}
					if(found)
					{
						coverages.add(geonames[1]);
						//break;
					}
					found=false;
					if(to_check.equalsIgnoreCase(geonames[2]))
					{
							found=true;
							//geonames_id=geonames[16];
					}
					if(found)
					{
						coverages.add(geonames[2]);
						//break;
					}
					
				}
				found=false;
			}
			br.close();
			

			fstream = new FileInputStream(absolute_path+"cities15000.txt");
			br = new BufferedReader(new InputStreamReader(fstream));
			while ((strLine = br.readLine()) != null)   
			{

				String[] geonames=strLine.split("\t");
			  
				boolean found=false;
				String geonames_id="";
				
				if(value[j].equalsIgnoreCase(geonames[1]))
				{
						found=true;
				}
				if(found)
				{
					coverages.add(geonames[1]);
					//break;
				}
				found=false;
				
				if(value[j].equalsIgnoreCase(geonames[2]))
				{
					found=true;
				}
				if(found)
				{
					coverages.add(geonames[2]);
					//break;
				}
				found=false;
				
				if(j!=value.length-1)
				{
					String to_check=value[j]+" "+value[j+1];
					if(to_check.equalsIgnoreCase(geonames[1]))
					{
							found=true;
							//geonames_id=geonames[16];
					}
					if(found)
					{
						coverages.add(geonames[1]);
						//break;
					}
					found=false;
					if(to_check.equalsIgnoreCase(geonames[2]))
					{
							found=true;
							//geonames_id=geonames[16];
					}
					if(found)
					{
						coverages.add(geonames[2]);
						//break;
					}
					
				}
				found=false;
			}
			br.close();
			

			 */
		
		
		for(int i=0;i<coverages.size();i++)
		{
			int j;
			for(j=i+1;j<coverages.size();j++)
			{
				if(coverages.get(j).equals(coverages.get(i)))
					break;
			}
			if(j==coverages.size())
				enrichments+="\t<dc:coverage>"+coverages.get(i)+"</dc:coverage>\n";
		}
		
		//enrichments+="<dc:coverage>foundsomething..</dc:coverage>";
		
	}
	
	void searchRights() throws Exception
	{
		prefix="RU0";
		String url="http://www.akstem.com/centercode/"+prefix;
	
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new URL(url).openStream());

		
		doc.getDocumentElement().normalize();
    	
    	System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
    	//System.out.println("ToString method call:" + doc.getDocumentElement().getTextContent());
    			
    	NodeList nList = doc.getElementsByTagName("nodes");
    	for (int temp = 0; temp < nList.getLength(); temp++) 
    	{
    		Node nNode = nList.item(temp);

    		if (nNode.getNodeType() == Node.ELEMENT_NODE) 
    		{    			
    			Element eElement = (Element) nNode;    			
    			NodeList node_list = eElement.getChildNodes();
    			for(int i=0;i<node_list.getLength();i++)
    			{
    				Node node=node_list.item(i);	    				
    				if (node.getNodeType() == Node.ELEMENT_NODE) 
            		{    					
    					Element eElementInner = (Element) node;    			
    	    			NodeList node_list_inner = eElementInner.getChildNodes();
    	    			for(int j=0;j<node_list_inner.getLength();j++)
    	    			{
    	    				Node title_rights=node_list_inner.item(j);	    				
    	    				if (title_rights.getNodeType() == Node.ELEMENT_NODE) 
    	            		{
    	    					//System.out.println("XML read node:"+title_rights.getNodeName());
    	    					if(title_rights.getNodeName().equals("rights"))
    	    					{
    	    						rights_list.add(title_rights.getTextContent());
    	    					}
    	            		}
    	    			}
            		}
    			}
    		}
    	}
    	
    	if(!rights_list.isEmpty())
    	{
    		String stricter="";
    		int score=0;
    		for(int i=0;i<rights_list.size();i++)
    		{
    			String current=rights_list.get(i);
    			/*
    			 * 
    			 	Free access							40
					Full text							50
					Open Access							60
					Some resources free					70
					Full text limited to Institution	80
					Paid - subscription					90
					Paid - per item						100
    			 * 
    			 */
    			if(current.equals("Paid - per item") && score<100)
    			{
    				score=100;
    				stricter=current;
    				break;
    			}
    			else if(current.equals("Paid - subscription") && score<90)
    			{
    				score=90;
    				stricter=current;
    			}
    			else if(current.equals("Full text limited to Institution") && score<80)
    			{
    				score=80;
    				stricter=current;
    			}
    			else if(current.equals("Some resources free") && score<70)
    			{
    				score=70;
    				stricter=current;
    			}
    			else if(current.equals("Open Access") && score<60)
    			{
    				score=60;
    				stricter=current;
    			}
    			else if(current.equals("Full text") && score<50)
    			{
    				score=50;
    				stricter=current;
    			}
    			else if(current.equals("Free access") && score<40)
    			{
    				score=40;
    				stricter=current;
    			}
    		}
    		System.out.println("STRICTER"+stricter);
    		
    		if(!stricter.isEmpty() && stricter!="")
    			rights="<dc:rights>"+stricter+"</dc:rights>";
    	}
	}
}















