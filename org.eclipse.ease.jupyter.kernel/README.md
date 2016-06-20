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

To run the Jupyter kernel for now please start the *Dispatcher* class from *org.eclipse.ease.jupyter.kernel*. This will set up the Dispatcher server that receives the connection file and actually starts the kernel.

Note that this is only necessary during development phase as development is faster with a plain Java application.

After this you can use the EASE kernel like you would any other Jupyter kernel.

This **README** will be updated accordingly whenever the usage changes.