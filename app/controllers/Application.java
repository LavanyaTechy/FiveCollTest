package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import database.StorageException;
import play.*;
import play.mvc.*;

import views.html.*;
import models.UserHit;
import play.data.Form;
import database.java.*;
import static play.libs.Json.toJson;


public class Application extends Controller {


    // logic to wait for 10 seconds after trying 5 times
    public static boolean  insert(String value1,String value2,int counter) throws StorageException,InterruptedException{
        if(counter > 5){
            Thread.sleep(10000);
        }
        try {
            DummyDBJava.INSTANCE.put(value1, value2);
        }catch(StorageException ex){
            counter = counter+1;
            insert(value1 ,value2,counter);
        }
      return true;
    }

    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }
    public static Result displayForm() {
        return ok(hitRequest.render("Hit Request"));
    }

    public static Result registerHit() {
        UserHit user =null;
        try {
            // receiving post request
            user = Form.form(UserHit.class).bindFromRequest().get();
            try {
                String userCount = DummyDBJava.INSTANCE.get(user.getUserId());
                Integer count = new Integer(1);
                if(userCount !=null) {
                    count = new Integer(userCount);
                    count = count + 1;
                }
                    boolean saved =insert(user.getUserId(),count.toString(),1);

            } catch(StorageException storageException)
            {
                System.out.println("Storage Exception ........");


            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok(toJson(user));
    }
    public static Result findUserHits()
    {
        String userId = request().getQueryString("userId");
        String userCount = null;
        try {
            userCount = DummyDBJava.INSTANCE.get(userId);
        }catch(StorageException exception)
        {
            System.out.println("Storage Exception ......");
        }
        UserHit hits = new UserHit();
        hits.setUserId(userId);
        hits.setHits(userCount);
        return ok(toJson(hits));

    }

}
