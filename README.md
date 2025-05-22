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

# The Basics: Interfaces & Classes  
This is where the tree magic starts.  

These interfaces and helpers define **what every syntax tree node looks like**—including the ones Astranaut generates
for you. They’re:

- **Minimal** (No fluff.)  
- **Immutable** (No surprises.)  
- **Codegen-friendly** (Easy to auto-implement.)  

## **Node – The Bare Minimum (But in a Good Way)**  

So, **`Node`**—this is the interface that makes the tree magic happen.
If you want Astranaut’s algorithms to play nice with your trees, your nodes gotta implement it.  

**Using Astranaut’s DSL?** Cool. It’ll auto-generate classes that already implement `Node` for you. No extra work.  

Now, here’s the fun part: **We made `Node` as tiny as humanly possible.** What’s in it? Just the essentials:  
- A **type** (so you know what kind of node it is).  
- Some **data** (as a plain ol’ `String`—keep it simple).  
- A list of **child nodes** (because trees gotta branch).  

That’s it. No bloat, no nonsense. Just enough to keep things moving.  

---  

**Why?**  
- **Flexibility**: You can build *anything* on top of this.  
- **Predictability**: No hidden surprises in the interface.  
- **Codegen-friendly**: Easy for Astranaut to generate and for you to use.  

So yeah, `Node`’s small. But that’s the point. 🚀

## **Type – Because Not All Nodes Are Created Equal**  

Alright, so your `Node` has a `Type` – that's this handy little interface that tells you *what kind* of node
you're dealing with.  

**Key things about `Type`:**  
- **One type, many nodes** – Like a cookie cutter stamping out cookies.  
- **Singleton by default** – If you're using Astranaut DSL, it'll generate these for you as singletons (no duplicates, no fuss).  
- **Comes fully loaded** with:  
  - A **name** (so you know what to call it)  
  - **Child descriptors** (to validate node structure when building)  
  - **Parent hierarchy** (to check whether nodes belong to a specific group)
  - A **Builder factory** (for crafting new nodes of this type)  
  - Optional **custom properties** (key-value metadata, because sometimes you need extra sauce)  

**But here's the cool part:** If you're *manually* implementing `Type`, you only **need** two things:  
1. The type's **name**  
2. A way to **create Builders** for it  

Everything else? Optional. We keep it flexible.  

## **Builder – Your Safe Node Creation Buddy**  

Let’s talk **`Builder`**—the only *proper* way to create nodes when you’re dealing with Astranaut-generated trees.  

#### **Why Not Just Use a Constructor?**  
- **No public constructors**: If your node comes from Astranaut DSL, it’s **immutable** and has **no public `new`**.  
- **Mutation? Nope**: Hand-written nodes *might* be mutable, but Astranaut’s aren’t. **Builder enforces correctness.**  
- **Algorithms expect it**: Every tree-modifying tool in this library uses `Builder`.  

### **How It Works**  
1. **Create a `Builder`** (usually via `Type`).  
2. **Feed it stuff** (in any order):  
   - A **`Fragment`** (optional, if your node tracks source code).  
   - **Data** if exists (as a `String`—returns `false` if invalid).  
   - **Child nodes** if any (also returns `false` if the list is illegal).  
3. **Check `isValid()`** (because mistakes happen).  
4. **Call `createNode()`** → *Boom!* New node.  
   - (Or `IllegalStateException` if you skipped `isValid()` and the builder’s unhappy.)  

**Bonus:** Calling `createNode()` again gives you a **fresh copy**—same specs, new object.  

### **The Golden Rule**  
> *"If a node exists, it’s correct."*  
Builders **guarantee** valid nodes. No half-baked, malformed nonsense.  

### **DSL vs. Hand-Rolled Nodes**  
- **Using Astranaut DSL?** Builders are **auto-generated**. Easy.  
- **Writing custom nodes?** You have to implement a `Builder` — or most algorithms **won’t work**.  
- **Pro tip:** Just **use the DSL**. Let Astranaut handle this.  

## **Tree – Fancy Node Wrapper (But Useful)**  

Let’s be real: **any `Node` is already a tree** (or at least a subtree) because it can have children.
But we went ahead and made `Tree` anyway. Why? Because sometimes you wanna point at a root and say:  

*"Behold! This right here? This is a **Syntax Tree**."*  

### **What’s Inside?**  
- **A root `Node`** (obviously).  
- **Extra utility methods** (for tree-wide operations).  

That’s it. No magic, no overengineering—just a clean way to mark *"this node is the whole deal."*  

### **Philosophy**  
- **`Node`**: A piece of the tree.  
- **`Tree`**: The *official* container for the root + helpers.  

## **DummyNode – The Polite Null Alternative**  

Meet **`DummyNode`**—the singleton stand-in for *"I’d return `null`, but that’s rude."*  

### **Why It Exists**  
- **`null` is messy**: Forces null checks everywhere.  
- **But sometimes you need *nothing***: Like empty list elements or placeholder nodes.  

