package javaplay.redblacktree;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javaplay.linesplitter.LineSplitter;

public class ReadTree {
	public static void main(String args[]) {
		InputStreamReader isr = null;
		if (args.length == 0 || args[0].equals("-")) {
			isr = new InputStreamReader(System.in);
		} else {
			File f = new File(args[0]);
			if (!f.exists() || !f.isFile()) {
				System.err.printf("File '%s' does not exist or is not a file\n", args[0]);
				System.exit(1);
			}
			try {
				FileInputStream fis = new FileInputStream(f);
				isr = new InputStreamReader(fis);
			} catch (IOException ioe) {
				ioe.printStackTrace();
				System.exit(2);
			}
		}
		
		boolean bufferedLog = true;
		if (args.length > 1) {
			if (args[1].equals("nobuffer")) {
				bufferedLog = false;
			}
		}
		
		BufferedReader br = new BufferedReader(isr);
		PrintWriter pr = new PrintWriter(System.out);
		BufferedWriter bw = new BufferedWriter(pr);
		
		RedBlackTree map = new RedBlackTree(); 
		long intervalStart = 0;
		try {
			String inputLine = null;
			while ((inputLine = br.readLine()) != null) {
				String[] tokens = LineSplitter.split(inputLine);
				if (tokens.length == 0) {
					continue;
				}
				switch (tokens[0]) {
				case "put":
					if (tokens.length < 2) {
						System.err.println("put: No key specified");
						break;
					}
					if (tokens.length < 3) {
						System.err.println("put: No value specified");
						break;
					}
					map.put(tokens[1], tokens[2]);
					break;
				case "get":
					if (tokens.length < 2) {
						System.err.println("get: No key specified");
						break;
					}
					
					boolean mustBeNull = false;
					boolean mustExist = false;
					if (tokens.length > 2) {
						if (tokens[2].equals("mustbenull")) {
							mustBeNull = true;
						} else if (tokens[2].equals("mustexist")) {
							mustExist = true;
						}
					}
					
					String value = map.get(tokens[1]);
					String checkStatus = "no_check";
					if (mustBeNull) {
						if (value != null) {
							checkStatus = "bad_should_be_null";
						} else {
							checkStatus = "good";
						}
					}
					
					if (mustExist) {
						if (value == null) {
							checkStatus = "bad_should_exist";
						} else {
							checkStatus = "good";
						}
					}

					String valueString = "null";
					if (value != null) {
						valueString = "'"+value+"'";
					}
					bw.write(String.format("Key: '%s', value '%s', status: %s\n", tokens[1],
							valueString, checkStatus));
					break;
				case "remove":
					if (tokens.length < 2) {
						System.err.println("remove: No key specified");
						break;
					}
					value = map.remove(tokens[1]);
					valueString = "null";
					if (value != null) {
						valueString = "'"+value+"'";
					}
					bw.write(String.format("Key: '%s'; value = %s removed\n", tokens[1], valueString));
					break;	
				case "check":
					String checkMessage = map.check();
					if (checkMessage == null) {
						bw.write("check: Tree is sound");
						bw.newLine();
					} else {
						bw.write("check: Tree is corrupt!!!!!!!!! ");
						bw.write(checkMessage);
						bw.newLine();
					}
					break;
				case "dump":
					bw.write(map.toString());
					bw.newLine();
					break;
				case "size":
					bw.write(String.valueOf(map.size()));
					bw.newLine();
					break;
				case "height":
					bw.write(String.format("Height: %d\n", map.height()));
					break;
				case "startintv":
					intervalStart = System.nanoTime();
					break;
				case "endintv":
					bw.write(String.format("Finished t=%.7f\n",
						(System.nanoTime()-intervalStart)/1000000000.0));
					break;
				}
				if (!bufferedLog) {
					bw.flush();
				}
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(3);
		} finally {
			try { br.close(); } catch (Exception e) {}
			try { bw.flush(); } catch (Exception e) {}
		}
		
	}
}
