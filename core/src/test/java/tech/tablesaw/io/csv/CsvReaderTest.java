/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.io.csv;

import org.junit.Ignore;
import org.junit.Test;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static tech.tablesaw.api.ColumnType.*;
import static tech.tablesaw.io.csv.CsvReader.*;

/**
 * Tests for CSV Reading
 */
public class CsvReaderTest {

    private final ColumnType[] bus_types = {NUMBER, STRING, STRING, NUMBER, NUMBER};
    private final ColumnType[] bus_types_with_SKIP = {NUMBER, STRING, SKIP, NUMBER, NUMBER};

    @Test
    public void testWithBusData() throws Exception {
        // Read the CSV file
        Table table = Table.read().csv(CsvReadOptions
                .builder("../data/bus_stop_test.csv")
                .columnTypes(bus_types));

        // Look at the column names
        assertEquals("[stop_id, stop_name, stop_desc, stop_lat, stop_lon]", table.columnNames().toString());

        table = table.sortDescendingOn("stop_id");
        table.removeColumns("stop_desc");
    }

    @Test
    public void testWithColumnSKIP() throws Exception {
        // Read the CSV file
        Table table = Table.read().csv(CsvReadOptions
                .builder("../data/bus_stop_test.csv")
                .columnTypes(bus_types_with_SKIP));

        assertEquals(4, table.columnCount());
        // Look at the column names
        assertEquals("[stop_id, stop_name, stop_lat, stop_lon]", table.columnNames().toString());
    }

    @Test
    public void testWithColumnSKIPWithoutHeader() throws Exception {
        // Read the CSV file
        Table table = Table.read().csv(CsvReadOptions
                .builder("../data/bus_stop_noheader_test.csv")
                .header(false)
                .columnTypes(bus_types_with_SKIP));

        assertEquals(4, table.columnCount());
        // Look at the column names
        assertEquals("[C0, C1, C3, C4]", table.columnNames().toString());
    }

    @Test
    public void testWithBushData() throws Exception {
        // Read the CSV file
        ColumnType[] types = {LOCAL_DATE, NUMBER, STRING};
        Table table = Table.read().csv(
                CsvReadOptions.builder("../data/bush.csv")
                        .columnTypes(types));

        assertEquals(323, table.rowCount());

        // Look at the column names
        assertEquals("[date, approval, who]", table.columnNames().toString());
    }

    @Test
    public void testBushDataWithoutSamplingForTypeDetection() throws Exception {
        // Read the CSV file
        Table table = Table.read().csv(CsvReadOptions
                .builder("../data/bush.csv")
                .sample(false));

        assertEquals(323, table.rowCount());

        // Look at the column names
        assertEquals("[date, approval, who]", table.columnNames().toString());
    }


    @Test
    public void testDataTypeDetection() throws Exception {
        InputStream stream = new FileInputStream(new File("../data/bus_stop_test.csv"));
        CsvReadOptions options = CsvReadOptions.builder(stream, "")
                .header(true)
                .separator(',')
                .sample(false)
                .locale(Locale.getDefault())
                .build();

        ColumnType[] columnTypes = detectColumnTypes(stream, options);
        assertTrue(Arrays.equals(bus_types, columnTypes));
    }

