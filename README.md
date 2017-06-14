# Controlling Task Execution Order

I'm going to discuss how to control task execution order around a use case.

Let's imagine that you are developing an order management system. Orders needs to be processed in any "fair" order 
except where there is a natural dependency such as Order-1 Initial request can not be processed simultaneously 
with Order-1 Update or Order-1 Close.

We will start with empty project and switch to particular branch to analyse progressive solutions.

Build tool               : Maven
Additional Collection lib: Guava

Feel to free to IDE of choice.

To clone the project      : git clone ....
To switch different branch: git checkout <BRANCH>

After a quick look into src directory, please switch to L1
   >> git checkout L1

