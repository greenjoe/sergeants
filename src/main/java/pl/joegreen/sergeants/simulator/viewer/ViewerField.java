package pl.joegreen.sergeants.simulator.viewer;

import lombok.Value;
import pl.joegreen.sergeants.framework.model.Position;

@Value
public class ViewerField {
    Position position;
    Integer owner;
    int army;
    boolean city;
    boolean general;
    boolean mountain;

    public String serializeToString() {
        StringBuilder builder = new StringBuilder();
        if (army > 0) {
            builder.append(army);
        }
        if (city) {
            builder.append("c");
        }
        if (general) {
            builder.append("g");
        }
        if (mountain) {
            builder.append("m");
        }
        if (owner != null) {
            builder.append("o").append(owner);
        }
        return builder.toString();
    }
}
