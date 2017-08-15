package communication.msg.client;

import com.google.gson.JsonElement;
import communication.msg.Message;
import com.google.gson.Gson;

/**
 * Created by sujay on 8/14/17.
 */
public class CreateAccountMessage extends Message {

    private String username;
    private String password;

    @Override
    public String toJson() {
        // Assuming the connection (and therefore the serialized JSON) is encrypted, we can keep the user data as plain-text.
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(this);
        jsonElement.getAsJsonObject().addProperty("type", getClass().toString());
        return gson.toJson(jsonElement);
    }

    @Override
    public void consumeJson(String s) {
        Gson gson = new Gson();
        CreateAccountMessage cam = gson.fromJson(s, getClass());
        this.username = cam.username;
        this.password = cam.password;
    }
}
