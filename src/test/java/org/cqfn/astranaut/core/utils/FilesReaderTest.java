/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 Ivan Kniazkov
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

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test for {@link FilesReader} class.
 * @since 2.0.0
 */
class FilesReaderTest {
    @Test
    void wroteAndReadFile(@TempDir final Path tmpdir) {
        final Path path = tmpdir.resolve("data.txt");
        final FilesWriter writer = new FilesWriter(path.toString());
        final boolean flag = writer.writeStringNoExcept("test\r string");
        Assertions.assertTrue(flag);
        Assertions.assertTrue(Files.exists(path));
        final FilesReader reader = new FilesReader(path.toString());
        final String expected = "test string";
        Assertions.assertDoesNotThrow(
            () -> {
                final String result = reader.readAsString();
                Assertions.assertEquals(expected, result);
            }
        );
        Assertions.assertDoesNotThrow(
            () -> {
                final String result = reader.readAsString(CustomException::new);
                Assertions.assertEquals(expected, result);
            }
        );
    }

    @Test
    void readWithoutException() {
        final FilesReader reader = new FilesReader("path.to.file.that.does.not.exist");
        final String result = reader.readAsStringNoExcept();
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testCustomException() {
        final FilesReader reader = new FilesReader("path.to.another.file.that.does.not.exist");
        Assertions.assertThrows(
            CustomException.class,
            () -> reader.readAsString(CustomException::new)
        );
    }

    /**
     * Custom exception for testing purposes.
     * @since 2.0.0
     */
    private static class CustomException extends Exception {
        private static final long serialVersionUID = -1;
    }
}
