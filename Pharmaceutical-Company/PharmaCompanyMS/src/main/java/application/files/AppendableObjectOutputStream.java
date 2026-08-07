package application.files;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Special version of ObjectOutputStream that allows appending objects to an
 * existing file without rewriting the stream header.
 * <p>
 * ObjectOutputStream normally writes a header the first time it writes to a
 * stream. If you use a second ObjectOutputStream to append, it will write
 * another header and corrupt the file (ObjectInputStream will fail to read
 * after the first appended object). This class overrides writeStreamHeader
 * to call reset() instead, which clears the cached-object state but does not
 * emit a new header, so we can keep adding records to an existing .bin file.
 */
public class AppendableObjectOutputStream extends ObjectOutputStream {

    public AppendableObjectOutputStream(OutputStream out) throws IOException {
        super(out);
    }

    @Override
    protected void writeStreamHeader() throws IOException {
        // Reset the stream instead of writing a new header.
        // This preserves the existing header written by the first
        // ObjectOutputStream that created the file.
        reset();
    }
}