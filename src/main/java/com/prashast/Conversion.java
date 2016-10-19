package com.prashast;

import com.google.common.base.CaseFormat;
import com.prashast.constants.Format;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 *
 */
public class Conversion {

    private final static Logger log = Logger.getLogger(Conversion.class);

    private JSONObject jsonObject = null;
    private JSONObject jsonObjectResult;

    public static void main(String args[]) throws JSONException{
        String for_underscore_json = new String("{\n" +
                "\"firstName\":\"Prashast\",\n" +
                "\"lastName\":\"Saxena\",\n" +
                "\"Age\":28,\n" +
                "\"Sex\": \"Male\",\n" +
                "\"dateOfBirth\":[\n" +
                "        {\n" +
                "        \"dateOfBirth\":13,\n" +
                "        \"monthOfBirth\":1,\n" +
                "        \"yearOfBirth\":1988\n" +
                "        }\n" +
                "\n" +
                "       ]\n" +
                "}");
        Conversion conversion = new Conversion(for_underscore_json);
        conversion.toUnderscore(Format.LOWER_UNDERSCORE,false,"lastName","firstName");

        String for_camelcase_json = new String("{\n" +
                "\"first_name\":\"Prashast\",\n" +
                "\"last_name\":\"Saxena\",\n" +
                "\"age_of_person\":28,\n" +
                "\"sex\": \"Male\",\n" +
                "\"date_of_birth\":[\n" +
                "        {\n" +
                "        \"date_of_birth\":13,\n" +
                "        \"month_of_birth\":1,\n" +
                "        \"year_of_birth\":1988\n" +
                "        }\n" +
                "\n" +
                "       ]\n" +
                "}");
        conversion = new Conversion(for_camelcase_json);
        conversion.toCamelCase(Format.LOWER_UNDERSCORE,false,"age_of_person");
    }

    public Conversion(String json){
        try{
            this.jsonObject = new JSONObject(json);
            this.jsonObjectResult = new JSONObject();
        }catch (JSONException e){
            log.error("Error while converting string json into JSON Object"+e.getMessage());
            log.info("json received: "+ json);
        }
    }

    /**
     *
     * @param inputFormat - takes the character case type of keys in input JSON. For more reference see Format.class
     * @param toUppercase - boolean variable which lets you convert keys to upper case
     * @return
     * This method will transform all the keys in given JSON Object from camelCase to underscore pattern.
     * For e.g. camelCase would look like camel_Case
     * having upper case as true would convert camelCase to CAMEL_CASE
     */
    public String toUnderscore(Format inputFormat, boolean toUppercase, String... skipKeys){

        log.debug("Converting to underscore pattern");
        log.debug("Input format of JSON: "+inputFormat);
        log.debug("Input JSON received: "+ this.jsonObject);

        if(this.jsonObject == null){
            log.error("Looks like conversion didn't happen properly.");
            return null;
        }

        if(inputFormat.equals(Format.LOWER_UNDERSCORE) || inputFormat.equals(Format.UPPER_UNDERSCORE)){
            log.error("Conversion from/to same format is not allowed.");
            return null;
        }

        CaseFormat jsonFormat = CaseFormat.valueOf(inputFormat.toString());
        this.jsonObjectResult = toUnderscore(this.jsonObject,jsonFormat, toUppercase, skipKeys);
        log.debug("Output JSON object: "+this.jsonObjectResult);

        if(this.jsonObjectResult != null){
            return this.jsonObjectResult.toString();
        }
        return null;
    }

