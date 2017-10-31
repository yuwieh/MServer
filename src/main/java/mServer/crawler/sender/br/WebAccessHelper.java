/*
 * WebAccessHelper.java
 * 
 * Projekt    : MLib
 * erstellt am: 04.10.2017
 * Autor      : Sascha
 * 
 */
// Ist eigentlich aus MLib in der neuen Architektur, aber für den neuen BR-Crawler erstmal hierher kopiert
package mserver.crawler.sender.br;

import java.net.URL;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

public class WebAccessHelper {
    
    public static String getJsonResultFromPostAccess(URL serverUrl, String request) throws IllegalArgumentException {
        if(null == serverUrl)
            throw new IllegalArgumentException("Es wurde keine gültige ServerURL angegeben");
        
        WebTarget target = WebTargetDS.getInstance(serverUrl.toString());
        
        String result = target.request(MediaType.APPLICATION_JSON_TYPE)
                              .post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE), String.class);

        return result;
    }

}