### **What It Is**  
- A **singleton fake node** (only one exists globally).  
- **No type, no data, no children** (truly empty—but in a *valid* way).  
- **Still a `Node`**: Fits anywhere a real node would, without blowing up your code.  

### **Key Difference vs. `null`**  
|                | `DummyNode`       | `null`            |  
|----------------|------------------|------------------|  
| **Safe to call methods?** | ✅ (no-op) | 💥 `NullPointerException` |  
| **Children?**            | ❌ (but reports `0`) | 💥 Crash |  
| **Type?**               | ❌ (but won’t complain) | 💥 Crash |  

## **EmptyTree – The Polite Way to Say "Nothing Here"**  

**What it is:**  
- A **singleton tree** that contains a `DummyNode` as its root.  
- The **official "empty" representation** when you need a valid `Tree` instead of `null`.  

## **Fragment, Position & Source – The Location Trio**  

### **Fragment**  
Every node can **optionally** have a `Fragment` – a bookmark pointing to the **exact chunk of source code**
it represents.  

**Out-of-the-box implementations:**  
- `EmptyFragment` → For nodes that **don’t track source** (default).  
- `DefaultFragment` → If you **have source mappings** (uses `Position` start/end).  

**Roll your own?**  
Implement `Fragment` if you need **custom source tracking** (e.g., for non-textual formats).  

### **Position**  
A `Position` pinpoints a **specific spot** (line + column) in the source. Comes with:  
- `DefaultPosition` → Ready-to-use implementation.  
- **Works with `Source`** → To resolve actual code snippets.  

### **Source**  
The "know-it-all" backend for `Position`. Tracks:  
- **What’s being parsed** (e.g., a file, a string).  
- **How to extract code** between two `Position`s.  

**Implementations included:**  
- `StringSource` → For raw strings.  
- `FileSource` → For file-based code.  

### **How They Work Together**  
1. **Node** → Has a `Fragment` (or `EmptyFragment`).  
2. **Fragment** → Stores start/end `Position`s.  
3. **Position** + **Source** → Can reconstruct the **original code snippet**.  

## **Factory – Your Node Creation Command Center**  

**What it does:**  
- **Gives you `Type` and `Builder` objects** just by asking for a type name.  
- **Centralized node production** – no manual instantiation headaches.  

**Key Features:**  
- **`getType(String name)`** → Fetch a `Type` by its registered name.  
- **`createBuilder(String name)`** → Spawn a ready-to-use `Builder` for that type.  
- **DSL-generated** → If you're using Astranaut, factories come pre-built with all your node types.  

**Why It Matters for Transformations:**  
Algorithms that **rewrite trees** rely on factories to:  
1. **Create new nodes** on the fly (without knowing their internals).  
2. **Ensure type safety** (only valid nodes get built).  

## **Transformer – The "Tree In, Tree Out" Black Box**  

**What it is:**  
- The simplest interface ever: **`Tree transform(Tree input)`**  
  - Takes a tree, *maybe* changes it, returns a tree (new or same instance).  
- **No promises** about *how* it transforms—just that it **does**.  

### **How Astranaut DSL Handles It**  
1. **You write rules** in the DSL (e.g., *"replace all `Foo` nodes with `Bar`"*).  
2. **Astranaut generates:**  
   - **`Converter` classes** (one per rule) – do the actual grunt work.  
   - **A master `Transformer`** – glues all `Converter`s together into one `transform()` call.  

**Result:**  
- Your **`Transformer` is just a facade** hiding a pipeline of smaller conversions.  
- **Zero boilerplate** – the generated code handles the complexity.  

### **Key Points**  
- **Immutable-friendly**: Always returns a tree; never mutates the input.  
- **Idempotent? Optional**: Depends on your rules (not enforced by the interface).  
- **DSL advantage**: Lets you **think in rules**, not manual tree-walking.  

## **Provider – Your One-Stop Shop for Factories & Transformers**  

**What it is:**  
The **final generated piece** that ties everything together — a central registry for:  
- **Factories** (to create nodes)  
- **Transformers** (to modify trees)  

### **How It Works**  
1. **You define languages and rules** in your DSL.  
2. **Astranaut generates** a `Provider` that:  
   - **Exposes factories/transformers per language** (e.g., `getFactory("python")`).  
   - Defaults to `"common"` if no language is specified.  

### **Why It’s Handy**  
- **Single access point** – No hunting for factories/transformers.  
- **Language-aware** – Swap implementations by name (e.g., `"java"` vs `"kotlin"`).  
- **Required by some tools** – Like the built-in **JSON deserializer**.  

### **Key Details**  
- **Minimal interface**: Just `getFactory()` and `getTransformer()`.  
- **No magic**: All heavy lifting is done during codegen.  
- **Extensible**: Add custom providers for non-DSL use cases.  



# Contributors

* Ivan Kniazkov, @kniazkov
* Polina Volkhontseva, @pollyvolk
* Andrei Grishchenko, @GingerYouth

See our [Contributing policy](CONTRIBUTING.md).
