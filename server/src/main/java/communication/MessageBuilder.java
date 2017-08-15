package communication;

import communication.msg.Message;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

/**
 * Builds the appropriate Message instance from a provided stringified-JSON.
 *
 * Created by sujay on 8/14/17.
 */
public class MessageBuilder {

    public static Optional<Message> build(String s) {
        // Parse the stringified-json into a temporary javax.json object...
        JsonObject jsonObject = Json
                .createReader(new StringReader(s)).readObject();
        // ... so that you can determine the class type.
        String classType = jsonObject.getString("type");

        /*
         * Once you know the class type, use Gson in conjunction
         * with java reflection to build the object in question.
         */

        Class myClass;
        Class[] types = {};
        Constructor constructor;
        Object[] parameters = {};
        Message instanceOfMyClass;

        try {
            myClass = Class.forName(classType);
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }

        try {
            constructor = myClass.getConstructor(types);
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }

        try {
            instanceOfMyClass = (Message) constructor.newInstance(parameters);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return Optional.empty();
        }

        // Invoke the message's consumeJson method to update its contents.
        return Optional.of(instanceOfMyClass.consumeJson(s));
    }

}
