# Parallel Programing Interface

A high level Java interface to develop distributed protocols.

## Example

```java
import org.sar.ppi.Protocol;

public class HelloProtocol extends Protocol {

	@Override
	public void processMessage(Node src, Object message) {
		Node host = infra.getCurrentNode();
		System.out.println("" + host.getId() + " Received hello from "  + src.getId());
		if (host.getId() != 0) {
			infra.send(infra.getNode((host.getId() + 1) % infra.size()), 0);
		}
		infra.exit();
	}

	@Override
	public void startNode(Node node) {
		if (node.getId() == 0) {
			infra.send(infra.getNode(1), 0);
		}
	}
}
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
