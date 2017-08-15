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

}
