# GFC
Graph Fact Checking

# Purpose
This forked repository was developed for integration with the [FaVEL](https://github.com/saschaTrippel/favel) project. 

This fork is a derivative of the [GDRB](https://github.com/wsu-db/GDRB), which was originally developed by [Peng Lin](http://eecs.wsu.edu/~plin1). The purpose of this fork is to extend or modify the functionality of the original project. 

This fork introduces a new feature in the form of a Transmission Control Protocol (TCP) interface. This interface enables the submission of assertions as inputs to the GFC, rather than sampling the assertions from a knowledge graph. Additionally, this fork implements a separation of the training and testing phases to allow for distinct utilization of the two processes."

## Quick build

### Requirements

JDK 1.8+ and Maven 3.0+

### GFC (Graph Fact Checking Rules)
```java
$ mvn package
$ java -cp ./target/factchecking-1.0-SNAPSHOT-jar-with-dependencies.jar \
        edu.wsu.eecs.gfc.exps.GFC \
		./sample_data/ \
		./output \
		0.01 \
		0.0001 \
		4 \
		50
```
### Using Docker
This repository contains a Dockerfile for running the GFC.
By default, the container runs GFC and listens on port 4011. If you wish to use a different port, you can edit the appropriate line in the Dockerfile.

To build the container, navigate to the directory where the Dockerfile is located and execute the following command:
```
docker build -t gfc .
```
This will create an image named "gfc" that you can then use to start a container.

To run the docker execute the following command:
```
docker run -p 127.0.0.1:4011:4011 gfc
```
#### Prerequisites
In order to use the functionality of this extension, you must have the FaVEL software installed and configured on your system. The FaVEL software can be found at [FaVEL](https://github.com/saschaTrippel/favel).

#### Testing
To test the functionality of this extension, you will need to have the Docker container of GFC running and then run the FaVEL software with Fact Validation Service Interface available. Once this is the case, you can use the TCP interface to submit assertions to the GFC for validation.

## GFC documents

Paper: 2018-DASFAA-GFC-paper.pdf

Slides: 2018-DASFAA-GFC-slides.pptx

Source Code: SourceCode-GFC/

## Reference

```
@inproceedings{lin2018discovering,
  title={Discovering Graph Patterns for Fact Checking in Knowledge Graphs},
  author={Lin, Peng and Song, Qi and Shen, Jialiang and Wu, Yinghui},
  booktitle={International Conference on Database Systems for Advanced Applications},
  pages={783--801},
  year={2018},
  organization={Springer}
}
```
