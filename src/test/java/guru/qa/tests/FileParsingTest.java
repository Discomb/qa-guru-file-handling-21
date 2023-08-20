package guru.qa.tests;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import com.opencsv.CSVReader;
import model.JsonExampleModel;
import org.assertj.core.api.Assertions;
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
                String ext = Files.getFileExtension(entryName);


                switch (ext) {

                    case ("csv"):
                        try (InputStream str = cl.getResourceAsStream(entryName);
                             Reader reader = new InputStreamReader(str)) {

                            CSVReader csvReader = new CSVReader(reader);
                            List<String[]> content = csvReader.readAll();

                            Assertions.assertThat(content.size()).isEqualTo(3);

                            final String[] firstRow = content.get(0);
                            final String[] secondRow = content.get(1);
                            final String[] thirdRow = content.get(2);

                            Assertions.assertThat(firstRow).isEqualTo(new String[]{"тест1", "тест2", "тест123"});
                            Assertions.assertThat(secondRow).isEqualTo(new String[]{"это", "зеленый", "тест"});
                            Assertions.assertThat(thirdRow).isEqualTo(new String[]{"тест2", "тест3", "тест666"});

                            break;
                        }

                    case ("pdf"):
                        try (InputStream str = cl.getResourceAsStream(entryName)) {
                            assert str != null;
                            PDF pdf = new PDF(str);

                            String content = pdf.text;

                            Assertions.assertThat(content).contains("Билет на каток");

                            break;
                        }

                    case ("xlsx"):
                        try (InputStream str = cl.getResourceAsStream(entryName)) {
                            assert str != null;
                            XLS xls = new XLS(str);

                            String testedCell = xls.excel.getSheetAt(2).getRow(2).getCell(1).toString();

                            Assertions.assertThat(testedCell).isEqualTo("Склад");
                            break;
                        }
                }

            }
        }
    }

    @Test
    public void checkJsonFile() throws Exception {
        try (InputStream str = cl.getResourceAsStream("json_example.json")) {

            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode json = objectMapper.readTree(str);

            Assertions.assertThat(json.get("client").get("dateOfBirth").asText())
                    .isEqualTo("12 Jan 1988");


        }
    }

    @Test
    public void inprovedCheckJsonFile() throws Exception {
        try (InputStream str = cl.getResourceAsStream("json_example.json")) {

            ObjectMapper objectMapper = new ObjectMapper();

            JsonExampleModel json = objectMapper.readValue(str, JsonExampleModel.class);

            Assertions.assertThat(json.getClient().getUuid()).isNotEmpty();
            Assertions.assertThat(json.getClient().getTitle()).isEqualTo("Mr.");
            Assertions.assertThat(json.getClient().getName()).isEqualTo("John");
            Assertions.assertThat(json.getClient().getSurname()).isEqualTo("Doe");
            Assertions.assertThat(json.getClient().getDateOfBirth()).isEqualTo("12 Jan 1988");
            Assertions.assertThat(json.getClient().getDescription()).isEqualTo("example client");
            Assertions.assertThat(json.getClient().getUserGroups()).contains("EXT", "PREMIUM");
            Assertions.assertThat(json.getClient().getAddress().getCountry()).isEqualTo("France");
            Assertions.assertThat(json.getClient().getAddress().getCity()).isEqualTo("Paris");
            Assertions.assertThat(json.getClient().getAddress().getStreet()).isEqualTo("Rue Mumia Abu-Jamal");
            Assertions.assertThat(json.getClient().getAddress().getBuilding()).isEqualTo("6");
            Assertions.assertThat(json.getClient().getAddress().getFlat()).isEqualTo("104");
        }
    }
}
