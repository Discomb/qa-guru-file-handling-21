package guru.qa.tests;

import com.codeborne.pdftest.PDF;
import com.opencsv.CSVReader;
import guru.qa.utils.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileParsingTest {

    private final ClassLoader cl = FileParsingTest.class.getClassLoader();

    @Test
    public void checkZipFile() throws Exception {
        try (InputStream stream = cl.getResourceAsStream("zip_example.zip");
             ZipInputStream zis = new ZipInputStream(stream)) {

            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName();
                String ext = Utils.getFileExtension(entryName);


                switch (ext) {
                    case ("csv"):
                        try (InputStream str = cl.getResourceAsStream(entryName);
                             Reader reader = new InputStreamReader(str)) {

                            CSVReader csvReader = new CSVReader(reader);
                            List<String[]> content = csvReader.readAll();

                            Assertions.assertEquals(3, content.size());

                            final String[] firstRow = content.get(0);
                            final String[] secondRow = content.get(1);
                            final String[] thirdRow = content.get(2);

                            Assertions.assertArrayEquals(new String[]{"тест1", "тест2", "тест123"}, firstRow);
                            Assertions.assertArrayEquals(new String[]{"это", "зеленый", "тест"}, secondRow);
                            Assertions.assertArrayEquals(new String[]{"тест2", "тест3", "тест666"}, thirdRow);

                            break;
                        }

                    case ("pdf"):
                        try (InputStream str = cl.getResourceAsStream(entryName)) {
                            assert str != null;
                            PDF pdf = new PDF(str);

                            String content = pdf.text;

                            Assertions.assertTrue(content.contains("Билет на каток"));

                            break;
                        }

                    case ("xlsx"):
                        System.out.println(ext + " - это мы в кейсе эксель");
                        break;

                }
            }

        }
    }
}
