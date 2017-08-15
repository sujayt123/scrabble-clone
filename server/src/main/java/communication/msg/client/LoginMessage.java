package communication.msg.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import communication.msg.Message;

/**
 * Created by sujay on 8/14/17.
 */
public class LoginMessage extends Message {

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
        LoginMessage lm = gson.fromJson(s, getClass());
        this.username = lm.username;
        this.password = lm.password;
    }
}
