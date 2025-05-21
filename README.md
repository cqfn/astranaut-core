# Astranaut Core — The Heart of the Tree

![Build and test](https://github.com/cqfn/astranaut-core/workflows/Build%20and%20test/badge.svg)
[![Codecov](https://codecov.io/gh/cqfn/astranaut-core/branch/master/graph/badge.svg)](https://codecov.io/gh/cqfn/astranaut-core)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/unified-ast//astranaut-core/blob/master/LICENSE.txt)
___

# Brief

Welcome to **Astranaut Core** — or just **Core**.

This module contains all the **fundamental interfaces and base classes** that define the structure of syntax trees,
along with a growing collection of **reusable algorithms** for working with them.

It’s packaged as a standalone **Maven dependency**, and it serves two main roles:

- ✅ It powers the **[Astranaut](https://github.com/cqfn/astranaut)**  tool itself — including code generation,
  interpretation, and transformation.
- ✅ It’s what you’ll **include in your own project** when Astranaut generates Java source files for your custom trees.

So if you're working with anything Astranaut produces, you're working with Core — whether you know it or not.

# Requirements

* Java 1.8
* Maven 3.6.3+ (to build)

# How to Add Astranaut Core to Your Project

Astranaut Core is published as a regular Maven dependency.

Just add this to your `pom.xml`:

```xml
<dependency>
  <groupId>org.cqfn</groupId>
  <artifactId>astranaut-core</artifactId>
  <version><!-- latest version here --></version>
</dependency>
```

# Basic interfaces

These interfaces form the backbone of Astranaut’s syntax trees. Every generated entity—whether it’s a language
statement, expression, or custom AST node — implements these contracts. They define the rules of the game:
how nodes are structured, how types enforce validity, and how builders safely assemble pieces. When Astranaut generates
code for your DSL or language, it’s weaving these interfaces into the fabric of your tree. In short: if it’s in your
AST, it’s built on this core.  

## `Node`  
*The atomic building block of every AST—immutable, thread-safe, and endlessly cloneable.*  

**What it does:**  
- Represents a single node in your syntax tree (leaf or branch)  
- Tracks type, data, properties, and child nodes  
- Provides deep comparison, cloning, and traversal utilities  

**Key Features:**  
```java
// Get node metadata
String type = node.getTypeName(); 
String data = node.getData(); 

// Navigate children
Node firstChild = node.getChild(0); 
node.forEachChild(child -> { ... }); 

// Safety-first ops
Node clone = node.deepClone(); 
boolean isIdentical = node.deepCompare(other); 
```

**Notes:**  
- **Immutable by design** – Use `Builder` (via `getType().createBuilder()`) for modifications.  
- **Efficient child access** – Wrapped in `ChildrenList`/`ChildrenIterator` to avoid overhead.  
- **Thread-safe** – All methods are either pure or final.  

**Extras:**  
- `getLocalHash()` – Quick hash for type + data (ignores children).  
- `belongsToGroup()` – Type hierarchy checks.  

## `Type`  
*The DNA of your nodes - defines what they are and what they can contain.*

**What it does:**  
- Acts as a blueprint for `Node` objects (name, allowed children, properties)  
- Validates node structure through child type descriptors  
- Manages type hierarchies (e.g., "BinaryExpression" > "ArithmeticExpression" > "Expression")  

**Key Methods:**  
```java
// Core identity
String name = type.getName(); 

// Hierarchy checks
boolean isArithmetic = type.belongsToGroup("ArithmeticExpression");

// Child constraints
List<ChildDescriptor> allowedChildren = type.getChildTypes(); 

// Node construction
Builder builder = type.createBuilder(); 
```

**Notes:**  
- **Default-empty** – Child types and properties return empty collections unless overridden.  
- **Immutable** – All returned collections are unmodifiable.  
- **Builder factory** – Each type knows how to create its own `Builder`.  

**When to use:**  
- Validating node structures before creation  
- Checking type compatibility in transformations  
- Generating code from AST definitions  

*(See `ChildDescriptor` for fine-grained child node constraints.)*  

## `Builder`  
*The architect behind every `Node` — assembles pieces and validates before construction.*  

**What it does:**  
- Constructs `Node` instances piece-by-piece (data, children, source fragments)  
- Validates configurations before creation via `isValid()`  
- Enforces type safety when setting children/data  

**Key Workflow:**  
```java  
Builder builder = type.createBuilder();  

// Configure node  
builder.setData("x > 0");  
builder.setChildrenList(Arrays.asList(left, right));  

// Build only if valid  
if (builder.isValid()) {  
    Node node = builder.createNode();  
}  
```  

**Notes:**  
- **Validation-first** – `setData()`/`setChildrenList()` return `false` for invalid inputs.  
- **Fragment tracking** – Optional source code mapping via `setFragment()`.  
- **One-shot use** – Typically discarded after `createNode()`.  

**Why it matters:**  
- Core to safe AST modifications (unlike direct node mutation)  
- Generated implementations enforce your language’s grammar rules  

## `Fragment` and Friends  
*Your AST's GPS — tracks where nodes come from in source code.*

**The Players:**
- `Fragment`: Interface for source code spans (start/end positions + helpers)  
- `EmptyFragment`: Singleton for nodes without source locations (∅ symbol in toString)  
- `DefaultFragment`: Workhorse implementation for real code spans  

**Why It Matters:**  
```java
// Get original source code for a node
String code = node.getFragment().getCode(); 

// Format as "file.java, 1.5-2.10"
String location = fragment.getPositionAsString(); 

// Combine fragments from multiple nodes
Fragment combined = Fragment.fromNodes(children); 
```

**Key Features:**  
- **Null-safe** – `EmptyFragment` avoids null checks  
- **Smart constructors** – `fromNodes()`/`fromPositions()` auto-calculate bounds  
- **Source fidelity** – Row/column precision for error messages  

**Behind the Scenes:**  
- Powered by `Position` and `Source` (not shown here)  
- Used by AST printers, linters, and debuggers  

*(Note: Most nodes get their fragment automatically during parsing/generation.)*  

## `Position` & `DefaultPosition`  
*Precise source code coordinates - the "line and column numbers" of your AST.*

**What it tracks:**  
- **Source file** (via `Source` object)  
- **Row/line number** (1-based)  
- **Column number** (1-based)  

**Key Features:**  
```java
// Create position (line 5, column 10)
Position pos = new DefaultPosition(source, 5, 10);  

// Compare positions (throws if sources differ)
if (pos1.compareTo(pos2) < 0) { ... }  

// Find bounds of multiple positions
Pair<Position,Position> range = Position.bounds(listOfPositions); 
```

**Rules:**  
- **Immutable** – All fields are final  
- **Same-source requirement** – Comparisons require matching `Source`  
- **1-based indexing** – Rows/columns start at 1 (like most IDEs)  

**Used by:**  
- `Fragment` for source code spans  
- Error reporting (precise location pinpointing)  
- Refactoring tools  

## `Source` & Implementations  
*The "source" of truth - connects AST nodes back to original code.*

**Core Abstraction:**  
```java
public interface Source {
    String getFragmentAsString(Position start, Position end);  // Gets code snippet
    String getFileName();  // Returns "" for anonymous sources
}
```

**Implementations:**  
1. **`StringSource`**  
   - Wraps in-memory code (split into lines)  
   - Handles out-of-bounds positions gracefully  
   - Used for:  
     ```java
     // Create from string
     Source src = new StringSource("print('hello')");  
     // Get "hello" substring (line 1, columns 7-11)
     src.getFragmentAsString(new Position(...), new Position(...));  
     ```

2. **`FileSource`** (extends `StringSource`)  
   - Reads code from disk  
   - Preserves original file path  
   - Usage:  
     ```java
     Source src = new FileSource("src/main.py");  // Loads file content
     src.getFileName();  // Returns "src/main.py"
     ```

**Key Behaviors:**  
- **1-based indexing** – Rows/columns start at 1  
- **Safe slicing** – Adjusts invalid ranges automatically  
- **Lazy file reading** – `FileSource` loads content on creation  

**When to Use:**  
- Constructing `Position` objects  
- Error reporting (showing code snippets)  
- IDE integrations  

# Extending the Core

While the base interfaces (`Node`, `Type`, `Builder`) define the essential structure of ASTs, Astranaut also provides
**extended interfaces and classes** to enable more advanced tree manipulations.
These additions allow for:  

- **Prototype-based inheritance** – Lightweight node variations without full copies  
- **Tree transformations** – Swapping, wrapping, and modifying subtrees  
- **Metadata attachment** – Carrying extra context across passes  

## `PrototypeBasedNode`  
*The AST's Russian nesting doll — nodes that delegate to their prototypes.*

**What it does:**  
- Lets nodes **inherit behavior** from a prototype node  
- Enables **lightweight modifications** (override just 1-2 methods)  
- Supports **prototype chains** (A delegates to B, B delegates to C...)  

**Typical Use Case:**  
```java  
Node original = getOriginalNode();  

// Create tweaked version that only changes getData()  
Node modified = new PrototypeBasedNode() {  
    @Override  
    public Node getPrototype() { return original; }  

    @Override  
    public String getData() { return "OVERRIDDEN"; }  
};  
```  

**Why It Matters:**  
✅ **Memory efficient** – Shares unchanged properties/children via prototype  
✅ **Non-destructive** – Original node remains immutable  
✅ **Pattern-friendly** – Enables decorator/composite-like patterns  

## `ExtNode` & `ExtNodeCreator`  
*Supercharged AST navigation — adds parent/child/sibling links and subtree hashing to regular nodes.*

**Why It Exists:**  
- **Augments basic `Node`** with traversal context (parent/sibling references)  
- **Maintains prototype chain** (via `PrototypeBasedNode` inheritance)  
- **Precomputes subtree hashes** for fast equality checks  

**Key Features:**  
```java
// Convert regular node to extended version
ExtNode extNode = new ExtNodeCreator().create(node);  

// Navigate tree structurally
ExtNode parent = extNode.getParent();  
ExtNode leftSibling = extNode.getLeft();  

// Compare entire subtrees instantly
if (extNode1.getAbsoluteHash() == extNode2.getAbsoluteHash()) { ... }  
```

**Implementation Notes:**  
- **Wrapper pattern** – `ExtNodeImpl` delegates to original `Node`  
- **Lazy-free** – All relationships computed during creation  
- **Hash consistency** – `AbsoluteHash` ensures identical trees → identical hashes  

**Typical Use Cases:**  
- Tree diffing algorithms  
- Pattern matching with context awareness  
- Refactoring tools needing sibling/parent access  

*(Remember: Original nodes remain immutable—extended nodes are a read-only view.)*  

**Why This Matters:**  
1. **Context matters** – Many algorithms need parent/sibling awareness  
2. **Hashing shortcut** – `getAbsoluteHash()` avoids recursive comparisons  
3. **Backward compatible** – Original nodes stay unchanged  

## `DummyNode`  
*The polite null - a type-safe placeholder for empty nodes.*

**What it solves:**  
- Eliminates null checks in AST processing  
- Provides a valid `Node` when no real node exists  
- Maintains type safety across all operations  

**Key Characteristics:**  
```java
// Singleton access
Node dummy = DummyNode.INSTANCE;  

// Always behaves like an empty node
dummy.getChildCount();  // → 0  
dummy.getChild(0);     // throws IndexOutOfBoundsException  
dummy.toString();       // → "∅" (empty set symbol)  
```

**When to Use:**  
- Initializing empty tree positions  
- Representing deleted/optional nodes in transformations  
- As a safe return value for failed operations  

*(Pro tip: Check for `node == DummyNode.INSTANCE` instead of `null` checks.)*  

**Why This Matters:**  
1. **Null-object pattern** – Makes tree algorithms more robust  
2. **Visual debugging** – Clearly shows empty nodes as `∅`  
3. **Consistent behavior** – Fails fast with exceptions for invalid operations  

## `DraftNode`  
*The AST's wildcard - creates unvalidated trees for prototyping and testing.*

**What it's for:**  
- Quickly building test ASTs without validation rules  
- Importing trees from external parsers  
- Prototyping language structures before final implementation  

**Key Features:**  
```java
// From string description (BFS-style)
Node tree = DraftNode.create("Root(Left<\"data\">, Right())");  

// Programmatic construction
Node node = DraftNode.create("Type", "data", child1, child2);  

// Builder-style (with fragment support)
DraftNode.Constructor builder = new DraftNode.Constructor();
builder.setName("IfStatement")
      .setData("x > 0")
      .addChild(thenBranch);
Node ifNode = builder.createNode();
```

**When to Use:**  
✅ **Testing** – Mock complex trees in unit tests  
✅ **Parser development** – Visualize output during development  
✅ **Quick experiments** – Try AST shapes without defining types  

**Limitations:**  
- No validation of type hierarchies or child constraints  
- Immutable once created (but builder is mutable)  

*(Pro tip: Use `DraftNode.Constructor.toString()` for debug-friendly output.)*  

## `DraftNode.Constructor`  
*The loose cannon of node builders - no rules, just results.*

**Differs from regular `Builder`:**  
- No `isValid()` checks beyond non-empty name  
- Allows any child combinations  
- Exposes raw setters (`setName()`, `addChild()`)  

**Example Workflow:**  
```java
// Building a malformed but usable AST
Constructor builder = new DraftNode.Constructor();
builder.setName("WeirdNode")
      .setData("¯\_(ツ)_/¯")
      .addChild(DummyNode.INSTANCE);  // Why not?

Node weird = builder.createNode();  // Always works if name is set
```
**Why It Exists:**  
- Enables "sketching" invalid ASTs for error recovery tests  
- Useful for fuzzing/negative testing scenarios  
- Powers `DraftNode.create()` string parsing  

# Contributors

* Ivan Kniazkov, @kniazkov
* Polina Volkhontseva, @pollyvolk
* Andrei Grishchenko, @GingerYouth

See our [Contributing policy](CONTRIBUTING.md).
