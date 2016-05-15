package org.bitfunnel.workbench;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.bitfunnel.test.WikipediaDumpProcessor;

/*
x Is DocumentFile a good name? Seems given DocumentFileProcessor.
x   What is name of input file type? WikipediaDump?
x   What is name of output file type? BitFunnelCorpus?
Write out corpus file as UTF8 bytes.
Fix package name.
README.md
Release vs debug builds.
Test running on Windows and Linux.
Put code into real repository.
Sample data files.
  Perhaps public domain poems.
  Very short Wikipedia articles.
x Rename App to CorpusConverter.
Test converting large corpus files.
Measure conversion time.
Print progress indicator.
JUnit test function to convert and then read back one file.
Corpus statistics class
  Counts of files, documents, streams per document
  Byte size of files.

InputStream is = new ByteArrayInputStream( myString.getBytes( charset ) );
 */

public class MakeCorpusFile {
  Path input;
  Path output;

  public static void main(String[] args) {
    if (args.length != 2) {
      System.out.println("Usage");
    } else {
      try {
        MakeCorpusFile processor = new MakeCorpusFile(Paths.get(args[0]), Paths.get(args[1]));
        processor.ProcessAllFiles();
      } catch (IOException e) {
        System.out.println("IOException: " + e);
      }
    }
  }


  public MakeCorpusFile(Path input, Path output) {
    this.input = input;
    this.output = output;
  }


  public void ProcessAllFiles() throws IOException {
    if (!Files.exists(input)) {
      System.out.println("Error: input location " + input.getFileName() + " does not exist.");
    } else if (Files.exists(output)) {
      System.out.println("Error: output location " + output.getFileName() + " already exists.");
    } else {
      System.out.println("Making corpus file.");
      System.out.println("Input (Wikipedia dump): " + input.toAbsolutePath());
      System.out.println("Output (corpus file: " + output.toAbsolutePath());

      Files.walk(input).filter(Files::isRegularFile).forEach(this::ProcessOneFile);
    }
  }


  public void ProcessOneFile(Path path) {
    try {
      // Determine the path to the destination file.
      Path relativePath = input.relativize(path);
      Path destination = output.resolve(relativePath);

      System.out.println("source = " + path);

      System.out.println("createDirectories(" + destination.getParent() + ")");
      Files.createDirectories(destination.getParent());

      System.out.println("Open file " + destination + " inside of try.");

//       Files.createDirectories(output.getParent());
//        try (OutputStream outputStream = Files.newOutputStream(output)) {

//      OutputStream outputStream = System.out;
      try (InputStream inputStream = Files.newInputStream(path);
//           /*OutputStream outputStream = System.out*/) {
           OutputStream outputStream = Files.newOutputStream(destination)) {
        WikipediaDumpProcessor processor =
            new WikipediaDumpProcessor(inputStream, outputStream);
        processor.ProcessFile();
      }

    } catch (Exception e) {
      System.out.println("EXCEPTION");
      e.printStackTrace();
    }
  }
}