    @Test
    public void testMillis() {

        long[] times = {1530486314124L, 1530488214124L};
        DoubleColumn d = DoubleColumn.create("times", times);
        DateTimeColumn column = d.asDateTimes(ZoneOffset.UTC);
        assertEquals(1530486314124L, column.get(0).toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    @Test
    public void testLocalDateDetectionEnglish() throws Exception {

        final InputStream stream = new ByteArrayInputStream((
                "Date\n"
            + "\"Nov 1, 2017\"\n"
            + "\"Oct 1, 2017\"\n"
            + "\"Sep 1, 2017\"\n"
            + "\"Aug 1, 2017\"\n"
            + "\"Jul 1, 2017\"\n"
            + "\"Jun 1, 2017\"\n").getBytes());

        final boolean header = true;
        final char delimiter = ',';
        final boolean useSampling = true;

        CsvReadOptions options = CsvReadOptions.builder(stream, "")
                .header(header)
                .separator(delimiter)
                .sample(useSampling)
                .locale(Locale.ENGLISH)
                .build();

        final List<ColumnType> actual = asList(detectColumnTypes(stream, options));

        assertThat(actual, is(equalTo(Collections.singletonList(LOCAL_DATE))));
    }
    
    @Test
    public void testLocalDateTimeDetectionEnglish() throws Exception {

        final InputStream stream = new ByteArrayInputStream((
              "Date\n"
            + "09-Nov-2014 13:03\n"
            + "09-Oct-2014 13:03\n"
            + "09-Sep-2014 13:03\n"
            + "09-Aug-2014 13:03\n"
            + "09-Jul-2014 13:03\n"
            + "09-Jun-2014 13:03\n").getBytes());

        final boolean header = true;
        final char delimiter = ',';
        final boolean useSampling = true;

        CsvReadOptions options = CsvReadOptions.builder(stream, "")
                .header(header)
                .separator(delimiter)
                .sample(useSampling)
                .locale(Locale.ENGLISH)
                .build();

        final List<ColumnType> actual = asList(detectColumnTypes(stream, options));

        assertThat(actual, is(equalTo(Collections.singletonList(LOCAL_DATE_TIME))));

    }

    @Test
    public void testLocalDateDetectionFrench() throws Exception {

        final InputStream stream = new ByteArrayInputStream((
                "Date\n"
            + "\"nov. 1, 2017\"\n"
            + "\"oct. 1, 2017\"\n"
            + "\"sept. 1, 2017\"\n"
            + "\"août 1, 2017\"\n"
            + "\"juil. 1, 2017\"\n"
            + "\"juin 1, 2017\"\n").getBytes());
        
        final boolean header = true;
        final char delimiter = ',';
        final boolean useSampling = true;

        CsvReadOptions options = CsvReadOptions.builder(stream, "")
                .header(header)
                .separator(delimiter)
                .sample(useSampling)
                .locale(Locale.FRENCH)
                .build();

        final List<ColumnType> actual = asList(detectColumnTypes(stream, options));

        assertThat(actual, is(equalTo(Collections.singletonList(LOCAL_DATE))));
    }

    @Test
    public void testLocalDateTimeDetectionFrench() throws Exception {

        final InputStream stream = new ByteArrayInputStream((
              "Date\n"
            + "09-nov.-2014 13:03\n"
            + "09-oct.-2014 13:03\n"
            + "09-sept.-2014 13:03\n"
            + "09-août-2014 13:03\n"
            + "09-juil.-2014 13:03\n"
            + "09-juin-2014 13:03\n").getBytes());

        final boolean header = true;
        final char delimiter = ',';
        final boolean useSampling = true;

        CsvReadOptions options = CsvReadOptions.builder(stream, "")
                .header(header)
                .separator(delimiter)
                .sample(useSampling)
                .locale(Locale.FRENCH)
                .build();

        final List<ColumnType> actual = asList(detectColumnTypes(stream, options));

        assertThat(actual, is(equalTo(Collections.singletonList(LOCAL_DATE_TIME))));
    }

    @Test
    public void testDateWithFormatter() throws Exception {

        final InputStream stream = new ByteArrayInputStream((
              "Date\n"
            + "2014.10.03\n"
            + "2014.07.04\n"
            + "2014.11.23\n"
            + "2014.12.03\n").getBytes());

        final boolean header = true;
        final char delimiter = ',';
        final boolean useSampling = true;

        CsvReadOptions options = CsvReadOptions.builder(stream, "")
                .header(header)
                .separator(delimiter)
                .sample(useSampling)
                .dateFormat("yyyy.MM.dd")
                .build();

        final List<ColumnType> actual = asList(detectColumnTypes(stream, options));
        assertThat(actual, is(equalTo(Collections.singletonList(LOCAL_DATE))));
    }

    @Test
    public void testDateWithFormatter2() throws Exception {

        final boolean header = false;
        final char delimiter = ',';
        final boolean useSampling = true;

        CsvReadOptions options = CsvReadOptions.builder("../data/date_format_test.txt")
                .header(header)
                .separator(delimiter)
                .sample(useSampling)
                .dateFormat("yyyy.MM.dd")
                .build();

        final Table table = Table.read().csv(options);
        DateColumn date = table.dateColumn(0);
        assertNotNull(date.firstElement());
    }

    @Test
    public void testPrintStructure() throws Exception {
        String output =
                "ColumnType[] columnTypes = {\n" +
                        "LOCAL_DATE, // 0     date        \n" +
                        "NUMBER,     // 1     approval    \n" +
                        "STRING,     // 2     who         \n" +
                        "}\n";
        assertEquals(output, CsvReader.printColumnTypes("../data/bush.csv", true, ',', Locale.getDefault()));
    }

    @Test
    public void testDataTypeDetection2() throws Exception {
        InputStream stream = new FileInputStream(new File("../data/bush.csv"));
        CsvReadOptions options = CsvReadOptions.builder(stream, "")
                .header(true)
                .separator(',')
                .sample(false)
                .locale(Locale.getDefault())
                .build();

        ColumnType[] columnTypes = detectColumnTypes(stream, options);
        assertEquals(LOCAL_DATE, columnTypes[0]);
        assertEquals(NUMBER, columnTypes[1]);
        assertEquals(STRING, columnTypes[2]);
    }

    @Ignore
    @Test
    public void testLoadFromUrl() throws Exception {
        ColumnType[] types = {LOCAL_DATE, NUMBER, STRING};
        String location = "https://raw.githubusercontent.com/jAirframe/Airframe/master/data/bush.csv";
        Table table;
        try (InputStream input = new URL(location).openStream()) {
            table = Table.read().csv(CsvReadOptions
                    .builder(input, "Bush approval ratings")
                    .columnTypes(types));
        }
        assertNotNull(table);
    }

    @Test
    public void testBoundary1() throws Exception {
        Table table1 = Table.read().csv("../data/boundaryTest1.csv");
        table1.structure();  // just make sure the import completed
    }

    @Test
    public void testBoundary2() throws Exception {
        Table table1 = Table.read().csv("../data/boundaryTest2.csv");
        table1.structure(); // just make sure the import completed
    }

    @Test
    public void testReadFailure() throws Exception {
        Table table1 = Table.read().csv("../data/read_failure_test.csv");
        table1.structure(); // just make sure the import completed
        NumberColumn test = table1.numberColumn("Test");
        //TODO(lwhite): Better tests
        assertNotNull(test.summary());
    }

    @Test
    public void testReadFailure2() throws Exception {
        Table table1 = Table.read().csv("../data/read_failure_test2.csv");
        table1.structure(); // just make sure the import completed
        NumberColumn test = table1.numberColumn("Test");

        //TODO(lwhite): Better tests
        assertNotNull(test.summary());
    }

    @Test
    public void testEmptyFileHeaderEnabled() throws Exception {
        Table table1 = Table.read().csv(CsvReadOptions
                .builder("../data/empty_file.csv")
                .header(true));
        assertEquals("0 rows X 0 cols", table1.shape());
    }

    @Test
    public void testEmptyFileHeaderDisabled() throws Exception {
        Table table1 = Table.read().csv(CsvReadOptions
                .builder("../data/empty_file.csv")
                .header(false));
        assertEquals("0 rows X 0 cols", table1.shape());
    }

}
