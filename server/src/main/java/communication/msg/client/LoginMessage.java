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

}
