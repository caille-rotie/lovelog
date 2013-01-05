package fr.rotie.caille.lovelog.camel;

import org.apache.camel.dataformat.castor.CastorDataFormat;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class LoveRoute extends SpringRouteBuilder{

	@Override
	public void configure() throws Exception {
        
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
            .beanRef("logMessageDao", "create")
        	.log("ligne: ${body}")
//        	.to("jpa:fr.rotie.caille.lovelog.model.LogMessage")
        	.end();
        
        from("direct:emesene")
    		.log("type : emesene")
//        	.split(body().tokenize("\n\\["))
//            .unmarshal(new EmeseneLogMessageDataFormat())
//            .log("ligne: ${body}")
    		//                .to("jpa:fr.rotie.caille.lovelog.model.LogMessage")
            .end();

    	
    	
    	
//      .choice()
//            .when()
//                .xpath("log/message")
//                .log("xml")
//            .otherwise()

		//                .get
		//                .log(".")
		//                .convertBodyTo(logMessage.class, "UTF-8")
		//                .log(";")
		//                .to("file://io/output");
		
	}

}
