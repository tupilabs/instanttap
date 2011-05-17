package org.tap4j.instanttap;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.tap4j.consumer.TapConsumer;
import org.tap4j.consumer.TapConsumerException;
import org.tap4j.consumer.TapConsumerFactory;
import org.tap4j.model.TestSet;

@SuppressWarnings("serial")
public class InstanttapServlet extends HttpServlet
{
	public void doPost( HttpServletRequest req, HttpServletResponse resp )
			throws IOException
	{
		
		StringBuilder response = new StringBuilder();
		
		String tap = req.getParameter("tap");
		
		if ( StringUtils.isNotBlank( tap ) )
		{
			TapConsumer consumer = TapConsumerFactory.makeTap13YamlConsumer();
			try
			{
				TestSet testSet = consumer.load( tap );
				response.append( "<div class='success'>Valid TAP Stream.</div>" );
				response.append( "<table style='border: 1px solid #eee;'>" );
				response.append( this.addTapTableEntry( "TAP Plan", testSet.getPlan() ) );
				response.append( this.addTapTableEntry( "TAP Plan initial test number", testSet.getPlan().getInitialTestNumber() ) );
				response.append( this.addTapTableEntry( "TAP Plan last test number", testSet.getPlan().getLastTestNumber() ) );
				response.append( this.addTapTableEntry( "Skip tests?", testSet.getPlan().getSkip()!=null ) );
				response.append( this.addTapTableEntry( "Contains OK's?", testSet.containsOk() ) );
				response.append( this.addTapTableEntry( "Contains NOT OK's?", testSet.containsNotOk() ) );
				response.append( this.addTapTableEntry( "Contains Bail Out!'s?", testSet.containsBailOut() ) );
				response.append( this.addTapTableEntry( "Number of Test Results", testSet.getNumberOfTestResults() ) );
				response.append( this.addTapTableEntry( "Number of Bail Out!'s", testSet.getNumberOfBailOuts() ) );
				response.append( this.addTapTableEntry( "Number of Comments", testSet.getNumberOfComments() ) );
				response.append( this.addTapTableEntry( "Total number of Tap Lines", testSet.getNumberOfTapLines() ) );
				response.append( "</table>" );
			} 
			catch ( TapConsumerException tce )
			{
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter( sw );
				tce.printStackTrace( pw );
				response.append("<div class='error'><span>Invalid TAP Stream. Check below stack trace for details.</span></div>");
				response.append( "<textarea style='width: 98%' wrap='off'>" );
				response.append( sw.toString() );
				response.append( "</textarea>" );
				pw.close();
				sw.close();				
			}
		}
		else
		{
			response.append("<div class='error'>Missing TAP Stream.</div>");
		}
		resp.setContentType("text/html");
		resp.getWriter().println(response.toString());
	}

	/**
	 * @param name Name of TAP Table entry.
	 * @param value Value of TAP Table entry.
	 */
	private String addTapTableEntry( String name, Object value )
	{
		return "<tr><td>"+name+"</td><td>"+value+"</td></tr>";
	}
}
