Kernel Launcher for EASE Jupyter Kernels
========================================

Prerequisites:
--------------

To use the EASE Jupyter kernels users need to have [Jupyter](http://jupyter.org) installed and configured correctly. A simple test to see if Jupyter is working correctly is to use the command:
**jupyter** *kernelspec list*

This will output the kernels currently available to Jupyter. If you get any errors on this call, please verify your installation.


Installation:
-------------

To make Jupyter aware of the EASE kernels, users need to install the kernel specification.

Eclipse will automatically generate the kernel specification using Apache ANT. If the *build* directory is not created, please run the *kernelLauncher* task from *build.xml*. This will populate the kernel template with this project's directory to avoid Java search path problems. 

Install the kernel using the Jupyter command: **kernelspec install**
This command will copy the appropriate files to the correct location independent of your operating system.

The command needs to be aware of where to get the kernelspec information from. The easiest way is to have this project's *build* directory as the current working directory while calling.

The full command should look like this (note that the first part symbolizes the cwd):
{WORKSPACE}/org.eclipse.ease.jupyter.kernel.launcher/build$ **jupyter** *kernelspec install*  *ease*

To see if the command was successful, use the command:
**jupyter** *kernelspec list*

If **ease** is among the available kernels, you successfully installed the launcher tool.


Running Jupyter Kernels:
------------------------

