# Poly-Process Interface

[![build][buildbadge]][buildworkflow] [![javadoc][javadocbadge]][javadocurl] [![release][releasebadge]][releaseurl]

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

import org.sar.ppi.communication.Message;
import org.sar.ppi.communication.MessageHandler;
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
	public void init(String[] args) {
		if (infra.getId() == 0) {
			infra.send(new ExampleMessage(infra.getId(), 1, "hello"));
		}
	}
}
```

Then you can compile your class and run it with the bundled jar which will
dynamically load it:

```bash
javac -cp ppi-0.2-dev-bundle.jar ExampleNodeProcess
java -jar ppi-0.2-dev-bundle.jar ExampleNodeProcess org.sar.ppi.peersim.PeerSimRunner --np=4
```

Alternatively you can call `Ppi.main()` yourself like this and the run your class:

```java
public static void main(String[] args) {
	Ppi.main(ExampleNodeProcess.class, new MpiRunner(), args, 3);
}
```

```bash
javac -cp ppi-0.2-dev-bundle.jar ExampleNodeProcess
java -cp .:ppi-0.2-dev-bundle.jar ExampleNodeProcess
```

### API reference

The API consists of the [public methods of the `Infrastructure` class](https://polyprocessinterface.github.io/ppi/org/sar/ppi/Infrastructure.html)
which can be access via the `infra` property.

### CLI reference

```
Usage: ppi [-s=<path> | -c=<json>] [-hV] [--np=<number>] <process-class>
           <runner-class> [<args>...]
      <process-class>     Fully qualified name of the class to use as process
      <runner-class>      Fully qualified name of the class to use as runner
      [<args>...]         Args to pass to the processes
  -c, --content=<json>    Content of the scenario
  -h, --help              Display a help message
      --np=<number>       Number of processus in the network
                            Default: 4
  -s, --scenario=<path>   Path to the scenario file
  -V, --version           Print version info
```

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

## Build executable jar

    mvn package

## Docs

### Install latex

    sudo apt install texlive latexmk texlive-lang-french cm-super

[buildbadge]: https://github.com/PolyProcessInterface/ppi/workflows/build/badge.svg
[buildworkflow]: https://github.com/PolyProcessInterface/ppi/actions?query=workflow%3Abuild+branch%3Amaster
[javadocbadge]: https://img.shields.io/github/deployments/PolyProcessInterface/ppi/github-pages?label=javadoc
[javadocurl]: https://polyprocessinterface.github.io/ppi
[releasebadge]: https://img.shields.io/github/v/release/PolyProcessInterface/ppi
[releaseurl]: https://github.com/PolyProcessInterface/ppi/releases/latest