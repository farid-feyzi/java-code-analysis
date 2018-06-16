package staticmetrics.metrics;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithOptionalBlockStmt;

import staticmetrics.MethodHasNoBodyException;
// TODO: Refactor to StaticMethodResult, add StaticFileResult, add Method Count, File size, number of constants etc.
// Fix deprecated with java 8 streams

public class StaticResult {

    private StaticMetric[] staticMetrics;
    private List<String> values;
    private List<String> header;

    public <T extends CallableDeclaration<T> & NodeWithOptionalBlockStmt<T>> StaticResult(File file,  T md) throws MethodHasNoBodyException {
        this.staticMetrics = new StaticMetric[] {
            new WordCount(md),
            new LineCount(md),
            new Density(md),
            new ParameterCount(md),
            new FunctionCallCount(md),
            new VariableCount(md),
            new RepeatedWords(md),
            new CommentPercentage(md),
            new MaxDepth(md),
            new BlockCount(md),
            new CyclomaticComplexity(md)
        };

        // Construct header record
        List<String> metricHeader = Arrays.stream(this.staticMetrics)
                                    .map(metric -> metric.getTag())
                                    .collect(Collectors.toList());
        this.header = new LinkedList<>();
        this.header.add("File");
        this.header.add("Method");
        this.header.addAll(metricHeader);

        // Construct value record
        List<String> metricValues = Arrays.stream(this.staticMetrics)
                                    .map(value -> String.valueOf(value.getValue()))
                                    .collect(Collectors.toList());
        this.values = new LinkedList<String>() {
            {
                add(file.getPath());
                add(cleanSignature(cd));
                addAll(metricValues);
            }
        };
    }

    public List<String> getValues() {
        return this.values;

    }
    public List<String> getHeader() {
        return this.header;
    }

    private static String cleanSignature(CallableDeclaration<?> cd) {
        return cd.getSignature().asString()
               .replaceAll(",", "|")
               .replaceAll(" ", "");
    }

    public String toString() {
        return Arrays.stream(this.staticMetrics).map(
                   metric -> metric.toString() + "\n"
               ).reduce("", String::concat);
    }
}
