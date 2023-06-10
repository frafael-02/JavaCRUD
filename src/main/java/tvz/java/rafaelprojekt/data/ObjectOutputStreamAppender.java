package tvz.java.rafaelprojekt.data;

import java.io.IOException;


import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class ObjectOutputStreamAppender extends ObjectOutputStream {

    public ObjectOutputStreamAppender(OutputStream out) throws IOException {
        super(out);
    }

    public ObjectOutputStreamAppender() throws IOException, SecurityException {
    }

    @Override
    protected void writeStreamHeader() throws IOException
    {
        reset();
    }
}
