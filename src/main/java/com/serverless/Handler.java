package com.serverless;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Handler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private static final Logger LOG = LogManager.getLogger(Handler.class);

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		LOG.info("received: {}", input);
		try{
            Map<String,String> pathParameters =  (Map<String,String>)input.get("pathParameters");
            String query = pathParameters.get("searchQuery");

            LOG.info("Query = " + query);

            CraigsListScraper scraper = new CraigsListScraper();
            List<Item> items = scraper.scrape(query);

            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(items)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & serverless"))
                    .build();
        }catch(Exception e){
		    LOG.error("Error : " + e);
            Response responseBody = new Response("Error while processing URL: ", input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();
        }
	}
}
