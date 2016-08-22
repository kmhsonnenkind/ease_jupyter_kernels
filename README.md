# Jupyter Kernels for Eclipse EASE script engines

## About

This project allows users to have [EASE](http://www.eclipse.org/ease/) script engines as [Jupyter](http://jupyter.org) kernels.

It is mainly intended to be used by the Jupyter notebook but theoretically the kernels work for any Jupyter clients.


## Prerequisites / Installation:

In order to use the **EASE Jupyter Kernels**, you will need to have the following three components installed:

+ EASE
+ Jupyter
+ Kernels

(Who would have thought that...)

### EASE

The simplest way to install [EASE](http://www.eclipse.org/ease/) is to get it from the [update site](http://download.eclipse.org/ease/update/release). Simply install it as you would any other eclipse plugin.

Chances are you already have **EASE** running or you would not be here...

### Jupyter

To use the **EASE Jupyter Kernels** [Jupyter](http://jupyter.org) must be installed and configured correctly. A simple test to see if Jupyter is working, is to use the command:

**jupyter** *--version*

This will output the version information of the installed Jupyter, thereby verifying that the **jupyter** command can be found on the system's search path.

If you do not have Jupyter installed, the [Anaconda](https://www.continuum.io/anaconda) Python distribution bundles all necessary components for Jupyter in a single simple to use installer.

### Kernels

At the moment the Eclipse integration for the **EASE Jupyter Kernels** must be build manually.

The *org.eclipse.ease.jupyter.feature* project bundles all plugins to a single Eclipse feature.

Open the project in Eclipse (RCP), right-click on the project and click *Export...*.

Select *Plug-in Development* -> *Deployable features* and click *Next>*.

Choose a destination directory and in the *Options* tab make sure that:

+ *Package as individual JAR archives (required for JNLP and update sites)*
+ *Generate p2 repository* 
+ *Categorize repository*

are checked.

For *Categorize repository* select *Browse* and select *category.xml* from *org.eclipse.ease.jupyter.feature* project.

Make sure that *org.eclipse.ease.jupyter.feature* is selected in the list of available features and click *Finish*.

Once the p2 directory has been created, you can install the feature in Eclipse from the location you just created.



## Usage:

The **EASE Jupyter Kernels** already come with Eclipse UI integration.

To create a new EASE Jupyter notebook file simply right-click somewhere in a project and select *New* -> *Other*. 

Under *Jupyter* you can find the option *Jupyter Notebook File*. Use this and select an appropriate filename. As these files will be started using Jupyter based on their extensions, *.ipynb* will be added to the filename automatically.

On the next page you can select the EASE script engine you want to use for this notebook. The list of options is populated automatically based on the engines you currently have installed.

Once you have created the notebook file you can start it by simply double-clicking it. This will open a browser showing you the notebook. From here on you can use it as you would any other Jupyter notebook.

**Note:** As the **EASE Jupyter Kernels** need the EASE script engines it is (currently) only possible to use them directly from Eclipse.


## Bugs:

The easiest way to report bugs is to use the official Eclipse [bug-tracker](https://bugs.eclipse.org/bugs/describecomponents.cgi?product=Ease).

The project is currently not hosted on Eclipse as the external requirements do not match the necessary criteria.
Nevertheless the bug-tracker can be used.

Otherwise you can contact me by [mail](mailto:martin.kloesch@gmail.com).



## Developers:

See the [Developer Documentation](DEVELOPERS.md) for more information.


## License:

Licensed under [Eclipse Public License - v 1.0](https://www.eclipse.org/legal/epl-v10.html)



## Notes:

*org.eclipse.ease.jupyter.kernel* project based on parts of the [Japyter](https://github.com/openanalytics/japyter) project.


*org.eclipse.ease.jupyter.kernel* has several third-party dependencies.
Whenever possible the dependencies are fetched from [Eclipse Orbit](http://www.eclipse.org/orbit/)

As the [commons-codec](https://commons.apache.org/proper/commons-codec/) version in Orbit is too old, the unmodified binaries of version 1.10 have been copied to *org.eclipse.ease.jupyter.kernel/lib*.

As the [jeromq](https://github.com/zeromq/jeromq) library for the ZeroMQ protocol is not available in Orbit, the unmodified binaries of version 0.3.5 have been copied to *org.eclipse.ease.jupyter.kernel/lib*.

