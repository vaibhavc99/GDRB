# GFCs
Graph Fact Checking Rules

## GFC documents

**Paper: 2018-DASFAA-GFC-paper.pdf**

**Discovering Graph Patterns for Fact Checking in Knowledge Graphs**

International Conference on Database Systems for Advanced Applications (DASFAA), 2018

Peng Lin, Qi Song, Jialiang Shen, and Yinghui Wu

**Slides: 2018-DASFAA-GFC-slides.pptx**

#### Preliminary work.

2018-Preprint-Fact Checking Benchmarks.pdf

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

## Quick build and test

### Requirements

JDK 1.8+ and Maven 3.0+

### GFC (Graph Fact Checking Rules)
```java
$ mvn package
$ java -cp ./target/factchecking-1.0-SNAPSHOT-jar-with-dependencies.jar \
        edu.wsu.eecs.gfc.exps.TestGFC \
		./sample_data/ \
		./output \
		0.01 \
		0.0001 \
		4 \
		50
```
