package rockpaperscissor;

/* Messages are Strings.
* -ConnectBackRequest => 
* -Ack
* -
*/
public class Message {
    private String type; 
    private Object msgObj;
    
    //ConnectBackRequest/Ack
    public Message(String type, Object o) {
        this.type = type;
        msgObj = o;
    }
    
     /**
     * @return the type
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
