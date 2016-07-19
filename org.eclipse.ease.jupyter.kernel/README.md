EASE Jupyter Kernels
====================

Notes:
------

Based on large parts on the [Japyter](https://github.com/openanalytics/japyter) project. During the initial implementation phase of the Jupyter Kernels this repository will be used. Once the code is stable it will be merged back to the other project. 


Prerequisites:
--------------

To use the EASE Jupyter kernels users need to have [Jupyter](http://jupyter.org) installed and configured correctly. A simple test to see if Jupyter is working correctly is to use the command:
**jupyter** *kernelspec list*

This will output the kernels currently available to Jupyter. If you get any errors on this call, please verify your installation.

To use the EASE kernels they must be installed. Check the *org.eclipse.ease.jupyter.kernel.launcher* project from this repository for more information.


Running Jupyter Kernels:
------------------------

To simplify the start of a Jupyter kernel a EASE module */Jupyter/Jupyter Kernel* has been created.
Call the function **startKernel(***IScriptEngine***)** with an EASE script engine of your choice. The simplest way to acquire a script engine is to use **getScriptEngine()** from the automatically loaded */System/Environment* module.

After this you can use the EASE kernel like you would any other Jupyter kernel.

This **README** will be updated accordingly whenever the usage changes.