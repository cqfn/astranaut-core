# Astranaut Core

![Build and test](https://github.com/cqfn/astranaut-core/workflows/Build%20and%20test/badge.svg)
[![Codecov](https://codecov.io/gh/cqfn/astranaut-core/branch/master/graph/badge.svg)](https://codecov.io/gh/cqfn/astranaut-core)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/unified-ast//astranaut-core/blob/master/LICENSE.txt)
___

## Brief

This project contains classes describing a tree, primarily an abstract syntax tree (AST) of some programming language, 
and functionality for a tree conversion.
We developed our [AST model](#ast-model) in order to be able to use common interfaces for analysis of ASTs, that
can come from different sources, for example various third-party parsers.
Moreover, this library provides base logic for processing of ASTs.

[Astranaut](https://github.com/cqfn/astranaut) uses this library to create and process syntax trees according to the
rules described with our custom [DSL](https://github.com/cqfn/astranaut#domain-specific-language).
Also we use this model in the [UAST](https://github.com/unified-ast/unified-ast) research.

## Requirements

* Java 1.8
* Maven 3.6.3+ (to build)

## How to use

TODO after release

## AST model

In the project, an *abstract syntax tree* (or just a *syntax tree*) is a directed graph (by the definition used in discrete mathematics) with
the following additional properties:
* Each node has a type (represented as a string);
* Each node may optionally have data (also represented as a string);
* The order of the successors is significant.

A *type* is a required property of a node, that allows you to assign the node to a certain class.
For example, `VariableDeclaration` is a common name for all nodes that represent declarations of variables in source code.

A *data* is an optional property of a node that represents a string value.
Most commonly, data is a property of terminal (leaf) nodes.
For instance, string and numeric literals contain data.

Non-terminal nodes have children, that are stored in a list.
The list is arranged in ascending order of children indexes.
The order of the children is determined by a grammar of the programming language from which an AST is built.
For example, if a grammar rule is `<assignment> ::= <left expression> <operator> <assignment expression>`, then
the node `assignment` has 3 children. The child `left expression` has the index of `0`,
the child `operator` has the index of `1`, and the `assignment expression` has the index of `2`.

> In our project, we construct an AST only of nodes and their relation. We do not use edge entities.

### §1. Node

The base interface in the project is `Node` which represent each node in an AST.
Every specific node in AST or UAST extends or implements `Node`.

We have tried to make the node interface as minimal as possible.
Thus, the implementation should contain at least the following methods:

* `getFragment()` - returns the `Fragment` associated with the node;
* `getType()` - returns the `Type` of the node (as an object);
* `getData()` - returns node`s data if it exists;
* `getChildCount()` - returns how many children the node has;
* `getChild(int index)` - returns a specific child node by its index.

The following methods are default, meaning they use the data that the above methods return.
They are not needed to be overridden:
 
* `getTypeName()` - returns the type of the node as a string;
* `getChildrenList()` - returns all children of a node as a list;
* `belongsToGroup(String type)` - checks if the node type belongs to the specific [hierarchy](#2-type-hierarchy) of nodes.

### §2. Type hierarchy

A *type hierarchy* is a list of node types, starting from the current node with some type and going upwards by abstraction level.

Example:

In Java `Binary Expression` is a variation of `Expression` construct.
Also, `Binary Expression` is a common name for relational and arithmetic expressions.
Moreover, arithmetic expressions include a variety of operands, like addition, multiplication and others.
Then the hierarchy for the node that performs addition will be the sequence:
```
Expression <- Binary Expression <- Arithmetic Expression <- Addition
```

### §3. Abstract node

An *abstract node* is a node that extend the `Node` class or its descendant.

We use an abstract node to describe a generalized name of several language constructs.

Example:

The `Arithmetic Expression` construct can be implemented in source code by `Addition`, `Subtraction`,
`Multiplication` and other binary expressions.
In our AST `Arithmetic Expression` will be an abstract node.

If the full hierarchy of language constructs is
```
Expression <- Binary Expression <- Arithmetic Expression <- Addition
```

then `Expression` and `BinaryExpression` will also be abstract.

### §4. Final node

A *final (non-terminal) node* is a node that implements the `Node` class or its descendant.

We use a final node to describe the last node in the hierarchy of language constructs.

Such nodes either contain a list of child nodes, or are independent units in the language, or represent a literal.

Example:

If the full hierarchy of language constructs is
```
Expression <- Binary Expression <- Arithmetic Expression <- Addition
```
then in our AST the `Addition` will be a final node.

### §5. Type

To describe the properties of nodes we also use the interface `Type`.
The `TypeImpl` class within each `Node` class implements this interface.
Each final node has a `Type`.
This project uses `Type` objects to store and collect additional data about nodes and their inheritance hierarchy.

A `Type` has the following methods:

* `getName()` - returns a type of the node as a string;
* `getChildTypes()` - returns a list of child node descriptions (ChildDescriptor) with additional information;
* `getHierarchy()` - returns the hierarchy of type names to which the current type belongs;
* `belongsToGroup(String type)` - checks if the node type belongs to a specific hierarchy;
* `createBuilder()` - returns a constructor class that creates nodes if the given type;
* `getProperty(String name)` - returns additional properties describing the features of the node type.

A `Type` has properties which we use as part of the unification task.
Properties is a dictionary, in which a key is the name of the type characteristic,
and a value is one of the possible options of this characteristic.

For now, we use the following properties:
* `color` - a color of the node type which can be:
    - *green*, if all the languages under consideration have constructs of this type;
    - *red*, if a current type is language-specific, i.e. only a specific language has a construct of this type.
* `language` - a name of a programming language under processing, may be:
    - `java`, if a *red* node belongs to Java;
    - `python`, if a *red* node belongs to Python;
    - `js`, if a *red* node belongs to JavaScript;
    - `common`, if a node is *green*.

### §6. Node tags

The [DSL](https://github.com/cqfn/astranaut#domain-specific-language) that we use for nodes generation has syntax
that allows to add tags to node's children.

Knowing a tag you can get some node's child directly by the name of its tag.
The name of such a getter is `get` + capitalized `<tag>`.

In some cases using a tag is more convenient than referring to a child by its index.

Example:

Suppose you need to analyze a `FunctionDeclaration` node which is obtained as a result of source code parsing and
may have a variable number of children.

If such a node is created by the following rule:

```
FunctionDeclaration <- [ModifierBlock], [TypeName], Identifier, ParameterBlock, StatementBlock;
```
then to get the name of the function, which is of `Identifier` type you will need to, firstly, get the amount of children.
Secondly, you will iterate over them and check their types in order to find the expected one.

However, if a node is created with the usage of tags:

```
FunctionDeclaration <- [modifiers@ModifierBlock], [restype@TypeName], name@Identifier, parameters@ParameterBlock, body@StatementBlock;
```
you can get a function name with the only one method `getName()`.

## AST processing

The core includes the following classes for a tree processing.

For creation of ASTs using DSL:

- `Converter` - applies a single transformation rule.
  The conversion consists of two steps. 
  Firstly, `Matcher` the subtree to the template.
  Secondly, if the subtree satisfies the comparison conditions, the converter builds a new subtree using `Factory`, 
  that is a collection of possible (supported) nodes.
  If the transformation rule cannot be applied, it returns an empty subtree.
- `Matcher` - compares the subtree to the template of the tree to be modified.
- `Adapter` - replaces the original tree with a new one. 
  Adapter sequentially traverses all nodes of a tree, applying all converters to each node. 
  The order in which a tree is traversed is determined by topological sorting. 
  Thus, leaf nodes are analyzed first, followed by nodes that contain leaf nodes, and so on up to the root node, being analyzed last.

For post-processing of previously created ASTs (described with `Node` classes):

- `NodeReplacer` - takes the initial tree and replaces the specified subtree with a new one.

### Contributors

* Ivan Kniazkov, @kniazkov
* Polina Volkhontseva, @pollyvolk
* Andrei Grishchenko, @GingerYouth

See our [Contributing policy](CONTRIBUTING.md).
