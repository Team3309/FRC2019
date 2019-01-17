package org.usfirst.frc.team3309.lib.geometry;

import org.usfirst.frc.team3309.lib.util.CSVWritable;
import org.usfirst.frc.team3309.lib.util.Interpolable;

public interface State<S> extends Interpolable<S>, CSVWritable {
    double distance(final S other);

    boolean equals(final Object other);

    String toString();

    String toCSV();
}
