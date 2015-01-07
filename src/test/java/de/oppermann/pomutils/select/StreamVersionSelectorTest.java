package de.oppermann.pomutils.select;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;

public class StreamVersionSelectorTest {
	
	@Test
	public void testEndOfStream() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ByteArrayInputStream bais = new ByteArrayInputStream(new byte[0]);
		StreamVersionSelector selector = new StreamVersionSelector(baos, bais);
		assertNull(selector.selectVersion("id", "our", "their"));
	}

	@Test
	public void testSkip() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ByteArrayInputStream bais = new ByteArrayInputStream("s\n".getBytes());
		StreamVersionSelector selector = new StreamVersionSelector(baos, bais);
		assertNull(selector.selectVersion("id", "our", "their"));
	}

	@Test
	public void testOur() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ByteArrayInputStream bais = new ByteArrayInputStream("1\n".getBytes());
		StreamVersionSelector selector = new StreamVersionSelector(baos, bais);
		assertEquals("our", selector.selectVersion("id", "our", "their"));
	}

	@Test
	public void testOurFullVersion() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ByteArrayInputStream bais = new ByteArrayInputStream("our\n".getBytes());
		StreamVersionSelector selector = new StreamVersionSelector(baos, bais);
		assertEquals("our", selector.selectVersion("id", "our", "their"));
	}

	@Test
	public void testTheir() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ByteArrayInputStream bais = new ByteArrayInputStream("2\n".getBytes());
		StreamVersionSelector selector = new StreamVersionSelector(baos, bais);
		assertEquals("their", selector.selectVersion("id", "our", "their"));
	}

	@Test
	public void testTheirFullVersion() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ByteArrayInputStream bais = new ByteArrayInputStream("their\n".getBytes());
		StreamVersionSelector selector = new StreamVersionSelector(baos, bais);
		assertEquals("their", selector.selectVersion("id", "our", "their"));
	}

	@Test
	public void testTrim() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ByteArrayInputStream bais = new ByteArrayInputStream("   2   \n".getBytes());
		StreamVersionSelector selector = new StreamVersionSelector(baos, bais);
		assertEquals("their", selector.selectVersion("id", "our", "their"));
	}

	@Test
	public void testUnknown() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ByteArrayInputStream bais = new ByteArrayInputStream("x\n2\n".getBytes());
		StreamVersionSelector selector = new StreamVersionSelector(baos, bais);
		assertEquals("their", selector.selectVersion("id", "our", "their"));
	}

}
