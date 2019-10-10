package com.rubrik;

import com.rubrik.utility.SalesforceAPI;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        SalesforceAPI rubrikAPI = new SalesforceAPI();
        String token = rubrikAPI.fetchToken();
        System.out.println("token: "+ token);
        rubrikAPI.callAPI(token, "SlaProcess"); // HierachyTree
    }
}
