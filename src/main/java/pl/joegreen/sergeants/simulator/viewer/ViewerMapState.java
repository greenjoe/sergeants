package pl.joegreen.sergeants.simulator.viewer;

import lombok.Value;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Value
public class ViewerMapState {
    int tick;
    int height;
    int width;
    Set<ViewerField> fields;

    public String serializeToString(){
        StringBuilder builder = new StringBuilder();
        builder.append(tick);
        builder.append(";").append(height).append(";").append(width).append(";");
        Comparator<ViewerField> orderComparator = Comparator.<ViewerField, Integer>comparing(vf -> vf.getPosition().getRow()).thenComparing(vf -> vf.getPosition().getCol());
        String fields = this.fields.stream().sorted(orderComparator).map(ViewerField::serializeToString).collect(Collectors.joining("|"));
        builder.append(fields);
        return builder.toString();
    }
}
