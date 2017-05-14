package messages;

import java.io.Serializable;

/**
 * Created by dtkmn on 1/04/2017.
 */
public class SuspendRouteRequest implements Serializable{

    private String component;

    public SuspendRouteRequest(String component) {
        this.component = component;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

}
