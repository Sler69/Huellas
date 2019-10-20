package com.example.huellas.models;

import org.json.JSONObject;
import org.json.JSONArray;

public class JsonMinutiasParser {

    private boolean success;
    private Minutia[] minutias;
    private String lastError;

    public JsonMinutiasParser(String jsonResult){
        try{
            JSONArray json = new JSONArray(jsonResult);
            parseToMinutias(json);
            this.success = true;
        }
        catch(Exception e){
            this.success = false;
            this.lastError = e.toString();
        }

    }

    private void parseToMinutias (JSONArray json) throws Exception{
        if (json != null && json.length() >0){
            this.minutias = new Minutia[json.length()];
            for (int i = 0; i < json.length(); i++) {
                JSONObject object = json.getJSONObject(i);
                int x = object.getInt("X");
                int y = object.getInt("Y");
                double angle = object.getDouble("Angle");
                int minutiaType = object.getInt("MinutiaType");
                boolean flag = object.getBoolean("Flag");
                Minutia minutia = new Minutia(x,y,angle,minutiaType,flag);
                this.minutias[i] = minutia;

            }
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public Minutia[] getMinutias() {
        return minutias;
    }

    public String getLastError() {
        return lastError;
    }
}
/* ------Ejemplo para el uso de esta clase
String jsonString = "[{\"X\":71,\"Y\":581,\"Angle\":3.9916706657376197,\"MinutiaType\":1,\"Flag\":false},{\"X\":34,\"Y\":626,\"Angle\":4.1148703776431024,\"MinutiaType\":1,\"Flag\":false},{\"X\":58,\"Y\":606,\"Angle\":4.3119899166918731,\"MinutiaType\":1,\"Flag\":false},{\"X\":71,\"Y\":483,\"Angle\":1.7247959666767496,\"MinutiaType\":1,\"Flag\":false},{\"X\":317,\"Y\":15,\"Angle\":3.1292726823992449,\"MinutiaType\":2,\"Flag\":false},{\"X\":14,\"Y\":563,\"Angle\":4.4351896285973549,\"MinutiaType\":2,\"Flag\":false},{\"X\":87,\"Y\":352,\"Angle\":1.897275563344424,\"MinutiaType\":1,\"Flag\":false},{\"X\":23,\"Y\":329,\"Angle\":4.9279884762192836,\"MinutiaType\":1,\"Flag\":false},{\"X\":204,\"Y\":468,\"Angle\":1.7740758514389423,\"MinutiaType\":2,\"Flag\":false},{\"X\":55,\"Y\":299,\"Angle\":3.8931108962132339,\"MinutiaType\":1,\"Flag\":false},{\"X\":35,\"Y\":204,\"Angle\":0.443518962859736,\"MinutiaType\":1,\"Flag\":false},{\"X\":100,\"Y\":494,\"Angle\":1.3551968309603026,\"MinutiaType\":2,\"Flag\":false},{\"X\":659,\"Y\":702,\"Angle\":1.5523163700090743,\"MinutiaType\":1,\"Flag\":false},{\"X\":102,\"Y\":555,\"Angle\":4.8787085914570909,\"MinutiaType\":2,\"Flag\":false},{\"X\":74,\"Y\":604,\"Angle\":5.29758761193573,\"MinutiaType\":1,\"Flag\":false},{\"X\":89,\"Y\":488,\"Angle\":4.4844695133595476,\"MinutiaType\":1,\"Flag\":false},{\"X\":109,\"Y\":356,\"Angle\":1.675516081914556,\"MinutiaType\":1,\"Flag\":false},{\"X\":125,\"Y\":518,\"Angle\":1.7740758514389423,\"MinutiaType\":1,\"Flag\":false},{\"X\":30,\"Y\":370,\"Angle\":5.6671867476521758,\"MinutiaType\":2,\"Flag\":false},{\"X\":100,\"Y\":515,\"Angle\":4.4844695133595476,\"MinutiaType\":1,\"Flag\":false},{\"X\":70,\"Y\":566,\"Angle\":4.2873499743107768,\"MinutiaType\":1,\"Flag\":false},{\"X\":81,\"Y\":467,\"Angle\":1.4044767157224953,\"MinutiaType\":2,\"Flag\":false},{\"X\":77,\"Y\":617,\"Angle\":2.587193950015124,\"MinutiaType\":1,\"Flag\":false},{\"X\":82,\"Y\":536,\"Angle\":1.158077291911531,\"MinutiaType\":2,\"Flag\":false},{\"X\":49,\"Y\":358,\"Angle\":2.4886341804907381,\"MinutiaType\":1,\"Flag\":false},{\"X\":160,\"Y\":492,\"Angle\":1.9958353328688103,\"MinutiaType\":2,\"Flag\":false},{\"X\":120,\"Y\":547,\"Angle\":4.8787085914570909,\"MinutiaType\":1,\"Flag\":false},{\"X\":22,\"Y\":260,\"Angle\":1.1087974071493383,\"MinutiaType\":1,\"Flag\":false},{\"X\":25,\"Y\":701,\"Angle\":5.4700672086034041,\"MinutiaType\":1,\"Flag\":false},{\"X\":43,\"Y\":341,\"Angle\":2.4393542957285455,\"MinutiaType\":1,\"Flag\":false},{\"X\":128,\"Y\":571,\"Angle\":4.9279884762192836,\"MinutiaType\":2,\"Flag\":false},{\"X\":105,\"Y\":245,\"Angle\":3.8438310114510408,\"MinutiaType\":1,\"Flag\":false},{\"X\":38,\"Y\":284,\"Angle\":0.68991838667069949,\"MinutiaType\":2,\"Flag\":false},{\"X\":82,\"Y\":380,\"Angle\":5.100468072886958,\"MinutiaType\":2,\"Flag\":false},{\"X\":48,\"Y\":338,\"Angle\":4.6076692252650293,\"MinutiaType\":1,\"Flag\":false},{\"X\":96,\"Y\":345,\"Angle\":4.6076692252650293,\"MinutiaType\":2,\"Flag\":false},{\"X\":109,\"Y\":226,\"Angle\":4.1395103200241987,\"MinutiaType\":2,\"Flag\":false},{\"X\":68,\"Y\":357,\"Angle\":2.045115217631003,\"MinutiaType\":1,\"Flag\":false},{\"X\":49,\"Y\":622,\"Angle\":0.098559769524385388,\"MinutiaType\":2,\"Flag\":false},{\"X\":118,\"Y\":518,\"Angle\":4.9279884762192836,\"MinutiaType\":1,\"Flag\":false},{\"X\":103,\"Y\":390,\"Angle\":2.1190350447742921,\"MinutiaType\":2,\"Flag\":false},{\"X\":76,\"Y\":396,\"Angle\":1.8479956785822313,\"MinutiaType\":1,\"Flag\":false},{\"X\":63,\"Y\":387,\"Angle\":1.0348775800060492,\"MinutiaType\":2,\"Flag\":false},{\"X\":271,\"Y\":15,\"Angle\":6.2831853071795862,\"MinutiaType\":2,\"Flag\":false},{\"X\":112,\"Y\":548,\"Angle\":1.675516081914556,\"MinutiaType\":2,\"Flag\":false},{\"X\":106,\"Y\":600,\"Angle\":4.7801488219327046,\"MinutiaType\":2,\"Flag\":false},{\"X\":23,\"Y\":588,\"Angle\":1.2319971190548209,\"MinutiaType\":1,\"Flag\":false},{\"X\":107,\"Y\":435,\"Angle\":5.2729476695546333,\"MinutiaType\":2,\"Flag\":false},{\"X\":84,\"Y\":618,\"Angle\":5.1497479576491507,\"MinutiaType\":1,\"Flag\":false}]";
JsonMinutiasParser parser = new JsonMinutiasParser(jsonString);
if(parser.isSuccess()) {
    Minutia[] minutias = parser.getMinutias();
    for (Minutia minutia : minutias) {
        System.out.println("X: " + minutia.getX() +
        " ,Y: " + minutia.getY() +
        " ,Angle: " + minutia.getAngle() +
        " ,Minutia Type: " + minutia.getMinutiaType() +
        " ,Flag: " + minutia.getFlag());
    }
}
else{
    System.out.println("Error: " + parser.getLastError());
}
-------Termina ejemplo*/