    /**
     *
     * @param inputFormat - takes the character case type of keys in input JSON. For more reference see Format.class
     * @param toUppercase - boolean variable which lets you convert keys to upper case camel. i.e. FirstName. Lowercase camel is like camelCase.
     * @return
     * This method will transform all the keys in given JSON Object from underscore to camelcase pattern.
     */
    public String toCamelCase(Format inputFormat, boolean toUppercase, String... skipKeys){

        log.debug("Converting to underscore pattern");
        log.debug("Input format of JSON: "+inputFormat);
        log.debug("Input JSON received: "+ this.jsonObject);

        if(this.jsonObject == null){
            log.error("Conversion of JSON string to JSON object did not happen properly.");
            return null;
        }

        if(inputFormat.equals(Format.LOWER_CAMEL) || inputFormat.equals(Format.UPPER_CAMEL)){
            log.error("Conversion from/to same format is not allowed.");
            return null;
        }

        CaseFormat jsonFormat = CaseFormat.valueOf(inputFormat.toString());
        this.jsonObjectResult = toCamelCase(this.jsonObject, jsonFormat, toUppercase,skipKeys);
        log.debug("Output JSON object: "+this.jsonObjectResult);

        if(this.jsonObjectResult!=null){
            return this.jsonObjectResult.toString();
        }
        return null;
    }

    private JSONObject toUnderscore(JSONObject jsonObjectRecd, CaseFormat format, boolean uppercase, String... skipKeys){
        try{
            Iterator iterator = jsonObjectRecd.keys();
            JSONObject jsonObjToReturn = new JSONObject();

            ArrayList<String> skipKeysList = new ArrayList<String>(Arrays.asList(skipKeys));

            while (iterator.hasNext()) {

                String key = (String) iterator.next();
                Object value = jsonObjectRecd.get(key);

                if(skipKeysList.contains(key)){
                    jsonObjToReturn.put(key,value);
                    continue;
                }

                if (jsonObjectRecd.get(key) instanceof JSONArray) {
                    JSONArray jsonArray = jsonObjectRecd.getJSONArray(key);
                    JSONArray resultJsonArray = new JSONArray();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject objInArr = jsonArray.getJSONObject(i);
                        resultJsonArray.put(toUnderscore(objInArr,format,uppercase));
                    }
                    key = convertToUnderscore(format,key, uppercase);
                    jsonObjToReturn.put(key,resultJsonArray);
                }else{
                    key = convertToUnderscore(format,key, uppercase);
                    jsonObjToReturn.put(key, value);
                }
            }
            return jsonObjToReturn;
        }catch (JSONException e){
            log.error("Error while transforming JSON "+ e.getMessage());
            return null;
        }
    }

    private JSONObject toCamelCase(JSONObject jsonObjectRecd, CaseFormat format, boolean uppercase, String... skipKeys){
        try{
            Iterator iterator = jsonObjectRecd.keys();
            JSONObject jsonObjToReturn = new JSONObject();

            ArrayList<String> skipKeysList = new ArrayList<String>(Arrays.asList(skipKeys));

            while (iterator.hasNext()) {

                String key = (String) iterator.next();
                Object value = jsonObjectRecd.get(key);

                if(skipKeysList.contains(key)){
                    jsonObjToReturn.put(key,value);
                    continue;
                }

                if (jsonObjectRecd.get(key) instanceof JSONArray) {
                    JSONArray jsonArray = jsonObjectRecd.getJSONArray(key);
                    JSONArray resultJsonArray = new JSONArray();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject objInArr = jsonArray.getJSONObject(i);
                        resultJsonArray.put(toCamelCase(objInArr,format,uppercase));
                    }
                    key = convertToCamelCase(format,key,uppercase);
                    jsonObjToReturn.put(key,resultJsonArray);
                }else{
                    key = convertToCamelCase(format,key,uppercase);
                    jsonObjToReturn.put(key, value);
                }
            }
            return jsonObjToReturn;
        }catch (JSONException e){
            log.error("Error while transforming JSON "+e.getMessage());
            return null;
        }
    }

    private String convertToUnderscore(CaseFormat format, String s, boolean uppercase){
        if(format.equals(CaseFormat.LOWER_CAMEL)){
            if(uppercase){
                return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE,s);
            }
            return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,s);

        }else{
            if(uppercase){
                return CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE,s);
            }
            return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,s);
        }
    }

    private String convertToCamelCase(CaseFormat format, String s, boolean uppercase){
        if(format.equals(CaseFormat.LOWER_UNDERSCORE)){
            if(uppercase){
                return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL,s);
            }
            return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,s);

        }else{
            if(uppercase){
                return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL,s);
            }
            return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,s);
        }
    }

}
