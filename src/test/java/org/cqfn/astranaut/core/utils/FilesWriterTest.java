/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Ivan Kniazkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cqfn.astranaut.core.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Locale;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test for {@link FilesWriter} class.
 * @since 2.0.0
 */
class FilesWriterTest {
    @Test
    void testWriteStringNoExceptSuccess(@TempDir final Path tmpdir) {
        final Path path = tmpdir.resolve("testFile.txt");
        final FilesWriter writer = new FilesWriter(path.toString());
        final boolean result = writer.writeStringNoExcept("Hello, World!");
        Assertions.assertTrue(result);
        Assertions.assertTrue(Files.exists(path));
    }

    @Test
    void testWriteStringNoExceptFailure(@TempDir final Path tmpdir) {
        boolean oops = false;
        try {
            final Path path = tmpdir.resolve("protectedFile.txt");
            FilesWriter writer = new FilesWriter(path.toString());
            writer.writeString("Initial content");
            if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("win")) {
                Files.setAttribute(path, "dos:readonly", true);
            } else {
                final Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(path);
                permissions.remove(PosixFilePermission.OWNER_WRITE);
                Files.setPosixFilePermissions(path, permissions);
            }
            writer = new FilesWriter(path.toString());
            final boolean result = writer.writeStringNoExcept("New content");
            if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("win")) {
                Files.setAttribute(path, "dos:readonly", false);
            } else {
                final Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(path);
                permissions.add(PosixFilePermission.OWNER_WRITE);
                Files.setPosixFilePermissions(path, permissions);
            }
            Assertions.assertFalse(result);
        } catch (final IOException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }
}
