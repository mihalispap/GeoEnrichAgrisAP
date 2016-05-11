package com.agroknow.geoenrich;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AgrisAPHandler 
{
	String to_write="";
	List<String> stack=new ArrayList<String>();
	
	public void generate(String filename, String output, String only_filename)
	{
		try {

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
		
		String data=to_write+"\n</ags:resource>";
		
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
	
	
	
	
}















