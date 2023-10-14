package cli.rabbit.client.rabbitclient.logger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;

public class ConditionalPatternLayoutEncoder extends LayoutWrappingEncoder<ILoggingEvent> {
    private String errorPattern;
    private String pattern;
    protected Layout<ILoggingEvent> errorLayout;

    @Override
    public void start() {
        PatternLayout errorPatternLayout = this.constructPatternLayout(getErrorPattern());
        PatternLayout patternLayout = this.constructPatternLayout(getPattern());
        this.errorLayout = errorPatternLayout;
        this.layout=patternLayout;
        super.start();
    }
    private PatternLayout constructPatternLayout(String pattern){
        PatternLayout patternLayout = new PatternLayout();
        patternLayout.setContext(context);
        patternLayout.setPattern(pattern);
        patternLayout.setOutputPatternAsHeader(false);
        patternLayout.start();
        return patternLayout;
    }

    @Override
    public byte[] encode(ILoggingEvent event) {
        if (event.getLevel().isGreaterOrEqual(Level.ERROR)){
            String txt = this.errorLayout.doLayout(event);
            return this.convertToBytes(txt);
        } else {
            String txt = this.layout.doLayout(event);
            return this.convertToBytes(txt);
        }
    }
    private byte[] convertToBytes(String s) {
        if (getCharset() == null) {
            return s.getBytes();
        } else {
            return s.getBytes(getCharset());
        }
    }


    public String getErrorPattern() {
        return errorPattern;
    }

    public void setErrorPattern(String errorPattern) {
        this.errorPattern = errorPattern;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
