package pl.joegreen.sergeants.framework.model;

import lombok.Value;
import lombok.experimental.Wither;

@Value
@Wither
public class Position {
    int row;
    int col;
}
