/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.io.input;


import java.io.IOException;
import java.io.InputStream;

/**
 * A filtering input stream that ensures the content will have unix-style line endings, LF.
 *
 * @since 2.5
 */
public class UnixLineEndingInputStream extends InputStream {

    private boolean slashNSeen = false;

    private boolean eofSeen = false;

    private final InputStream target;

    private final boolean ensureLineFeedAtEndOfFile;

    /**
     * Create an input stream that filters another stream
     *
     * @param in                        The input stream to wrap
     * @param ensureLineFeedAtEndOfFile true to ensure that the file ends with LF
     */
    public UnixLineEndingInputStream( InputStream in, boolean ensureLineFeedAtEndOfFile ) {
        this.target = in;
        this.ensureLineFeedAtEndOfFile = ensureLineFeedAtEndOfFile;
    }

    private int readWithUpdate() throws IOException {
        final int target = this.target.read();
        eofSeen = target == -1;
        if ( eofSeen ) {
            return target;
        }
        slashNSeen = target == '\n';
        return target;
    }

    /**
     * @inheritDoc
     */

    @Override
    public int read() throws IOException {
        if ( eofSeen ) {
            return eofGame();
        }
        else {
            int target = readWithUpdate();
            if ( eofSeen ) {
                return eofGame();
            }
            if ( target == '\r' ) {
                target = readWithUpdate();
            }
            return target;
        }
    }

    private int eofGame() {
        if ( !ensureLineFeedAtEndOfFile ) {
            return -1;
        }
        if ( !slashNSeen ) {
            slashNSeen = true;
            return '\n';
        } else {
            return -1;
        }
    }

    /**
     * Closes the stream. Also closes the underlying stream.
     */
    @Override
    public void close() throws IOException {
        super.close();
        target.close();
    }

    /**
     * @inheritDoc
     */
    @Override
    public synchronized void mark( int readlimit ) {
        throw new UnsupportedOperationException( "Mark notsupported" );
    }
}
