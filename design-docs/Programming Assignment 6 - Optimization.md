# Programming Assignment 6 - Optimization

## Collaborators
* Michael Maitland, mtm68
* Scott Bass, sb2383
* Michael Tobin, mat292

## Running the Program

**Main class:** `mtm68.Main.java`

Run `xic-build` (this requires having Maven installed). Then `xic [options...] <source-files>` becomes available.

## Summary

In this assignment we implemented register allocation with move coalescing. We used a live variable analysis,  enabling reusing of the same register for multiple variables. We also handled spilling. In addition, we implemented Common subexpression elimination (CSE) which used an available exprssions analysis. We also implemented copy propagation and dead code elimination which required available copies and live variable analysis. We also implemented inlining function definitions. Lastly, we implemented copy propogation which required a reaching definions analysis.

We did our testing seperatley via unit tests, but verified that everything worked together using our integration tests from the previous assignment.

## Specification

The following are choices we made regarding specification:

**[INSERT HERE]**

## Design and Implementation 

### Architecture ###
The key classes and packages we created or updated for this assignment are the following...
 
- mtm68.Main.java
    - Our Main functions very similarly to the previous assignment. We added new options to the command line as described in the spec. We also added intermediate code generation to the source file pipeline. 

- mtm68.ir.cfg.AvailableCopies
    - This class performs an available copies analysis on the given IR

- mtm68.ir.cfg.AvailableExps
    - This class performs an available exprs analysis on the given IR
    
- mtm68.ir.cfg.ConstantPropTransformer
    - This class performs constant propogation on the program.

- mtm68.ir.cfg.CopyPropTransformer
    - This class performs copy propogation on the program

- mtm68.ir.cfg.CSETransformer
    - This class performs CSE on the program

- mtm68.IRCFGBuilder
    - This class coverts IR to CFG and allows that CFG to be converted back to IR form, useful for getting the transformed IR back

- mtm68.ir.cfg.LiveVariables
    - This class performs a live variables analysis on the given IR

- mtm68.ir.cfg.ReachingDefns
    - This class performs a reaching definitions analysis on the IR

### Code Design ###

- For this assignment we were able to split our code into three main parts: optimization on the AST, on the IR, and on the assembly. 

- **[INSERT AST OPTIMIZATION DESIGN HERE]**

- With regard to optimization on the IR, we had a class CFGBuilder that did work on the basic block and also performed transformation. Although this built the CFG, it had extra functionality that we did not need when doing our IR optimizations. Therefore, we opted to make a new IRCFGBuilder that was responsible for creating a CFG and converting back to IR. This made it reusable for many different CFG transformations that used the IR.

- Once we had a CFG, we were able to split optimizations into two main parts: an analysis and a transformation. We did CSE which required an available expressions analysis. Copy propogation required an avaliable copies analysis. Dead code removal required live variables analysis, and constant propogation required a reachng definitions analysis. The analysis all used the Data Flow Analysis framework. Although each of these classes have lots of similiar functionality, we found that they did have enough differences where it was easier to reason about by having some duplicate code. In the future, we would love to refactor similiar functionality out into a DFAFramework class. However, keeping things in seperate classes made it very clear exactly what was going on in that analysis and made it easy to reason and test our code.

- Our transformer classes would take an IR, convert to CFG, get the needed analysis, do the transformation, and convert back to IR. This made it very easy to test since we could pass in IR and assert againt the output IR. All the transformations had the same form which makes it easy to read, reason, and add in new transformations.

- **[INSERT ASM OPTIMIZATION HERE]**


### Programming ###

- One challenge during this project was the dependency on the CFGBuilders at the start. We wanted the CFG builders to feel the same at the IR and Assem level. We had a CFG builder from the past that contained transforming functionality. We decided it would be best to start fresh, but this meant that we had to do this all together and was more difficult to work in parallel until this was finished. We ended up being able to write analysis and transormation but needed the CFG conversion in order to test. Once we finished the CFG builders this was no loger a problem and it was very easy to work in parallel.

- The following is the team coding/responsibility breakdown for this assignment...
    - **Tobin:** 
        * Optimizer
        * Function inlining + tests
        * Command line options
        * IRCFGBuilder + tests
    - **Maitland:**
        * CSE + tests
        * Available Expressions + tests
        * Dead Code + tests
        * Available copies + testes
        * Reaching defns + tests
        * Copy prop + tests
        * Constant prop + tests
        * live vars + tests
        * IRCFGBuilder
        * Benchmark programs
    - **Bass:**
        * AssemCFGBuilder
        * Register Allocation

        
 - We used our previous code for lexing, parsing, typechecking, and code generation. Fortunately, we have been on top of correcting our errors after each assignment so there were very few changes that needed to be made to this code.
 
## Testing

In this assignment testing was very helpful, although sometimes we wrote tests that output a CFG and we would analyze the CFG ourselves.

When testing the DFA analysis, we would write tests cases to make sure in and out were as they were supposed to. When testing the IR transfomrations, we wrote tests cases that asserted a given IR was optimized into a new IR. This testing helped find bugs in optimizations and transformations and let us know exactly what was going wrong when something broke.

Our integration tests stepped through the entire pipeline of compilation. First, we turned the xi program into IR code and tested the output of this IR code against the expected output using the IRSimulator. Then, we generated assembly from the IR code and automatically ran it using linkxi.sh. Setting up this architecture in JUnit allows us to very easily add more programs as test cases. It simply requires writing xi programs and determining their expected output. It allows us to check to make sure optimizations didn't break the pipeline

When we ran into bugs in our integration tests, we would write a simpler program that isolated the issue. Then, we would generate the executable for this smaller program and step through the execution using gdb. Although being very painful, this was effective in helping us isolate a number of issues (such as improper stack pointer alignment and register value clobbering).

It should also be said that we utilized the xth test suite to make sure our output was compliant with the autograder's expectations.

## Work plan

**[INSERT HERE]**

We did a great job of communicating frequently, working together to clarify things, and adapting to challenges we faced.

## Known Problems

We currently are not aware of any issues with our compiler.

## Comments

Thank you for a great year, this class was a bunch of fun and we learned a crazy amount!!!
