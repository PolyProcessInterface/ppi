# Parallel Programing Interface

[![build][buildbadge]][buildworkflow] [![javadoc][javadocbadge]][javadocurl]

A high level Java interface to develop distributed protocols.

## Usage

### Example

Create a class that extends `NodeProcess` and at least one class that extends
`Message`. The function `start` is the first one to be run by PPI. It must be
overriden with the initialisation of the process.

Inside of `NodeProces` there is an instance on `Infrastructure` name `infra`.
This is the entry point of the [API](#api-reference).

```java
// ExampleNodeProcess.java

import org.sar.ppi.Message;
import org.sar.ppi.MessageHandler;
import org.sar.ppi.NodeProcess;

public class ExampleNodeProcess extends NodeProcess {

	public static class ExampleMessage extends Message{
		private static final long serialVersionUID = 1L;
		public String content;
		public ExampleMessage(int src, int dest, String content) {
			super(src, dest);
			this.content = content;
		}
	}

	@MessageHandler
	public void processExampleMessage(ExampleMessage message) {
		int host = infra.getId();
		System.out.printf(
			"%d Received '%s' from %d\n",
			host, message.content, message.getIdsrc()
		);
		if (host != 0) {
			int dest = (host + 1) % infra.size();
			infra.send(new ExampleMessage(infra.getId(), dest, "hello"));
		}
		infra.exit();
	}

	@Override
	public void start() {
		if (infra.getId() == 0) {
			infra.send(new ExampleMessage(infra.getId(), 1, "hello"));
		}
	}
}
```

### API reference

The API consists of the [public methods of the `Infrastructure` class](https://atlaoui.github.io/ParallelProgramingInterface/org/sar/ppi/Infrastructure.html)
which can be access via the `infra` property.

## Requirement

- Java >= 1.8
- [Maven](https://maven.apache.org/)

## Install dependencies

### OPENMI java

```bash
wget https://download.open-mpi.org/release/open-mpi/v4.0/openmpi-4.0.2.tar.gz
tar zxvf openmpi-4.0.2.tar.gz
cd openmpi-4.0.2
./configure --enable-mpi-java --with-jdk-bindir=$JAVA_HOME/bin/ --with-jdk-headers=$JAVA_HOME/include
make -j2
sudo make install
sudo ldconfig
cd -
```

### Peersim

```bash
wget http://downloads.sourceforge.net/project/peersim/peersim-1.0.5.zip
unzip peersim-1.0.5.zip
sudo cp peersim-1.0.5/*.jar /usr/local/lib
```

## Compile

    mvn compile

If you installed the libraries in a different location you can use the options
`-Dmpi.path=/your/prefix/lib` and `-Dpeersim.path=/your/prefix/lib`.

## Run tests

    mvn test

## Docs

### Install latex

    sudo apt install texlive latexmk texlive-lang-french cm-super

[buildbadge]: https://github.com/Atlaoui/ParallelProgramingInterface/workflows/build/badge.svg
[buildworkflow]: https://github.com/Atlaoui/ParallelProgramingInterface/actions?query=workflow%3Abuild+branch%3Amaster
[javadocbadge]: https://img.shields.io/github/deployments/Atlaoui/ParallelProgramingInterface/github-pages?label=javadoc
[javadocurl]: https://atlaoui.github.io/ParallelProgramingInterface