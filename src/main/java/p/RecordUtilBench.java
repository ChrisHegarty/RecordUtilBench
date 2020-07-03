package p;

/*
Shows not much difference between method handle and code reflection
implementations. Much of the time seems to be in the native methods
in the VM.

Benchmark                          Mode  Cnt     Score     Error  Units
RecordUtilBench.getCanonicalCtr    avgt   15  1646.658 ±  55.932  ns/op
RecordUtilBench.getCanonicalCtrMH  avgt   15  1658.783 ± 104.682  ns/op
RecordUtilBench.isRecord           avgt   15    36.056 ±   2.523  ns/op
RecordUtilBench.isRecordMH         avgt   15    37.669 ±   5.467  ns/op

 */
import java.lang.reflect.Constructor;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
@Fork(value = 3, jvmArgsAppend = "--enable-preview")
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class RecordUtilBench {

    public record R (int x, long l) { }

    @Benchmark
    public boolean isRecord() {
        return RecordUtil.isRecord(R.class);
    }

    @Benchmark
    public boolean isRecordMH() {
        return RecordUtilMH.isRecord(R.class);
    }

    @Benchmark
    public Constructor<?> getCanonicalCtr() {
        return RecordUtil.getCanonicalConstructor(R.class);
    }

    @Benchmark
    public Constructor<?> getCanonicalCtrMH() {
        return RecordUtilMH.getCanonicalConstructor(R.class);
    }
}
