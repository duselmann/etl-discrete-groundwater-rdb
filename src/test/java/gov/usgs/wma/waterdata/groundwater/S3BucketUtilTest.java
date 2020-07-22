package gov.usgs.wma.waterdata.groundwater;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class S3BucketUtilTest {

	Properties properties;
	S3BucketUtil s3util;
	String suffix;
	String metadata;

	Pattern fileTimestamp = Pattern.compile("^ts\\w{2,4}.WI\\.gw_lev_01\\.06\\.\\d{8}_\\d{6}\\.full$");

	@BeforeEach
	public void setup() {
		properties = new Properties();
		properties.setRegion("JUNIT-WEST");
		properties.setBucket("JUNIT-S3");
		properties.setTier("TEST"); // TEST, QA, and PROD-INTERNAL

		s3util = new S3BucketUtil(properties);

		metadata = "some-date-metadata";
		suffix = "WI";
	}


	@Test
	void testFilenameTEST() throws Exception {
		// SETUP
		String expectedStartWith = "tstest";

		// ACTION UNDER TEST
		String filename = s3util.createFilename(suffix);
		System.out.println(filename);
		// ASSERTIONS
		assertNotNull(filename);
		assertTrue(filename.startsWith(expectedStartWith));
		assertTrue(fileTimestamp.matcher(filename).matches());
	}
	@Test
	void testFilenameQA() throws Exception {
		// SETUP
		properties.tier = "QA";
		String expectedStartWith = "tsqa";

		// ACTION UNDER TEST
		String filename = s3util.createFilename(suffix);

		// ASSERTIONS
		assertNotNull(filename);
		assertTrue(filename.startsWith(expectedStartWith));
		assertTrue(fileTimestamp.matcher(filename).matches());
	}
	@Test
	void testFilenamePROD() throws Exception {
		// SETUP
		properties.tier = "PROD-INTERNAL";
		String expectedStartWith = "tspr";

		// ACTION UNDER TEST
		String filename = s3util.createFilename(suffix);

		// ASSERTIONS
		assertNotNull(filename);
		assertTrue(filename.startsWith(expectedStartWith));
		assertTrue(fileTimestamp.matcher(filename).matches());
	}

	@Test
	void testOpenS3File() throws Exception {
		// SETUP
		String filename = "test-filename";

		// ACTION UNDER TEST
		S3Bucket s3 = s3util.openS3(filename);

		try {
			// ASSERTIONS
			assertNotNull(s3);
			assertEquals(properties.bucket, s3.bucket);
			assertEquals(properties.region, s3.region);
			assertEquals(filename, s3.keyName);
			assertNotNull(s3.file);
			String tmpPath = s3.file.getAbsolutePath().toLowerCase();
			assertTrue(tmpPath.contains("tmp") || tmpPath.contains("temp"));

			assertEquals(true, s3.disposeFile); // default state is to dispose of the tmp file
			assertNull(s3.writer); // initial state is without a writer
		} finally {
			// CLEANUP
			if (s3.file != null) {
				s3.file.delete();
			}
		}
	}

	@Test
	void testSimpleDate() throws Exception {
		LocalDateTime localDT = LocalDateTime.of(2000, 11, 22, 02, 03, 04);
		Date datetime = Timestamp.valueOf(localDT);
		String actual = new SimpleDateFormat("YYYYMMdd_HHmmss").format(datetime);
		assertEquals("20001122_020304", actual);
	}
}