package net.ion.neo.bleujin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;

import org.apache.commons.io.IOUtils;

import junit.framework.TestCase;

public class TestViewFile extends TestCase{

	public void testRead() throws Exception {
		String readed = IOUtils.toString(new InputStreamReader(new FileInputStream(new File("./resource/0000000000.graphy")), "UTF-8"));
		Debug.line(readed) ;
	}
	
}
