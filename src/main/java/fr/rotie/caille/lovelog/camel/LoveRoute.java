package fr.rotie.caille.lovelog.camel;

import org.apache.camel.dataformat.castor.CastorDataFormat;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.stereotype.Component;

import fr.rotie.caille.lovelog.model.emesene.EmeseneLogMessageDataFormat;

@Component
public class LoveRoute extends SpringRouteBuilder{

	@Override
	public void configure() throws Exception {
        
        CastorDataFormat castorEmpathy = new CastorDataFormat ();
        castorEmpathy.setMappingFile("castor-empathy-mapping.xml");
        
        CastorDataFormat castorGtalk = new CastorDataFormat ();
        castorEmpathy.setMappingFile("castor-gtalk-mapping.xml");
    	
        from("file://io/input?noop=true")
            .log("file : ${file:name}")
            .setHeader("logFile", method("logMessageDao","getLogfile"))
            .log("header : ${headers.logFile}")
            .choice()
                .when(simple("${file:name} == 'schemas.xml'")).to("direct:other")
                .when(simple("${file:name} == 'gtalk.xml'")).to("direct:other")
            	.when(body().contains("google:archive:conversation")).to("direct:gtalk")
            	.otherwise()
		            .doTry()
		            	.to("validator:empathy.xsd")
		            	.to("direct:empathy")
		            	
		            .doCatch(java.lang.Exception.class)
			            .to("direct:emesene")
	        .end();
        
        from("direct:other")
			.log("type : non-parsé\n\n")
	    	.end();
        
        from("direct:empathy")
    		.log("type : empathy")
        	.split().tokenizeXML("message")
        	.log("ligne: ${body}")
            .unmarshal(castorEmpathy)
            .beanRef("empathyLogMessageDao", "parseInstant")
            .beanRef("logMessageDao", "attachLogFile(${body}, ${headers.logFile})")
            .beanRef("logMessageDao", "createLogMessage")
        	.log("ligne: ${body}\n\n")
//        	.to("jpa:fr.rotie.caille.lovelog.model.LogMessage")
        	.end();
        
        from("direct:emesene")
    		.log("type : emesene")
        	.split(body().tokenize("\n\\["))
            .unmarshal(new EmeseneLogMessageDataFormat())
            .beanRef("logMessageDao", "attachLogFile(${body}, ${headers.logFile})")
            .beanRef("logMessageDao", "createLogMessage")
            .log("ligne: ${body}\n\n")
    		//                .to("jpa:fr.rotie.caille.lovelog.model.LogMessage")
            .end();
        
        from("direct:gtalk")
    		.log("type : gtalk")
        	.split().tokenizeXML("cli:message")
        	.log("ligne: ${body}")
//            .unmarshal(castorGtalk)
//            .beanRef("empathyLogMessageDao", "parseInstant")
//            .beanRef("logMessageDao", "attachLogFile(${body}, ${headers.logFile})")
//            .beanRef("logMessageDao", "createLogMessage")
        	.log("ligne: ${body}\n\n")
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
