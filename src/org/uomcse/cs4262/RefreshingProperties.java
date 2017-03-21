package org.uomcse.cs4262;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by sathya on 1/29/17.
 */
class RefreshingProperties extends Properties {

    private final File file;

    public RefreshingProperties (File file) throws IOException {
        this.file = file;
        refresh ();
    }

    private void refresh () throws IOException {
        load(new FileInputStream(file));
    }

    @Override
    public String getProperty (String name) {
        try { refresh (); }
        catch (IOException e) {}
        return (String)super.get(name);
    }
}
