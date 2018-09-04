package com.serverless;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CraigsListScraper {

    private final String baseUrl = "https://sfbay.craigslist.org/search/sss?query=";

    public List<Item> scrape(String query) {

        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled(false);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setUseInsecureSSL(true);
        List<Item> itemList = new ArrayList<>();
        try {
            String searchUrl = baseUrl + query + "&sort=rel";
            HtmlPage page = client.getPage(searchUrl);
            List<HtmlElement> items = (List<HtmlElement>) page.getByXPath("//li[@class='result-row']");
            if(items.isEmpty()){
                System.out.println("No items found");
            }else{
                for(HtmlElement htmlItem : items){
                    HtmlAnchor itemAnchor = ((HtmlAnchor) htmlItem.getFirstByXPath("p[@class='result-info']/a"));
                    HtmlElement spanPrice = ((HtmlElement) htmlItem.getFirstByXPath(".//a/span[@class='result-price']"));

                    String itemPrice = spanPrice == null ? "0.0": spanPrice.asText();

                    Item item = new Item();
                    item.setTitle(itemAnchor.asText());
                    item.setUrl(itemAnchor.getHrefAttribute());

                    item.setPrice(new BigDecimal(itemPrice.replace("$", "")));


                    itemList.add(item);

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return itemList ;

    }
}
