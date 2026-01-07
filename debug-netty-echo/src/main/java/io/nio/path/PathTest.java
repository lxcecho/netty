package io.nio.path;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author lxcecho lxcecho@gmail.com
 * @since 15.09.2021
 */
@Slf4j
public class PathTest {

    @Test
    public void testPath() throws Exception {
        Path path = Paths.get("D:\\file01.txt");

        Path test = Paths.get("D:\\images", "test");

        Path file = Paths.get("D:\\images", "test\\01.txt");

        String originalPath = "d:\\test\\projects\\..\\yygh-project";

        Path path1 = Paths.get(originalPath);
        log.info("path1 = {}", path1);

        Path path2 = path1.normalize();
        log.info("path2 = {}", path2);
    }

}
