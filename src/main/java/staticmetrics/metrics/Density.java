package staticmetrics.metrics;

import com.github.javaparser.ast.body.CallableDeclaration;

import exceptions.MethodHasNoBodyException;

/**
 * Calculates the density metric of text,
 * which is 'word per line'.
 * On class level the average is calculated over the method averages, not just all words / all lines.
 * On project level the average is calculated over the class averages.
 */
public class Density extends StaticMetric {

    <T extends CallableDeclaration<T>> Density(T md) throws MethodHasNoBodyException {
        super(md, "S-Density");
    }

    protected <T extends CallableDeclaration<T>> float calculate(T md) {
        float lines = 0;
        float words = 0;
        try {
            lines = new LineCount(md).getValue();
            words = new WordCount(md).getValue();
        } catch (MethodHasNoBodyException e) {
            throw new RuntimeException("Mother class catches this case.");
        }
        if (lines == 0) return 0;
        return words / lines;
    }
}
