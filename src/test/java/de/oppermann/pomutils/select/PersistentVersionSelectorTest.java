package de.oppermann.pomutils.select;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import de.oppermann.pomutils.util.VersionFieldType;

public class PersistentVersionSelectorTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
	private File tempFile;
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@Mock
	private VersionSelector delegateSelector;

	private PersistentVersionSelector persistentSelector;
	
	@Before
	public void setup() throws IOException {
		tempFile = tempFolder.newFile();
		persistentSelector = new PersistentVersionSelector(delegateSelector, tempFile);
	}
	
	
	@Test
	public void testFileDoesNotExist() throws IOException {
		tempFile.delete();
		when(delegateSelector.selectVersion("id", VersionFieldType.PROJECT, "our", "their")).thenReturn("our");
		assertEquals("our", persistentSelector.selectVersion("id", VersionFieldType.PROJECT, "our", "their"));
		
		assertEquals("our", persistentSelector.selectVersion("id", VersionFieldType.PROJECT, "our", "their"));
		
		verify(delegateSelector).selectVersion("id", VersionFieldType.PROJECT, "our", "their");
	}
	
	@Test
	public void testSkip() throws IOException {
		assertNull(persistentSelector.selectVersion("id", VersionFieldType.PROJECT, "our", "their"));
		
		assertNull(persistentSelector.selectVersion("id", VersionFieldType.PROJECT, "our", "their"));
		
		verify(delegateSelector).selectVersion("id", VersionFieldType.PROJECT, "our", "their");
	}
	
	@Test
	public void testSelect() throws IOException {
		when(delegateSelector.selectVersion("id", VersionFieldType.PARENT, "our", "their")).thenReturn("their");
		assertEquals("their", persistentSelector.selectVersion("id", VersionFieldType.PARENT, "our", "their"));
		
		assertEquals("their", persistentSelector.selectVersion("id", VersionFieldType.PARENT, "our", "their"));
		
		verify(delegateSelector).selectVersion("id", VersionFieldType.PARENT, "our", "their");
	}
	
	@Test
	public void testNotValid() throws IOException {
		when(delegateSelector.selectVersion("id", VersionFieldType.PARENT, "our", "their")).thenReturn("their");
		when(delegateSelector.selectVersion("id", VersionFieldType.PARENT, "our2", "their2")).thenReturn("our2");
		
		assertEquals("their", persistentSelector.selectVersion("id", VersionFieldType.PARENT, "our", "their"));
		
		assertEquals("our2", persistentSelector.selectVersion("id", VersionFieldType.PARENT, "our2", "their2"));
		
		verify(delegateSelector).selectVersion("id", VersionFieldType.PARENT, "our", "their");
		verify(delegateSelector).selectVersion("id", VersionFieldType.PARENT, "our2", "their2");
	}
}
