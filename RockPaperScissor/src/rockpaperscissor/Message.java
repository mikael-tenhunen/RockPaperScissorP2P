package rockpaperscissor;

import java.io.Serializable;

/* Message types are Strings:
* -ServerSocketAddressRequestFromListener
* -ServerSocketAddressRequestFromConnecter
* -ServerSocketAddressToListener
* -ServerSocketAddressToListenerNoList
* -ServerSocketAddressToConnecter
* -PeerServerList
* -ScoreRequest
* -Score
* -Gesture
* -DisconnectNotification
* -TextMessage
*/
public class Message implements Serializable {
    private String type; 
    private Object msgObj;
    
    //ConnectBackRequest/Ack
    public Message(String type, Object o) {
        this.type = type;
        msgObj = o;
    }
    
     /**
     * @return the type of message
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the msgObj
     */
    public Object getMsgObj() {
        return msgObj;
    }

    /**
     * @param msgObj the msgObj to set
     */
    public void setMsgObj(Object msgObj) {
        this.msgObj = msgObj;
    }
}
