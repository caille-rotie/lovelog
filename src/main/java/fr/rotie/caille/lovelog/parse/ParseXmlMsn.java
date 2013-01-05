package fr.rotie.caille.lovelog.parse;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.dataformat.castor.CastorDataFormat;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.util.jndi.JndiContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import fr.rotie.caille.lovelog.model.emesene.EmeseneLogMessageDataFormat;
import fr.rotie.caille.lovelog.model.empathy.EmpathyLogMessageDao;
import fr.rotie.caille.lovelog.model.msn.MsnLogMessage;

import java.lang.Exception;

public class ParseXmlMsn {

    private ParseXmlMsn() {        
    }
    
    public static void main(String args[]) throws Exception {
    	
//    	JndiContext jndicontext = new JndiContext();
//    	jndicontext.bind("empathyLogMessageDao", new EmpathyLogMessageDao());
//        
//        // START SNIPPET: e1
//        CamelContext context = new DefaultCamelContext(jndicontext);
//        // END SNIPPET: e1
		ClassPathXmlApplicationContext springContext = new ClassPathXmlApplicationContext("META-INF/spring/camel-context.xml");
    	
    	CamelContext context = springContext.getBean(CamelContext.class);
        
        // Add some configuration by hand ...
        // START SNIPPET: e3
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                
                CastorDataFormat castor = new CastorDataFormat ();
                castor.setMappingFile("castor-mapping.xml");
            	
                from("file://io/input?noop=true")
	                .log("file ${file:name}")
	                .doTry()
	                	.to("validator:empathy.xsd")
	                	.to("direct:empathy")
	                	
	                .doCatch(java.lang.Exception.class)
			            .to("direct:emesene")
			        .end();
                
                from("direct:empathy")
            		.log("type : empathy")
	            	.split().tokenizeXML("message")
	            	.log("ligne: ${body}")
		            .unmarshal(castor)
		            .beanRef("empathyLogMessageDao", "parseInstant")
// TODO : Enrichir l'objet pour mettre à jour instant
	            	.log("ligne: ${body}")
	            	.end();
                
                from("direct:emesene")
            		.log("type : emesene")
//	            	.split(body().tokenize("\n\\["))
//	                .unmarshal(new EmeseneLogMessageDataFormat())
//	                .log("ligne: ${body}")
	        		//                .to("jpa:fr.rotie.caille.lovelog.model.LogMessage")
	                .end();

            	
            	
            	
//              .choice()
//	                .when()
//	                    .xpath("log/message")
//		                .log("xml")
//		            .otherwise()

        		//                .get
        		//                .log(".")
        		//                .convertBodyTo(logMessage.class, "UTF-8")
        		//                .log(";")
        		//                .to("file://io/output");
                
            }
        });
        // END SNIPPET: e3
        
        
        context.start();

        // wait a bit and then stop
        Thread.sleep(5000);
        context.stop();
    }

}
