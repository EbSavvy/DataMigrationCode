package com.rubrik.utility;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.rubrik.db.PostGresHelper;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class SalesforceAPI {

    private ResponseEntity responseEntity;
    private PostGresHelper client;

    public String fetchToken(){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id","3MVG9ahGHqp.k2_z1Q1lUykC6gZW4N5sS9uNve30qSi2ne.YVbP3Z96CKEZCSQhcUGTYPYJ3CNN2.ww1ZIe9A");
        map.add("client_secret","AE3E14A2876DBCBA2CB53035D8C629732B05009972303C2828990F923D1522A0");
        map.add("username","bhushan.patil@bluvium.com.rbkbackup");
        map.add("password","Blue@123");
        map.add("grant_type","password");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<HashMap> response =
                restTemplate.exchange("https://rbkbackup-rubrikpartnerregistration.cs17.force.com/services/oauth2/token",
                        HttpMethod.POST,
                        entity,
                        HashMap.class);
        HashMap<String, String> dt = response.getBody();
        String accessToken = dt.get("access_token");
        return accessToken;
    }

    public void callAPI(String accessToken, String objName){
        String url = "jdbc:postgresql://localhost:5432/";
        String user = "postgres";
        String password = "root";

        client = new PostGresHelper(
                url,
                "Rubrik",
                user,
                password);

        String endPoint = "https://rbkbackup-rubrikpartnerregistration.cs17.force.com/services/apexrest/objschema/"+objName;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> req = new HttpEntity<String>(headers);
        responseEntity = restTemplate.exchange(endPoint, HttpMethod.GET, req, String.class);
        System.out.println("responseEntity: "+ responseEntity);
//        createTables();
    }

    private void createTables(){
        Gson gson = new Gson();
        HashMap<String, LinkedTreeMap> treeMap = gson.fromJson(String.valueOf(responseEntity.getBody()), HashMap.class);
        for(Map.Entry<String, LinkedTreeMap> entry : treeMap.entrySet()){
            String key = entry.getKey();
//            if(key.equals("zsfjira__ZJIRAIdsList__ChangeEvent")) {
            LinkedTreeMap value = entry.getValue();
            Set keys = value.keySet();
            for(Iterator i = keys.iterator(); i.hasNext();){
                String keya = (String)i.next();

                if(keya.equals("Hierachy_history__c")){
                    HashMap<String, LinkedTreeMap> treeMap1 = gson.fromJson(value.get(keya).toString(), HashMap.class);
                    for(Map.Entry<String, ?> entry1 : treeMap1.entrySet()){
                        Object value1 = entry1.getValue();
                        String key1 = entry1.getKey();

                        if(key1.equals("lstFields")){
                            ArrayList<LinkedTreeMap> fields = (ArrayList<LinkedTreeMap>) value1;

                            try{
                                Connection conn = client.connect();
                                String str="";
                                str += "CREATE TABLE "+key+" (";

                                int fieldSize = fields.size();
                                int init = 0;
                                for(LinkedTreeMap field : fields){

                                    str += " "+ field.get("fieldName") + " VARCHAR (50) ";
                                    init++;
                                    if(init<fieldSize){
                                        str += ", ";
                                    }
                                }
                                str +=");";

                                Statement stmt = conn.createStatement();
                                stmt.execute(str);
//                                    stmt.executeQuery(str);
                                stmt.close();
                                conn.close();
                            }catch(SQLException | ClassNotFoundException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
//            }
        }
    }
}
