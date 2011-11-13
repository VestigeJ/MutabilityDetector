package org.mutabilitydetector.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mutabilitydetector.AnalysisResult.analysisResult;
import static org.mutabilitydetector.MutabilityReason.MUTABLE_TYPE_TO_FIELD;
import static org.mutabilitydetector.MutabilityReason.NOT_DECLARED_FINAL;
import static org.mutabilitydetector.TestUtil.unusedMutableReasonDetails;
import static org.mutabilitydetector.locations.ClassLocation.fromInternalName;
import static org.mutabilitydetector.locations.ClassLocation.fromSlashed;
import static org.mutabilitydetector.locations.Slashed.slashed;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.mutabilitydetector.AnalysisResult;
import org.mutabilitydetector.MutableReasonDetail;
import org.mutabilitydetector.IAnalysisSession;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.cli.CommandLineOptions.ReportMode;
import org.mutabilitydetector.locations.FieldLocation;

public class SessionResultsFormatterTest {

    private final ClassListReaderFactory unusedReaderFactory = null;

    @Test
    public void printsReadableMessage() throws Exception {
        BatchAnalysisOptions options = mock(BatchAnalysisOptions.class);
        when(options.reportMode()).thenReturn(ReportMode.ALL);
        when(options.isUsingClassList()).thenReturn(false);

        IAnalysisSession analysisSession = mock(IAnalysisSession.class);
        Collection<AnalysisResult> analysisResults = Arrays.asList(analysisResult("a.b.c",
                                                                                  IsImmutable.IMMUTABLE,
                                                                                  unusedMutableReasonDetails()),
                                                                   analysisResult("d.e.f",
                                                                                  IsImmutable.EFFECTIVELY_IMMUTABLE,
                                                                                  unusedMutableReasonDetails()),
                                                                   analysisResult("g.h.i",
                                                                                  IsImmutable.NOT_IMMUTABLE,
                                                                                  unusedMutableReasonDetails()));
        when(analysisSession.getResults()).thenReturn(analysisResults);

        SessionResultsFormatter formatter = new SessionResultsFormatter(options, unusedReaderFactory);

        StringBuilder result = formatter.format(analysisSession);

        assertThat(result.toString(),
                   allOf(containsString("a.b.c is IMMUTABLE\n"),
                         containsString("d.e.f is EFFECTIVELY_IMMUTABLE\n"),
                         containsString("g.h.i is NOT_IMMUTABLE\n")));
    }

    @Test
    public void verboseOutputIncludesDetailedReasonAndPrettyPrintedCodeLocation() throws Exception {
        Collection<MutableReasonDetail> reasons = Arrays.asList(new MutableReasonDetail("1st checker reason message",
                                                                                        fromSlashed(slashed("path/to/MyClass")),
                                                                                        NOT_DECLARED_FINAL),
                                                                new MutableReasonDetail("2nd checker reason message",
                                                                                        FieldLocation.fieldLocation("myField",
                                                                                                                    fromInternalName("path/to/OtherClass")),
                                                                                        MUTABLE_TYPE_TO_FIELD));

        BatchAnalysisOptions options = mock(BatchAnalysisOptions.class);
        when(options.reportMode()).thenReturn(ReportMode.ALL);
        when(options.isUsingClassList()).thenReturn(false);
        when(options.verbose()).thenReturn(true);

        IAnalysisSession analysisSession = mock(IAnalysisSession.class);
        Collection<AnalysisResult> analysisResults = Arrays.asList(analysisResult("a.b.c", IsImmutable.NOT_IMMUTABLE, reasons));
        when(analysisSession.getResults()).thenReturn(analysisResults);

        SessionResultsFormatter formatter = new SessionResultsFormatter(options, unusedReaderFactory);

        StringBuilder result = formatter.format(analysisSession);

        assertThat(result.toString(), containsString("a.b.c is NOT_IMMUTABLE\n" + "\t1st checker reason message [Class: path.to.MyClass]\n"
                + "\t2nd checker reason message [Field: myField, Class: path.to.OtherClass]\n"));
    }
}
